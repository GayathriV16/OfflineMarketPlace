package com.example.offlinemarketplace.sync

import com.example.offlinemarketplace.data.remote.ApiListing
import com.example.offlinemarketplace.data.remote.MockListingApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class MockListingApiTest {

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
    fun update_listing_returns_updated_values() = runBlocking {
        val api = MockListingApi.instance

        val listing = ApiListing(
            id = 9103L,
            title = "Updated Laptop",
            price = 45000.0,
            category = "Electronics",
            description = "Updated laptop description",
            imageUrl = null,
            updatedAt = System.currentTimeMillis() + 5000
        )

        val result = api.updateListing(listing)

        assertEquals(listing.id, result.id)
        assertEquals("Updated Laptop", result.title)
        assertEquals(45000.0, result.price, 0.01)
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