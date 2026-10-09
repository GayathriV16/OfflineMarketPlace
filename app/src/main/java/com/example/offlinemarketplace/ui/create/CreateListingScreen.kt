package com.example.offlinemarketplace.ui.create

import androidx.compose.runtime.Composable
import com.example.offlinemarketplace.ui.components.ListingFormScreen

@Composable
fun CreateListingScreen(
    onBackClick: () -> Unit,
    onListingCreated: (
        title: String,
        price: Double,
        category: String,
        description: String,
        imageUri: String?
    ) -> Unit
) {
    ListingFormScreen(
        isEditMode = false,
        onSave = { title, price, category, description, imageUri ->
            onListingCreated(
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