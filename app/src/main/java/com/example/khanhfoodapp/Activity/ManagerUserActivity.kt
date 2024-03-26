package com.example.khanhfoodapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.khanhfoodapp.databinding.ActivityManagerUserBinding

class ManagerUserActivity : AppCompatActivity() {
    private lateinit var binding:ActivityManagerUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagerUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initListener()
    }

    private fun initListener() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}