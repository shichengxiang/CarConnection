package com.linruan.carconnection.entities.net

import com.linruan.carconnection.net.BaseResponse

/**
 * author：shichengxiang on 2020/6/1 10:21
 * email：1328911009@qq.com
 */
class LoginResponse : BaseResponse() {
    var data: DataBean? = null

    class DataBean {
        var id=""
        var name=""
        var avatar=""
        var login_ip: String = ""
        var login_time: String = ""
        var token: String?=null
        var account:String?=null
        var identify:String?=null //0:未提交过 1：提交过
        var identify_status:String?=null //1：审核通过   0 待审核
        var group_id=1
    }
}
