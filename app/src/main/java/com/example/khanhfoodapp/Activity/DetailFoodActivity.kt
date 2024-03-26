package com.example.khanhfoodapp.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.khanhfoodapp.Activity.BaseActivity.Companion.MY_REQUEST_CODE
import com.example.khanhfoodapp.Domain.Category
import com.example.khanhfoodapp.Domain.Foods
import com.example.khanhfoodapp.Domain.Location
import com.example.khanhfoodapp.Fragment.MainFragment
import com.example.khanhfoodapp.Helper.ManagmentCart
import com.example.khanhfoodapp.R
import com.example.khanhfoodapp.databinding.ActivityDetailFoodBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import java.util.UUID

class DetailFoodActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailFoodBinding
    private lateinit var food: Foods
    private lateinit var mUri: Uri
    private var num = 1
    private var isBoolean: Boolean = false
    private lateinit var managementCart: ManagmentCart

    private var mActivityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Xử lý kết quả khi hoạt động thành công
                val data: Intent = result.data ?: return@registerForActivityResult
                // Xử lý dữ liệu từ Intent (nếu có)
                val uri = data.data!!
//                val inputStream = contentResolver.openInputStream(uri)
//                val bitmap = BitmapFactory.decodeStream(inputStream)
//                binding.picImg.setImageBitmap(bitmap)
                binding.apply {
                    Glide.with(this@DetailFoodActivity)
                        .load(uri)
                        .error(R.drawable.images)
                        .into(binding.picImg)
                }
                uploadImageToFirebase(uri)
