package com.example.offlinemarketplace.di

import com.example.offlinemarketplace.domain.repository.SyncRequestScheduler
import com.example.offlinemarketplace.sync.WorkManagerSyncScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SyncModule {

    @Binds
    @Singleton
    abstract fun bindSyncRequestScheduler(
        implementation: WorkManagerSyncScheduler
    ): SyncRequestScheduler
}