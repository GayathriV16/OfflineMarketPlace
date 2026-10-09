package com.example.offlinemarketplace.data.remote
data class ApiListing(
    val id: Long,
    val title: String,
    val price: Double,
    val category: String,
    val description: String,
    val imageUrl: String?,
    val updatedAt: Long,
    val isFavorite: Boolean = false
)
