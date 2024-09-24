package com.example.rmas18577


data class Filters(
    val user: String,
    val ratingFrom: Int?,
    val ratingTo: Int?,
    val startDate: Long?,
    val endDate: Long?,
    val radius: Float?
)