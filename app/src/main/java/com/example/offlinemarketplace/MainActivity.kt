package com.example.offlinemarketplace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.*
import com.example.offlinemarketplace.data.model.Listing
import com.example.offlinemarketplace.ui.browse.BrowseScreen
import com.example.offlinemarketplace.ui.create.CreateListingScreen
import com.example.offlinemarketplace.ui.details.DetailsScreen
import com.example.offlinemarketplace.ui.theme.OfflineMarketPlaceTheme
import com.example.offlinemarketplace.viewmodel.BrowseViewModel
import com.example.offlinemarketplace.viewmodel.BrowseViewModelFactory

class MainActivity : ComponentActivity() {

    private val appContainer by lazy {
        AppContainer(applicationContext)
    }

    private val browseViewModel: BrowseViewModel by viewModels {
        BrowseViewModelFactory(
            appContainer.listingRepository,
            appContainer.syncManager,
            applicationContext
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            OfflineMarketPlaceTheme {

                var selectedListing by remember {
                    mutableStateOf<Listing?>(null)
                }

                var showCreateScreen by remember {
                    mutableStateOf(false)
                }
                BackHandler(
                    enabled = showCreateScreen || selectedListing != null
                ) {
                    if (showCreateScreen) {
                        showCreateScreen = false
                    } else if (selectedListing != null) {
                        selectedListing = null
                    }
                }
                if (showCreateScreen) {

                    CreateListingScreen(
                        onBackClick = {
                            showCreateScreen = false
                        },
                        onListingCreated = { title,
                                             price,
                                             category,
                                             description,
                                             imageUri
                            ->
                            browseViewModel.createListing(
                                title = title,
                                price = price,
                                category = category,
                                description = description,
                                imageUri = imageUri
                            )

                            showCreateScreen = false
                        }
                    )

                } else if (selectedListing == null) {

                    BrowseScreen(
                        viewModel = browseViewModel,
                        onListingClick = { listing ->
                            selectedListing = listing
                        },
                        onCreateListingClick = {
                            showCreateScreen = true
                        }
                    )

                } else {

                    DetailsScreen(
                        listing = selectedListing!!,

                        onBackClick = {
                            selectedListing = null
                        },

                        onListingUpdated = { title,
                                             price,
                                             category,
                                             description,
                                             imageUri
                            ->

                            browseViewModel.updateListing(
                                listingId = selectedListing!!.id,
                                title = title,
                                price = price,
                                category = category,
                                description = description,
                                imageUri = imageUri,
                                onComplete = {
                                    selectedListing = null
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}