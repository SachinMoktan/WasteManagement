package com.example.wastemanagement.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.wastemanagement.R
import com.example.wastemanagement.databinding.ActivityAdminSignUpBinding
import com.example.wastemanagement.databinding.ActivityUserSignUpBinding
import com.example.wastemanagement.firebase.FirestoreClass
import com.example.wastemanagement.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AdminSignUpActivity : BaseActivity() {
    private var binding: ActivityAdminSignUpBinding?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminSignUpBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupActionBar()
    }


    private fun setupActionBar() {

        setSupportActionBar(binding?.toolbarAdminSignUpActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        binding?.toolbarAdminSignUpActivity?.setNavigationOnClickListener {
            onBackPressed() }

        binding?.btnSignUp?.setOnClickListener {
            registerAdmin()
        }
    }


    private fun registerAdmin(){
        val name: String = binding?.etAdminName?.text.toString().trim { it <= ' ' }
        val email: String = binding?.etAdminEmail?.text.toString().trim { it <= ' ' }
        val password: String = binding?.etAdminPassword?.text.toString().trim{ it <= ' '}
        val number: Int = binding?.etAdminNumber?.text.toString().toInt()

        if (validateForm(name, email, password, number))  {

            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->

                        if (task.isSuccessful) {

                            val firebaseAdmin: FirebaseUser = task.result!!.user!!
                            val registeredEmail = firebaseAdmin.email!!
                            val user = User(
                                firebaseAdmin.uid, name, registeredEmail
                            )
                            FirestoreClass().registerUser(this, user)


                            FirebaseAuth.getInstance().signOut()

                            finish()
                        } else {
                            Toast.makeText(
                                this@AdminSignUpActivity,
                                task.exception!!.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
        }
    }


    private fun validateForm(name: String, email: String, password: String, number: Int): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please enter name.")
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter email.")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter password.")
                false
            }
            TextUtils.isEmpty(number.toString()) -> {
                showErrorSnackBar("Please enter number.")
                false
            }
            else -> {
                true
            }
        }
    }

    fun adminRegisteredSuccess() {
        Toast.makeText(
            this@AdminSignUpActivity,
            "You have successfully registered.",
            Toast.LENGTH_SHORT
        ).show()

        hideProgressDialog()

        FirebaseAuth.getInstance().signOut()
        finish()
    }

}