package com.example.khanhfoodapp.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.khanhfoodapp.Adapter.UserAdapter
import com.example.khanhfoodapp.Domain.User
import com.example.khanhfoodapp.databinding.FragmentManagerUserBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ManagerUserFragment : Fragment() {
    private lateinit var binding:FragmentManagerUserBinding
    private lateinit var listUser:ArrayList<User>
    private lateinit var adapter:RecyclerView.Adapter<*>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
         binding = FragmentManagerUserBinding.inflate(layoutInflater, container, false)
        initUser()
        return binding.root
    }

    private fun initUser() {
        val myRef = MainFragment.database.getReference("Users")
        binding.progressBarBestFood.visibility = View.VISIBLE
        listUser = ArrayList()

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(listUser.size >0 ) listUser.clear()
                if (snapshot.exists()) {
                    for (issue in snapshot.children) {
                        issue.getValue(User::class.java)?.let { listUser.add(it) }
                    }
                    if (listUser.size > 0) {
                        binding.apply {
                            rclListUser.layoutManager = LinearLayoutManager(
                                requireContext(),
                                LinearLayoutManager.VERTICAL,false
                            )
                            //Thiết lập số cột
                            //  (rclCategory.layoutManager as GridLayoutManager).spanCount = 3
                            val adapter = UserAdapter(listUser)
                         //   adapter.setItemClickListener(this@MainFragment)
                            rclListUser.adapter = adapter
                            binding.textProTotal.text = listUser.size.toString()
                        }
                    }
                    binding.progressBarBestFood.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}