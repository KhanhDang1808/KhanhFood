package com.example.khanhfoodapp.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.khanhfoodapp.Activity.DetailFoodActivity
import com.example.khanhfoodapp.Domain.Foods
import com.example.khanhfoodapp.Helper.ManagmentCart
import com.example.khanhfoodapp.databinding.ViewHolderOrderBinding
import com.google.gson.Gson
import java.text.DecimalFormat

class OrderAdapter(
    private var list: ArrayList<Foods>,
    private val context: Context,
) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    private lateinit var binding: ViewHolderOrderBinding
    private var managmentCart = ManagmentCart(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderAdapter.ViewHolder {
        binding = ViewHolderOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder()
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: OrderAdapter.ViewHolder, position: Int) {
        val binding = ViewHolderOrderBinding.bind(holder.itemView)
        binding.apply {
            val decimalFormat = DecimalFormat("#.##")
            textTitle.text = list[position].Title
            textFeeEach.text = "$${decimalFormat.format(
                list[position].numberInCart *list[position].Price
            )}"
            textTotalItem.text =
                "${list[position].numberInCart}" +
                        " * $" +
                        "${list[position].Price}"
            textNumberItem.text = (list[position].numberInCart).toString()

            Glide.with(binding.root.context)
                .load(list[position].ImagePath)
                .transform(CenterCrop(), RoundedCorners(30))
                .into(binding.imagePic)

        }

        holder.itemView.setOnClickListener{
            val intent = Intent(binding.root.context, DetailFoodActivity::class.java)
            // Convert Foods object to JSON string
            val foodJson:String = Gson().toJson(list[position])
            intent.putExtra("food_json",foodJson)
            binding.root.context.startActivity(intent)

        }
    }

    override fun getItemCount() = list.size

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root)

}