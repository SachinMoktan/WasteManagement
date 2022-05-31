package com.example.wastemanagement.activities

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wastemanagement.R
import com.example.wastemanagement.databinding.ActivityComplaintBinding
import com.example.wastemanagement.databinding.DialogUpdateBinding
import com.example.wastemanagement.room.ComplaintApp
import com.example.wastemanagement.room.ComplaintDao
import com.example.wastemanagement.room.ComplaintEntity
import com.example.wastemanagement.room.ItemAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.log

class ComplaintActivity : AppCompatActivity() {
    private var binding:ActivityComplaintBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComplaintBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val complaintDao = (application as ComplaintApp).db.complaintDao()
        binding?.btnAdd?.setOnClickListener {

            addRecord(complaintDao)
        }
        lifecycleScope.launch {
            complaintDao.fetchAllComplaint().collect {
                Log.d("exactcomplaint","$it")
                val list = ArrayList(it)
                setupListOfDataIntoRecyclerView(list,complaintDao)
            }
        }
        setupActionBar()
    }

    private fun setupActionBar() {

        setSupportActionBar(binding?.toolbarUserSignUpActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        binding?.toolbarUserSignUpActivity?.setNavigationOnClickListener {
            onBackPressed() }

    }


    private fun setupListOfDataIntoRecyclerView(employeesList:ArrayList<ComplaintEntity>,
                                                employeeDao: ComplaintDao) {

        if (employeesList.isNotEmpty()) {


            val itemAdapter = ItemAdapter(employeesList,{updateId ->
                updateRecordDialog(updateId,employeeDao)
            }){ deleteId->
                lifecycleScope.launch {
                    employeeDao.fetchComplaintById(deleteId).collect {
                        if (it != null) {
                            deleteRecordAlertDialog(deleteId, employeeDao, it)
                        }
                    }
                }

            }
            binding?.rvItemsList?.layoutManager = LinearLayoutManager(this)

            binding?.rvItemsList?.adapter = itemAdapter
            binding?.rvItemsList?.visibility = View.VISIBLE
            binding?.tvNoRecordsAvailable?.visibility = View.GONE
        } else {

            binding?.rvItemsList?.visibility = View.GONE
            binding?.tvNoRecordsAvailable?.visibility = View.VISIBLE
        }
    }

    fun addRecord(complaintDao: ComplaintDao) {
        val name = binding?.etName?.text.toString()
        val problem = binding?.etProblemId?.text.toString()
        if (name.isNotEmpty() && problem.isNotEmpty()) {
            lifecycleScope.launch {
                complaintDao.insert(ComplaintEntity(name = name, problem = problem))
                Toast.makeText(applicationContext, "Record saved", Toast.LENGTH_LONG).show()
                binding?.etName?.text?.clear()
                binding?.etProblemId?.text?.clear()

            }
        } else {
            Toast.makeText(
                applicationContext,
                "Name or Email cannot be blank",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    fun updateRecordDialog(id:Int,employeeDao: ComplaintDao)  {
        val updateDialog = Dialog(this, R.style.Theme_Dialog)
        updateDialog.setCancelable(false)

        val binding = DialogUpdateBinding.inflate(layoutInflater)
        updateDialog.setContentView(binding.root)

        lifecycleScope.launch {
            employeeDao.fetchComplaintById(id).collect {
                if (it != null) {
                    binding.etUpdateName.setText(it.name)
                    binding.etUpdateProblemId.setText(it.problem)
                }
            }
        }
        binding.tvUpdate.setOnClickListener {

            val name = binding.etUpdateName.text.toString()
            val email = binding.etUpdateProblemId.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty()) {
                lifecycleScope.launch {
                    employeeDao.update(ComplaintEntity(id, name, email))
                    Toast.makeText(applicationContext, "Record Updated.", Toast.LENGTH_LONG)
                        .show()
                    updateDialog.dismiss() // Dialog will be dismissed
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "Name or Email cannot be blank",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        binding.tvCancel.setOnClickListener{
            updateDialog.dismiss()
        }

        updateDialog.show()
    }


    fun deleteRecordAlertDialog(id:Int, employeeDao: ComplaintDao, employee: ComplaintEntity) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Delete Record")

        builder.setMessage("Are you sure you wants to delete ${employee.name}.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            lifecycleScope.launch {
                employeeDao.delete(ComplaintEntity(id))
                Toast.makeText(
                    applicationContext,
                    "Record deleted successfully.",
                    Toast.LENGTH_LONG
                ).show()

                dialogInterface.dismiss()
            }

        }


        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()

        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}