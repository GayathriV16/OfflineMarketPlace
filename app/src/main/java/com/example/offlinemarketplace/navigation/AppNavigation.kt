package com.example.offlinemarketplace.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.offlinemarketplace.ui.browse.BrowseScreen
import com.example.offlinemarketplace.ui.create.CreateListingScreen
import com.example.offlinemarketplace.ui.details.DetailsScreen
import com.example.offlinemarketplace.viewmodel.BrowseViewModel
import com.example.offlinemarketplace.viewmodel.DetailsUiState
import com.example.offlinemarketplace.viewmodel.DetailsViewModel
import com.example.offlinemarketplace.viewmodel.ListingFormViewModel

private object AppRoutes {
    const val BROWSE = "browse"
    const val CREATE_LISTING = "create_listing"
    const val LISTING_ID_ARG = "listingId"
    const val DETAILS = "details/{$LISTING_ID_ARG}"

    fun details(listingId: Long): String = "details/$listingId"
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.BROWSE
    ) {
        composable(AppRoutes.BROWSE) {
            val viewModel: BrowseViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            BrowseScreen(
                uiState = uiState,
                events = viewModel.events,
                onSyncNow = viewModel::syncNow,
                onFavoriteClick = { listingId, isFavorite ->
                    viewModel.toggleFavorite(listingId, isFavorite)
                },
                onListingClick = { listing ->
                    navController.navigate(AppRoutes.details(listing.id))
                },
                onCreateListingClick = {
                    navController.navigate(AppRoutes.CREATE_LISTING)
                }
            )
        }

        composable(AppRoutes.CREATE_LISTING) {
            val viewModel: ListingFormViewModel = hiltViewModel()
            val formState by viewModel.uiState.collectAsStateWithLifecycle()

            ListingFormNavigationHandler(
                events = viewModel.events,
                navController = navController
            )

            CreateListingScreen(
                isSaving = formState.isSaving,
                saveErrorResId = formState.errorResId,
                onBackClick = { navController.popBackStack() },
                onSave = { draft -> viewModel.save(draft) }
            )
        }

        composable(
            route = AppRoutes.DETAILS,
            arguments = listOf(
                navArgument(AppRoutes.LISTING_ID_ARG) { type = NavType.LongType }
            )
        ) {
            val detailsViewModel: DetailsViewModel = hiltViewModel()
            val formViewModel: ListingFormViewModel = hiltViewModel()

            val detailsState by detailsViewModel.uiState.collectAsStateWithLifecycle()
            val formState by formViewModel.uiState.collectAsStateWithLifecycle()

            ListingFormNavigationHandler(
                events = formViewModel.events,
                navController = navController
            )

            when (val state = detailsState) {
                DetailsUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                DetailsUiState.NotFound -> {
                    LaunchedEffect(Unit) { navController.popBackStack() }
                }

                is DetailsUiState.Content -> {
                    DetailsScreen(
                        listing = state.listing,
                        isSaving = formState.isSaving,
                        saveErrorResId = formState.errorResId,
                        onBackClick = { navController.popBackStack() },
                        onSave = { draft ->
                            formViewModel.save(draft, listingId = state.listing.id)
                        }
                    )
                }
            }
        }
    }
}