//            } else {
                // Xử lý kết quả khi hoạt động không thành công hoặc bị hủy
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)


        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.BLACK
        }

        getIntentExtra()
        initCategory()
        initLocation()
        setVariable()
        initListener()
    }

    @SuppressLint("SetTextI18n")
    private fun setVariable() {
        managementCart = ManagmentCart(this)

        binding.apply {
            Glide.with(this@DetailFoodActivity)
                .load(food.ImagePath)
                .error(R.drawable.images)
                .into(binding.picImg)

            textPrice.setText("${food.Price}")
            textTitle.setText(food.Title)
            textDescription.setText(food.Description)
            textRate.setText("${food.Star}")
            textTime.setText("${food.TimeValue}")
            ratingBar.rating = food.Star.toFloat()
            checkBox.isChecked = food.BestFood
        }

    }

    private fun getIntentExtra() {
        val json = intent.getStringExtra("food_json")
        isBoolean = intent.getBooleanExtra("isBoolean", false)
        // Convert JSON string back to Foods object
        food = Gson().fromJson(json, Foods::class.java)
        mUri = Uri.parse(food.ImagePath)
    }

    private fun initLocation() {
        val myRef = MainFragment.database.getReference("Location")
        val list: ArrayList<Location> = ArrayList()
        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.exists()) {
                    for (issue in dataSnapshot.children) {
                        issue.getValue(Location::class.java)?.let { list.add(it) }
                    }
                    val adapter: ArrayAdapter<Location> =
                        ArrayAdapter(
                            this@DetailFoodActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            list
                        )
                    adapter.let {
                        it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spinnerLocation.adapter = it
                    }
                    binding.spinnerLocation.setSelection(food.LocationId)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })
    }

    private fun initCategory() {
        val myRef = MainFragment.database.getReference("Category")
        val list: ArrayList<String> = ArrayList()
        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.exists()) {
                    for (issue in dataSnapshot.children) {
                        issue.getValue(Category::class.java)?.let { list.add(it.Name) }
                    }
                    val adapter: ArrayAdapter<String> = ArrayAdapter(
                        this@DetailFoodActivity,
                        android.R.layout.simple_spinner_dropdown_item, list
                    )
                    adapter.let {
                        it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spinnerDanhmuc.adapter = it
                    }
                    binding.spinnerDanhmuc.setSelection(food.CategoryId)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })
    }

    private fun initListener() {
        binding.btnBack.setOnClickListener {
            (finish())
        }
        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Xóa sản phẩm")
                .setMessage("Sản phẩm sẽ bị xóa khỏi KhanhFood. Bạn có chắc muốn xóa?")
                .setPositiveButton("Yes") { _, _ ->
                    //  progressBar.visibility = View.VISIBLE
                    val my = MainFragment.database.getReference("Foods")
                    my.child("${food.Id}").removeValue().addOnCompleteListener {
                        Toast.makeText(
                            this,
                            "Sản phẩm đã bị xóa khỏi KhanhFood",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        finish()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
        binding.picImg.setOnClickListener {
            onClickRequestPermission()
        }

        binding.btnSave.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Cập nhật Thông tin")
                .setMessage("Thông tin Sản phẩm sẽ Cập nhật. Bạn có chắc muốn Thay đổi?")
                .setPositiveButton("Yes") { _, _ ->
                    setProductInformation()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun setProductInformation() {
        if (validate()) {
            try {
                val time = binding.textTime.text.toString().trim().toInt()
                //var price = binding.textPrice.text.toString().trim().toDouble()
                var timeId = 1
                if (time > 30) timeId = 2
                else if (time < 10) timeId = 0

                var priceId = 1
                if (binding.textPrice.text.toString().trim().toDouble() > 30.0) {
                    priceId = 2
                } else if (binding.textPrice.text.toString().trim().toDouble() <= 10) priceId = 0

                val map = mutableMapOf(
                    "Id" to food.Id,
                    "BestFood" to binding.checkBox.isChecked,
                    "CategoryId" to binding.spinnerDanhmuc.selectedItemPosition,
                    "Description" to binding.textDescription.text.toString(),
                    "Price" to binding.textPrice.text.toString().trim().toDouble(),
                    "PriceId" to priceId,
                    "Star" to binding.textRate.text.toString().trim().toDouble(),
                    "TimeId" to timeId,
                    "TimeValue" to time,
                    "LocationId" to binding.spinnerLocation.selectedItemPosition,
                    "Title" to binding.textTitle.text.toString()
                    //Thêm các cặp key-value cần thiết
                )
                val my = MainFragment.database.getReference("Foods")
                my.child("${food.Id}").let {
                    if (isBoolean) {
                       // it.child("ImagePath").setValue(mUri.toString())
                        map["ImagePath"] = mUri.toString()
                        it.setValue(map)
                    } else {
                        it.updateChildren(map as Map<String, Any>)
                    }
                }.addOnCompleteListener {
                    finish()
                    Toast.makeText(
                        this,
                        "Cập nhật sản phẩm thành công",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                //Log.e("khanh", e.printStackTrace().toString())
            }
        }

    }

    private fun onClickRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                openGallery()
            } else {
                val permissions = arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
                ActivityCompat.requestPermissions(this, permissions, MY_REQUEST_CODE)
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                openGallery()
            } else {
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(this, permissions, MY_REQUEST_CODE)
            }

        }
    }

    // Hàm để tải ảnh lên Firebase Storage
    private fun uploadImageToFirebase(imageUri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imagesRef = storageRef.child("${UUID.randomUUID()}")

        val uploadTask = imagesRef.putFile(imageUri)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            // Tải ảnh lên thành công, lấy URL của ảnh
            taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                mUri = uri
                if (!isBoolean) {
                    val map = mapOf("ImagePath" to uri.toString())
                    val my = MainFragment.database.getReference("Foods")
                    my.child("${food.Id}").updateChildren(map)
                }
                // Sử dụng URL để làm gì đó, ví dụ: lưu vào cơ sở dữ liệu Firebase Realtime Database
            }.addOnFailureListener { exception ->
                // Xử lý khi không thể nhận URL của ảnh
            }
        }.addOnFailureListener {
            // Xử lý khi có lỗi xảy ra trong quá trình tải lên
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"))
    }

    private fun isDouble(input: String): Boolean {
        return try {
            input.trim().toDouble() // Chuyển đổi chuỗi thành số float
            true // Nếu không có lỗi, chuỗi đó là số float hợp lệ
        } catch (e: NumberFormatException) {
            false // Nếu có lỗi khi chuyển đổi, chuỗi đó không phải là số float hợp lệ
        }
    }

    private fun validate(): Boolean {
        if (binding.textTitle.text.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập Tên", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.textPrice.text.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập Giá", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.textDescription.text.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mô tả", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.textTime.text.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập thời gian", Toast.LENGTH_SHORT).show()
            return false
        }
        if (mUri.toString().trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng thêm ảnh", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.textPrice.text.toString().toDouble() == 0.0) {
            Toast.makeText(this, "Vui lòng nhập giá", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.textTime.text.toString().toDouble() == 0.0) {
            Toast.makeText(this, "Vui lòng nhập giá", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!isDouble(binding.textPrice.text.toString()) || !isDouble(binding.textTime.text.toString()) || !isDouble(
                binding.textRate.text.toString()
            )
        ) {
            Toast.makeText(this, "Giá, thời gian và đánh giá là kiểu số", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

}