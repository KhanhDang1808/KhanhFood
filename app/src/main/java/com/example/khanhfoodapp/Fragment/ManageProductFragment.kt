package com.example.khanhfoodapp.Fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.khanhfoodapp.Activity.DetailFoodActivity
import com.example.khanhfoodapp.Adapter.FoodListAdapter
import com.example.khanhfoodapp.Domain.Foods
import com.example.khanhfoodapp.Fragment.MainFragment.Companion.database
import com.example.khanhfoodapp.databinding.FragmentManagerProductBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson

class ManageProductFragment : Fragment() {
    private lateinit var adapter: RecyclerView.Adapter<*>
    private lateinit var listFood: ArrayList<Foods>

    private lateinit var binding: FragmentManagerProductBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentManagerProductBinding.inflate(
            layoutInflater,
            container,
            false
        )
        listFood = ArrayList<Foods>()

        initListProduct()
        initListener()

        return binding.root
    }

    private fun initListener() {
        binding.btnAddFood.setOnClickListener {
            val newFood = Foods()
            val intent = Intent(requireContext(), DetailFoodActivity::class.java)
            // Convert Foods object to JSON string
            //  val idNewFood = listFood.size
            val id = listFood.size
            newFood.Id = listFood[id-1].Id + 1
            val foodJson: String = Gson().toJson(newFood)
            intent.putExtra("food_json", foodJson)
            intent.putExtra("isBoolean", true)
            startActivity(intent)
        }
    }

    private fun initListProduct() {
        val myRef = database.getReference("Foods")
        binding.progressBarBestFood.visibility = View.VISIBLE
        myRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    if (listFood.size > 0) listFood.clear()
                    for (issue in snapshot.children) {
                        issue.getValue(Foods::class.java)?.let { listFood.add(it) }
                    }
                    if (listFood.size > 0) {
                        binding.apply {
                            rclFoodList.layoutManager =
                                GridLayoutManager(requireContext(), 2)
                            adapter = FoodListAdapter(listFood)
                            rclFoodList.adapter = adapter
                            binding.textProTotal.text = listFood.size.toString()
                        }
                        binding.progressBarBestFood.visibility = View.GONE
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    binding.progressBarBestFood.visibility = View.GONE
                    Snackbar.make(
                        binding.root,
                        "No product found",
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .show()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    companion object {

    }
}