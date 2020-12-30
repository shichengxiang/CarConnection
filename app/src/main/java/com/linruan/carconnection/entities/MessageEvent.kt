package com.linruan.carconnection.entities

/**
 * author：shichengxiang on 2020/6/13 11:40
 * email：1328911009@qq.com
 */
open  class MessageEvent {

    companion object{
        const val MAIN_SWITCHTAB=1000 //首页隐式切换tab
        @JvmField
        val WEIXIN_LOGIN=1001
        val REFRESH_STOREORDERACTIVITY=1002 //刷新商城订单页面
        val REFRESH_BATTLEFRAGMENT=1003//刷新battlefragment
        val REFRESH_REPAIRORDERACTIVITY=1007 //刷新修车订单列表页
        @JvmField
        val WXPAY_RESULT=10001 //微信支付返回
        @JvmField
        var ALIPAY_RESULT=10002 //支付宝支付返回
        val REFRESH_REPAIRORDERD_TOMASTER=1004 //刷新师傅维修订单详情页面
        val REFRESH_REPAIRORDERD_TOCUSTOMER=1006 //刷新客户维修订单
        val REFRESH_HOMEFRAGMENTRUNNINGORDER=1005 //刷新客户首页进行中订单状态
        val REFRESH_TALKFRAGMENT=1008 //刷新贴吧


    }
    var code=0
    var message:Array<out String>
    constructor(code:Int,vararg message: String){
        this.code=code
        this.message=message
    }
}
