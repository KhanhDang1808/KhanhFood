package com.example.khanhfoodapp.Activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import com.example.khanhfoodapp.databinding.ActivityIntroBinding

class IntroActivity : BaseActivity() {
    private lateinit var binding: ActivityIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setVariable()
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.parseColor("#FFE4B5")
        }
    }

//    private suspend fun getUser() {
//        if (mAuth.currentUser != null) {
//            val id = mAuth.currentUser!!.uid.toString()
//            withContext(Dispatchers.IO){
//                MySingleton.myRef.child(id).addListenerForSingleValueEvent(object :
//                    ValueEventListener {
//                    override fun onDataChange(dataSnapshot: DataSnapshot) {
//                        // Kiểm tra xem trường dữ liệu có tồn tại không
//                        if (dataSnapshot.exists()) {
//                           // mUser = User()
//                            mUser = dataSnapshot.getValue(User::class.java)!!
//                        }
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//
//                    }
//
//                })
//            }
//
//        }
//    }
    private fun setVariable() {
        binding.textLogin.setOnClickListener {
            if (mAuth.currentUser != null) {
                //  lifecycleScope.launch { getUser()}
                startActivity(Intent(this@IntroActivity, MainActivity::class.java))
            } else {
                startActivity(Intent(this@IntroActivity, LoginActivity::class.java))
            }
        }
    }


}