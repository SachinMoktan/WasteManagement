package com.example.wastemanagement.room

import android.app.Application

class ComplaintApp:Application() {
    val db by lazy {
        ComplaintDatabase.getInstance(this)
    }
}