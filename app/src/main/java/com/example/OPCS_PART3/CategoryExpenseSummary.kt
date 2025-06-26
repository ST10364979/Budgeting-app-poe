package com.example.OPCS_PART3

// CategoryExpenseSummary.kt

data class CategoryExpenseSummary(
    val categoryId: Long,
    val categoryName: String,
    val categoryColor: String,
    val budget: Double,
    val totalSpent: Double
)