package com.example.offlinemarketplace.domain.usecases

import com.example.offlinemarketplace.data.model.Listing
import com.example.offlinemarketplace.data.repository.ListingRepository
import kotlinx.coroutines.flow.Flow

class ObserveListingsUseCase(
    private val repository: ListingRepository
) {
    operator fun invoke(): Flow<List<Listing>> {
        return repository.getListings()
    }
}
