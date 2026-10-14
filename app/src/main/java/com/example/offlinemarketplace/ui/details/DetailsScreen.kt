
package com.example.offlinemarketplace.ui.details

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.MaterialTheme
import com.example.offlinemarketplace.domain.model.Listing
import com.example.offlinemarketplace.ui.components.ListingFormScreen

@Composable
fun DetailsScreen(
    listing: Listing,
    isSaving: Boolean,
    @StringRes saveErrorResId: Int?,
    onBackClick: () -> Unit,
    onSave: (com.example.offlinemarketplace.domain.model.Listing) -> Unit
) {
    ListingFormScreen(
        isEditMode = true,
        isSaving = isSaving,
        saveErrorResId = saveErrorResId,
        initialTitle = listing.title,
        initialPrice = listing.price.toString(),
        initialCategory = listing.category,
        initialDescription = listing.description,
        initialImageUri = listing.imageUrl,
        onSave = onSave,
        onCancel = onBackClick
    )
}

@Preview(showBackground = true)
@Composable
private fun DetailsScreenPreview() {
    val sampleListing = Listing(
        id = 1L,
        title = "Study Table",
        price = 1500.0,
        category = "Furniture",
        description = "A wooden study table in good condition.",
        imageUrl = "",
        isFavorite = false
    )

    MaterialTheme {
        DetailsScreen(
            listing = sampleListing,
            isSaving = false,
            saveErrorResId = null,
            onBackClick = {},
            onSave = {}
        )
    }
}
