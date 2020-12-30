package com.linruan.carconnection.entities.net

import com.linruan.carconnection.entities.Address
import com.linruan.carconnection.net.BaseResponse

/**
 * author：shichengxiang on 2020/6/12 11:04
 * email：1328911009@qq.com
 */
class GetAddressListResponse:BaseResponse() {
    var data: Databean?=null
    class Databean{
        var list:ArrayList<Address>?=null
    }
}