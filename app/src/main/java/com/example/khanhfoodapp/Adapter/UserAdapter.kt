package com.example.khanhfoodapp.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.khanhfoodapp.Activity.DetailUserActivity
import com.example.khanhfoodapp.Domain.User
import com.example.khanhfoodapp.R
import com.example.khanhfoodapp.databinding.ViewholderItemUserBinding
import com.google.gson.Gson

class UserAdapter(
    private var listUser: ArrayList<User>
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private lateinit var binding: ViewholderItemUserBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
        binding = ViewholderItemUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder()
    }


    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        val binding = ViewholderItemUserBinding.bind(holder.itemView)
        binding.apply {
            textEmail.text = listUser[position].email
            textNumber.text = listUser[position].phoneNumber
            textName.text = listUser[position].name
        }
        Glide.with(binding.root.context)
            .load(listUser[position].image)
            .error(R.drawable.ic_circle_image_default)
            .into(binding.imageUser)

        holder.itemView.setOnClickListener {
            val intent = Intent(binding.root.context, DetailUserActivity::class.java)
            // Convert Foods object to JSON string
            val foodJson:String = Gson().toJson(listUser[position])
            intent.putExtra("user_json",foodJson)
            binding.root.context.startActivity(intent)
        }
    }

    override fun getItemCount() = listUser.size

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root)

}