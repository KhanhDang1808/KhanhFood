package com.example.khanhfoodapp.Activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.khanhfoodapp.databinding.ActivityLoginBinding
import com.google.android.gms.tasks.OnCompleteListener

class LoginActivity : BaseActivity() {
    private lateinit var binding:ActivityLoginBinding
    private var validEmail = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.[a-z]+")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.progressBar.visibility = View.GONE

        setVariable()
        binding.apply {
            textForgotPass.setOnClickListener {
                val email = editUser.text.toString().trim()
                if(editUser.text.isEmpty() || !email.matches(validEmail)){
                    Toast.makeText(this@LoginActivity,"Email is not Empty",Toast.LENGTH_SHORT)
                        .show()
                }else{
                    AlertDialog.Builder(this@LoginActivity)
                        .setTitle("Quên mật khẩu")
                        .setMessage("Bạn có chắc muốn đặt lại mật khẩu?")
                        .setPositiveButton("OK") { _, _ ->
                            progressBar.visibility = View.VISIBLE
                            mAuth.sendPasswordResetEmail(email)
                                .addOnCompleteListener(OnCompleteListener<Void?> { task ->
                                    if (task.isSuccessful) {
                                        progressBar.visibility = View.GONE
                                        Toast.makeText(
                                            this@LoginActivity,
                                            "Hãy kiểm tra email của bạn!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                })
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }
            }
        }


    }

    private fun setVariable() {
        binding.buttonLogin.setOnClickListener{

            val email=binding.editUser.text.toString().trim()
            val password=binding.editPass.text.toString().trim()

            if(email.isNotEmpty() && password.isNotEmpty() && email.matches(validEmail)){
                binding.progressBar.visibility = View.VISIBLE
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            binding.progressBar.visibility = View.GONE
                           // lifecycleScope.launch { getUser()}
                            // Sign in success, update UI with the signed-in user's information
                            startActivity(Intent(this,AdminActivity::class.java))
                        } else {
                            binding.progressBar.visibility = View.GONE
                            // If sign in fails, display a message to the user.
                            Toast.makeText(
                                baseContext,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
            }else{
                Toast.makeText(this,"Please fill username and password",Toast.LENGTH_SHORT).show()
            }
        }
    }
}