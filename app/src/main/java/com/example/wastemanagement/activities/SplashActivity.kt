package com.example.wastemanagement.activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager

import com.example.wastemanagement.databinding.ActivitySplashBinding
import com.example.wastemanagement.firebase.FirestoreClass

class SplashActivity : AppCompatActivity() {
    private var binding: ActivitySplashBinding ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        window.statusBarColor = Color.TRANSPARENT

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.hide()

        Handler().postDelayed({
            val currentUserID = FirestoreClass().getCurrentUserID()

            if (currentUserID.isNotEmpty()) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, SignInActivity::class.java))
            }
            finish()
        },2500)

        Handler().postDelayed({
            val currentDriverID = FirestoreClass().getCurrentDriverID()

            if (currentDriverID.isNotEmpty()) {
                startActivity(Intent(this@SplashActivity, MainActivity2::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, SignInActivity::class.java))
            }
            finish()
        },2500)
    }
}
