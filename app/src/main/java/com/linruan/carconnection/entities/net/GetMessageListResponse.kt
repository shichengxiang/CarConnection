package com.linruan.carconnection.entities.net

import com.linruan.carconnection.entities.Message
import com.linruan.carconnection.net.BaseResponse

class GetMessageListResponse:BaseResponse() {
    var data:DataBean?=null
    class DataBean{
        var list:ArrayList<Message>?=null
    }
}