package com.example.offlinemarketplace.data.remote

interface ListingApi {

    suspend fun getListings(): List<ApiListing>

    suspend fun createListing(
        listing: ApiListing
    ): ApiListing

    suspend fun updateListing(
        listing: ApiListing
    ): ApiListing
}