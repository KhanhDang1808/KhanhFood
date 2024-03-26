package com.example.khanhfoodapp.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.khanhfoodapp.R
import com.google.firebase.storage.FirebaseStorage

class NotificationFragment : Fragment() {

    private lateinit var image:ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view  = inflater.inflate(R.layout.fragment_notification, container, false)
        // Inflate the layout for this fragment
        image= view.findViewById<ImageView>(R.id.imageView6)


        Glide.with(requireContext())
       // https://firebasestorage.googleapis.com/v0/b/khanhfoodapp.appspot.com/o/BBQ%20Chicken%20Delight.jpg?alt=media&token=4e143e69-4377-4399-9710-91ceebf2c4ce
            .load("https://firebasestorage.googleapis.com/v0/b/khanhfoodapp.appspot.com/o/BBQ%20Chicken%20Delight.jpg?alt=media&token=dd548be3-f0f0-469c-b9e2-f7458962804c")
            .into(image)
        return  view
    }

}