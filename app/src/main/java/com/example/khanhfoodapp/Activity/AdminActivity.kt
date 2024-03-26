package com.example.khanhfoodapp.Activity

import android.content.Intent
import android.os.Bundle
import com.example.khanhfoodapp.databinding.ActivityAdminBinding

class AdminActivity : BaseActivity() {
    private lateinit var binding : ActivityAdminBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListener()
    }

    private fun initListener() {
        binding.apply {
            layoutHome.setOnClickListener{
                startActivity(Intent(this@AdminActivity, MainActivity::class.java))
            }
            layoutPerson.setOnClickListener{
                startActivity(Intent(this@AdminActivity, ManagerUserActivity::class.java))
            }

        }
    }
}