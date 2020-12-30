package com.linruan.carconnection.entities.net

import com.linruan.carconnection.net.BaseResponse

class GetConsumeRecordResponse:BaseResponse() {
    var data:DataBean?=null
    class DataBean{
        var list:ArrayList<ConsumeBean>?=null
    }
    class ConsumeBean{
        var title:String?=null
        var content:String?=null
        var content_id:String?=null
        var money:String?=null
        var table:String?=null
        var create_time:String?=null
        var isShowDay=false
    }
}
