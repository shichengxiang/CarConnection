package com.linruan.carconnection.entities

import java.io.Serializable

/**
 * author：shichengxiang on 2020/6/1 13:48
 * email：1328911009@qq.com
 * 修车订单
 */
class RepairOrder : Serializable {
    companion object{
        val STATE_ALL=0
        val STATE_WAITPAY=1
        val STATE_RUNNING=2
        val STATE_COMPLETED=3
        val STATE_CANCELED=4
    }

    /**
     * *  status 1 待付款 2进行中  3已完成 4已取消
     *  step 1等待师傅接单 2师傅已接单 3师傅已到达 4维修完成待确认 5用户已确认  6订单失败
     */
    var create_time=""
    var finish_time="" //完成时间
    var fault_ids=""
    var id=""
    var orderno=""
    var imgs=""
    var intro=""
    var lat=""
    var leixing_id=""
    var lng=""
    var pay_price=""
    var pay_price2=""
    var pay_time=""
    var pay_time2=""
    var pay_way=""
    var pay_way2=""
    var status=0
    var step=0
    var user_id=""
    var worker_id=""
    var phone=""//下单人的手机号
    var area="" //自己计算位置
    var road="" //自己计算
    var distance="" //自己计算的距离  公里
    var faultstr=""
    var leixingstr=""
    var address:String?=null
    var juli:String?=null
}
