package com.example.khanhfoodapp.Activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.khanhfoodapp.Adapter.OrderAdapter
import com.example.khanhfoodapp.Domain.Bill
import com.example.khanhfoodapp.Domain.Foods
import com.example.khanhfoodapp.Fragment.MainFragment.Companion.database
import com.example.khanhfoodapp.Fragment.MainFragment.Companion.mAuth
import com.example.khanhfoodapp.Helper.ChangeNumberItemsListener
import com.example.khanhfoodapp.Helper.ManagmentCart
import com.example.khanhfoodapp.MySingleton.mUser
import com.example.khanhfoodapp.R
import com.example.khanhfoodapp.databinding.ActivityOrderBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.jar.Attributes.Name

class OrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderBinding
    private lateinit var adpater: RecyclerView.Adapter<*>
    private lateinit var managmentCart: ManagmentCart
    private lateinit var dialog: Dialog
    private lateinit var listFoods: ArrayList<Foods>
    private lateinit var billID :String
    private lateinit var bill :Bill
    private  var totalProductCart :Int =0
    private val decimalFormat = DecimalFormat("#.##")
    //
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        billID = mAuth.currentUser?.uid.toString()

        managmentCart = ManagmentCart(this)
        calculateCart()
        initList()
        initListener()
        initBill()
    }

    private fun initBill() {

        myRef.child("${mAuth.currentUser?.uid}")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    billID = "$billID${snapshot.childrenCount}"
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun initList() {
        listFoods =managmentCart.getListCart()
        val linearLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rclCart.layoutManager = linearLayoutManager
        adpater =
            OrderAdapter(
                listFoods,
                this)
        binding.rclCart.adapter = adpater
        for(item in listFoods){
            totalProductCart+=item.numberInCart
        }

    }

    @SuppressLint("SetTextI18n")
    private fun calculateCart() {
        val delivery = 10

        val total = intent.getDoubleExtra("total", 0.0)
        val totalFeeText = intent.getDoubleExtra("totalFeeText", 0.0)
        val tax = intent.getDoubleExtra("tax", 0.0)

        binding.apply {
            totalfeeText.text = "$${decimalFormat.format(totalFeeText)}"
            taxText.text = "$${decimalFormat.format(tax)}"
            deliveryText.text = "$${decimalFormat.format(delivery)}"
            totalText.text = "$${decimalFormat.format(total)}"
            txtTotalOrder.text = "$${decimalFormat.format(total)}"

            textNamePhone.text =
                "${mAuth.currentUser?.displayName.toString()} | ${mUser.phoneNumber} "
            textAddress.text = "${mUser.address} "
        }
    }

    private fun initListener() {
        binding.apply {
            btnBack.setOnClickListener { finish() }

            layoutAddressOrder.setOnClickListener {
                openEditDialog()
            }

            btnBuy.setOnClickListener {
                confirmBuy()
            }
            // Khai báo một mảng chuỗi chứa các mục cho Spinner
            val items = arrayOf(
                "Chọn phương thức thanh toán",
                "Thanh toan khi nhận hàng",
                "Thanh toán MoMo"
            )
            // Khởi tạo ArrayAdapter
            val adapter =
                ArrayAdapter(
                    this@OrderActivity,
                    android.R.layout.simple_spinner_item,
                    items
                )
            // Đặt layout cho dropdown list
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Gắn dữ liệu vào Spinner
            binding.spinnerShip.adapter = adapter
            // Đặt mục mặc định cho Spinner (ví dụ: mục thứ 2 trong danh sách)
            binding.spinnerShip.setSelection(0) // Note: Vị trí bắt đầu tính từ 0

        }
    }

    private fun confirmBuy() {
       if (!setBill()){
          return
       }
        AlertDialog.Builder(this)
            .setTitle("Đặt hàng")
            .setMessage("Đơn hàng sẽ đươc giao tới bạn ngay bây giờ. hãy chú ý điện thoại để nhận hàng.")
            .setPositiveButton("Ok") { _, _ ->
                val intent = Intent(this,BillActivity::class.java)
                intent.putExtra("Name_phone",bill.namePhone)
                intent.putExtra("Address",bill.billAddress)
                intent.putExtra("BillID",bill.billID)
                intent.putExtra("Total",bill.totalPrice)
                intent.putExtra("Total_Qtt",totalProductCart)
                intent.putExtra("Method",bill.methods)
                // Convert Foods object to JSON string
                val billJson:String = Gson().toJson(bill)
                intent.putExtra("bill_json",billJson)
                myRef.child(billID).setValue(bill).addOnCompleteListener {
                    managmentCart.tinyDB.putListObject("CartList", ArrayList<Foods>())
                    finish()
                    startActivity(intent)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setBill():Boolean {
        val ship = binding.spinnerShip.selectedItemPosition
        if(ship == 0){
            Toast.makeText(this,"Chọn phương thức thanh toán",Toast.LENGTH_SHORT).show()
            return false
        }
         bill = Bill()
        val currentTime = Calendar.getInstance().time
        // Định dạng thời gian
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedTime = sdf.format(currentTime)
        bill.billAddress = binding.textAddress.text.toString()
        bill.billDate = formattedTime.toString()
        bill.billID = billID
        bill.namePhone = binding.textNamePhone.text.toString()
        bill.note = binding.textNode.text.toString()
        bill.totalPrice =  binding.txtTotalOrder.text.toString()
        bill.totalQtt = totalProductCart
        bill.status =  "Chờ xác nhận"
        bill.billFoods =  listFoods
        bill.userId = mAuth.currentUser?.uid.toString()
        bill.methods = binding.spinnerShip.selectedItem.toString()
        return true
    }

    @SuppressLint("SetTextI18n")
    private fun openEditDialog() {
        dialog = Dialog(this)
        setDialog(dialog)

        val editPhone = dialog.findViewById<EditText>(R.id.edit_phone)
        editPhone.setText(mUser.phoneNumber)
        val editAddress = dialog.findViewById<EditText>(R.id.edit_address)
        editAddress.setText(mUser.address)
        val btnCancel = dialog.findViewById<Button>(R.id.btn_cancel)
        val btnSend = dialog.findViewById<Button>(R.id.btn_send)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSend.setOnClickListener {
            val textEditName = editPhone.text.toString().trim()
            val textEditAddress = editAddress.text.toString().trim()

            if (textEditAddress.isEmpty() || textEditName.isEmpty()) {
                Toast.makeText(this, "Nhập đầy đủ thông tin", Toast.LENGTH_LONG).show()
            } else {

                binding.textNamePhone.text =
                    "${mAuth.currentUser?.displayName.toString()} | $textEditName"
                binding.textAddress.text = textEditAddress
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setDialog(dialog: Dialog) {
        dialog.let {
            it.requestWindowFeature(Window.FEATURE_NO_TITLE)
            it.setContentView(R.layout.layout_dialog_address_phone_order)
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
    }

    companion object{
        val myRef = database.getReference("Bill")
    }
}