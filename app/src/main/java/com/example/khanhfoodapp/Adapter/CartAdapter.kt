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
import com.example.khanhfoodapp.Helper.ChangeNumberItemsListener
import com.example.khanhfoodapp.Helper.ManagmentCart
import com.example.khanhfoodapp.databinding.ViewholderItemCartBinding
import com.google.gson.Gson
import java.text.DecimalFormat

class CartAdapter(
    private var list: ArrayList<Foods>,
    private val context: Context,
    private var changeNumberItemsListener: ChangeNumberItemsListener
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    private lateinit var binding: ViewholderItemCartBinding
    private var managmentCart = ManagmentCart(context)
 //   private lateinit var onItemClickListener:OnItemClickListener

//    fun getTotalProduct(onItemClickListener:OnItemClickListener){
//        this.onItemClickListener=onItemClickListener
//    }
//    interface OnItemClickListener {
//        fun getTotal(total:Int)
//    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartAdapter.ViewHolder {
        binding = ViewholderItemCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder()
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: CartAdapter.ViewHolder, position: Int) {
        val binding = ViewholderItemCartBinding.bind(holder.itemView)
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
                .into(imagePic)

            plusCartItem.setOnClickListener {
                managmentCart.plusNumberItem(list, position, object : ChangeNumberItemsListener {
                    override fun change() {
                        notifyDataSetChanged()
                        changeNumberItemsListener.change()
                    }

                })
            }

            minusCartItem.setOnClickListener {
                managmentCart.minusNumberItem(list, position, object : ChangeNumberItemsListener {
                    override fun change() {
                        notifyDataSetChanged()
                        changeNumberItemsListener.change()
                    }

                })
            }
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