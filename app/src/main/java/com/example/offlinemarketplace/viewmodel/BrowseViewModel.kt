package com.example.offlinemarketplace.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.offlinemarketplace.data.model.Listing
import com.example.offlinemarketplace.data.repository.ListingRepository
import com.example.offlinemarketplace.sync.SyncManager
import com.example.offlinemarketplace.sync.SyncScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BrowseViewModel(
    private val repository: ListingRepository,
    private val syncManager: SyncManager,
    private val context: Context
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _syncStatus = MutableStateFlow("Not synced")
    val syncStatus: StateFlow<String> = _syncStatus

    val listings: StateFlow<List<Listing>> =
        repository.getListings()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val pendingOperationCount: StateFlow<Int> =
        repository.pendingOperationCount
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = 0
            )

    init {
        viewModelScope.launch {
            pendingOperationCount.collect { count ->
                _syncStatus.value = if (count > 0) {
                    "Pending sync"
                } else {
                    "Synced"
                }
            }
        }
    }

    fun toggleFavorite(listing: Listing) {
        viewModelScope.launch {
            repository.updateFavorite(
                listingId = listing.id,
                isFavorite = !listing.isFavorite
            )
        }
    }

    fun createListing(
        title: String,
        price: Double,
        category: String,
        description: String,
        imageUri: String?
    ) {
        viewModelScope.launch {

            val listing = Listing(
                id = System.currentTimeMillis(),
                title = title,
                price = price,
                category = category,
                description = description,
                imageUrl = imageUri,
                isFavorite = false
            )

            repository.createListingOffline(listing)
            SyncScheduler.scheduleSync(context)
        }
    }

    fun updateListing(
        listingId: Long,
        title: String,
        price: Double,
        category: String,
        description: String,
        imageUri: String?,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {

            repository.updateListingOffline(
                listingId = listingId,
                title = title,
                price = price,
                category = category,
                description = description,
                imageUri = imageUri
            )

            SyncScheduler.scheduleSync(context)

            onComplete()
        }
    }

    fun loadInitialListingsIfNeeded() {
        viewModelScope.launch {

            _isLoading.value = true

            try {
                if (!repository.hasListings()) {

                    val serverListings =
                        repository.fetchListingsFromServer()

                    repository.saveListings(serverListings)
                }

                if (repository.pendingOperationCount.first() > 0) {
                    SyncScheduler.scheduleSync(context)
                }

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun syncNow() {
        viewModelScope.launch {
            _syncStatus.value = "Syncing..."

            try {
                syncManager.syncPendingOperations()
                _syncStatus.value = "Sync complete"
            } catch (e: Exception) {
                _syncStatus.value = "Sync failed"
            }
        }
    }
}

class BrowseViewModelFactory(
    private val repository: ListingRepository,
    private val syncManager: SyncManager,
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(BrowseViewModel::class.java)) {
            return BrowseViewModel(repository, syncManager, context) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}