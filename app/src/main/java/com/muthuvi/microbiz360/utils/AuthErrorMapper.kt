package com.muthuvi.microbiz360.utils

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

fun getFriendlyAuthError(exception: Exception): String {

    return when (exception) {

        is FirebaseNetworkException ->
            "No internet connection. Please check your connection and try again."

        is FirebaseAuthInvalidCredentialsException ->
            "Incorrect email or password. Please try again."

        is FirebaseAuthInvalidUserException ->
            "No account was found for this email address."

        is FirebaseAuthUserCollisionException ->
            "An account already exists with this email address."

        is FirebaseAuthWeakPasswordException ->
            "Please choose a stronger password."

        else ->
            "Authentication failed. Please try again."
    }
}