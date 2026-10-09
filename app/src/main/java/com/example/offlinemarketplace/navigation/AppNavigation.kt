package com.example.offlinemarketplace.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.offlinemarketplace.ui.browse.BrowseScreen
import com.example.offlinemarketplace.ui.create.CreateListingScreen
import com.example.offlinemarketplace.ui.details.DetailsScreen
import com.example.offlinemarketplace.viewmodel.BrowseViewModel
import com.example.offlinemarketplace.viewmodel.ListingFormViewModel

private object AppRoutes {
    const val BROWSE = "browse"
    const val CREATE_LISTING = "create_listing"
    const val DETAILS = "details/{listingId}"

    fun details(listingId: Long): String {
        return "details/$listingId"
    }
}

@Composable
fun AppNavigation(
    browseViewModel: BrowseViewModel,
    listingFormViewModel: ListingFormViewModel
) {
    val navController = rememberNavController()

    val uiState by browseViewModel.uiState.collectAsStateWithLifecycle()
    val listings = uiState.listings

    NavHost(
        navController = navController,
        startDestination = AppRoutes.BROWSE
    ) {
        composable(AppRoutes.BROWSE) {
            BrowseScreen(
                viewModel = browseViewModel,
                onListingClick = { listing ->
                    navController.navigate(
                        AppRoutes.details(listing.id)
                    )
                },
                onCreateListingClick = {
                    navController.navigate(
                        AppRoutes.CREATE_LISTING
                    )
                }
            )
        }

        composable(AppRoutes.CREATE_LISTING) {
            CreateListingScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onListingCreated = { title,
                                     price,
                                     category,
                                     description,
                                     imageUri ->

                    listingFormViewModel.createListing(
                        title = title,
                        price = price,
                        category = category,
                        description = description,
                        imageUri = imageUri
                    )

                    navController.popBackStack()
                }
            )
        }

        composable(
            route = AppRoutes.DETAILS,
            arguments = listOf(
                navArgument("listingId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->

            val listingId = backStackEntry
                .arguments
                ?.getLong("listingId")

            val listing = listingId?.let { id ->
                listings.firstOrNull { it.id == id }
            }

            if (listing == null) {
                LaunchedEffect(listingId) {
                    navController.popBackStack()
                }
            } else {
                DetailsScreen(
                    listing = listing,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onListingUpdated = { title,
                                         price,
                                         category,
                                         description,
                                         imageUri ->

                        listingFormViewModel.updateListing(
                            listingId = listing.id,
                            title = title,
                            price = price,
                            category = category,
                            description = description,
                            imageUri = imageUri,
                            onComplete = {
                                navController.popBackStack()
                            }
                        )
                    }
                )
            }
        }
    }
}
