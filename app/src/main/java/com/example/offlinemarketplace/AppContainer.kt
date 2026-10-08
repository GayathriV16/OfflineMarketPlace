package com.example.offlinemarketplace

import android.content.Context
import com.example.offlinemarketplace.data.local.DatabaseProvider
import com.example.offlinemarketplace.data.remote.MockListingApi
import com.example.offlinemarketplace.data.repository.ListingRepository
import com.example.offlinemarketplace.sync.SyncManager

class AppContainer(context: Context) {

    private val database = DatabaseProvider.getDatabase(context)

    private val listingApi = MockListingApi.instance

    val listingRepository = ListingRepository(
        listingDao = database.listingDao(),
        syncOperationDao = database.syncOperationDao(),
        listingApi = listingApi
    )

    val syncManager = SyncManager(
        listingDao = database.listingDao(),
        syncOperationDao = database.syncOperationDao(),
        listingApi = listingApi
    )
}