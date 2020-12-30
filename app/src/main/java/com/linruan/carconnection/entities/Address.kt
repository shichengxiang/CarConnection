package com.linruan.carconnection.entities

import java.io.Serializable

class Address : Serializable {
    var id:String?=null
    var name=""
    var phone=""
    var address=""
    var province:String?=null
    var city:String?=null
    var area:String?=null
    fun getCountry():String{
        return "$province $city $area"
    }
}