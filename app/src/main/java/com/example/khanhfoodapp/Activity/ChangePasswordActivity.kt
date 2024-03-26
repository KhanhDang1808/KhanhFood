package com.example.khanhfoodapp.Activity

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.example.khanhfoodapp.R
import com.example.khanhfoodapp.databinding.ActivityChangePasswordBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ChangePasswordActivity : BaseActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private var boolean: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener { finish() }
        checkNewPassword()

        setPassword()

    }


    private fun checkNewPassword() {
        binding.editNewPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Được gọi trước khi văn bản trong EditText thay đổi
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


            }

            override fun afterTextChanged(s: Editable?) {
                val text1 = s.toString()
                val retypePassword = binding.editRetypeNewPassword.text.toString()

                if (text1 == retypePassword) {
                    binding.textError.visibility = View.GONE
                    boolean = true
                } else {
                    binding.textError.visibility = View.VISIBLE
                    boolean = false
                }
            }
        })

        binding.editRetypeNewPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Được gọi trước khi văn bản trong EditText thay đổi
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


            }

            override fun afterTextChanged(s: Editable?) {
                val text1 = s.toString()
                val password = binding.editNewPassword.text.toString()

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
    private fun setPassword(){
        binding.btnConfirm.setOnClickListener {
            val password = binding.editPassword.text
            val newPassword = binding.editNewPassword.text

            if(password.isEmpty()|| newPassword.isEmpty())  {
                Toast.makeText(
                    this,
                    "Password not Empty",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            val credential =
                EmailAuthProvider.getCredential(user?.email!!, password.toString().trim())

            user.reauthenticate(credential)
                .addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        // Người dùng đã xác minh thành công
                        // Cho phép họ đổi mật khẩu
                        if (boolean && newPassword.toString().trim().length >= 6) {
                            // Mật khẩu mới
                            AlertDialog.Builder(this)
                                .setTitle("Đổi mật khẩu")
                                .setMessage("Bạn có chắc muốn đổi mật khẩu?")
                                .setPositiveButton("Yes") { _, _ ->
                                    //  progressBar.visibility = View.VISIBLE
                                    mAuth.currentUser!!.sendEmailVerification()
                                        .addOnCompleteListener {
                                            user.updatePassword(newPassword.toString().trim())
                                                .addOnCompleteListener { updatePasswordTask ->
                                                    if (updatePasswordTask.isSuccessful) {
                                                        // Mật khẩu đã được cập nhật thành công
                                                        // Thực hiện các hành động bạn muốn ở đây
                                                        Toast.makeText(
                                                            this,
                                                            "Cập nhật mật khẩu thành công",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        finish()
                                                    }
                                                }
                                        }
                                }
                                .setNegativeButton("Cancel", null)
                                .show()
                        } else {
                            Toast.makeText(
                                this,
                                "Mật khẩu  > 6 kí tự",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Mật khẩu không chính xác",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    companion object {
        private val user = FirebaseAuth.getInstance().currentUser

    }

}