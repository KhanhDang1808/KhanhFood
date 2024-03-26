package com.example.khanhfoodapp.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.khanhfoodapp.Activity.BaseActivity.Companion.MY_REQUEST_CODE
import com.example.khanhfoodapp.Fragment.MainFragment.Companion.mAuth
import com.example.khanhfoodapp.MySingleton.mUser
import com.example.khanhfoodapp.MySingleton.myRef
import com.example.khanhfoodapp.R
import com.example.khanhfoodapp.databinding.ActivityProfileBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import java.util.concurrent.TimeUnit

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var dialog: Dialog
    private lateinit var mUri: Uri
    private lateinit var mUserName: String
    private lateinit var mCurrentUser: FirebaseUser

    private lateinit var sex:String
    private lateinit var name:String
    private lateinit var date:String

    private var mActivityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Xử lý kết quả khi hoạt động thành công
                val data: Intent = result.data ?: return@registerForActivityResult
                // Xử lý dữ liệu từ Intent (nếu có)
                mUri = data.data!!
                val inputStream = contentResolver.openInputStream(mUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                binding.imageUser.setImageBitmap(bitmap)
            } else {
                // Xử lý kết quả khi hoạt động không thành công hoặc bị hủy
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mCurrentUser = mAuth.currentUser!!
        mUri = mCurrentUser.photoUrl!!
        mUserName = mCurrentUser.displayName ?: ""

        name = mUserName
        sex = mUser.sex
        date = mUser.dateBirth

        setUserInformation()
        initListener()
    }

    @SuppressLint("ServiceCast")
    private fun initListener() {
        binding.apply {
            layoutEditName.setOnClickListener {

                openEditDialog()
            }
            btnBack.setOnClickListener {
                finish()
            }

            imageUser.setOnClickListener { setPicUser() }
            textImageUser.setOnClickListener { setPicUser() }

            btnSave.setOnClickListener {
                binding.progressBar3.visibility = View.VISIBLE
                onClickUpdateProfile()

            }

            layoutPhoneNumber.setOnClickListener {
                openEditDialog()
            }

            layoutEmail.setOnClickListener {
                if (!checkEmail()) {
                    textEmail.text = mCurrentUser.email
                }
                startActivity(Intent(this@ProfileActivity, ChangeEmailActivity::class.java))
            }

            layoutChangePassword.setOnClickListener {
                startActivity(Intent(this@ProfileActivity, ChangePasswordActivity::class.java))
            }

            setUserSDN(binding.editSex, binding.textSex,1)
            setUserSDN(binding.editDate, binding.textDate,2)
            setUserSDN(binding.editName, binding.textName,3)


            layoutAddress.setOnClickListener {
                startActivity(Intent(this@ProfileActivity, SettingAddressActivity::class.java))
            }

        }
    }

    private fun setUserSDN(edit: EditText, text: TextView,id:Int) {
        // Ẩn TextView
        text.setOnClickListener {
            text.visibility = View.GONE
            // Hiển thị EditText
            edit.visibility = View.VISIBLE
            // Focus vào EditText và hiển thị bàn phím mềm
            edit.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT)
        }
        edit.setOnClickListener {
            // Ẩn EditText
            val textEdit = edit.text.toString().trim()
            if (textEdit.isNotEmpty()) {
                when(id){
                    1-> sex = textEdit
                    2-> date = textEdit
                    3-> name = textEdit
                }
                text.text = textEdit
            }

            edit.visibility = View.GONE
            // Hiển thị TextView
            text.visibility = View.VISIBLE

        }

    }
    private fun openEditDialog() {
        dialog = Dialog(this)
        setDialog(dialog)

        val editName = dialog.findViewById<EditText>(R.id.edit_name)
        val btnCancel = dialog.findViewById<Button>(R.id.btn_cancel)
        val btnSend = dialog.findViewById<Button>(R.id.btn_send)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSend.setOnClickListener {
            val textEdit = editName.text.toString().trim()
            if (textEdit.isNotEmpty()) {
                val callbacks =
                    object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        override fun onCodeSent(
                            verificationId: String,
                            token: PhoneAuthProvider.ForceResendingToken
                        ) {
                            val intent = Intent(
                                this@ProfileActivity,
                                ChangeEmailActivity::class.java
                            )
                            intent.putExtra("PHONE_NUMBER", textEdit)
                            intent.putExtra("verificationId", verificationId)
                            intent.putExtra("PHONE", true)
                            startActivity(intent)
                            binding.progressBar3.visibility = View.GONE
                        }

                        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                            //
                            binding.progressBar3.visibility = View.GONE

                        }

                        override fun onVerificationFailed(p0: FirebaseException) {
                            Toast.makeText(
                                this@ProfileActivity,
                                "The verification Failed $textEdit",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("khanh", p0.message.toString())
                            binding.progressBar3.visibility = View.GONE
                        }
                    }

                val options = PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber("+84327179302") // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(this) // Activity (for callback binding)
                    .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
                binding.progressBar3.visibility = View.VISIBLE
                dialog.dismiss()

            }
        }
        dialog.show()
    }

    private fun setDialog(dialog: Dialog) {
        dialog.let {
            it.requestWindowFeature(Window.FEATURE_NO_TITLE)
            it.setContentView(R.layout.layout_dialog_edit)
        }
        val window = dialog.window ?: return

        window.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            // Đặt hiệu ứng mờ cho phần còn lại của màn hình
            setFlags(
                WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                WindowManager.LayoutParams.FLAG_DIM_BEHIND
            )
            setDimAmount(0.8f)
        }

        val windowAttributes = window.attributes
        windowAttributes.gravity = Gravity.CENTER
        window.attributes = windowAttributes
        //window.attributes.dimAmount = 0.8f

