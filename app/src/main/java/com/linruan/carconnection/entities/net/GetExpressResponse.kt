package com.linruan.carconnection.entities.net

import com.linruan.carconnection.entities.City
import com.linruan.carconnection.net.BaseResponse

/**
 * author：shichengxiang on 2020/6/12 13:07
 * email：1328911009@qq.com
 */
class GetExpressResponse:BaseResponse() {
    var data:DataBean?=null
    class DataBean{
        var list:ArrayList<ExpressBean>?=null
    }
    class ExpressBean{
        var id=0
        var status=0
        var name:String?=null
        var code:String?=null
    }
}
