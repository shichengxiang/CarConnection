package com.linruan.carconnection

object Constant {


    val URL_USERPROTOCOL="http://chelianyizhong.com/api/public/detail/field/yonghu.html" //用户协议
    val URL_PRIVACY="http://chelianyizhong.com/api/public/detail/field/yinsi.html"//隐私政策
    val URL_APP_INTRO="http://chelianyizhong.com/api/public/detail/field/about.html" //app介绍
    val URL_WORKERAUTH_STATEMENT="http://chelianyizhong.com/api/public/detail/field/shuoming.html" //维修师傅认证说明

    /**
     * 支付方式
     */
    val PAY_TYPE_WEIXIN="weixin"
    val PAY_TYPE_ZHIFUBAO="alipay"
    val PAY_TYPE_BALANCE="balance"
    /**
     * 平台支付价格
     */
    var PLATFORM_USER_PRICE=5.00//平台支付价格
    var PLATFROM_WORKER_PRICE=5.00
    /**
     * 客服电话
     */
    var CUSTOMERSERVICE_MOBILE="0510-83297898"

    /**
     * 广播 订单
     */
    val BROADCAST_ACTION_PLACEORDER_RECEIVED="com.linruan.tocustomer"//订单被接收提示
    val BROADCAST_ACTION_PAYRESULT_RECEIVED="com.linruan.payresult"//订单被接收提示

    /**
     * 开启第三方导航 包名
     */
    val PACKAGENAME_GAODE="com.autonavi.minimap"
    val PACKAGENAME_BAIDU="com.baidu.BaiduMap"
    val PACKAGENAME_TENCENT="com.tencent.map"



    /**
     * 通知类型
     */
    val NOTIFICATION_TYPE_NEWORDER=1 //新的接单通知 或者已被接单
    val NOTIFICATION_TYPE_BATTLED=1 //已被接单通知
    val NOTIFICATION_TYPE_REPAIRSUCCESS=4 //维修成功通知
    val NOTIFICATION_TYPE_REPAIRFAIL=5 //维修失败通知
    val NOTIFICATON_TYPE_CANCELORDER=3 //取消订单通知
    val NOTIFICATION_TYPE_SENDEDGOODS=6 //已发货通知
    val NOTIFICATION_TYPE_RETURNGOODS =7//退货通知
    val NOTIFICATION_TYPE_TALKMESSAGE=8 //贴吧消息通知
    val NOTIFICATION_TYPE_MASTERISRECEIVED=9 //师傅到达通知
    val NOTIFICATION_TYPE_PUSHSERVICE=10 //后台推送
    val NOTIFICATION_TYPE_REPAIRORDER_WAITCONFIRM=11 //等待客户确认维修结果
}
