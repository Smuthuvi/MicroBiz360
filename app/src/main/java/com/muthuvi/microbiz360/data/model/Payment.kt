package com.muthuvi.microbiz360.data.model

data class Payment(
    val id: String = "",
    val saleId: String = "",
    val method: String = "",
    val amount: Double = 0.0,
    val status: String = "",
    val timestamp: Long = 0L
)