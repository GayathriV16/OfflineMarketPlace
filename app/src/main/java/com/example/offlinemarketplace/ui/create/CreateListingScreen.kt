
package com.example.offlinemarketplace.ui.create

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.MaterialTheme
import com.example.offlinemarketplace.domain.model.Listing
import com.example.offlinemarketplace.ui.components.ListingFormScreen

@Composable
fun CreateListingScreen(
    isSaving: Boolean,
    @StringRes saveErrorResId: Int?,
    onBackClick: () -> Unit,
    onSave: (Listing) -> Unit
) {
    ListingFormScreen(
        isEditMode = false,
        isSaving = isSaving,
        saveErrorResId = saveErrorResId,
        onSave = onSave,
        onCancel = onBackClick
    )
}

@Preview(showBackground = true)
@Composable
private fun CreateListingScreenPreview() {
    MaterialTheme {
        CreateListingScreen(
            isSaving = false,
            saveErrorResId = null,
            onBackClick = {},
            onSave = {}
        )
    }
}