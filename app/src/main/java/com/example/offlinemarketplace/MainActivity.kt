package com.example.offlinemarketplace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.offlinemarketplace.navigation.AppNavigation
import com.example.offlinemarketplace.ui.theme.OfflineMarketPlaceTheme
import com.example.offlinemarketplace.viewmodel.BrowseViewModel
import com.example.offlinemarketplace.viewmodel.ListingFormViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val browseViewModel: BrowseViewModel by viewModels()
    private val listingFormViewModel: ListingFormViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            OfflineMarketPlaceTheme {
                AppNavigation(
                    browseViewModel = browseViewModel,
                    listingFormViewModel = listingFormViewModel
                )
            }
        }
    }
}
