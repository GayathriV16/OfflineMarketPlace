package com.example.offlinemarketplace.sync

import com.example.offlinemarketplace.data.remote.ApiListing
import com.example.offlinemarketplace.data.remote.MockListingApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class SyncManagerTest {

    @Test
    fun newer_update_wins() = runBlocking {

        val api = MockListingApi.instance

        val updatedListing = ApiListing(
            id = 1,
            title = "Updated iPhone",
            price = 36000.0,
            category = "Electronics",
            description = "Updated description",
            imageUrl = null,
            updatedAt = System.currentTimeMillis() + 1000
        )

        val result =
            api.updateListing(updatedListing)

        assertEquals(
            "Updated iPhone",
            result.title
        )

        assertEquals(
            36000.0,
            result.price,
            0.01
        )
    }

    @Test
    fun older_update_does_not_overwrite_newer_server_value() = runBlocking {

        val api = MockListingApi.instance

        val newerListing = ApiListing(
            id = 2,
            title = "New Office Chair",
            price = 6000.0,
            category = "Furniture",
            description = "Newer server version",
            imageUrl = null,
            updatedAt = System.currentTimeMillis() + 2000
        )

        val olderListing = ApiListing(
            id = 2,
            title = "Old Office Chair",
            price = 4000.0,
            category = "Furniture",
            description = "Older client version",
            imageUrl = null,
            updatedAt = System.currentTimeMillis()
        )

        api.updateListing(newerListing)

        val result =
            api.updateListing(olderListing)

        assertEquals(
            "New Office Chair",
            result.title
        )

        assertEquals(
            6000.0,
            result.price,
            0.01
        )
    }

    @Test
    fun create_listing_is_saved_on_server() = runBlocking {

        val api = MockListingApi.instance

        val newListing = ApiListing(
            id = 9999,
            title = "Test Bicycle",
            price = 15000.0,
            category = "Sports",
            description = "Test bicycle for unit testing",
            imageUrl = null,
            updatedAt = System.currentTimeMillis()
        )

        val result =
            api.createListing(newListing)

        assertEquals(
            9999,
            result.id
        )

        assertEquals(
            "Test Bicycle",
            result.title
        )

        assertEquals(
            15000.0,
            result.price,
            0.01
        )
    }
}