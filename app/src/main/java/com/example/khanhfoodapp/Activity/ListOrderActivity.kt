package com.example.khanhfoodapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.khanhfoodapp.Adapter.CategoryAdapter
import com.example.khanhfoodapp.Adapter.OrderListAdapter
import com.example.khanhfoodapp.Domain.Bill
import com.example.khanhfoodapp.Domain.Category
import com.example.khanhfoodapp.Domain.Foods
import com.example.khanhfoodapp.Fragment.MainFragment
import com.example.khanhfoodapp.Fragment.MainFragment.Companion.mAuth
import com.example.khanhfoodapp.R
import com.example.khanhfoodapp.databinding.ActivityListOrderBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson

class ListOrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListOrderBinding
    private lateinit var adapter: RecyclerView.Adapter<*>
    private var isMethod: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        isMethod = intent.getIntExtra("isMethod", 0)

        initBill()
        initListener()
    }

    private fun initListener() {
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun initBill() {
        val myRef = MainFragment.database.getReference("Bill")
        val query = myRef.orderByChild("userId").equalTo("${mAuth.currentUser?.uid}")
        binding.progressBar5.visibility = View.VISIBLE
        val listBill = ArrayList<Bill>()

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val reversedList = snapshot.children.reversed()
                    for (issue in reversedList) {
                        issue.getValue(Bill::class.java)?.let { listBill.add(it) }
                    }
                    if (listBill.size > 0) {
                       val list = filterBill(isMethod,listBill)
                        if(list.size == 0){
                            binding.progressBar5.visibility = View.GONE
                            Snackbar.make(
                                binding.root,
                                "Không có đơn hàng",
                                Snackbar.LENGTH_INDEFINITE
                            )
                                .setAction("CANCEL") {
                                    finish()
                                }
                                .show()
                            return
                        }
                        binding.apply {
                            rclListOrder.layoutManager = LinearLayoutManager(
                                this@ListOrderActivity,
                                LinearLayoutManager.VERTICAL, false
                            )
                            val adapter = OrderListAdapter(list)
                            rclListOrder.adapter = adapter
                            textTotal.text = list.size.toString()
                        }
                    }
                    binding.progressBar5.visibility = View.GONE
                } else {
                    binding.progressBar5.visibility = View.GONE
                    Snackbar.make(
                        binding.root,
                        "Không có đơn hàng",
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction("CANCEL") {
                            finish()
                        }
                        .show()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun filterBill(method:Int,list:ArrayList<Bill>):ArrayList<Bill>{
        val listBill = ArrayList<Bill>()
     //   if(listBill.size>0) listBill.clear()

        for(bill in list) {
            if (method == 1) {
                if (bill.status == "Chờ xác nhận") {
                    listBill.add(bill)
                }
            } else if (method == 2) {
                if (bill.status == "Đang vận chuyển") {
                    listBill.add(bill)
                }
            } else if (method == 3) {
                if (bill.status == "Hoàn thành") {
                    listBill.add(bill)
                }
            } else if (method == 4) {
                if (bill.status == "Đã hủy") {
                    listBill.add(bill)
                }
            } else {
                listBill.add(bill)
            }
        }


        return  listBill
    }

}