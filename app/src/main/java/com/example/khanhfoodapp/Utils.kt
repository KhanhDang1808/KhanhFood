package com.example.khanhfoodapp

import com.example.khanhfoodapp.Domain.User
import com.example.khanhfoodapp.Fragment.MainFragment

object MySingleton {
    // Khai báo biến dùng chung
    lateinit var mUser:User
    val myRef = MainFragment.database.getReference("Users")
}