package com.example.offlinemarketplace.ui.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.offlinemarketplace.R
import com.example.offlinemarketplace.data.model.Listing
import com.example.offlinemarketplace.ui.components.ListingCard
import com.example.offlinemarketplace.viewmodel.BrowseViewModel
import com.example.offlinemarketplace.viewmodel.SyncStatus

@Composable
fun BrowseScreen(
    viewModel: BrowseViewModel,
    onListingClick: (Listing) -> Unit,
    onCreateListingClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.loadInitialListingsIfNeeded()
    }

    BrowseContent(
        listings = uiState.listings,
        syncStatus = uiState.syncStatus,
        pendingCount = uiState.pendingOperationCount,
        isLoading = uiState.isLoading,
        onSyncNow = viewModel::syncNow,
        onListingClick = onListingClick,
        onCreateListingClick = onCreateListingClick,
        onFavoriteClick = viewModel::toggleFavorite
    )
}

@Composable
private fun BrowseContent(
    listings: List<Listing>,
    syncStatus: SyncStatus,
    pendingCount: Int,
    isLoading: Boolean,
    onSyncNow: () -> Unit,
    onListingClick: (Listing) -> Unit,
    onCreateListingClick: () -> Unit,
    onFavoriteClick: (Listing) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Text(
                    text = stringResource(R.string.loading_listings),
                    modifier = Modifier.padding(
                        top = dimensionResource(R.dimen.browse_loading_top_padding)
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = dimensionResource(
                            R.dimen.browse_horizontal_padding
                        )
                    )
            ) {
                Column(
                    modifier = Modifier.padding(
                        top = dimensionResource(
                            R.dimen.browse_header_top_padding
                        ),
                        bottom = dimensionResource(
                            R.dimen.browse_header_bottom_padding
                        )
                    )
                ) {
                    Text(
                        text = stringResource(R.string.marketplace_title),
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Text(
                        text = stringResource(R.string.browse_offline_subtitle),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = dimensionResource(
                            R.dimen.sync_card_elevation
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(
                            dimensionResource(R.dimen.sync_card_padding)
                        ),
                        verticalArrangement = Arrangement.spacedBy(
                            dimensionResource(R.dimen.sync_card_spacing)
                        )
                    ) {
                        Text(
                            text = when (syncStatus) {
                                SyncStatus.NOT_SYNCED ->
                                    stringResource(R.string.sync_not_started)

                                SyncStatus.PENDING ->
                                    stringResource(
                                        R.string.sync_pending,
                                        pendingCount
                                    )

                                SyncStatus.SYNCING ->
                                    stringResource(R.string.syncing_changes)

                                SyncStatus.SYNCED ->
                                    stringResource(R.string.sync_complete)

                                SyncStatus.FAILED ->
                                    stringResource(R.string.sync_failed)
                            }
                        )

                        Button(
                            onClick = onSyncNow,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.sync_now))
                        }
                    }
                }

                Text(
                    text = stringResource(
                        R.string.listing_count,
                        listings.size
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(
                        top = dimensionResource(
                            R.dimen.listing_count_top_padding
                        ),
                        bottom = dimensionResource(
                            R.dimen.listing_count_bottom_padding
                        )
                    )
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(
                        top = dimensionResource(
                            R.dimen.grid_content_top_padding
                        ),
                        bottom = dimensionResource(
                            R.dimen.grid_content_bottom_padding
                        )
                    ),
                    horizontalArrangement = Arrangement.spacedBy(
                        dimensionResource(R.dimen.grid_item_spacing)
                    ),
                    verticalArrangement = Arrangement.spacedBy(
                        dimensionResource(R.dimen.grid_item_spacing)
                    )
                ) {
                    items(
                        items = listings,
                        key = { it.id }
                    ) { listing ->
                        ListingCard(
                            listing = listing,
                            onClick = { onListingClick(listing) },
                            onFavoriteClick = {
                                onFavoriteClick(listing)
                            }
                        )
                    }
                }
            }

            FloatingActionButton(
                onClick = onCreateListingClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(dimensionResource(R.dimen.fab_padding))
            ) {
                Text("+")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BrowseContentPreview() {
    val sampleListings = listOf(
        Listing(
            id = 1L,
            title = "Study Table",
            price = 1500.0,
            category = "Furniture",
            description = "A wooden study table in good condition.",
            imageUrl = "",
            isFavorite = false
        ),
        Listing(
            id = 2L,
            title = "Headphones",
            price = 800.0,
            category = "Electronics",
            description = "Headphones in excellent condition.",
            imageUrl = "",
            isFavorite = true
        )
    )

    MaterialTheme {
        BrowseContent(
            listings = sampleListings,
            syncStatus = SyncStatus.SYNCED,
            pendingCount = 0,
            isLoading = false,
            onSyncNow = {},
            onListingClick = {},
            onCreateListingClick = {},
            onFavoriteClick = {}
        )
    }
}