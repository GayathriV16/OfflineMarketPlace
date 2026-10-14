package com.example.offlinemarketplace.ui.components

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.example.offlinemarketplace.R
import com.example.offlinemarketplace.domain.model.Listing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun ListingFormScreen(
    isEditMode: Boolean,
    isSaving: Boolean = false,
    @StringRes saveErrorResId: Int? = null,
    initialTitle: String = "",
    initialPrice: String = "",
    initialCategory: String = "",
    initialDescription: String = "",
    initialImageUri: String? = null,
    onSave: (Listing) -> Unit,
    onCancel: () -> Unit
) {
    // Preserve entered values across configuration changes.
    // Keying by isEditMode prevents background updates from
    // unexpectedly resetting the user's input.
    var title by rememberSaveable(isEditMode) {
        mutableStateOf(initialTitle)
    }

    var price by rememberSaveable(isEditMode) {
        mutableStateOf(initialPrice)
    }

    var category by rememberSaveable(isEditMode) {
        mutableStateOf(initialCategory)
    }

    var description by rememberSaveable(isEditMode) {
        mutableStateOf(initialDescription)
    }

    // Store the image URI as a String so it can be saved.
    var selectedImageUri by rememberSaveable(isEditMode) {
        mutableStateOf(initialImageUri)
    }

    var errorMessageResId by remember {
        mutableIntStateOf(0)
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    fun hideKeyboard() {
        focusManager.clearFocus()
        keyboardController?.hide()
    }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            if (uri == null) {
                return@rememberLauncherForActivityResult
            }

            scope.launch {
                // Copy the selected image into app-private storage.
                val result = withContext(Dispatchers.IO) {
                    runCatching {
                        val input = context.contentResolver
                            .openInputStream(uri)
                            ?: error("Could not open selected photo")

                        val file = File(
                            context.filesDir,
                            "listing_${System.currentTimeMillis()}.jpg"
                        )

                        input.use { source ->
                            file.outputStream().use { output ->
                                source.copyTo(output)
                            }
                        }

                        Uri.fromFile(file).toString()
                    }
                }

                result
                    .onSuccess {
                        selectedImageUri = it
                        errorMessageResId = 0
                    }
                    .onFailure {
                        errorMessageResId =
                            R.string.error_save_selected_photo
                    }
            }
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode != Activity.RESULT_OK) {
                return@rememberLauncherForActivityResult
            }

            val bitmap = result.data
                ?.extras
                ?.get("data") as? Bitmap

            if (bitmap == null) {
                errorMessageResId =
                    R.string.error_save_captured_photo
                return@rememberLauncherForActivityResult
            }

            scope.launch {
                val saved = withContext(Dispatchers.IO) {
                    runCatching {
                        val file = File(
                            context.filesDir,
                            "camera_${System.currentTimeMillis()}.jpg"
                        )

                        file.outputStream().use { output ->
                            val compressed = bitmap.compress(
                                Bitmap.CompressFormat.JPEG,
                                85,
                                output
                            )

                            check(compressed) {
                                "Failed to compress camera image"
                            }
                        }

                        Uri.fromFile(file).toString()
                    }
                }

                saved
                    .onSuccess {
                        selectedImageUri = it
                        errorMessageResId = 0
                    }
                    .onFailure {
                        errorMessageResId =
                            R.string.error_save_captured_photo
                    }
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(dimensionResource(R.dimen.screen_padding)),
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.form_spacing)
        )
    ) {
        Text(
            text = stringResource(
                if (isEditMode) {
                    R.string.edit_listing
                } else {
                    R.string.create_listing
                }
            ),
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = stringResource(
                if (isEditMode) {
                    R.string.edit_listing_description
                } else {
                    R.string.create_listing_description
                }
            )
        )

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                errorMessageResId = 0
            },
            label = {
                Text(stringResource(R.string.title_label))
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = price,
            onValueChange = {
                price = it
                errorMessageResId = 0
            },
            label = {
                Text(stringResource(R.string.price_label))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = category,
            onValueChange = {
                category = it
                errorMessageResId = 0
            },
            label = {
                Text(stringResource(R.string.category_label))
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = {
                description = it
                errorMessageResId = 0
            },
            label = {
                Text(stringResource(R.string.description_label))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    dimensionResource(R.dimen.description_field_height)
                )
        )

        Text(stringResource(R.string.product_photo))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(
                dimensionResource(R.dimen.card_corner_radius)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation =
                    dimensionResource(R.dimen.card_elevation)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        dimensionResource(R.dimen.card_padding)
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!selectedImageUri.isNullOrBlank()) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = stringResource(
                            R.string.listing_image_description
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(
                                dimensionResource(
                                    R.dimen.listing_image_height
                                )
                            )
                            .clip(
                                RoundedCornerShape(
                                    dimensionResource(
                                        R.dimen.image_corner_radius
                                    )
                                )
                            ),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(
                                dimensionResource(
                                    R.dimen.listing_image_height
                                )
                            )
                            .clip(
                                RoundedCornerShape(
                                    dimensionResource(
                                        R.dimen.image_corner_radius
                                    )
                                )
                            )
                            .background(
                                colorResource(
                                    R.color.image_placeholder_background
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            stringResource(R.string.no_photo_selected)
                        )
                    }
                }

                Spacer(
                    Modifier.height(
                        dimensionResource(
                            R.dimen.photo_placeholder_spacing
                        )
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        dimensionResource(R.dimen.photo_button_spacing)
                    )
                ) {
                    Button(
                        onClick = {
                            hideKeyboard()

                            imagePickerLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts
                                        .PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.choose_photo))
                    }

                    Button(
                        onClick = {
                            hideKeyboard()

                            try {
                                cameraLauncher.launch(
                                    Intent(
                                        MediaStore.ACTION_IMAGE_CAPTURE
                                    )
                                )
                            } catch (e: ActivityNotFoundException) {
                                errorMessageResId =
                                    R.string.error_save_captured_photo
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.take_photo))
                    }
                }
            }
        }

        // Display local validation/photo errors before save errors.
        val visibleErrorResId =
            if (errorMessageResId != 0) {
                errorMessageResId
            } else {
                saveErrorResId
            }

        if (visibleErrorResId != null) {
            Text(
                text = stringResource(visibleErrorResId),
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            enabled = !isSaving,
            onClick = {
                if (isSaving) return@Button
                val priceValue = price.toDoubleOrNull()

                when {
                    title.isBlank() -> {
                        errorMessageResId = R.string.error_enter_title
                    }

                    priceValue == null || priceValue < 0 -> {
                        errorMessageResId = R.string.error_invalid_price
                    }

                    category.isBlank() -> {
                        errorMessageResId = R.string.error_enter_category
                    }

                    description.isBlank() -> {
                        errorMessageResId =
                            R.string.error_enter_description
                    }

                    else -> {
                        hideKeyboard()

                        onSave(
                            Listing(
                                id = 0L,
                                title = title.trim(),
                                price = priceValue,
                                category = category.trim(),
                                description = description.trim(),
                                imageUrl = selectedImageUri
                            )
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(
                dimensionResource(R.dimen.button_corner_radius)
            )
        ) {
            Text(
                stringResource(
                    if (isEditMode) {
                        R.string.save_changes
                    } else {
                        R.string.create_listing
                    }
                )
            )
        }

        TextButton(
            enabled = !isSaving,
            onClick = {
                hideKeyboard()
                onCancel()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.cancel))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateListingPreview() {
    MaterialTheme {
        ListingFormScreen(
            isEditMode = false,
            onSave = {},
            onCancel = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ListingDetailPreview() {
    MaterialTheme {
        ListingFormScreen(
            isEditMode = true,
            onSave = {},
            onCancel = {}
        )
    }
}