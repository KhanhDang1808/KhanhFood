package com.example.khanhfoodapp.Activity

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.khanhfoodapp.Adapter.OrderAdapter
import com.example.khanhfoodapp.Domain.Bill
import com.example.khanhfoodapp.Fragment.MainFragment.Companion.database
import com.example.khanhfoodapp.R
import com.example.khanhfoodapp.databinding.ActivityDetailOrderBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson

class DetailOrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailOrderBinding
    private lateinit var adapter: RecyclerView.Adapter<*>
    private lateinit var bill: Bill
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getIntentExtra()
        setvaliable()
        setSpinner()
        initList()
        initListener()
        onClickSpinner()
    }

    private fun getIntentExtra() {
        val json = intent.getStringExtra("bill_json")
        // Convert JSON string back to Foods object
        bill = Gson().fromJson(json, Bill::class.java)
    }

    private fun setvaliable() {
        binding.apply {
            textBillId.text = bill.billID
            textNamePhone.text = bill.namePhone
            textAddress.text = bill.billAddress
            textNote.text = bill.note
            textDate.text = bill.billDate
            textTotalPay.text = bill.totalPrice
            textMethod.text = bill.methods
            textTotalQtt.text = bill.totalQtt.toString()
        }
    }

    private fun initList() {
        binding.progressBar2.visibility = View.VISIBLE

        if (bill.billFoods.size > 0) {
            binding.apply {
                rclListFood.layoutManager = LinearLayoutManager(
                    this@DetailOrderActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                adapter = OrderAdapter(bill.billFoods, this@DetailOrderActivity)
                rclListFood.adapter = adapter
            }
            binding.progressBar2.visibility = View.GONE
        } else {
            binding.progressBar2.visibility = View.GONE
            Snackbar.make(
                binding.root,
                "No product found",
                Snackbar.LENGTH_INDEFINITE
            )
                .show()
        }
    }

    private fun initListener() {
        binding.btnBack.setOnClickListener { finish() }
        binding.textX.setOnClickListener { binding.layoutSlogan.visibility = View.GONE }
    }


    private fun setSpinner() {
        val items = arrayOf(
            "Chờ xác nhận",
            "Đang vận chuyển",
            "Hoàn thành",
            "Hủy đơn hàng"
        )
        // Khởi tạo ArrayAdapter
        val adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                items
            )
        // Đặt layout cho dropdown list
        adapter.setDropDownViewResource(R.layout.method_item)
        // Gắn dữ liệu vào Spinner
        binding.spinnerMethod.adapter = adapter
        // Đặt mục mặc định cho Spinner (ví dụ: mục thứ 2 trong danh sách)
        checkSpinner()
    }

    private fun checkSpinner() {
        when (bill.status) {
            "Chờ xác nhận" -> {
                binding.spinnerMethod.setSelection(0)
            }

            "Đang vận chuyển" -> {
                binding.spinnerMethod.setSelection(1)
            }

            "Hoàn thành" -> {
                binding.spinnerMethod.setSelection(2)
            }

            else -> {
                binding.spinnerMethod.setSelection(3)
            }
        }
    }


    private fun onClickSpinner() {
        binding.spinnerMethod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Xử lý khi một mục được chọn
                val selectedItem = parent?.getItemAtPosition(position).toString()

                if (selectedItem == bill.status) {
                    checkSpinner()
                    return
                } else if (bill.status == "Hoàn thành") {
                    Toast.makeText(
                        this@DetailOrderActivity,
                        "Đơn hàng đã hoàn thành không thể thay đổi.",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.spinnerMethod.setSelection(2)
                    return
                } else if (bill.status == "Đã hủy") {
                    Toast.makeText(
                        this@DetailOrderActivity,
                        "Đơn hàng đã hủy không thể thay đổi.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                } else if (bill.status == "Chờ xác nhận") {
                    when (position) {
                        1 -> {
                            AlertDialog.Builder(this@DetailOrderActivity)
                                .setTitle("Đang vận chuyển")
                                .setMessage("Hãy chắc chắn rằng đơn hàng đang được vận chuyển?")
                                .setPositiveButton("Yes") { _, _ ->
                                    //  progressBar.visibility = View.VISIBLE
                                    database.getReference("Bill").child(bill.billID).child("status")
                                        .setValue("Đang vận chuyển")
                                }
                                .setNegativeButton("Cancel") { _, _ ->
                                    binding.spinnerMethod.setSelection(0)
                                }
                                .show()
                        }

                        3 -> {
                            AlertDialog.Builder(this@DetailOrderActivity)
                                .setTitle("Hủy đơn hàng")
                                .setMessage("Bạn muốn hủy đơn hàng?")
                                .setPositiveButton("Yes") { _, _ ->
                                    //  progressBar.visibility = View.VISIBLE
                                    database.getReference("Bill").child(bill.billID).child("status")
                                        .setValue("Đã hủy")
                                }
                                .setNegativeButton("Cancel") { _, _ ->
                                    binding.spinnerMethod.setSelection(0)
                                }
                                .show()
                        }

                        2 -> {
                            Toast.makeText(
                                this@DetailOrderActivity,
                                "Chuyển trạng thái Đang vận chuyển trước khi hoàn thành",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.spinnerMethod.setSelection(0)
                        }
                    }
                } else {
                    when (position) {
                        0 -> {
                            AlertDialog.Builder(this@DetailOrderActivity)
                                .setTitle("Chờ xác nhận")
                                .setMessage("Đơn hàng đang được vận chuyển, bạn có muốn thay đổi?")
                                .setPositiveButton("Yes") { _, _ ->
                                    //  progressBar.visibility = View.VISIBLE
                                    database.getReference("Bill").child(bill.billID).child("status")
                                        .setValue("Chờ xác nhận")
                                }
                                .setNegativeButton("Cancel") { _, _ ->
                                    binding.spinnerMethod.setSelection(1)
                                }
                                .show()
                        }

                        2 -> {
                            AlertDialog.Builder(this@DetailOrderActivity)
                                .setTitle("Hoàn thành")
                                .setMessage("Đơn hàng đã giao thành công?")
                                .setPositiveButton("Yes") { _, _ ->
                                    //  progressBar.visibility = View.VISIBLE
                                    database.getReference("Bill").child(bill.billID).child("status")
                                        .setValue("Hoàn thành")
                                }
                                .setNegativeButton("Cancel") { _, _ ->
                                    binding.spinnerMethod.setSelection(1)
                                }
                                .show()
                        }

                        3 -> {
                            AlertDialog.Builder(this@DetailOrderActivity)
                                .setTitle("Hủy đơn hàng")
                                .setMessage("Đơn hàng đang được vận chuyển, bạn có muốn hủy?")
                                .setPositiveButton("Yes") { _, _ ->
                                    //  progressBar.visibility = View.VISIBLE
                                    database.getReference("Bill").child(bill.billID).child("status")
                                        .setValue("Đã hủy")
                                }
                                .setNegativeButton("Cancel") { _, _ ->
                                    binding.spinnerMethod.setSelection(1)
                                }
                                .show()
                        }
                    }

                }


            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }
}