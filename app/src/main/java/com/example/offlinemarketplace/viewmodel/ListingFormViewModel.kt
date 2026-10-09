
package com.example.offlinemarketplace.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.offlinemarketplace.data.model.Listing
import com.example.offlinemarketplace.domain.usecases.CreateListingUseCase
import com.example.offlinemarketplace.domain.usecases.UpdateListingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ListingFormViewModel @Inject constructor(
    private val createListingUseCase: CreateListingUseCase,
    private val updateListingUseCase: UpdateListingUseCase
) : ViewModel() {

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

            createListingUseCase(listing)
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
            updateListingUseCase(
                listingId = listingId,
                title = title,
                price = price,
                category = category,
                description = description,
                imageUri = imageUri
            )

            onComplete()
        }
    }
}