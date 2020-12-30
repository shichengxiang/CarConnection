package com.linruan.carconnection.entities

import java.io.Serializable

class Master :Serializable{
    var name: String = ""
    var account = ""
    var phone="12345678123"
    var lat: Double = 0.0
    var lng = 0.0
    var id=""
    var avatar="" //头像
    var business="" //主营业务
    var leixing:ArrayList<String>?=null
    var comments:String?=null//评价
    var ordernum:String?=null//订单数量
    var worktime:String?=null //工作时间
    fun leixingToString():String{
        var str=StringBuffer()
        if(leixing!=null){
            leixing?.forEach {
                str.append(it).append(" / ")
            }
        }
        if(str.length>3){
            return str.substring(0,str.length-3)
        }else{
            return ""
        }
    }
}