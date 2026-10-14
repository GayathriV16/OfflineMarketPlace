package com.example.offlinemarketplace.di

import com.example.offlinemarketplace.domain.repository.ListingRepository
import com.example.offlinemarketplace.domain.repository.SyncRepository
import com.example.offlinemarketplace.domain.repository.SyncRequestScheduler
import com.example.offlinemarketplace.domain.usecases.LoadInitialListingsUseCase
import com.example.offlinemarketplace.domain.usecases.ObserveListingsUseCase
import com.example.offlinemarketplace.domain.usecases.ObservePendingOperationsUseCase
import com.example.offlinemarketplace.domain.usecases.SyncListingsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideObserveListingsUseCase(
        repository: ListingRepository
    ): ObserveListingsUseCase {
        return ObserveListingsUseCase(repository)
    }

    @Provides
    fun provideObservePendingOperationsUseCase(
        repository: ListingRepository
    ): ObservePendingOperationsUseCase {
        return ObservePendingOperationsUseCase(repository)
    }

    @Provides
    fun provideLoadInitialListingsUseCase(
        repository: ListingRepository,
        syncRequestScheduler: SyncRequestScheduler
    ): LoadInitialListingsUseCase {
        return LoadInitialListingsUseCase(repository, syncRequestScheduler)
    }

    @Provides
    fun provideSyncListingsUseCase(
        syncRepository: SyncRepository
    ): SyncListingsUseCase {
        return SyncListingsUseCase(syncRepository)
    }
}