package com.muthuvi.microbiz360.data.model

data class SaleItem(
    val id: String = "",
    val saleId: String = "",
    val productId: String = "",
    val productName: String = "",
    val quantity: Int = 0,
    val unitPrice: Double = 0.0,
    val lineTotal: Double = 0.0
)