package com.example.wastemanagement.models

import android.os.Parcel
import android.os.Parcelable

data class Admin(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val image: String = "",
    val mobile: Long = 0,
    val fcmToken: String = ""
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readLong(),
        source.readString()!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(name)
        writeString(email)
        writeString(image)
        writeLong(mobile)
        writeString(fcmToken)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Admin> = object : Parcelable.Creator<Admin> {
            override fun createFromParcel(source: Parcel): Admin = Admin(source)
            override fun newArray(size: Int): Array<Admin?> = arrayOfNulls(size)
        }
    }
}
