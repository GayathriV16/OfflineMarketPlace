package com.example.offlinemarketplace.domain.repository

interface SyncRepository {
    suspend fun syncPendingOperations()
}