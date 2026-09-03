package com.muthuvi.microbiz360.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.muthuvi.microbiz360.data.firebase.FirebaseManager
import com.muthuvi.microbiz360.data.model.Sale
import java.util.Calendar

object SalesRepository {

    private fun userReference() =
        FirebaseManager.auth.currentUser?.uid?.let { uid ->
            FirebaseManager.database
                .reference
                .child("users")
                .child(uid)
        }

    fun completeSale(
        productId: String,
        productName: String,
        unitPrice: Double,
        currentStock: Int,
        quantity: Int,
        totalAmount: Double,
        paymentMethod: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        val userRef = userReference()

        if (userRef == null) {
            onFailure(Exception("No authenticated Firebase user."))
            return
        }

        val saleId = userRef.child("sales").push().key

        if (saleId == null) {
            onFailure(Exception("Unable to generate sale ID."))
            return
        }

        val paymentId = userRef.child("payments").push().key
        val saleItemId = userRef.child("saleItems").push().key

        if (paymentId == null || saleItemId == null) {
            onFailure(Exception("Unable to generate transaction IDs."))
            return
        }

        val timestamp = System.currentTimeMillis()

        val newStock = (currentStock - quantity).coerceAtLeast(0)

        val saleValues = mapOf(
            "id" to saleId,
            "totalAmount" to totalAmount,
            "timestamp" to timestamp,
            "status" to "COMPLETED"
        )

        val saleItemValues = mapOf(
            "id" to saleItemId,
            "saleId" to saleId,
            "productId" to productId,
            "productName" to productName,
            "quantity" to quantity,
            "unitPrice" to unitPrice,
            "lineTotal" to totalAmount
        )

        val paymentValues = mapOf(
            "id" to paymentId,
            "saleId" to saleId,
            "method" to paymentMethod,
            "amount" to totalAmount,
            "status" to "PAID",
            "timestamp" to timestamp
        )

        val updates = hashMapOf<String, Any?>(
            "sales/$saleId" to saleValues,
            "saleItems/$saleId/$saleItemId" to saleItemValues,
            "payments/$paymentId" to paymentValues,
            "products/$productId/stock" to newStock
        )

        userRef.updateChildren(updates)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun observeTodaySummary(
        onSummaryChanged: (Int, Double) -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        val userRef = userReference()

        if (userRef == null) {
            onFailure(Exception("No authenticated Firebase user."))
            return
        }

        userRef.child("sales")
            .addValueEventListener(
                object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {

                        val calendar = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }

                        val startOfToday = calendar.timeInMillis

                        val todaySales = snapshot.children
                            .mapNotNull {
                                it.getValue(Sale::class.java)
                            }
                            .filter {
                                it.status == "COMPLETED" &&
                                        it.timestamp >= startOfToday
                            }

                        val count = todaySales.size

                        val revenue = todaySales.sumOf {
                            it.totalAmount
                        }

                        onSummaryChanged(count, revenue)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        onFailure(error.toException())
                    }
                }
            )
    }
}