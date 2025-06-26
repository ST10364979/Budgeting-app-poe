package com.example.OPCS_PART3

// Category.kt

data class Category(
    val id: Long = 0,
    val name: String,
    val color: String,
    val budget: Double,
    val userId: Long
)