package com.example.khanhfoodapp.Activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.khanhfoodapp.MySingleton
import com.example.khanhfoodapp.MySingleton.mUser
import com.example.khanhfoodapp.MySingleton.myRef
import com.example.khanhfoodapp.databinding.ActivityChangeEmailBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class ChangeEmailActivity : BaseActivity() {
    private lateinit var binding: ActivityChangeEmailBinding
    private lateinit var  mPhoneNumber:String
    private lateinit var  mVerificationId:String
    private lateinit var mForceResendingToken:PhoneAuthProvider.ForceResendingToken
    private var boolean:Boolean = false

    //  private val user = mAuth.currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mPhoneNumber = intent.getStringExtra("PHONE_NUMBER").toString()
        mVerificationId = intent.getStringExtra("verificationId").toString()
        boolean = intent.getBooleanExtra("PHONE",false)

        binding.progressBar4.visibility = View.GONE
        // checkEmail()
        binding.btnBack.setOnClickListener { finish() }

        if(!boolean){
            binding.editEmail.setText(mAuth.currentUser?.email)
            binding.textResend.visibility = View.GONE
            checkEmail()
        }else{
            getDataIntent()
        }

    }

    private fun getDataIntent() {

        binding.apply {
            textTitle.text = "Nhập mã OTP"
            btnConfirm.text = "Xác minh"
            editEmail.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)


            btnConfirm.setOnClickListener{
                val otp = editEmail.text.toString().trim()
                binding.textError.visibility = View.GONE
                onClickSendOtp(mVerificationId,otp)
            }

            textResend.setOnClickListener {
                resendOtp()
            }
        }
    }

    private fun onClickSendOtp(verificationId:String,otp:String){
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        mAuth.currentUser?.updatePhoneNumber(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Số điện thoại đã được cập nhật thành công
                    // Tiếp tục với các hành động khác nếu cần
                    Toast.makeText(
                        this,
                        "Phone number has been successfully verified",
                        Toast.LENGTH_SHORT
                    ).show()
                    mUser.phoneNumber = mPhoneNumber
                   myRef.child(mUser.id)
                        .child("phoneNumber").setValue(mPhoneNumber)
                    finish()
                } else {
                    // Có lỗi xảy ra khi cập nhật số điện thoại
                    handleUserPhoneNumber()
                    // Xử lý lỗi hoặc thông báo cho người dùng
                }
            }
    }
    private fun handleUserPhoneNumber(){
        MySingleton.myRef.child(mUser.id)
            .child("phoneNumber").setValue("")
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(
                        this,
                        "Phone number has been successfully verified",
                        Toast.LENGTH_SHORT
                    ).show()
                   finish()
                } else {
                    binding.textError.visibility = View.VISIBLE
                }
            }
    }

    private fun resendOtp(){
        val callbacks =
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    // Mã xác minh đã được gửi thành công
                    // Lưu trữ verificationId để sử dụng sau này
                    // Ví dụ:
                    // this.verificationId = verificationId
                    mVerificationId = verificationId
                    mForceResendingToken=token
                 //   dialog.dismiss()
                }

                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    //
                   // signInWithPhoneAuthCredential(p0)
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    Toast.makeText(
                        this@ChangeEmailActivity,
                        "The verification Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(mPhoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .setForceResendingToken(mForceResendingToken)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    @SuppressLint("SetTextI18n")
    private fun checkEmail() {
        val user = mAuth.currentUser
        if (user!=null && user.isEmailVerified ) {
            // Email của người dùng đã được xác minh
            // Thực hiện các hành động bạn muốn ở đây
            binding.btnConfirm.text = "Xác nhận"
            binding.textTitle.text = "Thay đổi Email"
            binding.btnConfirm.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Thay đổi Email")
                    .setMessage("Bạn chắc muốn thay đổi Email?")
                    .setPositiveButton("OK") { _, _ ->
                        binding.progressBar4.visibility = View.VISIBLE
                        user.updateEmail(binding.editEmail.toString().trim())
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    binding.progressBar4.visibility = View.GONE
                                    Toast.makeText(
                                        this, "Thay đổi thành công",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    finish()
                                }
                            }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        } else {
            // Email của người dùng chưa được xác minh
            // Thực hiện các hành động bạn muốn ở đây, ví dụ:
            // Hiển thị thông báo yêu cầu xác minh email hoặc gửi lại email xác minh
            binding.btnConfirm.text = "Xác minh"
            binding.textTitle.text = "Xác minh Email"

            binding.btnConfirm.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Xác minh Email")
                    .setMessage("Kiểm tra Email để xác minh?")
                    .setPositiveButton("Yes") { _, _ ->
                        //  progressBar.visibility = View.VISIBLE
                        mAuth.currentUser!!.sendEmailVerification()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        this, "Kiểm tra Email của bạn",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                    finish()
                                }
                            }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }
    }

}