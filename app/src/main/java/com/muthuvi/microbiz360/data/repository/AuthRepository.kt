package com.muthuvi.microbiz360.data.repository

import com.muthuvi.microbiz360.data.firebase.FirebaseManager

object AuthRepository {

    fun signIn(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        FirebaseManager.auth
            .signInWithEmailAndPassword(
                email.trim(),
                password
            )
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun register(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        FirebaseManager.auth
            .createUserWithEmailAndPassword(
                email.trim(),
                password
            )
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
    fun signOut() {
        FirebaseManager.auth.signOut()
    }
}