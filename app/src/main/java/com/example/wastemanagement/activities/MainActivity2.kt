package com.example.wastemanagement.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.example.wastemanagement.R
import com.example.wastemanagement.databinding.*
import com.example.wastemanagement.firebase.FirestoreClass
import com.example.wastemanagement.models.Driver
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity2 : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var bindingMain: ActivityMain2Binding?= null
    private  var bindingAppBar: AppBarMain2Binding?= null
    private var bindingNavHeader: NavHeaderMain2Binding?= null
    private var bindingContent: MainContent2Binding?= null


    companion object {
        const val MY_PROFILE_REQUEST_CODE: Int = 11
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingMain = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(bindingMain?.root)
        bindingAppBar = bindingMain?.appBarMain2

        val view = bindingMain!!.navView2.getHeaderView(0)
        bindingNavHeader = NavHeaderMain2Binding.bind(view)

        bindingMain?.navView2?.setNavigationItemSelectedListener(this)

        bindingContent = bindingAppBar?.mainContent2

        bindingContent?.nearByMe2?.setOnClickListener {
            val intent = Intent(this@MainActivity2, DriverGarbageActivity::class.java)
            startActivity(intent)
        }
        bindingContent?.complaints2?.setOnClickListener {
            val intent = Intent(this@MainActivity2, ComplaintActivity::class.java)
            startActivity(intent)
        }

        setupActionBar()

        FirestoreClass().loadDriverData(this)

    }

    private fun setupActionBar() {

        setSupportActionBar(bindingAppBar?.toolbarMainActivity2)
        bindingAppBar?.toolbarMainActivity2?.setNavigationIcon(R.drawable.ic_action_navigation_menu)


        bindingAppBar?.toolbarMainActivity2?.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    private fun toggleDrawer() {

        if (bindingMain?.drawerLayout2?.isDrawerOpen(GravityCompat.START) == true) {
            bindingMain?.drawerLayout2?.closeDrawer(GravityCompat.START)
        } else {
            bindingMain?.drawerLayout2?.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if (bindingMain?.drawerLayout2?.isDrawerOpen(GravityCompat.START) == true) {
            bindingMain?.drawerLayout2?.closeDrawer(GravityCompat.START)
        } else {
            doubleBackToExit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_profile2 -> {

                startActivityForResult(
                    Intent(this@MainActivity2, DriverProfileActivity::class.java),
                    MY_PROFILE_REQUEST_CODE)
            }

            R.id.nav_sign_out2 -> {
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this, SignInActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        bindingMain?.drawerLayout2?.closeDrawer(GravityCompat.START)
        return true
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK
            && requestCode == MY_PROFILE_REQUEST_CODE
        ) {
            FirestoreClass().loadDriverData(this@MainActivity2)
        } else {
            Log.e("Cancelled", "Cancelled")
        }
    }



    fun updateNavigationDriverDetails(driver: Driver) {

        Glide
            .with(this@MainActivity2)
            .load(driver.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(bindingNavHeader?.navUserImage2!!)


        bindingNavHeader?.tvUsername2?.text = driver.name
    }
}