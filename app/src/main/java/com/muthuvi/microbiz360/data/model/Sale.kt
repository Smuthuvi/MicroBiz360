package com.muthuvi.microbiz360.data.model

data class Sale(
    val id: String = "",
    val totalAmount: Double = 0.0,
    val timestamp: Long = 0L,
    val status: String = ""
)