package com.example.khanhfoodapp.Helper

import android.content.Context
import android.widget.Toast
import com.example.khanhfoodapp.Domain.Foods
import com.example.khanhfoodapp.MySingleton
import com.example.khanhfoodapp.MySingleton.mUser
import com.example.khanhfoodapp.MySingleton.myRef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.text.DecimalFormat

class ManagmentCart(private var context: Context) {
    val tinyDB = TinyDB(context)


    fun insertFood(item: Foods): Unit {
        val listpop: ArrayList<Foods> = getListCart()
        var existAlready: Boolean = false
        var n = 0
        for (i in 0 until listpop.size) {
            if (listpop[i].Title == item.Title) {
                existAlready = true
                n = i
                break
            }
        }
        if (existAlready) {
            listpop[n].numberInCart += item.numberInCart
        } else {
            listpop.add(item)
        }
        tinyDB.putListObject("CartList", listpop)
        Toast.makeText(context, "Added to your Cart", Toast.LENGTH_SHORT).show()
    }
    fun getListCart(): ArrayList<Foods> = tinyDB.getListObject("CartList")


    fun getTotalFee(): Double {
        val listItem: ArrayList<Foods> = getListCart()
        var fee = 0.0
        for (i in 0 until listItem.size) {
            fee += (listItem[i].Price * listItem[i].numberInCart)
        }
        val decimalFormat = DecimalFormat("#.##")
        return fee
    }

    fun minusNumberItem(
        listItem: ArrayList<Foods>,
        position: Int,
        changeNumberItemsListener:ChangeNumberItemsListener
    ) {
        if (listItem[position].numberInCart == 1) {
            listItem.removeAt(position)
        } else {
            listItem[position].numberInCart = listItem[position].numberInCart - 1
        }
        tinyDB.putListObject("CartList", listItem)
        changeNumberItemsListener.change()
    }
    fun  plusNumberItem(listItem:ArrayList<Foods>, position:Int, changeNumberItemsListener: ChangeNumberItemsListener)
    {
        listItem[position].numberInCart = listItem[position].numberInCart + 1
        tinyDB.putListObject("CartList", listItem)
        changeNumberItemsListener.change()
    }
}