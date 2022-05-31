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
import com.example.wastemanagement.models.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var bindingMain: ActivityMainBinding ? = null
    private  var bindingAppBar: AppBarMainBinding?= null
    private var bindingNavHeader: NavHeaderMainBinding?= null
    private var bindingContent: MainContentBinding?= null


    companion object {
        const val MY_PROFILE_REQUEST_CODE: Int = 11
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingMain = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingMain?.root)
        bindingAppBar = bindingMain?.appBarMain

        val view = bindingMain!!.navView.getHeaderView(0)
        bindingNavHeader = NavHeaderMainBinding.bind(view)

        bindingMain?.navView?.setNavigationItemSelectedListener(this)

        bindingContent = bindingAppBar?.mainContent

        bindingContent?.nearByMe?.setOnClickListener {
            val intent = Intent(this@MainActivity, UserGarbageActivity::class.java)
            startActivity(intent)
        }

        bindingContent?.complaints?.setOnClickListener {
            val intent = Intent(this@MainActivity, ComplaintActivity::class.java)
            startActivity(intent)
        }

        setupActionBar()

        FirestoreClass().loadUserData(this)

    }

    private fun setupActionBar() {

        setSupportActionBar(bindingAppBar?.toolbarMainActivity)
        bindingAppBar?.toolbarMainActivity?.setNavigationIcon(R.drawable.ic_action_navigation_menu)


        bindingAppBar?.toolbarMainActivity?.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    private fun toggleDrawer() {

        if (bindingMain?.drawerLayout?.isDrawerOpen(GravityCompat.START) == true) {
            bindingMain?.drawerLayout?.closeDrawer(GravityCompat.START)
        } else {
            bindingMain?.drawerLayout?.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if (bindingMain?.drawerLayout?.isDrawerOpen(GravityCompat.START) == true) {
            bindingMain?.drawerLayout?.closeDrawer(GravityCompat.START)
        } else {
            doubleBackToExit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_profile -> {

                startActivityForResult(Intent(this@MainActivity, UserProfileActivity::class.java),
                    MY_PROFILE_REQUEST_CODE)
            }

            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this, SignInActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        bindingMain?.drawerLayout?.closeDrawer(GravityCompat.START)
        return true
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK
            && requestCode == MY_PROFILE_REQUEST_CODE
        ) {
            FirestoreClass().loadUserData(this@MainActivity)
        } else {
            Log.e("Cancelled", "Cancelled")
        }
    }


    fun updateNavigationUserDetails(user: User) {

            Glide
                .with(this@MainActivity)
                .load(user.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(bindingNavHeader?.navUserImage!!)


            bindingNavHeader?.tvUsername?.text = user.name
    }


}