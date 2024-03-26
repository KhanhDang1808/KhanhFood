package com.example.khanhfoodapp.Activity

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.khanhfoodapp.databinding.ActivityAddCategoryBinding
import com.example.khanhfoodapp.Adapter.CategoryAdapter
import com.example.khanhfoodapp.Domain.Category
import com.example.khanhfoodapp.Fragment.MainFragment.Companion.database
import com.example.khanhfoodapp.ItemClickListener
import com.example.khanhfoodapp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
// quản lý danh mục

class AddCategoryActivity : AppCompatActivity(), ItemClickListener {
    private lateinit var binding: ActivityAddCategoryBinding
    private var numberCate: Int = 0
    private val myRef = database.getReference("Category")
    private var list = ArrayList<Category>()
    private lateinit var dialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.spinnerType.visibility = View.GONE
        binding.editCategory.visibility = View.GONE
        initCategory()
        initSpinnerType()
        initListener()
    }

    private fun initSpinnerType() {
        val type = arrayOf("Đồ ăn", "Đồ uống")
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, type)
        adapter.let {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerType.adapter = it
        }
        binding.spinnerType.setSelection(0)
    }

    private fun initCategory() {
        binding.progressBarBestFood.visibility = View.VISIBLE

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (list.size > 0) list.clear()
                if (snapshot.exists()) {
                    for (issue in snapshot.children) {
                        issue.getValue(Category::class.java)?.let { list.add(it) }
                    }
                    numberCate = list.size
                    if (numberCate > 0) {

                        binding.apply {
                            rclCate.layoutManager = GridLayoutManager(
                                this@AddCategoryActivity,
                                4
                            )
                            //Thiết lập số cột
                            //  (rclCategory.layoutManager as GridLayoutManager).spanCount = 3
                            val adapter = CategoryAdapter(list)
                            adapter.setItemClickListener(this@AddCategoryActivity)
                            rclCate.adapter = adapter
                            binding.textProTotal.text = list.size.toString()
                        }
                    }
                    binding.progressBarBestFood.visibility = View.GONE
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun initListener() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnAddCategory.setOnClickListener {
            if (binding.editCategory.visibility == View.GONE) {
                binding.editCategory.visibility = View.VISIBLE
                binding.spinnerType.visibility = View.VISIBLE
            } else {
                if (binding.editCategory.text.toString().trim().isEmpty()) {
                    Toast.makeText(this, "Nhập tên danh mục", Toast.LENGTH_SHORT).show()
                } else {
                    val editText = binding.editCategory.text.toString().trim()
                    var typeCate: String = "btn_1"
                    if (binding.spinnerType.selectedItemPosition == 1) typeCate = "btn_7"
                    for (item in list) {
                        if (editText == item.Name) {
                            Toast.makeText(this, "Đã có Danh mục này!", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                    }
                    AlertDialog.Builder(this)
                        .setTitle("Thêm Danh mục")
                        .setMessage("Bạn có chắc muốn thêm Danh mục này?")
                        .setPositiveButton("Yes") { _, _ ->
                            //  progressBar.visibility = View.VISIBLE
                            val map = mapOf(
                                "Id" to numberCate,
                                "ImagePath" to typeCate,
                                "Name" to editText
                            )
                            myRef.child("$numberCate").setValue(map).addOnCompleteListener {
                                Toast.makeText(
                                    this,
                                    "Thêm Danh mục thành công!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.editCategory.setText("")
                                binding.editCategory.clearFocus()
                            }
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }
            }
        }
    }


    override fun onEdit(pos: Int) {
        // Xử lý sự kiện khi chọn "Sửa"
        dialog = Dialog(this)
        setDialog(dialog)


        dialog.findViewById<Spinner>(R.id.spinner_cate).setSelection(0)
        var imageCate = list[pos].ImagePath
        if (imageCate == "btn_7") {
            dialog.findViewById<Spinner>(R.id.spinner_cate).setSelection(1)
        }

        val editName = dialog.findViewById<EditText>(R.id.edit_name)
        editName.setText(list[pos].Name)
        val btnCancel = dialog.findViewById<Button>(R.id.btn_cancel)
        val btnSend = dialog.findViewById<Button>(R.id.btn_send)

        btnSend.setOnClickListener {
            val type = dialog.findViewById<Spinner>(R.id.spinner_cate).selectedItemPosition
            val editCate = editName.text.toString().trim()
            if (editCate.isEmpty()) {
                Toast.makeText(
                    binding.root.context,
                    "Nhập đầy đủ thông tin",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (imageCate == "btn_7" && type == 0) {
                imageCate = "btn_1"
            } else if (type == 1) {
                imageCate = "btn_7"
            }


            val map = mapOf(
                "ImagePath" to imageCate,
                "Name" to editCate
            )
            AlertDialog.Builder(this)
                .setTitle("Cập nhật Danh mục")
                .setMessage("Bạn có chắc lưu Cập nhật danh mục?")
                .setPositiveButton("Yes") { _, _ ->
                    //  progressBar.visibility = View.VISIBLE
                    database.getReference("Category").child("$pos").setValue(map)
                        .addOnCompleteListener {
                            Toast.makeText(
                                binding.root.context,
                                "Cập nhật Danh mục thành công!",
                                Toast.LENGTH_SHORT
                            ).show()
                            dialog.dismiss()
                        }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onDelete(position: Int) {
        val myRef = database.getReference("Foods")
        val query = myRef.orderByChild("CategoryId").equalTo(position.toDouble())
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Đọc dữ liệu từ dataSnapshot
                    // Lấy dữ liệu từ mỗi child node và xử lý
                    if (dataSnapshot.exists()) {
                        Toast.makeText(
                            this@AddCategoryActivity,
                            "Danh mục đang có sản phẩm. Không thể xóa.",
                            Toast.LENGTH_LONG
                        ).show()
                        return
                    }
                    AlertDialog.Builder(this@AddCategoryActivity)
                        .setTitle("Xóa Danh mục")
                        .setMessage("Bạn có chắc muốn xóa Danh mục này?")
                        .setPositiveButton("Yes") { _, _ ->
                            //  progressBar.visibility = View.VISIBLE
                            database.getReference("Category").child("$position").removeValue()
                                .addOnCompleteListener {
                                    Toast.makeText(
                                        binding.root.context,
                                        "Xóa Danh mục thành công!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Xử lý khi có lỗi xảy ra
            }
        })
    }

    private fun setDialog(dialog: Dialog) {
        dialog.let {
            it.requestWindowFeature(Window.FEATURE_NO_TITLE)
            it.setContentView(R.layout.layout_dialog_edit_category)
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

        val type = arrayOf("Đồ ăn", "Đồ uống")
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, type)
        adapter.let {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dialog.findViewById<Spinner>(com.example.khanhfoodapp.R.id.spinner_cate).adapter = it
        }
    }

}