package com.example.rmas18577

import android.app.Application
import com.google.firebase.FirebaseApp

class RMAS18577 : Application() {

        override fun onCreate() {
            super.onCreate()
            FirebaseApp.initializeApp(this)
        }
    }
