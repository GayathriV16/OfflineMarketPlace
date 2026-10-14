
package com.example.offlinemarketplace.ui.browse

import com.example.offlinemarketplace.domain.model.Listing
import com.example.offlinemarketplace.viewmodel.SyncStatus

data class BrowseUiState(
    val listings: List<Listing> = emptyList(),
    val pendingOperationCount: Int = 0,
    val isLoading: Boolean = true,
    val syncStatus: SyncStatus = SyncStatus.NOT_SYNCED
)
