package com.linruan.carconnection.entities.net

import com.linruan.carconnection.net.BaseResponse

/**
 * 项目名称：CarConnection
 * 类描述：
 * 创建人：shichengxiang
 * 创建时间：2020/8/28 23:44
 * @version
 */
class GetBattleRepairResponse : BaseResponse() {
    var data: DataBean? = null

    class DataBean {
        var orderno: String? = null
        var status = 2 //==1时不用支付
    }
}
