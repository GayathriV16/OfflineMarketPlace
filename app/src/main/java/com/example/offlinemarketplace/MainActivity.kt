package com.example.offlinemarketplace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.offlinemarketplace.navigation.AppNavigation
import com.example.offlinemarketplace.ui.theme.OfflineMarketPlaceTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            OfflineMarketPlaceTheme {
                AppNavigation()
            }
        }
    }
}