package com.example.wastemanagement.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ComplaintDao {


    @Insert
    suspend fun insert(complaintEntity: ComplaintEntity)

    @Update
    suspend fun update(complaintEntity: ComplaintEntity)

    @Delete
    suspend fun delete(complaintEntity: ComplaintEntity)

    @Query("Select * from `complaint-table`")
    fun fetchAllComplaint(): Flow<List<ComplaintEntity>>

    @Query("Select * from `complaint-table` where id=:id")
    fun fetchComplaintById(id:Int):Flow<ComplaintEntity>




}