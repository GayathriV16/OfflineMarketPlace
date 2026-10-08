package com.example.offlinemarketplace.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.example.offlinemarketplace.data.model.Listing

@Composable
fun ListingCard(
    listing: Listing,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(16.dp)
    ) {

        Column {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {

                if (listing.imageUrl != null) {

                    AsyncImage(
                        model = listing.imageUrl.let { imageUrl ->
                            ImageRequest.Builder(
                                androidx.compose.ui.platform.LocalContext.current
                            )
                                .data(imageUrl)
                                .size(240, 240)
                                .build()
                        },
                        contentDescription = "Listing image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp
                                )
                            ),
                        contentScale = ContentScale.Crop
                    )

                } else {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No Image",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(40.dp)
                ) {
                    Text(
                        text = if (listing.isFavorite) {
                            "♥"
                        } else {
                            "♡"
                        },
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {

                Text(
                    text = listing.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )

                Text(
                    text = "₹${listing.price}",
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    text = listing.category,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}