package com.example.khanhfoodapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.khanhfoodapp.Adapter.FoodListAdapter
import com.example.khanhfoodapp.Domain.Bill
import com.example.khanhfoodapp.Domain.Foods
import com.example.khanhfoodapp.Fragment.MainFragment.Companion.database
import com.example.khanhfoodapp.R
import com.example.khanhfoodapp.databinding.ActivityBillBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson

class BillActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBillBinding
    private lateinit var adapter: RecyclerView.Adapter<*>
    private lateinit var bill: Bill
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBillBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getIntentExtra()
        initList()
        initListener()
    }


    private fun getIntentExtra() {
        val namePhone = intent.getStringExtra("Name_phone")
        val address = intent.getStringExtra("Address")
        val billID = intent.getStringExtra("BillID")
        val total = intent.getStringExtra("Total")
        val totalQtt = intent.getIntExtra("Total_Qtt", 0)
        val method = intent.getStringExtra("Method")

        val json = intent.getStringExtra("bill_json")
        // Convert JSON string back to Foods object
        bill = Gson().fromJson(json, Bill::class.java)

        binding.apply {
            textBillId.text = billID
            textNamePhone.text = namePhone
            textAddress.text = address
            textMethod.text = method
            textTotalQtt.text = totalQtt.toString()
            textTotalPay.text = total
        }
    }


    private fun initList() {
        val myRef = database.getReference("Foods")
        binding.progressBar2.visibility = View.VISIBLE

        val listFood = ArrayList<Foods>()

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (issue in snapshot.children) {
                        issue.getValue(Foods::class.java)?.let { listFood.add(it) }
                    }
                    if (listFood.size > 0) {
                        binding.apply {
                            rclListFood.layoutManager = GridLayoutManager(this@BillActivity, 2)
                            adapter = FoodListAdapter(listFood)
                            rclListFood.adapter = adapter
                        }
                        binding.progressBar2.visibility = View.GONE
                    }
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

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun initListener() {
        binding.btnBack.setOnClickListener { finish() }
        binding.textX.setOnClickListener { binding.layoutSlogan.visibility = View.GONE }
        binding.btnDetailBill.setOnClickListener {
            val billJson: String = Gson().toJson(bill)
            val intent = Intent(this, DetailBillActivity::class.java)
            intent.putExtra("bill_json", billJson)
            startActivity(intent)
        }
    }

}