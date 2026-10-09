package com.example.offlinemarketplace.di

import android.content.Context
import com.example.offlinemarketplace.data.repository.ListingRepository
import com.example.offlinemarketplace.domain.usecases.CreateListingUseCase
import com.example.offlinemarketplace.domain.usecases.LoadInitialListingsUseCase
import com.example.offlinemarketplace.domain.usecases.ObserveListingsUseCase
import com.example.offlinemarketplace.domain.usecases.ObservePendingOperationsUseCase
import com.example.offlinemarketplace.domain.usecases.SyncListingsUseCase
import com.example.offlinemarketplace.domain.usecases.ToggleFavoriteUseCase
import com.example.offlinemarketplace.domain.usecases.UpdateListingUseCase
import com.example.offlinemarketplace.sync.SyncManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideObserveListingsUseCase(
        repository: ListingRepository
    ) = ObserveListingsUseCase(repository)

    @Provides
    fun provideObservePendingOperationsUseCase(
        repository: ListingRepository
    ) = ObservePendingOperationsUseCase(repository)

    @Provides
    fun provideToggleFavoriteUseCase(
        repository: ListingRepository,
        @ApplicationContext context: Context
    ) = ToggleFavoriteUseCase(repository, context)

    @Provides
    fun provideCreateListingUseCase(
        repository: ListingRepository,
        @ApplicationContext context: Context
    ) = CreateListingUseCase(repository, context)

    @Provides
    fun provideUpdateListingUseCase(
        repository: ListingRepository,
        @ApplicationContext context: Context
    ) = UpdateListingUseCase(repository, context)

    @Provides
    fun provideLoadInitialListingsUseCase(
        repository: ListingRepository,
        @ApplicationContext context: Context
    ) = LoadInitialListingsUseCase(repository, context)

    @Provides
    fun provideSyncListingsUseCase(
        syncManager: SyncManager
    ) = SyncListingsUseCase(syncManager)
}
