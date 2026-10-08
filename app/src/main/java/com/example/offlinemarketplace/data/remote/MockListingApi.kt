package com.example.offlinemarketplace.data.remote

import kotlinx.coroutines.delay

class MockListingApi private constructor() : ListingApi {
    companion object {
        val instance: MockListingApi by lazy {
            MockListingApi()
        }
    }

    private val listings = mutableMapOf<Long, ApiListing>()

    init {
        val now = System.currentTimeMillis()

        listings[1] = ApiListing(
            id = 1,
            title = "iPhone 13",
            price = 35000.0,
            category = "Electronics",
            description = "iPhone 13 in good condition.",
            imageUrl = null,
            updatedAt = now
        )

        listings[2] = ApiListing(
            id = 2,
            title = "Office Chair",
            price = 4500.0,
            category = "Furniture",
            description = "Comfortable ergonomic office chair.",
            imageUrl = null,
            updatedAt = now
        )

        listings[3] = ApiListing(
            id = 3,
            title = "Programming Book",
            price = 800.0,
            category = "Books",
            description = "Clean and well-maintained programming book.",
            imageUrl = null,
            updatedAt = now
        )

        listings[4] = ApiListing(
            id = 4,
            title = "Mountain Bicycle",
            price = 12000.0,
            category = "Sports",
            description = "Good condition mountain bicycle.",
            imageUrl = null,
            updatedAt = now
        )

        listings[5] = ApiListing(
            id = 5,
            title = "Bluetooth Speaker",
            price = 2500.0,
            category = "Electronics",
            description = "Portable Bluetooth speaker.",
            imageUrl = null,
            updatedAt = now
        )

        listings[6] = ApiListing(
            id = 6,
            title = "Study Table",
            price = 3500.0,
            category = "Furniture",
            description = "Wooden study table.",
            imageUrl = null,
            updatedAt = now
        )
        for (id in 7L..200L) {
            listings[id] = ApiListing(
                id = id,
                title = "Marketplace Item $id",
                price = 500.0 + (id * 25),
                category = when (id % 4) {
                    0L -> "Electronics"
                    1L -> "Furniture"
                    2L -> "Books"
                    else -> "Sports"
                },
                description = "Sample marketplace item number $id.",
                imageUrl = null,
                updatedAt = now
            )
        }
    }

    override suspend fun getListings(): List<ApiListing> {
        delay(300)
        return listings.values.toList()
    }

    override suspend fun createListing(
        listing: ApiListing
    ): ApiListing {
        delay(300)

        val serverListing = listing.copy(
            updatedAt = System.currentTimeMillis()
        )
        listings[serverListing.id] = serverListing
        return serverListing
    }

    override suspend fun updateListing(
        listing: ApiListing
    ): ApiListing {

        delay(300)

        val existingListing =
            listings[listing.id]

        return if (
            existingListing == null ||
            listing.updatedAt >= existingListing.updatedAt
        ) {

            listings[listing.id] = listing

            listing

        } else {

            existingListing
        }
    }
}