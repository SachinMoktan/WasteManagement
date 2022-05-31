package com.example.wastemanagement.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.wastemanagement.R
import com.example.wastemanagement.databinding.ActivityDriverSignUpBinding
import com.example.wastemanagement.databinding.ActivityUserSignUpBinding
import com.example.wastemanagement.firebase.FirestoreClass
import com.example.wastemanagement.models.Driver
import com.example.wastemanagement.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class DriverSignUpActivity : BaseActivity() {
    private var binding: ActivityDriverSignUpBinding?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverSignUpBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupActionBar()
    }


    private fun setupActionBar() {

        setSupportActionBar(binding?.toolbarDriverSignUpActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        binding?.toolbarDriverSignUpActivity?.setNavigationOnClickListener {
            onBackPressed() }

        binding?.btnSignUp?.setOnClickListener {
            registerDriver()
        }
    }


    private fun registerDriver(){
        val name: String = binding?.etDriverName?.text.toString().trim { it <= ' ' }
        val email: String = binding?.etDriverEmail?.text.toString().trim { it <= ' ' }
        val password: String = binding?.etDriverPassword?.text.toString().trim{ it <= ' '}
        val number: Int = binding?.etDriverNumber?.text.toString().toInt()

        if (validateForm(name, email, password, number))  {

            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->

                        if (task.isSuccessful) {

                            val firebaseDriver: FirebaseUser = task.result!!.user!!
                            val registeredEmail = firebaseDriver.email!!
                            val driver = Driver(
                                firebaseDriver.uid, name, registeredEmail
                            )
                            FirestoreClass().registerDriver(this@DriverSignUpActivity, driver)


                            FirebaseAuth.getInstance().signOut()

                            finish()
                        } else {
                            Toast.makeText(
                                this@DriverSignUpActivity,
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

    fun DriverRegisteredSuccess() {
        Toast.makeText(
            this@DriverSignUpActivity,
            "You have successfully registered.",
            Toast.LENGTH_SHORT
        ).show()

        hideProgressDialog()

        FirebaseAuth.getInstance().signOut()
        finish()
    }

}