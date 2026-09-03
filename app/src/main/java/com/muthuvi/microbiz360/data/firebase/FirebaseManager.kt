package com.muthuvi.microbiz360.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object FirebaseManager {

    val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    val database by lazy {
        FirebaseDatabase.getInstance()
    }
}