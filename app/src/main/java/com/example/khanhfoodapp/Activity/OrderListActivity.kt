package com.example.khanhfoodapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.khanhfoodapp.Adapter.OrderListAdapter
import com.example.khanhfoodapp.Domain.Bill
import com.example.khanhfoodapp.Fragment.MainFragment
import com.example.khanhfoodapp.databinding.ActivityOrderListBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class OrderListActivity : AppCompatActivity() {
    private var isMethod: Int = 0
    private lateinit var binding: ActivityOrderListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isMethod = intent.getIntExtra("isMethod", 0)
        initBill()
        initListener()
    }

    private fun initBill() {
        val myRef = MainFragment.database.getReference("Bill")
        binding.progressBar2.visibility = View.VISIBLE
        val listBill = ArrayList<Bill>()
        val query: Query
        when (isMethod) {
            1 -> {
                binding.textTitle.text = "Chờ xác nhận"
                query = myRef.orderByChild("status").equalTo("Chờ xác nhận")
            }

            2 -> {
                binding.textTitle.text = "Đang vận chuyển"
                query = myRef.orderByChild("status").equalTo("Đang vận chuyển")
            }

            3 -> {
                binding.textTitle.text = "Hoàn thành"
                query = myRef.orderByChild("status").equalTo("Hoàn thành")
            }

            else -> {
                binding.textTitle.text = "Đã hủy"
                query = myRef.orderByChild("status").equalTo("Đã hủy")
            }
        }

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    listBill.clear()

                val reversedList = snapshot.children.reversed()
                for (issue in reversedList) {
                    issue.getValue(Bill::class.java)?.let { listBill.add(it) }
                }
                // if (listBill.size > 0) {
                binding.apply {
                    rclListOder.layoutManager = LinearLayoutManager(
                        this@OrderListActivity,
                        LinearLayoutManager.VERTICAL, false
                    )
                    val adapter = OrderListAdapter(listBill)
                    rclListOder.adapter = adapter
                    binding.textTotal.text = listBill.size.toString()
                    }
                }
                binding.progressBar2.visibility = View.GONE
                if (listBill.size == 0) {

                    binding.progressBar2.visibility = View.GONE
                    Snackbar.make(
                        binding.root,
                        "Không có đơn hàng ${listBill.size}",
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction("Cancel") {
                        finish()
                    }
                        .show()
                    binding.progressBar2.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun initListener() {
        binding.btnBack.setOnClickListener { finish() }
    }

}