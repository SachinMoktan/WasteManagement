package com.example.wastemanagement.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.wastemanagement.databinding.ActivityUserGarbageBinding
import com.example.wastemanagement.firebase.FirestoreClass
import com.example.wastemanagement.models.GarbageModel
import com.example.wastemanagement.utils.AlarmReceiver
import com.example.wastemanagement.utils.GetAddressFromLatLng
import com.google.android.gms.location.*
import com.google.firebase.firestore.ktx.toObject
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.text.SimpleDateFormat
import java.util.*


class UserGarbageActivity : BaseActivity() {
    private var binding: ActivityUserGarbageBinding? = null

    private val conversationCollectionRef = FirestoreClass().getConversationCollection()

    private var mGarbageList = ArrayList<GarbageModel>()

    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var calendar: Calendar

    private lateinit var pickUpTime : String

    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0
    private var mDLatitude: Double = 0.0
    private var mDLongitude: Double = 0.0
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserGarbageBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding?.toolbarAddPlace?.setNavigationOnClickListener {
            onBackPressed()
        }

        FirestoreClass().getConversationList(this)

        createNotificationChannel()

        subscribeToRealtimeUpdates()

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding?.notify?.setOnClickListener{
            setAlarm()
        }

        binding?.viewMap?.setOnClickListener{
            if (!isLocationEnabled()) {
                Toast.makeText(
                    this,
                    "Your location provider is turned off. Please turn it on.",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            } else {

                Dexter.withActivity(this)
                    .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                    .withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                            if (report!!.areAllPermissionsGranted()) {

                                requestNewLocationData()
                            }
                        }

                        override fun onPermissionRationaleShouldBeShown(
                            permissions: MutableList<PermissionRequest>?,
                            token: PermissionToken?
                        ) {
                            showRationalDialogForPermissions()
                        }
                    }).onSameThread()
                    .check()
            }


            val uri = java.lang.String.format(Locale.ENGLISH, "geo:%f,%f", mDLatitude, mDLongitude)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
        }

    }


    private fun setAlarm() {

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this,AlarmReceiver::class.java)
        val pendingFlags: Int
        pendingFlags = if (Build.VERSION.SDK_INT >= 23) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        //intent.setFlags(Intent.FLAG)
        pendingIntent = PendingIntent.getBroadcast(this,0,intent,pendingFlags)
        val sdf = SimpleDateFormat("yyMMddhhmmss")
        val pickUpCalendar = sdf.parse(pickUpTime)

        alarmManager.setRepeating(

            AlarmManager.RTC_WAKEUP,pickUpCalendar.time,
            AlarmManager.INTERVAL_DAY,pendingIntent

        )

        Toast.makeText(this,"Notify set Successfully",Toast.LENGTH_SHORT).show()

    }



    private fun createNotificationChannel() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            val name : CharSequence = "wastemanagementReminderChannel"
            val description = "Channel for Alarm Manager"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("wastemanagement",name,importance)
            channel.description = description
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )

            notificationManager.createNotificationChannel(channel)
        }
    }


    private fun subscribeToRealtimeUpdates() {
        conversationCollectionRef.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            firebaseFirestoreException?.let {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            querySnapshot?.let {
                val conversationList = ArrayList<GarbageModel>()
                for (i in it.documents) {
                    val conversation = i.toObject<GarbageModel>()
                    conversation?.documentId = i.id
                    if (conversation != null) {
                        conversationList.add(conversation)
                    }
                }
                mGarbageList = conversationList
            }
        }
    }


    fun onGetGarbageList(conversationList: ArrayList<GarbageModel>) {

        mGarbageList = conversationList

        pickUpTime = formatDateTime(mGarbageList[0].date,mGarbageList[0].time)

        mDLatitude = locationLatitude(mGarbageList[0].latitude)
        mDLongitude = locationLongitude(mGarbageList[0].longitude)

        Glide.with(this)
            .load(mGarbageList[0].image)
            .centerCrop()
            .into(binding?.ivPlaceImage!!)

        binding?.tvDate?.setText(mGarbageList[0].date)
        binding?.tvTime?.setText(mGarbageList[0].time)
        binding?.tvDescription?.setText(mGarbageList[0].description)
        binding?.tvLocation?.setText(mGarbageList[0].location)

        //if (conversationList.mobile != 0L) {
           // binding?.etDriverMobile?.setText(driver.mobile.toString())
       // }


    }

    private fun locationLongitude(longitude: Double): Double {
        return longitude
    }

    private fun locationLatitude(latitude: Double): Double {
        return latitude
    }

    private fun formatDateTime(date: String, time: String): String {

        return date+time

    }


    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {

        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()!!
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            mLatitude = mLastLocation.latitude
            Log.e("Current Latitude", "$mLatitude")
            mLongitude = mLastLocation.longitude
            Log.e("Current Longitude", "$mLongitude")

            val addressTask =
                GetAddressFromLatLng(this@UserGarbageActivity, mLatitude, mLongitude)

            addressTask.setAddressListener(object :
                GetAddressFromLatLng.AddressListener {
                override fun onAddressFound(address: String?) {
                    Log.e("Address ::", "" + address)

                }

                override fun onError() {
                    Log.e("Get Address ::", "Something is wrong...")
                }
            })

            addressTask.getAddress()
        }
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
            .setPositiveButton("GO TO SETTINGS"
            ) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog,
                                           _ ->
                dialog.dismiss()
            }.show()
    }
}


