package com.example.OPCS_PART3


data class Income(
    val id: Long = 0,
    val amount: Double,
    val source: String,
    val note: String = "",
    val date: String,
    val userId: Long
)