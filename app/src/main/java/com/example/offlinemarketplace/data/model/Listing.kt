package com.example.offlinemarketplace.data.model

data class Listing(
    val id: Long,
    val title: String,
    val price: Double,
    val category: String,
    val description: String,
    val imageUrl: String? = null,
    val isFavorite: Boolean = false
)