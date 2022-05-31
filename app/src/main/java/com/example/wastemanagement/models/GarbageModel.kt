package com.example.wastemanagement.models

import android.os.Parcel
import android.os.Parcelable

data class GarbageModel (


    val title: String = "",
    val image: String = "",
    val description: String = "",
    val date: String = "",
    val time: String = "",
    val location: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    var documentId: String = "",
): Parcelable {
    constructor(source: Parcel) : this(

        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readDouble(),
        source.readDouble(),
        source.readString()!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {

        writeString(title)
        writeString(image)
        writeString(description)
        writeString(date)
        writeString(time)
        writeString(location)
        writeDouble(latitude)
        writeDouble(longitude)
        writeString(documentId)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<GarbageModel> = object : Parcelable.Creator<GarbageModel> {
            override fun createFromParcel(source: Parcel): GarbageModel = GarbageModel(source)
            override fun newArray(size: Int): Array<GarbageModel?> = arrayOfNulls(size)
        }
    }
}