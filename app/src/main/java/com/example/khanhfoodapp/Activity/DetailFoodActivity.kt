package com.example.khanhfoodapp.Activity

import android.annotation.SuppressLint
import android.app.Notification
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.example.khanhfoodapp.Adapter.CartAdapter
import com.example.khanhfoodapp.Domain.Foods
import com.example.khanhfoodapp.Helper.ManagmentCart
import com.example.khanhfoodapp.R
import com.example.khanhfoodapp.databinding.ActivityDetailFoodBinding
import com.google.gson.Gson

class DetailFoodActivity : BaseActivity() {
    private lateinit var binding: ActivityDetailFoodBinding
    private lateinit var food: Foods
    private var num = 1
    private lateinit var managementCart:ManagmentCart
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.BLACK
        }

        getIntentExtra()
        setVariable()
    }

    @SuppressLint("SetTextI18n")
    private fun setVariable() {
        managementCart = ManagmentCart(this)
        binding.btnBack.setOnClickListener {
            (finish())
        }

        binding.apply {
            Glide.with(this@DetailFoodActivity)
                .load(food.ImagePath)
                .into(binding.picImg)

            textPrice.text = "$${food.Price}"
            textTitle.text = food.Title
            textDescription.text = food.Description
            textRate.text = "${food.Star} Rating"
            ratingBar.rating = food.Star.toFloat()
            textTime.text = food.TimeValue.toString()
            textTotalPrice.text = "${num * food.Price}$"
            plusBtn.setOnClickListener {
                num +=1
                textNumber.text = "$num "
                textTotalPrice.text = "$${num * food.Price} "
            }

            minusBtn.setOnClickListener {
               if(num>1){
                   num -=1
                   textNumber.text = "$num "
                   textTotalPrice.text = "$${num * food.Price} "
               }
            }

            addCartBtn.setOnClickListener {
                food.numberInCart = num
                managementCart.insertFood(food)
            }
        }

    }

    private fun getIntentExtra() {
        val json = intent.getStringExtra("food_json")
       // Convert JSON string back to Foods object
         food = Gson().fromJson(json, Foods::class.java)
    }
}