package com.linruan.carconnection.entities.net

import com.linruan.carconnection.entities.MasterStatus
import com.linruan.carconnection.net.BaseResponse

class GetMasterInfoResponse:BaseResponse() {
    var data:MasterStatus?=null
}