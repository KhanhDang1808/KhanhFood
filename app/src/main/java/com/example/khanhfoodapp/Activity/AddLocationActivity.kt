package com.example.khanhfoodapp.Activity

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.khanhfoodapp.Adapter.LocationAdapter
import com.example.khanhfoodapp.Domain.Category
import com.example.khanhfoodapp.Domain.Location
import com.example.khanhfoodapp.Fragment.MainFragment
import com.example.khanhfoodapp.Fragment.MainFragment.Companion.database
import com.example.khanhfoodapp.ItemClickListener
import com.example.khanhfoodapp.R
import com.example.khanhfoodapp.databinding.ActivityAddLocationBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class AddLocationActivity : AppCompatActivity(), ItemClickListener {
    private lateinit var binding: ActivityAddLocationBinding
    private lateinit var list: ArrayList<Category>
    private var numberLocation: Int = 0
    private lateinit var dialog: Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.layoutAddLocation.visibility = View.GONE
        initLocation()
        initListener()

    }

    private fun initListener() {
        binding.btnBack.setOnClickListener { finish() }

        binding.buttonAddLocation.setOnClickListener {
            if (binding.layoutAddLocation.visibility == View.GONE) {
                binding.layoutAddLocation.visibility = View.VISIBLE
                return@setOnClickListener
            } else {
                val text = binding.editLocation.text.toString().trim()
                if (text.isEmpty()) {
                    Toast.makeText(this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                } else {
                    val map = mapOf("Id" to numberLocation, "loc" to text)
                    database.getReference("Location").child("$numberLocation").setValue(map).addOnCompleteListener {
                        binding.editLocation.setText("")
                        binding.editLocation.clearFocus()
                    }
                }
            }
        }

    }

    private fun initLocation() {
        val myRef = MainFragment.database.getReference("Location")
        val list: ArrayList<Location> = arrayListOf()
        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                list.clear()
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.exists()) {
                    for (issue in dataSnapshot.children) {
                        issue.getValue(Location::class.java)?.let { list.add(it) }
                    }
                    if (list.size > 0) {
                        numberLocation = list[list.size - 1].Id + 1
                        binding.apply {
                            rclLocation.layoutManager =
                                LinearLayoutManager(
                                    this@AddLocationActivity,
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                            val adapter = LocationAdapter(list)
                            adapter.onItemClickListener(this@AddLocationActivity)

                            rclLocation.adapter = adapter

                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })
    }

    override fun onEdit(position: Int) {

        openEditDialog(position)

    }

    override fun onDelete(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Xóa vị trí")
            .setMessage("Bạn có chắc muốn xóa")
            .setPositiveButton("OK") { _, _ ->
                MainFragment.database.getReference("Location").child("$position").removeValue()
                    .addOnCompleteListener {
                        Toast.makeText(this, "Xóa thành công", Toast.LENGTH_SHORT)
                            .show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun openEditDialog(id: Int) {
        dialog = Dialog(this)
        setDialog(dialog)

        val editName = dialog.findViewById<EditText>(R.id.edit_name)
        val btnCancel = dialog.findViewById<Button>(R.id.btn_cancel)
        val btnSend = dialog.findViewById<Button>(R.id.btn_send)



        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSend.setOnClickListener {
            val textEdit = editName.text.toString().trim()
            if (textEdit.isEmpty()) {
                Toast.makeText(this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            } else {
                dialog.dismiss()
                AlertDialog.Builder(binding.root.context)
                    .setTitle("Sửa vị trí")
                    .setMessage("Bạn có muốn sửa?")
                    .setPositiveButton("OK") { _, _ ->
                        database.getReference("Location").child("$id").child("loc")
                            .setValue(textEdit)
                            .addOnCompleteListener {
                                Toast.makeText(
                                    binding.root.context,
                                    "Xóa thành công",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }
        dialog.show()
    }


    private fun setDialog(dialog: Dialog) {
        dialog.let {
            it.requestWindowFeature(Window.FEATURE_NO_TITLE)
            it.setContentView(R.layout.layout_dialog_edit)
        }
        val window = dialog.window ?: return

        window.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            // Đặt hiệu ứng mờ cho phần còn lại của màn hình
            setFlags(
                WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                WindowManager.LayoutParams.FLAG_DIM_BEHIND
            )
            setDimAmount(0.8f)
        }

        val windowAttributes = window.attributes
        windowAttributes.gravity = Gravity.CENTER
        window.attributes = windowAttributes

        val textNode = dialog.findViewById<TextView>(R.id.text_node)
        textNode.text = "Nhập vị trí tại đây"

        val textTitle = dialog.findViewById<TextView>(R.id.text_title)
        textTitle.text = "Vị trí"

    }


}