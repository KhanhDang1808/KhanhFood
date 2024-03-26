package com.example.khanhfoodapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.khanhfoodapp.Domain.Location
import com.example.khanhfoodapp.Fragment.MainFragment
import com.example.khanhfoodapp.ItemClickListener
import com.example.khanhfoodapp.databinding.ViewholderItemLocationBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class LocationAdapter(private val listLocation: ArrayList<Location>) :
    RecyclerView.Adapter<LocationAdapter.ViewHolder>() {
    private lateinit var binding: ViewholderItemLocationBinding
    private lateinit var itemClickListener: ItemClickListener


    fun onItemClickListener(itemClickListener: ItemClickListener){
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationAdapter.ViewHolder {
        binding = ViewholderItemLocationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )


        return ViewHolder()
    }

    override fun onBindViewHolder(holder: LocationAdapter.ViewHolder, position: Int) {
        val binding = ViewholderItemLocationBinding.bind(holder.itemView)

        binding.apply {
            textLocation.text = listLocation[position].loc
            getNumberFood(listLocation[position].Id, binding.textTotal)
            // textTotal.text = getNumberFood(listLocation[position].Id).toString()

            binding.btnDelete.setOnClickListener {
               itemClickListener.onDelete(listLocation[position].Id)

            }
            binding.btnEdit.setOnClickListener {
                itemClickListener.onEdit(listLocation[position].Id)
            }
        }
    }

    override fun getItemCount() = listLocation.size

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root)

    private fun getNumberFood(id: Int, text: TextView) {
        var number: Long = 0
        val myRef = MainFragment.database.getReference("Foods")
        val query = myRef.orderByChild("LocationId").equalTo(id.toDouble())
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                text.text = snapshot.childrenCount.toString()
                // Thực hiện các hành động cần thiết với giá trị mới của number tại đây
                // Ví dụ:
                // hoặc update giao diện người dùng với giá trị mới của number
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu
            }
        })
    }
}