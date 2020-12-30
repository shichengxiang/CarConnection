package com.linruan.carconnection.entities.net

import com.linruan.carconnection.net.BaseResponse

class GetAppVersion:BaseResponse() {
    var data:DataBean?=null
    class DataBean{
        var app_name:String?=null //appname
        var version_title:String?=null //version
        var version=1 //code
        var down_url2:String?=null //下载地址
        var app_intro:String?=null //版本介绍
        var down_filesize=1//包大小
        var user_price=5.0 //平台支付价格
        var woker_price=5.0 // 平台支付价格
    }
}
