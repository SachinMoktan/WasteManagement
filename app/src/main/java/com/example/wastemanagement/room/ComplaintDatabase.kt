package com.example.wastemanagement.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ComplaintEntity::class],version = 2)
abstract class ComplaintDatabase:RoomDatabase() {

    abstract fun complaintDao():ComplaintDao


    companion object {

        @Volatile
        private var INSTANCE: ComplaintDatabase? = null

        fun getInstance(context: Context): ComplaintDatabase {
            // Multiple threads can ask for the database at the same time, ensure we only initialize
            // it once by using synchronized. Only one thread may enter a synchronized block at a
            // time.
            synchronized(this) {

                var instance = INSTANCE


                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ComplaintDatabase::class.java,
                        "employee_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}