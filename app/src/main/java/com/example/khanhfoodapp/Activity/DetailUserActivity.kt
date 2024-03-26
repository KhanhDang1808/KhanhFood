package com.example.khanhfoodapp.Activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.khanhfoodapp.Domain.User
import com.example.khanhfoodapp.Fragment.MainFragment.Companion.database
import com.example.khanhfoodapp.databinding.ActivityDetailUserBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson

class DetailUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailUserBinding
    private lateinit var user: User
    private var total = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getIntentExtra()
        setvaliable()
        initlistener()
    }

    private fun initlistener() {
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setvaliable() {
        binding.apply {
            Glide.with(this@DetailUserActivity)
                .load(user.image)
                .into(imageUser)
            textName.text = user.name
            textSex.text = user.sex
            textDate.text = user.dateBirth
            textEmail.text = user.email
            textAddress.text = user.address
            textPhoneNumber.text = user.phoneNumber
        }
        initNumberBill()
    }

    private fun initNumberBill() {
        val myRef = database.getReference("Bill")
        val query = myRef.orderByChild("userId").equalTo(user.id)
        query.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.textTotalOrder.text = snapshot.childrenCount.toString()
                for (issue in snapshot.children){
                    val tl = issue.child("totalPrice").getValue(String::class.java)
                    val removedFirstChar = tl?.substring(1)?.trim()

                    // Chuyển đổi chuỗi thành kiểu double
                    val result = removedFirstChar?.toDouble()
                    if (result != null) {
                        total+=result
                    }
                }
                binding.textTotalPrice.text = "$${total.toString()}"

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun getIntentExtra() {
        val json = intent.getStringExtra("user_json")
        // Convert JSON string back to Foods object
        user = Gson().fromJson(json, User::class.java)
    }
}