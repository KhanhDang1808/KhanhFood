package com.example.khanhfoodapp.Activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.khanhfoodapp.Domain.User
import com.example.khanhfoodapp.MySingleton.mUser
import com.example.khanhfoodapp.MySingleton.myRef
import com.example.khanhfoodapp.R
import com.example.khanhfoodapp.databinding.ActivitySignupBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

class SignupActivity : BaseActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var password:String
    private lateinit var retypePassword:String
    private var boolean:Boolean = false
    private var validEmail = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.[a-z]+")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textError.visibility = View.GONE

        initPassword()
        setVariable()
        binding.textLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun initPassword() {
        binding.editPass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Được gọi trước khi văn bản trong EditText thay đổi
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


            }
            override fun afterTextChanged(s: Editable?) {
                val text1 = s.toString()
                retypePassword = binding.editRetypePass.text.toString()

                if (text1 == retypePassword) {
                    binding.textError.visibility = View.GONE
                    boolean = true
                } else {
                    binding.textError.visibility = View.VISIBLE
                    boolean = false
                }
            }
        })

        binding.editRetypePass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Được gọi trước khi văn bản trong EditText thay đổi
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


            }
            override fun afterTextChanged(s: Editable?) {
                val text1 = s.toString()
                password = binding.editPass.text.toString()

                if (text1 == password) {
                    boolean = true
                    binding.textError.visibility = View.GONE
                } else {
                    boolean = false
                    binding.textError.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun setVariable() {
        binding.buttonSignup.setOnClickListener {
            val email = binding.editUser.text.toString().trim()
            if( !boolean) {
                return@setOnClickListener
            }
            if (password.length < 6 || email.isEmpty() || !email.matches(validEmail)) {
                Toast.makeText(this,
                    "Your password must be 6 character and email's not empty",
                    Toast.LENGTH_SHORT)
                    .show()
                    return@setOnClickListener
                }

            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.i(TAG, "onComplete: ")

                        runBlocking {
                            if (mAuth.currentUser != null) {

                                val email = mAuth.currentUser?.email.toString()
                                val newUserId = mAuth.currentUser?.uid.toString()
                                myRef.child(newUserId).addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        // Kiểm tra xem trường dữ liệu có tồn tại không
                                        if (dataSnapshot.exists()) {
                                            // Trường dữ liệu tồn tại, bạn có thể lấy giá trị của nó
                                            //mUser = User()
                                            mUser = dataSnapshot.getValue(User::class.java)!!

                                        } else {
                                            // Trường dữ liệu không tồn tại
                                            mUser = User()
                                            mUser.id = newUserId
                                            mUser.email = email
                                            myRef.child(newUserId).setValue(mUser)
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        // Xử lý khi có lỗi xảy ra trong quá trình truy vấn dữ liệu

                                    }
                                })
                            }

                        }
                        startActivity(Intent(this, LoginActivity::class.java))

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.i(TAG, "Failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()

                    }
                }

        }
    }
}