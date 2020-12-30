package com.linruan.carconnection.entities

import java.io.Serializable

/**
 * author：shichengxiang on 2020/6/1 13:48
 * email：1328911009@qq.com
 * 修车订单
 */
class RepairOrderDetail : Serializable {

    /**
     * *  status 1 待付款 2进行中  3已完成 4已取消
     *  step 1等待师傅接单 2师傅已接单 3师傅已到达 4维修完成待确认 5用户已确认  6订单失败 7 维修失败待确认
     */
    var create_time=""
    var finish_time="" //完成时间
    var arrive_time:String?=null//到达时间
    var fault_ids=""
    var id=""
    var orderno=""
    var imgs:ArrayList<ImageBean>?=null
    var intro="" //用户备注
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
    var fault:ArrayList<FaultPart>?=null
    var price:String?=null //客户部位价格
    var total:String?=null //客户部位总价
    var other:String?=null //师傅部位价格
    var other_total:String?=null //师傅部位总价
    var other_imgs:ArrayList<ImageBean>?=null //师傅上传图片
    var worker_intro:String?=null //师傅备注
    var price1:String?=null //客户平台支付金额
    var price2:String?=null //师傅平台支付金额
    var user:UserBean?=null
    var fail_imgs:ArrayList<ImageBean>?=null
    var fail_reason:String?=null
    class ImageBean{
        var id=""
        var filepath=""
    }
    class UserBean{
        var avatar:String?=null
        var name:String?=null
        var account:String?=null
    }
}
