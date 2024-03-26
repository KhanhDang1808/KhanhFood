package com.example.khanhfoodapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.khanhfoodapp.Fragment.ManageProductFragment
import com.example.khanhfoodapp.Fragment.ManagerUserFragment
import com.example.khanhfoodapp.R
import com.example.khanhfoodapp.databinding.ActivityAddUserProBinding

class AddUserProActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddUserProBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUserProBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val clickedColor = ContextCompat.getColor(this, R.color.color_back_ground)

        binding.btnBack.setOnClickListener{
            finish()
        }
        binding.cardCategory.setCardBackgroundColor(clickedColor)
        binding.cardCategory.setOnClickListener {
            // Cập nhật màu nền của CardView
            binding.cardProduct.setCardBackgroundColor(
                ContextCompat.getColor(this, R.color.grey)
            )
            (it as CardView).setCardBackgroundColor(clickedColor)

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView2,ManageProductFragment())
                .addToBackStack(null)
                .setReorderingAllowed(true)
                .commit()

        }
        binding.cardProduct.setOnClickListener {
            binding.cardCategory.setCardBackgroundColor(
                ContextCompat.getColor(this, R.color.grey)
            )
            (it as CardView).setCardBackgroundColor(clickedColor)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView2,ManagerUserFragment())
                .addToBackStack(null)
                .setReorderingAllowed(true)
                .commit()
        }
    }
}