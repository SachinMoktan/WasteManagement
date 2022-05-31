package com.example.wastemanagement.activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.example.wastemanagement.R
import com.example.wastemanagement.databinding.ActivitySignInBinding
import com.example.wastemanagement.firebase.FirestoreClass
import com.example.wastemanagement.models.Driver
import com.example.wastemanagement.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class SignInActivity : BaseActivity() {
    private var binding: ActivitySignInBinding? = null
    private lateinit var auth: FirebaseAuth
    private var mUserList = ArrayList<String>()
    private var mDriverList = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        window.statusBarColor = Color.TRANSPARENT

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        auth = FirebaseAuth.getInstance()

        binding?.btnSignIn?.setOnClickListener {
            signInRegisteredUser()
        }
        binding?.go?.setOnClickListener {
            signInRegisteredUser()
        }

        binding?.btnSignUp?.setOnClickListener {
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }

        FirestoreClass().getAllUser(this)
        FirestoreClass().getAllDriver(this)


    }



    private fun signInRegisteredUser() {
        val email: String = binding?.etEmail?.text.toString().trim { it <= ' ' }
        val password: String = binding?.etPassword?.text.toString().trim{ it <= ' '}

        if (validateForm(email, password)) {
            showProgressDialog(resources.getString(R.string.please_wait))

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {

                        Toast.makeText(
                            this@SignInActivity,
                            "You have successfully signed in.",
                            Toast.LENGTH_LONG
                        ).show()
                        val currentUserid = FirestoreClass().getCurrentUserID()
                        for ( i in mUserList) {
                            if (i == currentUserid) {
                                startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                                finish()
                            }
                        }

                            for (i in mDriverList) {
                                if (i == currentUserid)
                                    startActivity(
                                        Intent(
                                            this@SignInActivity,
                                            MainActivity2::class.java
                                        )
                                    )
                                finish()

                            }


                    } else {
                        Toast.makeText(
                            this@SignInActivity,
                            task.exception!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        return if (TextUtils.isEmpty(email)) {
            showErrorSnackBar("Please enter email.")
            false
        } else if (TextUtils.isEmpty(password)) {
            showErrorSnackBar("Please enter password.")
            false
        } else {
            true
        }
    }


    fun signInSuccess(user: User) {

        hideProgressDialog()

        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
        finish()
    }

    fun signInSuccess(driver: Driver) {

        hideProgressDialog()

        startActivity(Intent(this@SignInActivity, MainActivity2::class.java))
        finish()
    }

    fun onGetUserList(userList:ArrayList<String>){
        mUserList = userList
    }

    fun onGetDriverList(driverList:ArrayList<String>){
        mDriverList = driverList
    }

}