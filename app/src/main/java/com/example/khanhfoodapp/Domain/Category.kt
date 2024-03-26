package com.example.khanhfoodapp.Domain

class Category() {
    var  Id:Int = 0
       var ImagePath:String=""
       var Name:String=""

     constructor(ImagePath:String,Name:String):this(){
          this.ImagePath=ImagePath
          this.Name=Name
     }
}