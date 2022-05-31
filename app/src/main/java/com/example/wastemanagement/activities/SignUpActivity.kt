package com.example.wastemanagement.activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.example.wastemanagement.R
import com.example.wastemanagement.databinding.ActivitySignUpBinding
import com.example.wastemanagement.firebase.FirestoreClass
import com.example.wastemanagement.models.Driver
import com.example.wastemanagement.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : BaseActivity() {
    private var binding: ActivitySignUpBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        window.statusBarColor = Color.TRANSPARENT

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.btnUser?.setOnClickListener {
            registerUser()
        }

        binding?.btnDriver?.setOnClickListener {
            registerDriver()
        }

        binding?.btnSignIn?.setOnClickListener {
            startActivity(Intent(this,SignInActivity::class.java))
        }

        setupActionBar()
    }



    private fun setupActionBar() {

        setSupportActionBar(binding?.toolbarUserSignUpActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        binding?.toolbarUserSignUpActivity?.setNavigationOnClickListener {
            onBackPressed() }

    }

    private fun registerUser(){
        val name: String = binding?.etSignUpName?.text.toString().trim { it <= ' ' }
        val email: String = binding?.etSignUpEmail?.text.toString().trim { it <= ' ' }
        val password: String = binding?.etSignUpPassword?.text.toString().trim{ it <= ' '}
        //val number: Int = binding?.etUserNumber?.text.toString().toInt()

        if (validateForm(name, email, password))  {

            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->

                        if (task.isSuccessful) {

                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            val registeredEmail = firebaseUser.email!!
                            val user = User(
                                firebaseUser.uid, name, registeredEmail
                            )
                            FirestoreClass().registerUser(this@SignUpActivity, user)

                        } else {
                            Toast.makeText(
                                this@SignUpActivity,
                                task.exception!!.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
        }
    }


    private fun registerDriver(){
        val name: String = binding?.etSignUpName?.text.toString().trim { it <= ' ' }
        val email: String = binding?.etSignUpEmail?.text.toString().trim { it <= ' ' }
        val password: String = binding?.etSignUpPassword?.text.toString().trim{ it <= ' '}
        //val number: Int = binding?.etUserNumber?.text.toString().toInt()

        if (validateForm(name, email, password))  {

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
                            FirestoreClass().registerDriver(this@SignUpActivity, driver)


                            FirebaseAuth.getInstance().signOut()

                            finish()
                        } else {
                            Toast.makeText(
                                this@SignUpActivity,
                                task.exception!!.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
        }
    }



    private fun validateForm(name: String, email: String, password: String): Boolean {
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
           // TextUtils.isEmpty(number.toString()) -> {
               // showErrorSnackBar("Please enter number.")
                //false
            //}
            else -> {
                true
            }
        }
    }

    fun userRegisteredSuccess() {

        Toast.makeText(
            this@SignUpActivity,
            "You have successfully registered.",
            Toast.LENGTH_SHORT
        ).show()

        hideProgressDialog()

        FirebaseAuth.getInstance().signOut()
        finish()
    }

    fun DriverRegisteredSuccess() {
        Toast.makeText(
            this@SignUpActivity,
            "You have successfully registered.",
            Toast.LENGTH_SHORT
        ).show()

        hideProgressDialog()

        FirebaseAuth.getInstance().signOut()
        finish()
    }


}