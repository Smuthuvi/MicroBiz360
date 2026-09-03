package com.muthuvi.microbiz360.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.muthuvi.microbiz360.data.firebase.FirebaseManager
import com.muthuvi.microbiz360.data.model.Product

object ProductRepository {

    private fun productsReference() =
        FirebaseManager.auth.currentUser?.uid?.let { uid ->
            FirebaseManager.database
                .reference
                .child("users")
                .child(uid)
                .child("products")
        }

    fun seedProductsIfEmpty(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        val reference = productsReference()

        if (reference == null) {
            onFailure(Exception("No authenticated Firebase user."))
            return
        }

        reference.get()
            .addOnSuccessListener { snapshot ->

                if (snapshot.exists()) {
                    onSuccess()
                    return@addOnSuccessListener
                }

                val products = listOf(
                    Product(
                        id = "sugar_1kg",
                        name = "Sugar 1 Kg",
                        category = "Groceries",
                        price = 180.00,
                        stock = 24
                    ),
                    Product(
                        id = "milk_500ml",
                        name = "Milk 500 ml",
                        category = "Dairy",
                        price = 65.00,
                        stock = 18
                    ),
                    Product(
                        id = "bread_400g",
                        name = "Bread 400 g",
                        category = "Bakery",
                        price = 70.00,
                        stock = 12
                    ),
                    Product(
                        id = "cooking_oil_1l",
                        name = "Cooking Oil 1 L",
                        category = "Groceries",
                        price = 320.00,
                        stock = 9
                    )
                )

                val updates = products.associate { product ->
                    product.id to product
                }

                reference.setValue(updates)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener { exception ->
                        onFailure(exception)
                    }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun observeProducts(
        onProductsChanged: (List<Product>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        val reference = productsReference()

        if (reference == null) {
            onFailure(Exception("No authenticated Firebase user."))
            return
        }

        reference.addValueEventListener(
            object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    val products = snapshot.children.mapNotNull { child ->
                        child.getValue(Product::class.java)
                    }

                    onProductsChanged(products)
                }

                override fun onCancelled(error: DatabaseError) {
                    onFailure(error.toException())
                }
            }
        )
    }
}