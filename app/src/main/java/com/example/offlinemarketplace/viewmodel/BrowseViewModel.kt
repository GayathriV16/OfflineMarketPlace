package com.example.offlinemarketplace.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.offlinemarketplace.data.model.Listing
import com.example.offlinemarketplace.domain.usecases.LoadInitialListingsUseCase
import com.example.offlinemarketplace.domain.usecases.ObserveListingsUseCase
import com.example.offlinemarketplace.domain.usecases.ObservePendingOperationsUseCase
import com.example.offlinemarketplace.domain.usecases.SyncListingsUseCase
import com.example.offlinemarketplace.domain.usecases.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SyncStatus {
    NOT_SYNCED,
    PENDING,
    SYNCING,
    SYNCED,
    FAILED
}

@HiltViewModel
class BrowseViewModel @Inject constructor(
    observeListingsUseCase: ObserveListingsUseCase,
    private val observePendingOperationsUseCase: ObservePendingOperationsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val loadInitialListingsUseCase: LoadInitialListingsUseCase,
    private val syncListingsUseCase: SyncListingsUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)

    private val _syncStatus =
        MutableStateFlow(SyncStatus.NOT_SYNCED)

    private val pendingOperationCount: StateFlow<Int> =
        observePendingOperationsUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = 0
            )

    private val _uiState = MutableStateFlow(BrowseUiState())

    val uiState: StateFlow<BrowseUiState> =
        _uiState.asStateFlow()

    private var initialLoadJob: Job? = null
    private var syncJob: Job? = null

    init {
        viewModelScope.launch {
            combine(
                observeListingsUseCase(),
                pendingOperationCount,
                _isLoading,
                _syncStatus
            ) { listings, pendingCount, isLoading, syncStatus ->
                BrowseUiState(
                    listings = listings,
                    pendingOperationCount = pendingCount,
                    isLoading = isLoading,
                    syncStatus = syncStatus
                )
            }.collect { state ->
                _uiState.value = state
            }
        }

        viewModelScope.launch {
            pendingOperationCount.collect { count ->
                if (_syncStatus.value != SyncStatus.SYNCING) {
                    when {
                        count == 0 -> {
                            _syncStatus.value = SyncStatus.SYNCED
                        }

                        _syncStatus.value != SyncStatus.FAILED -> {
                            _syncStatus.value = SyncStatus.PENDING
                        }
                    }
                }
            }
        }
    }

    fun toggleFavorite(listing: Listing) {
        viewModelScope.launch {
            try {
                toggleFavoriteUseCase(
                    listingId = listing.id,
                    isFavorite = !listing.isFavorite
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _syncStatus.value = SyncStatus.FAILED
            }
        }
    }

    fun loadInitialListingsIfNeeded() {
        if (initialLoadJob?.isActive == true) return

        initialLoadJob = viewModelScope.launch {
            _isLoading.value = true

            try {
                loadInitialListingsUseCase()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _syncStatus.value = SyncStatus.FAILED
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun syncNow() {
        if (syncJob?.isActive == true) return

        syncJob = viewModelScope.launch {
            _syncStatus.value = SyncStatus.SYNCING

            try {
                syncListingsUseCase()

                // Read the latest count directly from the observed data source.
                val pendingCount =
                    observePendingOperationsUseCase().first()

                _syncStatus.value = if (pendingCount > 0) {
                    SyncStatus.PENDING
                } else {
                    SyncStatus.SYNCED
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _syncStatus.value = SyncStatus.FAILED
            }
        }
    }
}

data class BrowseUiState(
    val listings: List<Listing> = emptyList(),
    val pendingOperationCount: Int = 0,
    val isLoading: Boolean = true,
    val syncStatus: SyncStatus = SyncStatus.NOT_SYNCED
)