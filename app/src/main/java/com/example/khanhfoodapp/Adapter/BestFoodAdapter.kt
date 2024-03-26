package com.example.khanhfoodapp.Adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.khanhfoodapp.Activity.DetailFoodActivity
import com.example.khanhfoodapp.Domain.Foods
import com.example.khanhfoodapp.databinding.ViewHolderBestFoodBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson

class BestFoodAdapter(private var item:ArrayList<Foods>): RecyclerView.Adapter<BestFoodAdapter.ViewHolder>() {
    private lateinit var binding: ViewHolderBestFoodBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       binding = ViewHolderBestFoodBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder()
    }
    override fun getItemCount() = item.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = ViewHolderBestFoodBinding.bind(holder.itemView)

        binding.apply {
            textTitle.text = item[position].Title.trim()
            textTime.text = item[position].TimeValue.toString()
            textStar.text = item[position].Star.toString()
            textPrice.text="$+${item[position].Price}"
        }


        Glide.with(binding.root.context)
            .load(item[position].ImagePath)
            .transform(CenterCrop(), RoundedCorners(30))
            .into(binding.imgPic)

        holder.itemView.setOnClickListener{
            val intent = Intent(binding.root.context, DetailFoodActivity::class.java)
            // Convert Foods object to JSON string
            val foodJson:String = Gson().toJson(item[position])
            intent.putExtra("food_json",foodJson)
            binding.root.context.startActivity(intent)
        }
    }
     inner class ViewHolder : RecyclerView.ViewHolder(binding.root) 
}