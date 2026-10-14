
package com.example.offlinemarketplace.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.offlinemarketplace.domain.usecases.LoadInitialListingsUseCase
import com.example.offlinemarketplace.domain.usecases.ObserveListingsUseCase
import com.example.offlinemarketplace.domain.usecases.ObservePendingOperationsUseCase
import com.example.offlinemarketplace.domain.usecases.SyncListingsUseCase
import com.example.offlinemarketplace.domain.usecases.ToggleFavoriteUseCase
import com.example.offlinemarketplace.ui.browse.BrowseUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SyncStatus {
    NOT_SYNCED,
    PENDING,
    SYNCING,
    SYNCED,
    FAILED
}

/**
 * One-time events for the UI, such as displaying a snackbar.
 */
sealed interface BrowseEvent {
    data object FavoriteUpdateFailed : BrowseEvent
    data object InitialLoadFailed : BrowseEvent
}

/**
 * Manages marketplace browsing actions and UI state.
 */
@HiltViewModel
class BrowseViewModel @Inject constructor(
    observeListings: ObserveListingsUseCase,
    observePendingOperations: ObservePendingOperationsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val loadInitialListingsUseCase: LoadInitialListingsUseCase,
    private val syncListingsUseCase: SyncListingsUseCase
) : ViewModel() {

    private val isLoading = MutableStateFlow(true)
    private val isSyncing = MutableStateFlow(false)
    private val lastSyncFailed = MutableStateFlow(false)

    private val _events = Channel<BrowseEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    /**
     * Combines database observations and operation state into one UI state.
     */
    val uiState: StateFlow<BrowseUiState> = combine(
        observeListings(),
        observePendingOperations(),
        isLoading,
        isSyncing,
        lastSyncFailed
    ) { listings, pendingCount, loading, syncing, failed ->
        BrowseUiState(
            listings = listings,
            pendingOperationCount = pendingCount,
            isLoading = loading,
            syncStatus = deriveSyncStatus(
                pendingCount = pendingCount,
                syncing = syncing,
                failed = failed,
                loading = loading
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BrowseUiState()
    )

    private var syncJob: Job? = null

    init {
        loadInitialListings()
    }

    /**
     * Updates the favorite state without accepting a Listing model.
     */
    fun toggleFavorite(
        listingId: Long,
        isFavorite: Boolean
    ) {
        viewModelScope.launch {
            try {
                toggleFavoriteUseCase(
                    listingId = listingId,
                    isFavorite = !isFavorite
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _events.trySend(BrowseEvent.FavoriteUpdateFailed)
            }
        }
    }

    /**
     * Runs synchronization when requested by the user.
     */
    fun syncNow() {
        if (syncJob?.isActive == true) return

        syncJob = viewModelScope.launch {
            isSyncing.value = true
            lastSyncFailed.value = false

            try {
                syncListingsUseCase()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                lastSyncFailed.value = true
            } finally {
                isSyncing.value = false
            }
        }
    }

    private fun loadInitialListings() {
        viewModelScope.launch {
            try {
                loadInitialListingsUseCase()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _events.trySend(BrowseEvent.InitialLoadFailed)
            } finally {
                isLoading.value = false
            }
        }
    }

    private fun deriveSyncStatus(
        pendingCount: Int,
        syncing: Boolean,
        failed: Boolean,
        loading: Boolean
    ): SyncStatus = when {
        loading -> SyncStatus.NOT_SYNCED
        syncing -> SyncStatus.SYNCING
        failed -> SyncStatus.FAILED
        pendingCount == 0 -> SyncStatus.SYNCED
        else -> SyncStatus.PENDING
    }
}