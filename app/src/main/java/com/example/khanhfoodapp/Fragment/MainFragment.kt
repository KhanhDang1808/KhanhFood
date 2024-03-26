package com.example.khanhfoodapp.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.khanhfoodapp.Activity.CartActivity
import com.example.khanhfoodapp.Activity.ListFoodsActivity
import com.example.khanhfoodapp.Activity.LoginActivity
import com.example.khanhfoodapp.Activity.SortListFoodActivity
import com.example.khanhfoodapp.Adapter.BestFoodAdapter
import com.example.khanhfoodapp.Adapter.CategoryAdapter
import com.example.khanhfoodapp.Domain.Category
import com.example.khanhfoodapp.Domain.Foods
import com.example.khanhfoodapp.Domain.Location
import com.example.khanhfoodapp.Domain.Price
import com.example.khanhfoodapp.Domain.Time
import com.example.khanhfoodapp.R
import com.example.khanhfoodapp.databinding.FragmentMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private var numberLocation: Int = 0
    private var numberTime: Int = 0
    private var numberPrice: Int = 0

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

    private fun initLocation() {
        val myRef = database.getReference("Location")
        val list: ArrayList<String> = arrayListOf("Location")
        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
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
        val list = ArrayList<Category>()

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
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
        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            Firebase.auth.signOut()
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

            btnCart.setOnClickListener {
                startActivity(
                    Intent(
                        requireContext(),
                        CartActivity::class.java
                    )
                )
            }
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

                        val intent = Intent(requireContext(), SortListFoodActivity::class.java)
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

                        val intent = Intent(requireContext(), SortListFoodActivity::class.java)
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

                        val intent = Intent(requireContext(), SortListFoodActivity::class.java)
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
        binding.btnCategory.setOnClickListener {
        val intent =Intent(requireContext(),SortListFoodActivity::class.java)
            intent.putExtra("Name","Danh sách món ăn")
            startActivity(intent)
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


}