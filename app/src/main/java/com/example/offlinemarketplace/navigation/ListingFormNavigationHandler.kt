package com.example.offlinemarketplace.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.offlinemarketplace.ui.util.ObserveAsEvents
import com.example.offlinemarketplace.viewmodel.ListingFormEvent
import kotlinx.coroutines.flow.Flow

@Composable
fun ListingFormNavigationHandler(
    events: Flow<ListingFormEvent>,
    navController: NavHostController
) {
    ObserveAsEvents(events) { event ->
        when (event) {
            ListingFormEvent.Saved -> navController.popBackStack()
        }
    }
}