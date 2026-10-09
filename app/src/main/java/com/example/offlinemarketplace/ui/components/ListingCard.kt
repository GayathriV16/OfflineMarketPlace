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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.example.offlinemarketplace.R
import com.example.offlinemarketplace.data.model.Listing

@Composable
fun ListingCard(
    listing: Listing,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(
            dimensionResource(R.dimen.listing_card_corner_radius)
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        dimensionResource(R.dimen.listing_card_image_height)
                    )
            ) {
                if (!listing.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(listing.imageUrl)
                            .size(240, 240)
                            .build(),
                        contentDescription = stringResource(
                            R.string.listing_image_description
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(
                                dimensionResource(
                                    R.dimen.listing_card_image_height
                                )
                            )
                            .clip(
                                RoundedCornerShape(
                                    topStart = dimensionResource(
                                        R.dimen.listing_card_corner_radius
                                    ),
                                    topEnd = dimensionResource(
                                        R.dimen.listing_card_corner_radius
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
                                    R.dimen.listing_card_image_height
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(
                                R.string.no_image_available
                            ),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(
                            dimensionResource(R.dimen.favorite_button_padding)
                        )
                        .size(
                            dimensionResource(R.dimen.favorite_button_size)
                        )
                ) {
                    Text(
                        text = stringResource(
                            if (listing.isFavorite) {
                                R.string.favorite_selected_symbol
                            } else {
                                R.string.favorite_unselected_symbol
                            }
                        ),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            Column(
                modifier = Modifier.padding(
                    dimensionResource(R.dimen.listing_card_content_padding)
                ),
                verticalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.listing_card_content_spacing)
                )
            ) {
                Text(
                    text = listing.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )

                Text(
                    text = stringResource(
                        R.string.listing_price,
                        listing.price.toString()
                    ),
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