package com.example.wastemanagement.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.wastemanagement.R
import com.example.wastemanagement.databinding.ActivityDriverProfileBinding
import com.example.wastemanagement.databinding.ActivityMyProfileBinding
import com.example.wastemanagement.firebase.FirestoreClass
import com.example.wastemanagement.models.Driver
import com.example.wastemanagement.models.User
import com.example.wastemanagement.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class DriverProfileActivity : BaseActivity() {
    private var binding: ActivityDriverProfileBinding? = null

    private var mSelectedImageFileUri: Uri? = null
    private lateinit var mDriverDetails: Driver
    private var mProfileImageURL: String = ""

    companion object {
        private const val READ_STORAGE_PERMISSION_CODE = 1

        private const val PICK_IMAGE_REQUEST_CODE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()

        FirestoreClass().loadDriverData(this@DriverProfileActivity)

        binding?.ivProfileDriverImage?.setOnClickListener {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                showImageChooser()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        binding?.btnUpdate?.setOnClickListener {

            if (mSelectedImageFileUri != null) {

                uploadDriverImage()
            } else {

                showProgressDialog(resources.getString(R.string.please_wait))

                updateDriverProfileData()
            }
        }
    }

    private fun setupActionBar() {

        setSupportActionBar(binding?.toolbarDriverProfileActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.my_profile)
        }

        binding?.toolbarDriverProfileActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }



    fun setDriverDataInUI(driver: Driver) {
        mDriverDetails = driver
        Glide
            .with(this@DriverProfileActivity)
            .load(driver.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding?.ivProfileDriverImage!!)

        binding?.etDriverName?.setText(driver.name)
        binding?.etDriverEmail?.setText(driver.email)
        if (driver.mobile != 0L) {
            binding?.etDriverMobile?.setText(driver.mobile.toString())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_STORAGE_PERMISSION_CODE) {

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                showImageChooser()

            } else {
                Toast.makeText(
                    this,
                    "Oops, you just denied the permission for storage. You can also allow it from settings.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showImageChooser() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == PICK_IMAGE_REQUEST_CODE
            && data!!.data != null
        ) {
            mSelectedImageFileUri = data.data

            try {
                Glide
                    .with(this@DriverProfileActivity)
                    .load(Uri.parse(mSelectedImageFileUri.toString()))
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(binding?.ivProfileDriverImage!!)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadDriverImage() {

        showProgressDialog(resources.getString(R.string.please_wait))

        if (mSelectedImageFileUri != null) {

            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "USER_IMAGE" + System.currentTimeMillis() + "." + getFileExtension(
                    mSelectedImageFileUri
                )
            )

            sRef.putFile(mSelectedImageFileUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    Log.e(
                        "Firebase Image URL",
                        taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                    )

                    taskSnapshot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            Log.e("Downloadable Image URL", uri.toString())

                            mProfileImageURL = uri.toString()

                            updateDriverProfileData()
                        }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        this@DriverProfileActivity,
                        exception.message,
                        Toast.LENGTH_LONG
                    ).show()

                    hideProgressDialog()
                }
        }
    }


    private fun updateDriverProfileData() {

        val userHashMap = HashMap<String, Any>()
        if (mProfileImageURL.isNotEmpty() && mProfileImageURL != mDriverDetails.image) {
            userHashMap[Constants.IMAGE] = mProfileImageURL
        }

        if (binding?.etDriverName?.text.toString() != mDriverDetails.name) {
            userHashMap[Constants.NAME] = binding?.etDriverName?.text.toString()
        }

        if (binding?.etDriverMobile?.text.toString() != mDriverDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = binding?.etDriverMobile?.text.toString().toLong()
        }

        FirestoreClass().updateDriverProfileData(this@DriverProfileActivity, userHashMap)
    }


    fun profileUpdateSuccess() {

        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }


    private fun getFileExtension(uri: Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }
}