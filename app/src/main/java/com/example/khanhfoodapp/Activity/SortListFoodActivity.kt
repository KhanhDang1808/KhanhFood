package com.example.khanhfoodapp.Activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.khanhfoodapp.Adapter.FoodListAdapter
import com.example.khanhfoodapp.Domain.Foods
import com.example.khanhfoodapp.Fragment.MainFragment.Companion.database
import com.example.khanhfoodapp.R
import com.example.khanhfoodapp.databinding.ActivityListFoodsBinding
import com.example.khanhfoodapp.databinding.ActivitySortListFoodBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class SortListFoodActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySortListFoodBinding
    private lateinit var adapter: RecyclerView.Adapter<*>
    private  var isSort:Int=-1
    private  var typeSort:Int=-1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySortListFoodBinding.inflate(layoutInflater)
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.BLACK
        }
        setContentView(binding.root)
        getIntentExtra()
        initList()
    }

    private fun initList() {
        val myRef = database.getReference("Foods")
        binding.progressBarFoodList.visibility= View.VISIBLE

        val listFood= ArrayList<Foods>()

        val query: Query = when (typeSort) {
            1 -> {
                myRef.orderByChild("PriceId").equalTo(isSort.toDouble())
            }
            2 -> {
                myRef.orderByChild("LocationId").equalTo(isSort.toDouble())
            }
            3 -> {
                myRef.orderByChild("TimeId").equalTo(isSort.toDouble())
            }
            else-> myRef
        }

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(issue in snapshot.children){
                        issue.getValue(Foods::class.java)?.let { listFood.add(it) }
                    }
                    if(listFood.size>0){
                        binding.apply {
                            foodListView.layoutManager= GridLayoutManager(this@SortListFoodActivity,2)
                            adapter = FoodListAdapter(listFood)
                            foodListView.adapter=adapter
                            textTotal.text = listFood.size.toString()
                        }
                        binding.progressBarFoodList.visibility= View.GONE
                    }
                }else{
                    binding.progressBarFoodList.visibility= View.GONE
                    Snackbar.make(binding.root,
                        "No product found",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("CANCEL"){
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

        isSort = intent.getIntExtra("isSort", -1)
        typeSort = intent.getIntExtra("Type_Sort", -1)



        binding.apply {
            textTitle.text = intent.getStringExtra("Name")
            btnBack.setOnClickListener{finish()}
        }
    }
}