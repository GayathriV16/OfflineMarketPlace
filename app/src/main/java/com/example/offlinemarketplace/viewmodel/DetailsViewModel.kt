package com.example.offlinemarketplace.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.offlinemarketplace.domain.model.Listing
import com.example.offlinemarketplace.domain.usecases.ObserveListingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface DetailsUiState {
    data object Loading : DetailsUiState
    data class Content(val listing: Listing) : DetailsUiState
    data object NotFound : DetailsUiState
}

/**
 * Manages the state of the selected listing's details screen.
 *
 * Observes listing data through a domain use case and exposes
 * loading, content, and not-found states to the UI.
 */
@HiltViewModel
class DetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeListings: ObserveListingsUseCase
) : ViewModel() {

    private val listingId: Long? =
        savedStateHandle["listingId"]

    val uiState: StateFlow<DetailsUiState> =
        observeListings()
            .map { listings ->
                val id = listingId

                if (id == null) {
                    DetailsUiState.NotFound
                } else {
                    val listing = listings.firstOrNull {
                        it.id == id
                    }

                    if (listing != null) {
                        DetailsUiState.Content(listing)
                    } else {
                        DetailsUiState.NotFound
                    }
                }
            }
            .distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = DetailsUiState.Loading
            )
}