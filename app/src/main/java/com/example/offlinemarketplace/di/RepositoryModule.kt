package com.example.offlinemarketplace.di

import com.example.offlinemarketplace.data.remote.ListingApi
import com.example.offlinemarketplace.data.remote.MockListingApi
import com.example.offlinemarketplace.data.repository.ListingRepositoryImpl
import com.example.offlinemarketplace.data.repository.SyncRepositoryImpl
import com.example.offlinemarketplace.domain.repository.ListingRepository
import com.example.offlinemarketplace.domain.repository.SyncRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindListingRepository(
        implementation: ListingRepositoryImpl
    ): ListingRepository

    @Binds
    @Singleton
    abstract fun bindSyncRepository(
        implementation: SyncRepositoryImpl
    ): SyncRepository

    companion object {
        @Provides
        @Singleton
        fun provideListingApi(): ListingApi {
            return MockListingApi.instance
        }
    }
}