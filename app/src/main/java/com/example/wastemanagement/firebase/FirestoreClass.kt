package com.example.wastemanagement.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.wastemanagement.activities.*
import com.example.wastemanagement.models.Admin
import com.example.wastemanagement.models.Driver
import com.example.wastemanagement.models.GarbageModel
import com.example.wastemanagement.models.User
import com.example.wastemanagement.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions



class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: Activity, userInfo: User) {

        mFireStore.collection(Constants.USERS)

            .document(getCurrentUserID())

            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                if (activity is SignUpActivity){
                    activity.userRegisteredSuccess()
                }

            }
            .addOnFailureListener { e ->
                Log.e(
                    activity.javaClass.simpleName,
                    "Error writing document",
                    e
                )
            }
    }

    fun updateUserProfileData(activity: UserProfileActivity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {

                Log.e(activity.javaClass.simpleName, "Profile Data updated successfully!")

                Toast.makeText(activity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()

                activity.profileUpdateSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board.",
                    e
                )
            }
    }

    fun loadUserData(activity: Activity) {

        mFireStore.collection(Constants.USERS)

            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(
                    activity.javaClass.simpleName, document.toString()
                )

                val loggedInUser = document.toObject(User::class.java)!!

                when (activity) {
                    is SignInActivity -> {
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser)
                    }
                    is UserProfileActivity -> {
                        activity.setUserDataInUI(loggedInUser)
                    }
                }

            }
            .addOnFailureListener { e ->

                when (activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting loggedIn user details",
                    e
                )
            }
    }



    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }


    fun registerDriver(activity: Activity, userInfo: Driver) {

        mFireStore.collection(Constants.DRIVERS)

            .document(getCurrentDriverID())

            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

                if (activity is SignUpActivity){
                    activity.DriverRegisteredSuccess()
                }

            }
            .addOnFailureListener { e ->
                Log.e(
                    activity.javaClass.simpleName,
                    "Error writing document",
                    e
                )
            }
    }

    fun updateDriverProfileData(activity: DriverProfileActivity, driverHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.DRIVERS)
            .document(getCurrentDriverID())
            .update(driverHashMap)
            .addOnSuccessListener {

                Log.e(activity.javaClass.simpleName, "Profile Data updated successfully!")

                Toast.makeText(activity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()

                activity.profileUpdateSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board.",
                    e
                )
            }
    }

    fun loadDriverData(activity: Activity) {

        mFireStore.collection(Constants.DRIVERS)

            .document(getCurrentDriverID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(
                    activity.javaClass.simpleName, document.toString()
                )

                val loggedInDriver = document.toObject(Driver::class.java)!!

                when (activity) {
                    is SignInActivity -> {
                        activity.signInSuccess(loggedInDriver)
                    }
                    is MainActivity2 -> {
                        activity.updateNavigationDriverDetails(loggedInDriver)
                    }
                    is DriverProfileActivity -> {
                        activity.setDriverDataInUI(loggedInDriver)
                    }
                }

            }
            .addOnFailureListener { e ->

                when (activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity2 -> {
                        activity.hideProgressDialog()
                    }
                    is DriverProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting loggedIn user details",
                    e
                )
            }
    }



    fun getCurrentDriverID(): String {
        val currentDriver = FirebaseAuth.getInstance().currentUser

        var currentDriverID = ""
        if (currentDriver != null) {
            currentDriverID = currentDriver.uid
        }

        return currentDriverID
    }

    fun getAllUser(activity: SignInActivity){
        mFireStore.collection(Constants.USERS)

            .get()
            .addOnSuccessListener {
                val userList = ArrayList<String>()
                for (i in it.documents) {
                    val user = i.toObject(User::class.java)!!
                    userList.add(user.id)
                }
                activity.onGetUserList(userList)
            }
    }

    fun getAllDriver(activity: SignInActivity){
        mFireStore.collection(Constants.DRIVERS)

            .get()
            .addOnSuccessListener {
                val driverList = ArrayList<String>()
                for (i in it.documents) {
                    val driver = i.toObject(User::class.java)!!
                    driver.id = i.id
                    driverList.add(driver.id)
                }
                activity.onGetDriverList(driverList)
            }
    }





    fun saveGarbageData(activity: Activity, garbage: GarbageModel) {

        mFireStore.collection(Constants.GARBAGE)

            .document()

            .set(garbage, SetOptions.merge())
            .addOnSuccessListener {
                if (activity is DriverGarbageActivity){
                    Log.e("Sachin","garbage save to firstore successful")
                }

                //if (activity is AdminSignUpActivity){
                    //activity.AdminRegisteredSuccess()
               // }
            }
            .addOnFailureListener { e ->
                Log.e(
                    activity.javaClass.simpleName,
                    "Error writing document",
                    e
                )
            }
    }


    fun loadGarbageData(activity: Activity) {

        mFireStore.collection(Constants.GARBAGE)

            .document()
            .get()
            .addOnSuccessListener { document ->
                Log.e(
                    activity.javaClass.simpleName, document.toString()
                )

                val loggedInUser = document.toObject(User::class.java)!!

                when (activity) {
                    is SignInActivity -> {
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser)
                    }
                    is UserProfileActivity -> {
                        activity.setUserDataInUI(loggedInUser)
                    }
                }

            }
            .addOnFailureListener { e ->

                when (activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting loggedIn user details",
                    e
                )
            }
    }


    fun getConversationCollection(): CollectionReference {
        return mFireStore.collection(Constants.GARBAGE)
    }

    fun getConversationList(activity: UserGarbageActivity){
        mFireStore.collection(Constants.GARBAGE)

            .get()
            .addOnSuccessListener {
                val conversationList = ArrayList<GarbageModel>()
                for (i in it.documents) {
                    val conversation = i.toObject(GarbageModel::class.java)!!
                    conversation.documentId = i.id
                    conversationList.add(conversation)
                }
                activity.onGetGarbageList(conversationList)
            }
    }

}