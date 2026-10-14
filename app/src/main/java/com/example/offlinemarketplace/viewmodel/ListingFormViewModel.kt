package com.example.offlinemarketplace.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.offlinemarketplace.R
import com.example.offlinemarketplace.domain.model.Listing
import com.example.offlinemarketplace.domain.usecases.CreateListingUseCase
import com.example.offlinemarketplace.domain.usecases.UpdateListingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class ListingFormUiState(
    val isSaving: Boolean = false,
    @StringRes val errorResId: Int? = null
)

sealed interface ListingFormEvent {
    data object Saved : ListingFormEvent
}

@HiltViewModel
class ListingFormViewModel @Inject constructor(
    private val createListingUseCase: CreateListingUseCase,
    private val updateListingUseCase: UpdateListingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListingFormUiState())
    val uiState: StateFlow<ListingFormUiState> = _uiState.asStateFlow()

    private val _events = Channel<ListingFormEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    /**
     * A null listingId creates a listing.
     * A non-null listingId updates an existing listing.
     */
    fun save(draft: Listing, listingId: Long? = null) {
        if (_uiState.value.isSaving) return

        _uiState.value = ListingFormUiState(isSaving = true)

        viewModelScope.launch {
            try {
                if (listingId == null) {
                    createListingUseCase(draft)
                } else {
                    updateListingUseCase(listingId, draft)
                }

                // Keep saving active until navigation removes the form.
                _events.send(ListingFormEvent.Saved)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = ListingFormUiState(
                    isSaving = false,
                    errorResId = R.string.error_save_listing_failed
                )
            }
        }
    }
}