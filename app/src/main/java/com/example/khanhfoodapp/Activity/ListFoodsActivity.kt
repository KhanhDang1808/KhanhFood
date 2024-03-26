package com.example.khanhfoodapp.Activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.khanhfoodapp.Adapter.FoodListAdapter
import com.example.khanhfoodapp.Domain.Foods
import com.example.khanhfoodapp.Fragment.MainFragment.Companion.database
import com.example.khanhfoodapp.ItemClickListener
import com.example.khanhfoodapp.databinding.ActivityListFoodsBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class ListFoodsActivity : AppCompatActivity(), ItemClickListener {
    private lateinit var adapter: RecyclerView.Adapter<*>
    lateinit var binding: ActivityListFoodsBinding
    private var categoryId: Int = 0
    private lateinit var categoryName: String
    private lateinit var searchText: String
    private var isSearch: Boolean = false
    private var isSort: Int = -1
    private var typeSort: Int = -1
    private lateinit var listFood: ArrayList<Foods>
    private var isAllFood: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListFoodsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.BLACK
        }

        getIntentExtra()
        initList()

    }

    private fun initList() {

        binding.progressBarFoodList.visibility = View.VISIBLE

        listFood = ArrayList<Foods>()

        val query: Query = if (isSearch) {
            myRef.orderByChild("Title").startAt(searchText).endAt(searchText + '\uf8ff')
        } else if (isAllFood) {
            myRef.orderByChild("BestFood").equalTo(true)
        } else if (typeSort == 1) {
            myRef.orderByChild("PriceId").equalTo(isSort.toDouble())
        } else if (typeSort == 2) {
            myRef.orderByChild("LocationId").equalTo(isSort.toDouble())
        } else if (typeSort == 3) {
            myRef.orderByChild("TimeId").equalTo(isSort.toDouble())
        } else {
            myRef.orderByChild("CategoryId").equalTo(categoryId.toDouble())
        }

        query.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    listFood.clear()

                    for (issue in snapshot.children) {
                        issue.getValue(Foods::class.java)?.let { listFood.add(it) }
                    }
                    if (listFood.size > 0) {
                        binding.apply {
                            foodListView.layoutManager =
                                GridLayoutManager(this@ListFoodsActivity, 2)
                            adapter = FoodListAdapter(listFood)
                            foodListView.adapter = adapter
                            textTotal.text = listFood.size.toString()
                        }
                        binding.progressBarFoodList.visibility = View.GONE
                    }
                    (adapter as FoodListAdapter).setItemClickListener(this@ListFoodsActivity)
                    adapter.notifyDataSetChanged()
                } else {
                    listFood.clear()
                    binding.progressBarFoodList.visibility = View.GONE
                    Snackbar.make(
                        binding.root,
                        "No product found",
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

    private fun getIntentExtra() {
        //val intent:Intent = intent
        categoryId = intent.getIntExtra("CategoryId", 0)
        categoryName = intent.getStringExtra("Category").toString().trim()
        searchText = intent.getStringExtra("text").toString().trim()
        isSearch = intent.getBooleanExtra("isSearch", false)

        isSort = intent.getIntExtra("isSort", -1)
        typeSort = intent.getIntExtra("Type_Sort", -1)

        isAllFood = intent.getBooleanExtra("isAllFood", false)
        binding.apply {

            if(isSort != -1 && typeSort != -1){
                textTitle.text = intent.getStringExtra("Name")
            }else{
                textTitle.text = categoryName
            }
            btnBack.setOnClickListener { finish() }
        }
    }

    override fun onEdit(position: Int) {
        //
    }

    override fun onDelete(position: Int) {
        AlertDialog.Builder(binding.root.context)
            .setTitle("Xóa sản phẩm")
            .setMessage("Sản phẩm sẽ bị xóa khỏi KhanhFood. Bạn có chắc muốn xóa?")
            .setPositiveButton("Yes") { _, _ ->
                //  progressBar.visibility = View.VISIBLE
                myRef.child("${listFood[position].Id}").removeValue()

                Toast.makeText(
                    binding.root.context,
                    "Sản phẩm đã bị xóa khỏi KhanhFood",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    companion object {
        val myRef = database.getReference("Foods")

    }
}