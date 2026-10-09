package com.example.offlinemarketplace.di

import com.example.offlinemarketplace.data.local.ListingDao
import com.example.offlinemarketplace.data.local.SyncOperationDao
import com.example.offlinemarketplace.data.remote.ListingApi
import com.example.offlinemarketplace.data.remote.MockListingApi
import com.example.offlinemarketplace.data.repository.ListingRepository
import com.example.offlinemarketplace.sync.SyncManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideListingApi(): ListingApi {
        return MockListingApi.instance
    }

    @Provides
    @Singleton
    fun provideListingRepository(
        listingDao: ListingDao,
        syncOperationDao: SyncOperationDao,
        listingApi: ListingApi
    ): ListingRepository {
        return ListingRepository(
            listingDao = listingDao,
            syncOperationDao = syncOperationDao,
            listingApi = listingApi
        )
    }

    @Provides
    @Singleton
    fun provideSyncManager(
        listingDao: ListingDao,
        syncOperationDao: SyncOperationDao,
        listingApi: ListingApi
    ): SyncManager {
        return SyncManager(
            listingDao = listingDao,
            syncOperationDao = syncOperationDao,
            listingApi = listingApi
        )
    }

}
