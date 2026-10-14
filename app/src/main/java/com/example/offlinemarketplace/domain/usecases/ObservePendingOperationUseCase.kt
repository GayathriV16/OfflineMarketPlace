package com.example.offlinemarketplace.domain.usecases

import com.example.offlinemarketplace.data.repository.ListingRepositoryImpl
import com.example.offlinemarketplace.domain.repository.ListingRepository
import kotlinx.coroutines.flow.Flow

class ObservePendingOperationsUseCase(
    private val repository: ListingRepository
) {
    operator fun invoke(): Flow<Int> {
        return repository.pendingOperationCount
    }
}
