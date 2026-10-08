package com.example.offlinemarketplace.ui.create

import android.net.Uri
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

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

    var title by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    val imagePickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            uri?.let {
                try {
                    val inputStream =
                        context.contentResolver.openInputStream(it)

                    if (inputStream != null) {
                        val fileName =
                            "listing_${System.currentTimeMillis()}.jpg"

                        val file =
                            java.io.File(context.filesDir, fileName)

                        file.outputStream().use { outputStream ->
                            inputStream.use { input ->
                                input.copyTo(outputStream)
                            }
                        }

                        selectedImageUri = Uri.fromFile(file)
                    }
                } catch (e: Exception) {
                    errorMessage = "Unable to save selected photo."
                }
            }
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == android.app.Activity.RESULT_OK) {

                val bitmap =
                    result.data?.extras?.get("data")
                            as? android.graphics.Bitmap

                if (bitmap != null) {
                    try {

                        val fileName =
                            "camera_${System.currentTimeMillis()}.jpg"

                        val file =
                            java.io.File(context.filesDir, fileName)

                        file.outputStream().use { outputStream ->
                            bitmap.compress(
                                android.graphics.Bitmap.CompressFormat.JPEG,
                                85,
                                outputStream
                            )
                        }

                        selectedImageUri = Uri.fromFile(file)

                    } catch (e: Exception) {
                        errorMessage =
                            "Unable to save captured photo."
                    }
                }
            }
        }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .navigationBarsPadding()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Create Listing"
        )

        Text(
            text = "Add details about the item you want to sell."
        )

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                errorMessage = null
            },
            label = {
                Text("Title")
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = price,
            onValueChange = {
                price = it
                errorMessage = null
            },
            label = {
                Text("Price (₹)")
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = category,
            onValueChange = {
                category = it
                errorMessage = null
            },
            label = {
                Text("Category")
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = {
                description = it
                errorMessage = null
            },
            label = {
                Text("Description")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        Text(
            text = "Product Photo"
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (selectedImageUri != null) {

                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Selected listing image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(
                                RoundedCornerShape(12.dp)
                            ),
                        contentScale = ContentScale.Crop
                    )

                } else {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(
                                RoundedCornerShape(12.dp)
                            )
                            .background(
                                androidx.compose.ui.graphics.Color.LightGray
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No photo selected"
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    Button(
                        onClick = {
                            imagePickerLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts
                                        .PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Choose Photo")
                    }

                    Button(
                        onClick = {
                            val cameraIntent =
                                Intent(
                                    MediaStore.ACTION_IMAGE_CAPTURE
                                )

                            cameraLauncher.launch(cameraIntent)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Take Photo")
                    }
                }
            }
        }

        errorMessage?.let {
            Text(
                text = it
            )
        }

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Button(
            onClick = {

                val priceValue =
                    price.toDoubleOrNull()

                when {

                    title.isBlank() ->
                        errorMessage =
                            "Please enter a title."

                    priceValue == null ||
                            priceValue < 0 ->
                        errorMessage =
                            "Please enter a valid price."

                    category.isBlank() ->
                        errorMessage =
                            "Please enter a category."

                    description.isBlank() ->
                        errorMessage =
                            "Please enter a description."

                    else ->
                        onListingCreated(
                            title.trim(),
                            priceValue,
                            category.trim(),
                            description.trim(),
                            selectedImageUri?.toString()
                        )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Create Listing")
        }

        TextButton(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel")
        }
    }
}