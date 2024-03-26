package com.example.khanhfoodapp.Fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.khanhfoodapp.Activity.AddUserProActivity
import com.example.khanhfoodapp.Activity.AddCategoryActivity
import com.example.khanhfoodapp.Activity.AddLocationActivity
import com.example.khanhfoodapp.Activity.AdminActivity
import com.example.khanhfoodapp.Activity.ListFoodsActivity
import com.example.khanhfoodapp.Adapter.BestFoodAdapter
import com.example.khanhfoodapp.Adapter.CategoryAdapter
import com.example.khanhfoodapp.Domain.Category
import com.example.khanhfoodapp.Domain.Foods
import com.example.khanhfoodapp.Domain.Location
import com.example.khanhfoodapp.Domain.Price
import com.example.khanhfoodapp.Domain.Time
import com.example.khanhfoodapp.ItemClickListener
import com.example.khanhfoodapp.R
import com.example.khanhfoodapp.databinding.FragmentMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainFragment : Fragment(), ItemClickListener {
    private lateinit var dialog: Dialog
    private lateinit var  list: ArrayList<Category>
    private var numberLocation: Int = 0
    private var numberTime: Int = 0
    private var numberPrice: Int = 0
    private lateinit var binding: FragmentMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(layoutInflater, container, false)

        binding.txtUser.text = mAuth.currentUser?.displayName ?: "KhanhFood"
        initLocation()
        initTime()
        initPrice()
        initBestFood()
        initCategory()
        initListener()


        return binding.root
    }

    override fun onStart() {
        super.onStart()
        initBestFood()
        initCategory()
    }

    private fun initLocation() {
        val myRef = database.getReference("Location")
        val list: ArrayList<String> = arrayListOf("Location")
        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.\
                list.clear()
                list.add("Location")
                if (dataSnapshot.exists()) {
                    for (issue in dataSnapshot.children) {
                        issue.getValue(Location::class.java)?.let { list.add(it.loc) }
                    }
                    val adapter: ArrayAdapter<String> =
                        ArrayAdapter(requireContext(), R.layout.sp_item, list)
                    adapter.let {
                        it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spinnerLocation.adapter = it
                    }
                    numberLocation = list.size
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })
    }

    companion object {
        val database = FirebaseDatabase.getInstance()
        val mAuth = FirebaseAuth.getInstance()
    }

    private fun initTime() {
        val myRef = database.getReference("Time")
        val list: ArrayList<String> = arrayListOf("Time")
        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.exists()) {
                    for (issue in dataSnapshot.children) {
                        issue.getValue(Time::class.java)?.let { list.add(it.Value) }
                    }
                    val adapter: ArrayAdapter<String> = ArrayAdapter(
                        requireContext(),
                        R.layout.sp_item, list
                    )
                    adapter.let {
                        it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spinnerTime.adapter = it
                    }
                    numberTime = list.size
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })
    }


    private fun initPrice() {
        val myRef = database.getReference("Price")
        val list: ArrayList<String> = arrayListOf("Price")
        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.exists()) {
                    for (issue in dataSnapshot.children) {
                        issue.getValue(Price::class.java)?.let { list.add(it.Value) }
                    }
                    val adapter: ArrayAdapter<String> = ArrayAdapter(
                        requireContext(),
                        R.layout.sp_item, list
                    )
                    adapter.let {
                        it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spinnerPrice.adapter = it
                    }
                    numberPrice = list.size
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })
    }


    private fun initBestFood() {
        binding.textAllBest.setOnClickListener {
            val intent = Intent(requireContext(), ListFoodsActivity::class.java)
            intent.putExtra("isAllFood", true)
            intent.putExtra("Category", "Best Food")
            startActivity(intent)
        }
        val myRef = database.getReference("Foods")
        binding.progressBarBestFood.visibility = View.VISIBLE
        val list = ArrayList<Foods>()
        val query = myRef.orderByChild("BestFood").equalTo(true)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    list.clear()
                    for (issue in snapshot.children) {
                        issue.getValue(Foods::class.java)?.let { list.add(it) }
                    }
                    if (list.size > 0) {
                        binding.apply {
                            rclBestFood.layoutManager = LinearLayoutManager(
                                requireContext(),
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                            val adapter = BestFoodAdapter(list)
                            rclBestFood.adapter = adapter
                        }
                    }
                    binding.progressBarBestFood.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }


        })
    }

    private fun initCategory() {
        val myRef = database.getReference("Category")
        binding.proressBarCategory.visibility = View.VISIBLE
        list = ArrayList<Category>()

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                if (snapshot.exists()) {
                    for (issue in snapshot.children) {
                        issue.getValue(Category::class.java)?.let { list.add(it) }
                    }
                    if (list.size > 0) {
                        binding.apply {
                            rclCategory.layoutManager = GridLayoutManager(
                                requireContext(),
                                4
                            )
                            //Thiết lập số cột
                            //  (rclCategory.layoutManager as GridLayoutManager).spanCount = 3
                            val adapter = CategoryAdapter(list)
                            adapter.setItemClickListener(this@MainFragment)
                            rclCategory.adapter = adapter
                        }
                    }
                    binding.proressBarCategory.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun initListener() {
            binding.btnBack.setOnClickListener {
                val intent = Intent(requireContext(), AdminActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }

        binding.apply {
            btnSearch.setOnClickListener {
                performSearch()
            }

            editTextText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch()
                    true
                } else {
                    false
                }
            }
            btnCategory.setOnClickListener {
                startActivity(Intent(requireContext(), AddUserProActivity::class.java))
            }

        }
        binding.textCategory.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    AddCategoryActivity::class.java
                )
            )
        }
        binding.btnLocation.setOnClickListener {
            startActivity(Intent(requireContext(), AddLocationActivity::class.java))
        }
        binding.spinnerPrice.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Lấy mục được chọn từ Spinner
                var number = 0
                do {
                    if ((position-1) == number) {

                        val intent = Intent(requireContext(), ListFoodsActivity::class.java)
                        intent.putExtra("isSort", number)
                        intent.putExtra("Name",binding.spinnerPrice.selectedItem.toString())
                        intent.putExtra("Type_Sort", 1)
                        startActivity(intent)
                        binding.spinnerPrice.setSelection(0)
                        break
                    }
                    number++
                } while (number < numberPrice)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        binding.spinnerLocation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Lấy mục được chọn từ Spinner
                var number = 0
                do {
                    if ((position-1) == number) {

                        val intent = Intent(requireContext(), ListFoodsActivity::class.java)
                        intent.putExtra("isSort", number)
                        intent.putExtra("Type_Sort", 2)
                        intent.putExtra("Name",binding.spinnerLocation.selectedItem.toString())
                        startActivity(intent)
                        binding.spinnerLocation.setSelection(0)
                        break
                    }
                    number++
                } while (number < numberLocation)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        binding.spinnerTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Lấy mục được chọn từ Spinner
                var number = 0
                do {
                    if ((position-1) == number) {

                        val intent = Intent(requireContext(), ListFoodsActivity::class.java)
                        intent.putExtra("isSort", number)
                        intent.putExtra("Name",binding.spinnerTime.selectedItem.toString())
                        intent.putExtra("Type_Sort", 3)
                        startActivity(intent)
                        binding.spinnerTime.setSelection(0)
                        break
                    }
                    number++
                } while (number < numberTime)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

    }

    private fun performSearch() {
        val searchTxt = binding.editTextText.text.toString().trim()
        binding.editTextText.setText("")
        binding.editTextText.clearFocus()
        if (searchTxt.isNotEmpty()) {
            val intent = Intent(requireContext(), ListFoodsActivity::class.java)
            intent.putExtra("Category", searchTxt)
            intent.putExtra("text", searchTxt)
            intent.putExtra("isSearch", true)
            startActivity(intent)
        }
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
                        requireContext(),
                        "Danh mục đang có sản phẩm. Không thể xóa.",
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }
                AlertDialog.Builder(requireContext())
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


    override fun onEdit(position: Int) {
        // Xử lý sự kiện khi chọn "Sửa"
        dialog = Dialog(requireContext())
        setDialog(dialog)


        dialog.findViewById<Spinner>(R.id.spinner_cate).setSelection(0)
        var imageCate = list[position].ImagePath
        if (imageCate == "btn_7") {
            dialog.findViewById<Spinner>(R.id.spinner_cate).setSelection(1)
        }

        val editName = dialog.findViewById<EditText>(R.id.edit_name)
        editName.setText(list[position].Name)
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
            AlertDialog.Builder(requireContext())
                .setTitle("Cập nhật Danh mục")
                .setMessage("Bạn có chắc lưu Cập nhật danh mục?")
                .setPositiveButton("Yes") { _, _ ->
                    //  progressBar.visibility = View.VISIBLE
                    database.getReference("Category").child("$position").setValue(map)
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
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, type)
        adapter.let {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dialog.findViewById<Spinner>(com.example.khanhfoodapp.R.id.spinner_cate).adapter = it
        }
    }


}