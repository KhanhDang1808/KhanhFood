package com.example.khanhfoodapp.Domain

import java.io.Serializable

class Foods():Serializable{
    var CategoryId:Int=0
       var Description:String =""
       var BestFood:Boolean = true
      val Id:Int=0
    var LocationId:Int=0
      var Price:Double=0.0
    var PriceId:Int=0
    var Star:Double=0.0
    var ImagePath:String=""
    var TimeId:Int =0
      var TimeValue:Int =0
       var Title:String=""
     var numberInCart:Int=0
    override fun toString(): String {
        return  Title
    }
}