package com.example.offlinemarketplace.domain.usecases

import com.example.offlinemarketplace.domain.repository.SyncRepository
import javax.inject.Inject

class SyncListingsUseCase @Inject constructor(
    private val syncRepository: SyncRepository
) {
    suspend operator fun invoke() {
        syncRepository.syncPendingOperations()
    }
}