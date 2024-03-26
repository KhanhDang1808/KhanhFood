package com.example.khanhfoodapp.Domain

import java.util.Date

class Bill() {
    var billID:String =""
    var billDate: String = ""
    var userId: String = ""
    var billAddress: String = ""
    var namePhone: String = ""
    var totalQtt: Int = 0
    var totalPrice: String =""
    var status: String = ""
    var note: String = ""
    var methods:String =""
    var billFoods: ArrayList<Foods> = arrayListOf()
}