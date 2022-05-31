package com.example.wastemanagement.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.wastemanagement.models.GarbageModel

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "GarbageDatabase"
        private const val TABLE_GARBAGE = "GarbageTable"
        private const val KEY_ID = "_id"
        private const val KEY_TITLE = "title"
        private const val KEY_IMAGE = "image"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_DATE = "date"
        private const val KEY_LOCATION = "location"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
    }

    override fun onCreate(db: SQLiteDatabase?) {

        val CREATE_GARBAGE_TABLE = ("CREATE TABLE " + TABLE_GARBAGE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_LOCATION + " TEXT,"
                + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT)")
        db?.execSQL(CREATE_GARBAGE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_GARBAGE")
        onCreate(db)
    }


    fun addGarbage(garbage: GarbageModel): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, garbage.title)
        contentValues.put(KEY_IMAGE, garbage.image)
        contentValues.put(
            KEY_DESCRIPTION,
            garbage.description
        )
        contentValues.put(KEY_DATE, garbage.date)
        contentValues.put(KEY_LOCATION, garbage.location)
        contentValues.put(KEY_LATITUDE, garbage.latitude)
        contentValues.put(KEY_LONGITUDE, garbage.longitude)

        val result = db.insert(TABLE_GARBAGE, null, contentValues)

        db.close()
        return result
    }

}
