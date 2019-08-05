package com.example.myapplication.models

import java.io.Serializable


data class TravelDeals(var imageURL:String?,
                       var title:String?,
                       var description:String?,
                       var price:String?,
                       var id:String?,
                       var imageName:String?):Serializable{
 constructor():this(null,null,null,null,null,null){

 }
}
