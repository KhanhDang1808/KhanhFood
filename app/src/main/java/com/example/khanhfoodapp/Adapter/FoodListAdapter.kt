package com.example.khanhfoodapp.Adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.khanhfoodapp.Activity.DetailFoodActivity
import com.example.khanhfoodapp.Domain.Foods
import com.example.khanhfoodapp.ItemClickListener
import com.example.khanhfoodapp.databinding.ViewholderListFoodBinding
import com.google.gson.Gson

class FoodListAdapter(private val listFood:ArrayList<Foods>):RecyclerView.Adapter<FoodListAdapter.ViewHolder>() {
    private lateinit var  binding:ViewholderListFoodBinding
    private  var itemClickListener: ItemClickListener? = null


    fun setItemClickListener(listener: ItemClickListener) {
        this.itemClickListener = listener
    }
     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
         binding = ViewholderListFoodBinding.inflate(LayoutInflater.from(parent.context),parent,false)
         return ViewHolder()
     }

     override fun getItemCount()=listFood.size
     @SuppressLint("NotifyDataSetChanged")
     override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
         val binding = ViewholderListFoodBinding.bind(holder.itemView)
         binding.apply {
             titleText.text = listFood[position].Title
             timeText.text = listFood[position].TimeValue.toString()
             priceText.text = listFood[position].Price.toString()
             rateStarText.text = listFood[position].Star.toString()


             Glide.with(root.context)
                 .load(listFood[position].ImagePath)
                 .into(foodImage)
         }
         holder.itemView.setOnClickListener{
             val intent = Intent(binding.root.context,DetailFoodActivity::class.java)
             // Convert Foods object to JSON string
             val foodJson:String = Gson().toJson(listFood[position])
             intent.putExtra("food_json",foodJson)
             binding.root.context.startActivity(intent)
         }
         binding.imgDelete.setOnClickListener{
             itemClickListener?.onDelete(position)
           //  notifyItemRemoved(position)
         }
     }

    inner class ViewHolder:RecyclerView.ViewHolder(binding.root)

 }