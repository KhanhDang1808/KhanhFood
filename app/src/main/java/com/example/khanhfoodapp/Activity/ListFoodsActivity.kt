package com.example.khanhfoodapp.Activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.khanhfoodapp.Adapter.FoodListAdapter
import com.example.khanhfoodapp.Domain.Foods
import com.example.khanhfoodapp.databinding.ActivityListFoodsBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class ListFoodsActivity : BaseActivity() {
    private lateinit var adapter:RecyclerView.Adapter<*>
    lateinit var binding:ActivityListFoodsBinding
    private  var categoryId:Int=0
    private  var isSort:Int=-1
    private  var typeSort:Int=-1
    private lateinit var categoryName:String
    private lateinit var searchText:String
    private var isSearch:Boolean=false
    private var isAllFood:Boolean=false
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
        setVariable()
    }

    private fun setVariable() {

    }

    private fun initList() {
        val myRef = database.getReference("Foods")
        binding.progressBarFoodList.visibility=View.VISIBLE

        val listFood= ArrayList<Foods>()

        val query:Query =  if(isSearch){
            myRef.orderByChild("Title").startAt(searchText).endAt(searchText+'\uf8ff')
        }else if(isAllFood){
            myRef.orderByChild("BestFood").equalTo(true)
        }
        else{
            myRef.orderByChild("CategoryId").equalTo(categoryId.toDouble())
        }

        query.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(issue in snapshot.children){
                        issue.getValue(Foods::class.java)?.let { listFood.add(it) }
                    }
                    if(listFood.size>0){
                        binding.apply {
                            foodListView.layoutManager=GridLayoutManager(this@ListFoodsActivity,2)
                            adapter = FoodListAdapter(listFood)
                            foodListView.adapter=adapter
                            textTotal.text = listFood.size.toString()
                        }
                        binding.progressBarFoodList.visibility= View.GONE
                    }
                }else{
                    binding.progressBarFoodList.visibility=View.GONE
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
        //val intent:Intent = intent
        categoryId = intent.getIntExtra("CategoryId", 0)
        categoryName = intent.getStringExtra("Category").toString().trim()
        searchText = intent.getStringExtra("text").toString().trim()
        isSearch = intent.getBooleanExtra("isSearch", false)


        isAllFood = intent.getBooleanExtra("isAllFood", false)
        isSort = intent.getIntExtra("isSort", -1)
        typeSort = intent.getIntExtra("Type_Sort", -1)



        binding.apply {
            textTitle.text = categoryName.toString()
            btnBack.setOnClickListener{finish()}
        }
    }
}