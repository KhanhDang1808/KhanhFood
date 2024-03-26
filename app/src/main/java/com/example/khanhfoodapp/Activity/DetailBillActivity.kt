package com.example.khanhfoodapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.khanhfoodapp.Adapter.CartAdapter
import com.example.khanhfoodapp.Adapter.FoodListAdapter
import com.example.khanhfoodapp.Adapter.OrderAdapter
import com.example.khanhfoodapp.Domain.Bill
import com.example.khanhfoodapp.Domain.Foods
import com.example.khanhfoodapp.Fragment.MainFragment
import com.example.khanhfoodapp.R
import com.example.khanhfoodapp.databinding.ActivityDetailBillBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson

class DetailBillActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBillBinding
    private lateinit var adapter: RecyclerView.Adapter<*>
    private lateinit var bill: Bill
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBillBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getIntentExtra()
        setvaliable()
        initList()
        initListener()
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
            textStatus.text = bill.status
            textTotalQtt.text = bill.totalQtt.toString()
        }
    }

    private fun initList() {
        binding.progressBar2.visibility = View.VISIBLE

        if (bill.billFoods.size > 0) {
            binding.apply {
                rclListFood.layoutManager = LinearLayoutManager(
                    this@DetailBillActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                adapter = OrderAdapter(bill.billFoods, this@DetailBillActivity)
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


}