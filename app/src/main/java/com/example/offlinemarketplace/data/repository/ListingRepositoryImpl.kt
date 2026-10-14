
package com.example.offlinemarketplace.data.repository

import com.example.offlinemarketplace.data.local.ListingEntity
import com.example.offlinemarketplace.data.local.LocalListingDataSource
import com.example.offlinemarketplace.data.local.SyncOperationEntity
import com.example.offlinemarketplace.data.remote.ApiListing
import com.example.offlinemarketplace.data.remote.ListingApi
import com.example.offlinemarketplace.domain.model.Listing
import com.example.offlinemarketplace.domain.repository.ListingRepository
import com.example.offlinemarketplace.sync.SyncOperationType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ListingRepositoryImpl @Inject constructor(
    private val localDataSource: LocalListingDataSource,
    private val listingApi: ListingApi
) : ListingRepository {

    override val pendingOperationCount: Flow<Int>
        get() = localDataSource.pendingOperationCount

    override fun getListings(): Flow<List<Listing>> {
        return localDataSource.observeListings().map { entities ->
            entities.map { entity -> entity.toDomain() }
        }
    }

    override suspend fun saveListings(listings: List<Listing>) {
        localDataSource.saveListings(
            listings.map { listing -> listing.toEntity() }
        )
    }

    override suspend fun createListingOffline(listing: Listing) {
        val now = System.currentTimeMillis()

        val entity = ListingEntity(
            id = listing.id,
            title = listing.title,
            price = listing.price,
            category = listing.category,
            description = listing.description,
            imageUrl = listing.imageUrl,
            isFavorite = listing.isFavorite,
            updatedAt = now,
            needsSync = true
        )

        val operation = SyncOperationEntity(
            listingId = listing.id,
            operation = SyncOperationType.CREATE,
            createdAt = now
        )

        localDataSource.createListingWithOperation(
            listing = entity,
            operation = operation
        )
    }

    override suspend fun updateFavorite(
        listingId: Long,
        isFavorite: Boolean
    ) {
        localDataSource.updateFavoriteAndQueue(
            listingId = listingId,
            isFavorite = isFavorite,
            updatedAt = System.currentTimeMillis()
        )
    }

    override suspend fun updateListingOffline(
        listingId: Long,
        title: String,
        price: Double,
        category: String,
        description: String,
        imageUri: String?
    ) {
        localDataSource.updateListingAndQueue(
            listingId = listingId,
            title = title,
            price = price,
            category = category,
            description = description,
            imageUri = imageUri,
            updatedAt = System.currentTimeMillis()
        )
    }

    override suspend fun hasListings(): Boolean {
        return localDataSource.hasListings()
    }

    override suspend fun fetchListingsFromServer(): List<Listing> {
        return listingApi.getListings().map { apiListing ->
            apiListing.toDomain()
        }
    }

    private fun Listing.toEntity(): ListingEntity {
        return ListingEntity(
            id = id,
            title = title,
            price = price,
            category = category,
            description = description,
            imageUrl = imageUrl,
            isFavorite = isFavorite,
            updatedAt = System.currentTimeMillis(),
            needsSync = false
        )
    }

    private fun ListingEntity.toDomain(): Listing {
        return Listing(
            id = id,
            title = title,
            price = price,
            category = category,
            description = description,
            imageUrl = imageUrl,
            isFavorite = isFavorite
        )
    }

    private fun ApiListing.toDomain(): Listing {
        return Listing(
            id = id,
            title = title,
            price = price,
            category = category,
            description = description,
            imageUrl = imageUrl,
            isFavorite = isFavorite
        )
    }
}