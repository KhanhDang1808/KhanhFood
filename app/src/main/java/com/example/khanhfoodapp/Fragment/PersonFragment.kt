package com.example.khanhfoodapp.Fragment

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.khanhfoodapp.Activity.BaseActivity
import com.example.khanhfoodapp.Activity.CartActivity
import com.example.khanhfoodapp.Activity.ListOrderActivity
import com.example.khanhfoodapp.Activity.LoginActivity
import com.example.khanhfoodapp.Activity.MapsActivity
import com.example.khanhfoodapp.Activity.ProfileActivity
import com.example.khanhfoodapp.MySingleton
import com.example.khanhfoodapp.R
import com.example.khanhfoodapp.databinding.FragmentPersonBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class PersonFragment : Fragment() {
    private lateinit var binding: FragmentPersonBinding
    private var resultLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPersonBinding.inflate(layoutInflater, container, false)

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
               setUserInformation()
            }
        }

        setUserInformation()
        initListener()

        return binding.root
    }

    private fun initListener() {
       binding.let {
           it.textX.setOnClickListener {
               binding.layoutSlogan.visibility = View.GONE
           }
           it.btnLogout.setOnClickListener {
               FirebaseAuth.getInstance().signOut()
               val intent = Intent(requireContext(), LoginActivity::class.java)
               intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
               Firebase.auth.signOut()
               startActivity(intent)
           }

           it.layoutMapKhanhFood.setOnClickListener{
               val intent = Intent(requireContext(),MapsActivity::class.java)
               startActivity(intent)
           }
           it.btnCart.setOnClickListener{
               startActivity(Intent(requireContext(),CartActivity::class.java))
           }
           it.layoutOrder.setOnClickListener{
                startActivity(Intent(requireContext(),ListOrderActivity::class.java))
           }
           it.layoutComplete.setOnClickListener{
               val intent = Intent(requireContext(),ListOrderActivity::class.java)
               intent.putExtra("isMethod",3)
               startActivity(intent)
           }
           it.layoutCancelled.setOnClickListener{
               val intent = Intent(requireContext(),ListOrderActivity::class.java)
               intent.putExtra("isMethod",4)
               startActivity(intent)
           }
           it.layoutConfirm.setOnClickListener{
               val intent = Intent(requireContext(),ListOrderActivity::class.java)
               intent.putExtra("isMethod",1)
               startActivity(intent)
           }
           it.layoutDelivery.setOnClickListener{
               val intent = Intent(requireContext(),ListOrderActivity::class.java)
               intent.putExtra("isMethod",2)
               startActivity(intent)
           }

       }
        setProfile()

    }
    private fun setProfile() {
       binding.layoutProfile.setOnClickListener{
           val intent = Intent(requireContext(), ProfileActivity::class.java)
                       // Set user address, sex, date of birth, and phone number if not empty
           resultLauncher?.launch(intent)
       }
        binding.layoutSetupAccount.setOnClickListener{
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            resultLauncher?.launch(intent)
        }
    }

    private fun setUserInformation(){
        val user = mAuth.currentUser
        user!!.let {
            // Name, email address, and profile photo Url
            val name = it.displayName

            val email = it.email
            val photoUrl = it.photoUrl

            // Check if user's email is verified
            val emailVerified = it.isEmailVerified

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            val uid = it.uid
            binding.apply {
                textEmail.text = email
                if (name == null) {
                    textNameUser.text = "User_Name"
                } else {
                    textNameUser.text = name
                }

                Glide.with(requireContext())
                    .load(photoUrl)
                    .error(R.drawable.ic_circle_image_default)
                    .into(imageUser)
            }
        }
    }

    companion object {
        private val mAuth = FirebaseAuth.getInstance()
    }
}

