package com.example.khanhfoodapp.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.khanhfoodapp.Activity.OrderListActivity
import com.example.khanhfoodapp.Adapter.OrderListAdapter
import com.example.khanhfoodapp.Domain.Bill
import com.example.khanhfoodapp.databinding.FragmentNotificationBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class OrderFragment : Fragment() {

    private lateinit var binding: FragmentNotificationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationBinding.inflate(layoutInflater, container, false)
        //replaceFragment(1)
        initBill()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.let {
            it.layoutComplete.setOnClickListener{
               val intent = Intent(requireContext(),OrderListActivity::class.java)
                intent.putExtra("isMethod",3)
                startActivity(intent)
            }
            it.layoutCancelled.setOnClickListener{
                val intent = Intent(requireContext(),OrderListActivity::class.java)
                intent.putExtra("isMethod",1)
                startActivity(intent)
            }
            it.layoutConfirm.setOnClickListener{
                val intent = Intent(requireContext(),OrderListActivity::class.java)
                intent.putExtra("isMethod",1)
                startActivity(intent)
            }
            it.layoutDelivery.setOnClickListener{
                val intent = Intent(requireContext(),OrderListActivity::class.java)
                intent.putExtra("isMethod",2)
                startActivity(intent)
            }

        }

    }

    private fun initBill() {
        val myRef = MainFragment.database.getReference("Bill")
        binding.progressBar5.visibility = View.VISIBLE
        val listBill = ArrayList<Bill>()
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    listBill.clear()

                    val reversedList = snapshot.children.reversed()
                    for (issue in reversedList) {
                        issue.getValue(Bill::class.java)?.let { listBill.add(it) }
                    }
                     if (listBill.size > 0) {
                    binding.apply {
                        rclListOrder.layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.VERTICAL, false
                        )
                        val adapter = OrderListAdapter(listBill)
                        rclListOrder.adapter = adapter
                        }
                    }
                    binding.progressBar5.visibility = View.GONE
                    binding.textTotal.text = listBill.size.toString()
                }else{

                    binding.progressBar5.visibility = View.GONE
                    Snackbar.make(
                        binding.root,
                        "Không có đơn hàng nào.",
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .show()
                    binding.progressBar5.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}