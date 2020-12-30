package com.linruan.carconnection.entities.net

import com.linruan.carconnection.net.BaseResponse

class GetGuideResponse:BaseResponse() {
    var data:DataBean?=null
    class DataBean{
        var list:ArrayList<ListBean>?=null
    }
    class ListBean{
        var id:String?=null
        var cover:String?=null
        var url:String?=null
    }
}