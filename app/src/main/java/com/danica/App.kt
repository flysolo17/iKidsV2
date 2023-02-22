package com.danica

import android.app.Application
import com.danica.ikidsv2.service.auth.AuthServiceImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        AuthServiceImpl(auth, firestore, FirebaseStorage.getInstance())
    }
}