package com.example.khanhfoodapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.khanhfoodapp.Domain.Bill
import com.example.khanhfoodapp.R
import com.example.khanhfoodapp.databinding.ViewholderCategoryBinding
import com.example.khanhfoodapp.databinding.ViewholderListOrderBinding

class OrderListAdapter(private var listOrder: ArrayList<Bill>) :
    RecyclerView.Adapter<OrderListAdapter.ViewHolder>() {

    private lateinit var binding: ViewholderListOrderBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderListAdapter.ViewHolder {
         binding =
            ViewholderListOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = ViewholderListOrderBinding.bind(holder.itemView)

        binding.apply {
            textNamePhone.text = listOrder[position].namePhone
            textTotal.text = listOrder[position].totalPrice
            textTotalNumber.text = listOrder[position].totalQtt.toString()
            textTime.text = listOrder[position].billDate
            textDelivery.text = listOrder[position].status
            textAddress.text = listOrder[position].billAddress

            Glide.with(binding.root.context)
                .load(listOrder[position].billFoods[0].ImagePath)
                .error(R.drawable.images)
                .into(imagePic)
        }
    }

    override fun getItemCount() = listOrder.size
   inner class ViewHolder : RecyclerView.ViewHolder(binding.root)
}