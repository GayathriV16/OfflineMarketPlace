package com.example.offlinemarketplace.ui.details

import androidx.compose.runtime.Composable
import com.example.offlinemarketplace.data.model.Listing
import com.example.offlinemarketplace.ui.components.ListingFormScreen

@Composable
fun DetailsScreen(
    listing: Listing,
    onBackClick: () -> Unit,
    onListingUpdated: (
        title: String,
        price: Double,
        category: String,
        description: String,
        imageUri: String?
    ) -> Unit
) {
    ListingFormScreen(
        isEditMode = true,
        initialTitle = listing.title,
        initialPrice = listing.price.toString(),
        initialCategory = listing.category,
        initialDescription = listing.description,
        initialImageUri = listing.imageUrl,
        onSave = { title, price, category, description, imageUri ->
            onListingUpdated(
                title,
                price,
                category,
                description,
                imageUri
            )
        },
        onCancel = onBackClick
    )
}
