package com.example.khanhfoodapp.Adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.khanhfoodapp.Activity.ListFoodsActivity
import com.example.khanhfoodapp.Domain.Category
import com.example.khanhfoodapp.ItemClickListener
import com.example.khanhfoodapp.R
import com.example.khanhfoodapp.databinding.ViewholderCategoryBinding

class CategoryAdapter(private var item: ArrayList<Category>) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    private lateinit var binding: ViewholderCategoryBinding
    private  var itemClickListener: ItemClickListener? = null

    fun setItemClickListener(listener: ItemClickListener) {
        this.itemClickListener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ViewholderCategoryBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return ViewHolder(binding.root)
    }

    override fun getItemCount() = item.size

    @SuppressLint("DiscouragedApi")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = ViewholderCategoryBinding.bind(holder.itemView)
        binding.let {
            it.categoryNameTxt.text = item[position].Name

            when ((position % 7)) {
                0 -> it.imgCategory.setBackgroundResource(R.drawable.cat_0_background)
                1 -> it.imgCategory.setBackgroundResource(R.drawable.cat_1_background)
                2 -> it.imgCategory.setBackgroundResource(R.drawable.cat_2_background)
                3 -> it.imgCategory.setBackgroundResource(R.drawable.cat_3_background)
                4 -> it.imgCategory.setBackgroundResource(R.drawable.cat_4_background)
                5 -> it.imgCategory.setBackgroundResource(R.drawable.cat_5_background)
                6 -> it.imgCategory.setBackgroundResource(R.drawable.cat_6_background)
                7 -> it.imgCategory.setBackgroundResource(R.drawable.cat_7_background)
                else -> it.imgCategory.setBackgroundResource(R.drawable.cat_0_background)
            }
            val drawableResourceId: Int = it.root.resources.getIdentifier(
                item[position].ImagePath,
                "drawable",
                it.root.context.packageName
            )
            Glide.with(it.root.context)
                .load(drawableResourceId)
                .transform(CenterCrop(), RoundedCorners(30))
                .into(it.imgCategory)
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(binding.root.context, ListFoodsActivity::class.java)
            intent.putExtra("CategoryId", item[position].Id)
            intent.putExtra("Category", item[position].Name)
            binding.root.context.startActivity(intent)
        }

        holder.itemView.setOnLongClickListener {
            showPopupMenu(holder.itemView, position, binding)
            true
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showPopupMenu(view: View, position: Int, binding: ViewholderCategoryBinding) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.menuInflater.inflate(R.menu.edit_category_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.edit_menu -> {
                    itemClickListener?.onEdit(position)
                    true
                }

                R.id.delete_menu -> {
                    // Xử lý sự kiện khi chọn "Xóa"
                    itemClickListener?.onDelete(position)
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)


}