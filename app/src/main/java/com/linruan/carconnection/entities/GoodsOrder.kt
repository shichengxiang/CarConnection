package com.linruan.carconnection.entities

class GoodsOrder {
    companion object {
        val STATE_ALL = -1
        val STATE_WAITPAY = 1 //待付款
        val STATE_WAITEXPRESS = 2 //代发货
        val STATE_WAITRECEIVE = 3 //待收货
        val STATE_WAITAFTERSALE = 4 //退货 /售后

        val STEP_COMPLETED = 1 //已完成
        val STEP_CANCELED = 2 //已取消
        val STEP_RETURNING = 3 //退款中
        val STEP_RETURNED = 4  //已退款
        val REFUND_STATUS_APPLING = 0//退款申请中
        val REFUND_STATUS_AGREE_NOEXPRESS = 1 //后台同意退款待填写物流信息
        val REFUND_STATUS_AGREE_HASEXPRESS = 2 //等待退款 已填写物流信息
        val REFUND_STATUS_COMPLETED = 3//后台已点击退款
        val REFUND_STATUS_FAIL = 4//退款失败 有失败原因
    }

    var id: String? = null
    var status = STATE_WAITPAY
    var orderno: String? = null
    var ordersGoods: ArrayList<Goods>? = null
    var name: String? = null
    var price: String? = null
    var payprice: String? = null
    var fee: String? = null
    var total: String? = null
    var address: String? = null
    var phone: String? = null
    var create_time: String? = null
    var paytime: String? = null
    var taketime: String? = null
    var step = 1
    var refund: RefundBean? = null

    class RefundBean {
        var status = 0
        var failure: String? = null
        var explain:String?=null //退款理由
        var total:String?=null //退款金额
        var orderno_t:String?=null
        var create_time:String?=null// 退款时间
    }
}
