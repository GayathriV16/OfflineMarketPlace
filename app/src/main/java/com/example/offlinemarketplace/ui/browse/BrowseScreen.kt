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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.offlinemarketplace.data.model.Listing
import com.example.offlinemarketplace.ui.components.ListingCard
import com.example.offlinemarketplace.viewmodel.BrowseViewModel

@Composable
fun BrowseScreen(
    viewModel: BrowseViewModel,
    onListingClick: (Listing) -> Unit,
    onCreateListingClick: () -> Unit
) {

    val listings by viewModel.listings.collectAsStateWithLifecycle()

    val syncStatus by viewModel.syncStatus
        .collectAsStateWithLifecycle()

    val pendingCount by viewModel.pendingOperationCount
        .collectAsStateWithLifecycle()

    val isLoading by viewModel.isLoading
        .collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadInitialListingsIfNeeded()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        if (isLoading) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    CircularProgressIndicator()

                    Text(
                        text = "Loading listings...",
                        modifier = Modifier.padding(top = 12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

        } else {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {

                Column(
                    modifier = Modifier.padding(
                        top = 20.dp,
                        bottom = 12.dp
                    )
                ) {

                    Text(
                        text = "Marketplace",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Text(
                        text = "Browse items available offline",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            text = when {
                                pendingCount > 0 ->
                                    "Changes waiting to sync: $pendingCount"

                                syncStatus == "Syncing..." ->
                                    "Syncing changes..."

                                syncStatus == "Sync complete" ->
                                    "All changes synced"

                                syncStatus == "Sync failed" ->
                                    "Sync failed — try again"

                                else ->
                                    "All changes synced"
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Button(
                            onClick = {
                                viewModel.syncNow()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Sync Now")
                        }
                    }
                }

                Text(
                    text = "${listings.size} Listings",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(
                        top = 16.dp,
                        bottom = 8.dp
                    )
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = 4.dp,
                        bottom = 100.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(
                        items = listings,
                        key = { it.id }
                    ) { listing ->

                        ListingCard(
                            listing = listing,
                            onClick = {
                                onListingClick(listing)
                            },
                            onFavoriteClick = {
                                viewModel.toggleFavorite(listing)
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
                    .padding(20.dp)
            ) {
                Text(
                    text = "+"
                )
            }
        }
    }
}