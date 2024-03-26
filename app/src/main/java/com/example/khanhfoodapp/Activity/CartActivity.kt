package com.example.khanhfoodapp.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextPaint
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.khanhfoodapp.Adapter.CartAdapter
import com.example.khanhfoodapp.Helper.ChangeNumberItemsListener
import com.example.khanhfoodapp.Helper.ManagmentCart
import com.example.khanhfoodapp.MySingleton
import com.example.khanhfoodapp.MySingleton.mUser
import com.example.khanhfoodapp.databinding.ActivityCartBinding
import kotlinx.coroutines.runBlocking
import java.text.DecimalFormat

class CartActivity : BaseActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var adpater: RecyclerView.Adapter<*>
    private lateinit var managmentCart: ManagmentCart
    private val decimalFormat = DecimalFormat("#.##")

    private var tax = 0.0
    private var itemTotal = 0.0
    private var total = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart = ManagmentCart(this)

        initListener()
        calculateCart()
    }

    override fun onStart() {
        super.onStart()


        initList()
    }

    private fun initList() {
        if (managmentCart.getListCart().isEmpty()) {
            binding.emptyCartText.visibility = View.VISIBLE
        } else {
            binding.emptyCartText.visibility = View.GONE
        }
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rclCart.layoutManager = linearLayoutManager
        adpater =
            CartAdapter(managmentCart.getListCart(), this, object : ChangeNumberItemsListener {
                override fun change() {
                    calculateCart()
                }

            })
        binding.rclCart.adapter = adpater
    }

    @SuppressLint("SetTextI18n")
    private fun calculateCart() {
        val percentTax = 0.02
        val delivery = 10

        tax = managmentCart.getTotalFee() * percentTax

         total = managmentCart.getTotalFee() + tax + delivery
         itemTotal = managmentCart.getTotalFee()

        binding.totalfeeText.text = "$${decimalFormat.format(itemTotal)}"
        binding.taxText.text = "$${decimalFormat.format(tax)}"
        binding.deliveryText.text = "$${decimalFormat.format(delivery)}"
        binding.totalText.text = "$${decimalFormat.format(total)}"

    }

    private fun initListener() {
        binding.apply {
            btnBack.setOnClickListener { finish() }

            btnOrder.setOnClickListener {
                if (managmentCart.getListCart().isNotEmpty()) {
                    if (mUser.phoneNumber.isEmpty() || mUser.address.isEmpty()) {
                        Toast.makeText(
                            this@CartActivity,
                            "Bạn cần thiết lập số điện thoại và địa chỉ mặc định",
                            Toast.LENGTH_LONG
                        ).show()
                        startActivity(Intent(this@CartActivity, ProfileActivity::class.java))
                    } else {
                        val intent = Intent(this@CartActivity, OrderActivity::class.java)
                        intent.putExtra("totalFeeText",itemTotal)
                        intent.putExtra("total",total)
                        intent.putExtra("tax",tax)
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(
                        this@CartActivity,
                        "Thêm sản phẩm để đặt hàng",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }


    }
}