//        if (Gravity.BOTTOM == gravity) {
//            dialog.setCancelable(true)
//        } else {
//            dialog.setCancelable(false)
//        }
    }

    private fun setPicUser() {
        onClickRequestPermission()
    }

    private fun onClickRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                openGallery()
            } else {
                val permissions = arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
                ActivityCompat.requestPermissions(this, permissions, MY_REQUEST_CODE)
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                openGallery()
            } else {
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(this, permissions, MY_REQUEST_CODE)
            }

        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                Toast.makeText(this, "Quyền truy cập bị từ chối", Toast.LENGTH_SHORT)
                    .show()
                return
            }
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"))
    }

    private fun onClickUpdateProfile() {
        val profileUpdates = userProfileChangeRequest {
            displayName = name
            photoUri = mUri
        }
        //address or sex
        mUser.sex = sex
        mUser.dateBirth = date
        mCurrentUser.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    binding.progressBar3.visibility = View.GONE
                    Toast.makeText(
                        this@ProfileActivity,
                        "Update profile success",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    myRef.child(mUser.id).setValue(mUser)
                    val intent = Intent()
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }


    }

    private fun setUserInformation() {

        binding.apply {
            if (mUserName.isNotEmpty()) {
                textName.text = mUserName
            }
            Glide.with(this@ProfileActivity)
                .load(mUri)
                .error(R.drawable.ic_circle_image_default)
                .into(imageUser)
            if (mUser.address.isNotEmpty()) textAddress.text = mUser.address
            if (mUser.dateBirth.isNotEmpty()) textDate.text = mUser.dateBirth
            if (mUser.sex.isNotEmpty()) textSex.text = mUser.sex
        }
        checkEmail()
        checkPhoneNumber()

    }

    @SuppressLint("SetTextI18n")
    private fun checkEmail(): Boolean {
        if (mCurrentUser.isEmailVerified) {
            // Email của người dùng đã được xác minh
            // Thực hiện các hành động bạn muốn ở đây
            binding.textEmail.text = "${mCurrentUser.email} - Thay đổi "
            return true
        }
        return false
    }

    private fun checkPhoneNumber() {
        val phoneNumber = mCurrentUser.phoneNumber
        if (phoneNumber != null) {
            if (phoneNumber.isNotEmpty()) {
                binding.textPhoneNumber.text = phoneNumber
            }
        }
    }
}
