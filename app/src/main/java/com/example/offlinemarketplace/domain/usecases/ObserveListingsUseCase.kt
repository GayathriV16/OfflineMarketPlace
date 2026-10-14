
package com.example.offlinemarketplace.domain.usecases

import com.example.offlinemarketplace.domain.model.Listing
import com.example.offlinemarketplace.domain.repository.ListingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveListingsUseCase @Inject constructor(
    private val repository: ListingRepository
) {
    operator fun invoke(): Flow<List<Listing>> {
        return repository.getListings()
    }
}
