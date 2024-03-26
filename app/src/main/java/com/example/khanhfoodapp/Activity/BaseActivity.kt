package com.example.khanhfoodapp.Activity

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


open class BaseActivity : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth
    lateinit var database: FirebaseDatabase
    private lateinit var bitmap: Bitmap



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()



        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.BLACK
        }
    }



    companion object {
        const val MY_REQUEST_CODE = 10
        const val TAG: String = "TAG"
    }
}