package com.linruan.carconnection.net

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.linruan.carconnection.UserManager
import com.linruan.carconnection.entities.GetComplainType
import com.linruan.carconnection.entities.GetRepairOrderDetailResponse
import com.linruan.carconnection.entities.net.GetAddressListResponse
import com.linruan.carconnection.entities.net.*
import com.linruan.carconnection.logI
import com.lzy.okgo.OkGo
import com.lzy.okgo.cache.CacheMode
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * author：shichengxiang on 2020/6/1 09:26
 * email：1328911009@qq.com
 */
class Client {
    private constructor()

    companion object {
        val BASEURL = "http://chelianyizhong.com"
        val BASEURL_API = "$BASEURL/api"

        @JvmField
        val APPID = "1000"

        @JvmField
        val APPSECRET = ""
        private var client: Client? = null
        public fun getInstance(): Client {
            if (client == null) client = Client()
            return client!!
        }
    }

    /**
     * 登录
     */
    fun login(
        account: String,
        group: Int,
        nick: String,
        avatar: String,
        openId: String,
        jsonCallback: JsonCallback<LoginResponse>
    ) {
        OkGo.post<LoginResponse>("$BASEURL_API/user/login")
            .params("account", account)
//            .params("group", group)
            .params("name", nick)
            .params("avatar", avatar)
            .params("openid", openId)
            .tag("login")
            ?.execute(jsonCallback)
    }

    /**
     * 密码登录
     */
    fun plogin(account:String,password:String,callback:JsonCallback<LoginResponse>){
        OkGo.post<LoginResponse>("$BASEURL_API/user/plogin")
            .params("account",account)
            .params("password",password)
            .execute(callback)
    }

    /**
     * 重置密码
     */
    fun resetPassword(account:String,password:String,repassword:String,callback: JsonCallback<BaseResponse>){
        OkGo.post<BaseResponse>("$BASEURL_API/user/resetpsd")
            .params("password",password)
            .params("repassword",repassword)
            .params("account",account)
            .execute(callback)
    }

    fun wxLogin(openId: String, callback: JsonCallback<LoginResponse>) {
        OkGo.post<LoginResponse>("$BASEURL_API/user/ologin")
//            .params("group", if (UserManager.isMaster) 2 else 1)
            .params("openid", openId)
            .tag("wxogin")
            ?.execute(callback)
    }

    /**
     * 师傅认证
     */
    fun masterAuth(
        avatarId: String, name: String, sex: String, address: String, jingyan: String,
        leixing: String, shebei: String, idcard: String, idcard_front: String,
        idcard_back: String, num: String, contact: String, store: String, storeImgs: String,
        callback: JsonCallback<BaseResponse>
    ) {
        OkGo.post<BaseResponse>("$BASEURL_API/worker/identify")
            .params("avatar", avatarId)
            .params("name", name)
            .params("sex", sex)
            .params("address", address)
            .params("jingyan", jingyan)
            .params("leixing", leixing)
            .params("shebei", shebei)
            .params("idcard", idcard)
            .params("idcard_front", idcard_front)
            .params("idcard_back", idcard_back)
            .params("num", num)
            .params("contact", contact)
            .params("store", store)
            .params("imgs", storeImgs)
            ?.execute(callback)
    }

    /**
     * 引导页广告
     */
    fun guideAdvert(callback: JsonCallback<GetGuideResponse>) {
        OkGo.post<GetGuideResponse>("$BASEURL_API/public/getGuide")
            .execute(callback)
    }

    /**
     * 检测版本更新
     */
    fun checkAppVersion(callback: JsonCallback<GetAppVersion>) {
        OkGo.post<GetAppVersion>("$BASEURL_API/public/platform")
            .execute(callback)
    }

    /**
     * 获取用户信息
     */
    fun getUserInfo(callback: JsonCallback<GetUserInfo>) {
        OkGo.post<GetUserInfo>("$BASEURL_API/public/userInfo")
            .execute(callback)
    }

    /**
     * 消息 1.系统消息  2. 推送消息
     */
    fun getMessages(
        mold: Int,
        currentPage: Int,
        prePage: Int,
        callback: JsonCallback<GetMessageListResponse>
    ) {
        OkGo.post<GetMessageListResponse>("$BASEURL_API/public/message")
            .params("mold", mold)
            .params("page", currentPage)
            .params("prePage", prePage)
            .execute(callback)
    }

    /**
     * 更新用户头像昵称
     */
    fun updateUserInfo(
        avatar: String?,
        name: String?,
        address: String?,
        callback: JsonCallback<BaseResponse>
    ) {
        OkGo.post<BaseResponse>("$BASEURL_API/public/upInfo")
            .params("avatar", avatar ?: "")
            .params("name", name ?: "")
            .params("address", address ?: "")
            .execute(callback)
    }

    /**
     * 拉取当前是否有进行中订单 客户首页广告图
     */
    fun getRunningOrder(callback: JsonCallback<GetRunningOrderResponse>) {
        OkGo.post<GetRunningOrderResponse>("$BASEURL_API/public/home")
            .execute(callback)
    }

    /**
     * 师傅  抢单列表
     */
    fun getBattleOrderOfMaster(
        page: Int,
        perPage: Int,
        callback: JsonCallback<GetRepairOrderListResponse>
    ) {
        OkGo.post<GetRepairOrderListResponse>("$BASEURL_API/worker/grablist")
            .params("page", page)
            .params("perpage", perPage)
            .execute(callback)
    }

    /**
     * 抢单设置数据
     */
    fun getMasterSettingInfo(callback: JsonCallback<GetMasterInfoResponse>) {
        OkGo.post<GetMasterInfoResponse>("$BASEURL_API/worker/setting")
            .execute(callback)
    }

    /**
     * 门店设置
     */
    fun setupStore(
        phone: String,
        address: String,
        imgs: String,
        callback: JsonCallback<BaseResponse>
    ) {
        OkGo.post<BaseResponse>("$BASEURL_API/worker/store")
            .params("phone", phone)
            .params("address", address)
            .params("imgs", imgs)
            .execute(callback)
    }

    /**
     * 抢单设置
     */
    fun setupBattle(
        grab: Int = 0,
        scope: Int,
        leixingIds: String?,
        callback: JsonCallback<BaseResponse>
    ) {
        OkGo.post<BaseResponse>("$BASEURL_API/worker/store")
            .params("grab", grab)
            .params("position", scope)
            .params("leixing", leixingIds)
            .execute(callback)
    }

    /**
     * 师傅接口 ||||||
     */


    /**
     * 客户修车下单
     */
    fun placeRepairOrder(
        workId: String,
        typeId: String,
        fault_ids: String,
        imgs: String,
        intro: String,
        lng: String,
        lat: String,
        tag: String = "placeorder",
        jsonCallback: JsonCallback<PlaceRepairOrderResponse>
    ) {
        OkGo.post<PlaceRepairOrderResponse>("$BASEURL_API/user/repair")
            .params("worker_id", workId)
            .params("leixing_id", typeId)
            .params("fault_ids", fault_ids)
            .params("imgs", imgs)
            .params("intro", intro)
            .params("lng", lng)
            .params("lat", lat)
            .tag(tag)
            .execute(jsonCallback)
    }

    /**
     * 确认修车成功
     */
    fun confirmRepairSuccess(
        repair_id: String,
        tag: String = "confirm repair",
        jsonCallback: JsonCallback<BaseResponse>
    ) {
        OkGo.post<BaseResponse>("$BASEURL_API/user/repair_confirm")
            .params("repair_id", repair_id)
            .tag(tag)
            .execute(jsonCallback)
    }

    /**
     * 师傅 获取维修订单列表
     */
    fun getRepairCarOrderListOfMaster(
        status: Int,
        page: Int,
        prePage: Int,
        calback: JsonCallback<GetRepairOrderListResponse>,
        step: Int = 0
    ) {
        OkGo.post<GetRepairOrderListResponse>("$BASEURL_API/worker/getlist")
            .params("status", status)
            .params("page", page)
            .params("prePage", prePage)
            .params("step", step)
            .execute(calback)
    }

    /**
     * 获取维修订单列表
     */
    fun getRepairCarOrderList(
        status: Int,
        page: Int,
        prePage: Int,
        calback: JsonCallback<GetRepairOrderListResponse>,
        step: Int = 0
    ) {
        OkGo.post<GetRepairOrderListResponse>("$BASEURL_API/user/repair_orders")
            .params("status", status)
            .params("page", page)
            .params("prePage", prePage)
            .params("step", step)
            .execute(calback)
    }

    /**
     * 抢单
     */
    fun battleOrder(repair_id: String, callback: JsonCallback<GetBattleRepairResponse>) {
        OkGo.post<GetBattleRepairResponse>("$BASEURL_API/worker/grab")
            .params("repair_id", repair_id)
            .tag("battle")
            .execute(callback)
    }

    /**
     * 师傅 我已到达
     */
    fun reachDestination(repair_id: String, callback: JsonCallback<BaseResponse>) {
        OkGo.post<BaseResponse>("$BASEURL_API/worker/reach")
            .params("repair_id", repair_id)
            .tag("reach")
            .execute(callback)
    }

    /**
     * 维修订单详情
     */
    fun getDetailOfRepairOrder(
        repair_id: String,
        callback: JsonCallback<GetRepairOrderDetailResponse>
    ) {
        OkGo.post<GetRepairOrderDetailResponse>("$BASEURL_API/worker/getinfo")
            .params("repair_id", repair_id)
            .tag("reach")
            .execute(callback)
    }

    /**
     * 提交等待用户确认
     */
    fun commitFinishAndWaitConfirm(
        repair_id: String,
        partPrices: String,
        partTotal: String,
        other: String,
        otherTotal: String,
        otherImgs: String,
        workerIntro: String,
        callback: JsonCallback<BaseResponse>
    ) {
        OkGo.post<BaseResponse>("$BASEURL_API/worker/finish")
            .params("repair_id", repair_id)
            .params("price", partPrices)
            .params("total", partTotal)
            .params("other", other)
            .params("other_total", otherTotal)
            .params("other_imgs", otherImgs)
            .params("worker_intro", workerIntro)
            .tag("workerfinish")
            .execute(callback)
    }

    /**
     * 客户确认修车失败
     */
    fun confirmRepairFail(
        repair_id: String,
        fail_imgs: String,
        fail_reason: String,
        tag: String = "repair fail",
        jsonCallback: JsonCallback<BaseResponse>
    ) {
        OkGo.post<BaseResponse>("$BASEURL_API/user/repair_fail")
            .params("repair_id", repair_id)
            .params("fail_imgs", fail_imgs)
            .params("fail_reason", fail_reason)
            .tag(tag)
            .execute(jsonCallback)
    }

    /**
     * 师傅确认维修失败
     */
    fun confirmRepairFailForWorker(repairId: String, callback: JsonCallback<BaseResponse>) {
        OkGo.post<BaseResponse>("$BASEURL_API/worker/worker_fail")
            .params("repair_id", repairId)
            .execute(callback)
    }

    /**
     * 拉取附近师傅列表
     */
    fun getNearbyMasters(
        lng: String,
        lat: String,
        tag: String,
        jsonCallback: JsonCallback<GetMasterList>
    ) {
        OkGo.post<GetMasterList>("$BASEURL_API/user/getworker")
            .params("lng", lng)
            .params("lat", lat)
            .tag(tag)
            .execute(jsonCallback)
    }

    /**
     * 取消修车订单
     */
    fun cancelRepairOrder(
        repair_id: String,
        tag: String = "cancel repair",
        jsonCallback: JsonCallback<BaseResponse>, isMaster: Boolean = false
    ) {
        OkGo.post<BaseResponse>("$BASEURL_API${if (isMaster) "/worker/worker_cancel" else "/user/repair_cancel"}")
            .params("repair_id", repair_id)
            .tag(tag)
            .execute(jsonCallback)
    }

    /**
     * 删除修车订单
     */
    fun deleteRepairOrder(
        repairId: String,
        jsonCallback: JsonCallback<BaseResponse>,
        isMaster: Boolean = false
    ) {
        OkGo.post<BaseResponse>("$BASEURL_API${if (isMaster) "/worker/worker_delete" else "/user/repair_delete"}")
            .params("repair_id", repairId)
            .execute(jsonCallback)
    }

    /**
     * 继续等待接单
     */
    fun goonWaitRepair(repairId: String, callback: JsonCallback<BaseResponse>) {
        OkGo.post<BaseResponse>("$BASEURL_API/user/repair_go")
            .params("repair_id", repairId)
            .execute(callback)
    }

    /**
     * 获取商品订单详情
     */
    fun getGoodsOrderDetail(orderId: String, callback: JsonCallback<GetOrderDetailResponse>) {
        OkGo.post<GetOrderDetailResponse>("$BASEURL_API/orders/getinfo")
            .params("id", orderId)
            .execute(callback)
    }

    /**
     * 投诉
     */
    fun complaintRepair(
        repair_id: String,
        items_id: String,
        content: String,
        imgs: String,
        tag: String = "complaint repair",
        callback: JsonCallback<BaseResponse>
    ) {
        OkGo.post<BaseResponse>("$BASEURL_API/user/repair_complaint")
            .params("repair_id", repair_id)
            .params("repair_id", repair_id)
            .params("repair_id", repair_id)
            .params("repair_id", repair_id)
            .tag(tag)
            .execute(callback)
    }

    /**
     * 发表评论
     */
    fun releaeCommentOfRepair(
        repair_id: String,
        title: String,
        content: String,
        star: String,
        callback: JsonCallback<BaseResponse>
    ) {
        OkGo.post<BaseResponse>("$BASEURL_API/user/repair_comments")
            .params("repair_id", repair_id)
            .params("title", title)
            .params("content", content)
            .params("star", star)
            .execute(callback)
    }

    /**
     * 获取评价师傅标题
     */
    fun getPingJiaOfWorker(callback: JsonCallback<GetPingJiaWorkerResponse>) {
        OkGo.post<GetPingJiaWorkerResponse>("$BASEURL_API/public/getpingjia")
            .execute(callback)
    }

    /***
     * 图片上传
     */
    fun loadupImages(file: File, tag: String = "loadup", callback: JsonCallback<LoadUpFile>) {
        OkGo.post<LoadUpFile>("$BASEURL_API/public/upfile")
            .isMultipart(true)
            .tag(tag)
            .params("file", file)
            .execute(callback)
    }

    /**
     * 获取省市区
     */
    fun getArea(parent_id: String = "0", tag: String = "getArea", callback: JsonCallback<GetArea>) {
        OkGo.post<GetArea>("$BASEURL_API/public/getarea")
            .params("parent_id", parent_id)
            .tag(tag)
            .execute(callback)
    }

    /**
     * 获取故障原因
     */
    fun getFaults(callback: JsonCallback<GetFaults>) {
        OkGo.post<GetFaults>("$BASEURL_API/public/getFault")
            .execute(callback)
    }

    /**
     * 获取维修类型
     */
    fun getRepairType(callback: JsonCallback<GetRepairType>) {
        OkGo.post<GetRepairType>("$BASEURL_API/public/getLeixing")
            .execute(callback)
    }

    /**
     * 获取投诉类型
     */
    fun getComplainTye(callback: JsonCallback<GetComplainType>) {
        OkGo.post<GetComplainType>("$BASEURL_API/public/getComplaint")
            .execute(callback)
    }

    /**
     * 获取客服电话
     */
    fun getSericeTel(callback:JsonCallback<GetTelResponse>){
        OkGo.post<GetTelResponse>("$BASEURL_API/public/service_tel")
            .execute(callback)
    }

    /**
     * 商城首页
     */
    fun getGoodsIndex(callback: JsonCallback<GetGoodsIndex>) {
        OkGo.post<GetGoodsIndex>("$BASEURL_API/goods/goodsIndex")
            .execute(callback)
    }

    /**
     * 商品详情
     */
    fun getGoodsDetail(id: String, callback: JsonCallback<GetGoodsDetailResponse>) {
        OkGo.post<GetGoodsDetailResponse>("$BASEURL_API/goods/getinfo")
            .params("id", id)
            .execute(callback)
    }

    /**
     * 商品列表 搜索
     */
    fun getGoodsList(
        items_id: String,
        sort: Int,
        brand_id: String,
        keys: String,
        callback: JsonCallback<GetGoodsListResponse>
    ) {
        OkGo.post<GetGoodsListResponse>("$BASEURL_API/goods/getlist")
            .params("items_id", items_id)
            .params("sort", sort)
            .params("brand_id", brand_id)
            .params("keys", keys)
            .execute(callback)

    }

    /**
     * 商城订单列表
     */
    fun getGoodsOrderList(
        status: Int,
        page: Int,
        perPage: Int,
        callback: JsonCallback<GetGoodsOrderListResponse>
    ) {
        OkGo.post<GetGoodsOrderListResponse>("$BASEURL_API/orders/getlist")
            .params("status", status)
            .params("page", page)
            .params("perpage", perPage)
            .execute(callback)

    }

    /**
     * 加入购物车
     * @param sku_id 规格
     * @param num 数量
     */
    fun addGoodsToCar(
        goodsId: String,
        sku_id: String,
        num: Int,
        callback: JsonCallback<BaseResponse>
    ) {
        OkGo.post<BaseResponse>("$BASEURL_API/goods/addcart")
            .params("goods_id", goodsId)
            .params("sku_id", sku_id)
            .params("num", num)
            .execute(callback)
    }

    /**
     * 支付
     */
    fun goodsOrderPay(orderNo: String, payway: String, callback: JsonCallback<GetPayResponse>) {
        OkGo.post<GetPayResponse>("$BASEURL_API/pay/shop_pay")
            .params("orderno", orderNo)
            .params("payway", payway)
            .execute(callback)
    }

    /**
     * 平台支付
     */
    fun repairOrderPay(
        orderNo: String,
        payway: String,
        isMaster: Boolean,
        callback: JsonCallback<GetPayResponse>
    ) {
        OkGo.post<GetPayResponse>("$BASEURL_API/pay/repair_pay")
            .params("orderno", orderNo)
            .params("payway", payway)
            .params("group_id", if (isMaster) 2 else 1)
            .execute(callback)
    }

    /**
     * 调取计价规则和发单声明
     */
    fun getStatement(callback: JsonCallback<GetStatementResponse>) {
        OkGo.post<GetStatementResponse>("$BASEURL_API/public/getstatement")
            .execute(callback)
    }

    /**
     * 提醒发货
     */
    fun remindSendGoods(orderId: String, callback: JsonCallback<BaseResponse>) {
        OkGo.post<BaseResponse>("$BASEURL_API/orders/remind")
            .params("id", orderId)
            .execute(callback)
    }

    /**
     * 取消订单
     */
    fun cancelGoodsOrder(orderId: String, callback: JsonCallback<BaseResponse>) {
        OkGo.post<BaseResponse>("$BASEURL_API/orders/cancel")
            .params("id", orderId)
            .execute(callback)
    }

    /**
     * 删除订单
     */
    fun deleteGoodsOrder(orderId: String, callback: JsonCallback<BaseResponse>) {
        OkGo.post<BaseResponse>("$BASEURL_API/orders/delinfo")
            .params("id", orderId)
            .execute(callback)
    }

    /**
     * 确认收货
     */
    fun confirmReceivedGoods(orderId: String, callback: JsonCallback<BaseResponse>) {
        OkGo.post<BaseResponse>("$BASEURL_API/orders/confirm")
            .params("id", orderId)
            .execute(callback)
    }

    /**
     * 商品评价
     */
    fun evaluateGoods(
        orderId: String,
        goodsId: String,
        content: String,
        callback: JsonCallback<BaseResponse>
    ) {
        var goods = JsonObject().apply {
            addProperty("goods_id", goodsId)
            addProperty("content", content)
        }
        var json = JsonArray().apply {
            add(goods)
        }
        OkGo.post<BaseResponse>("$BASEURL_API/orders/comments")
            .params("orders_id", orderId)
            .params("content", Gson().toJson(json))
            .execute(callback)
    }

    /**
     * 拉取购物车列表
     */
    fun getCartList(callback: JsonCallback<GetCartListResponse>) {
        OkGo.post<GetCartListResponse>("$BASEURL_API/goods/cartlist")
            .execute(callback)
    }

    /**
     * 修改购物车数量
     */
    fun reduceCartNum(goodsId: String,skuId: String,num: Int,callback: JsonCallback<BaseResponse>){
        OkGo.post<BaseResponse>("$BASEURL_API/goods/cartReduce")
            .params("goods_id",goodsId)
            .params("sku_id",skuId)
            .params("num",num)
            .execute(callback)
    }

    /**
     * 购物车下单
     */
    fun placeGoodsOrderFromCart(
        cartIds: String,
        addressId: String,
        callback: JsonCallback<GetPlaceGoodsOrderResponse>
    ) {
        OkGo.post<GetPlaceGoodsOrderResponse>("$BASEURL_API/orders/push")
            .params("cart_ids", cartIds)
            .params("address_id", addressId)
            .execute(callback)

    }

    /**
     * 商品直接下单
     */
    fun placeGoodsOrderInDetail(
        goodsId: String,
        skuId: String,
        num: Int,
        addressId: String,
        callback: JsonCallback<GetPlaceGoodsOrderResponse>
    ) {
        OkGo.post<GetPlaceGoodsOrderResponse>("$BASEURL_API/orders/push_goods")
            .params("goods_id", goodsId)
            .params("sku_id", skuId)
            .params("num", num)
            .params("address_id", addressId)
            .execute(callback)

    }

    /**
     * 删除购物车
     */
    fun deleteGoodsInCart(cart_ids: String, callback: JsonCallback<BaseResponse>) {
        OkGo.post<BaseResponse>("$BASEURL_API/goods/cartDeletes")
            .params("cart_ids", cart_ids)
            .execute(callback)
    }

    /**
     * 获取贴吧话题类型和分类
     */
    fun getTypeAndClass(callback: JsonCallback<GetTypeAndClassify>) {
        OkGo.post<GetTypeAndClassify>("$BASEURL_API/public/bbs")
            .tag("typeclassify")
            .execute(callback)
    }

    /**
     * 贴吧发布
     */
    fun releaseBBS(
        content: String,
        imgs: String,
        leixing_id: String,
        items_id: String,
        lat: String,
        lng: String,
        callback: JsonCallback<BaseResponse>
    ) {
        OkGo.post<BaseResponse>("$BASEURL_API/bbs/push")
            .params("content", content)
            .params("imgs", imgs)
            .params("leixing_id", leixing_id)
            .params("items_id", items_id)
            .params("lat", lat)
            .params("lng", lng)
            .tag("release")
            .execute(callback)
    }

    /**
     * 贴吧详情
     */
    fun getBBSDetail(
        id: String,
        page: Int,
        perPage: Int,
        callback: JsonCallback<GetBBSDetailResponse>
    ) {
        OkGo.post<GetBBSDetailResponse>("$BASEURL_API/bbs/getinfo")
            .params("id", id)
            .params("page", page)
            .params("perpage", perPage)
            .tag("bbsdetail")
            .execute(callback)
    }

    /**
     * 发帖子
     */
    fun sendTalkComments(
        content_id: String,
        content: String,
        reply_id: String,
        topId: String,
        callback: JsonCallback<BaseResponse>
    ) {
        OkGo.post<BaseResponse>("$BASEURL_API/bbs/comments")
            .params("content_id", content_id)
            .params("content", content)
            .params("reply_id", reply_id)
            .params("top_id", topId)
            .tag("sendcomment")
            .execute(callback)
    }

    /**
     * 账户充值1 （先获取订单号 在根据订单号支付）
     */
    fun recharge1(money: String, callback: JsonCallback<GetOrderNoResponse>) {
        OkGo.post<GetOrderNoResponse>("$BASEURL_API/public/recharge")
            .params("money", money)
            .execute(callback)
    }

    /**
     * 账户充值2
     */
    fun recharge2(payway: String, orderNo: String, callback: JsonCallback<GetPayResponse>) {
        OkGo.post<GetPayResponse>("$BASEURL_API/pay/recharge_pay")
            .params("payway", payway)
            .params("orderno", orderNo)
            .execute(callback)
    }

    /**
     * 消费记录
     */
    fun getConsumeRecord(
        mold: Int,
        currentPage: Int,
        prePage: Int,
        callback: JsonCallback<GetConsumeRecordResponse>
    ) {
        OkGo.post<GetConsumeRecordResponse>("$BASEURL_API/user/logs")
            .params("mold", mold)
            .params("page", currentPage)
            .params("prePage", prePage)
            .execute(callback)
    }

    /**
     * 管理地址列表
     */
    fun getAddressList(callback: JsonCallback<GetAddressListResponse>) {
        OkGo.post<GetAddressListResponse>("$BASEURL_API/address/getlist")
            .execute(callback)
    }

    /**
     * 修改/添加地址
     * @param id 修改地址时用 添加时为空
     */
    fun addAddress(
        id: String,
        name: String,
        phone: String,
        province_id: String,
        city_id: String,
        area_id: String,
        address: String,
        callback: JsonCallback<BaseResponse>
    ) {
        OkGo.post<BaseResponse>("$BASEURL_API/address/push")
            .params("id", id)
            .params("name", name)
            .params("phone", phone)
            .params("province_id", province_id)
            .params("city_id", city_id)
            .params("area_id", area_id)
            .params("address", address)
            .execute(callback)

    }

    fun deleteAddress(id: String, callback: JsonCallback<BaseResponse>) {
        OkGo.post<BaseResponse>("$BASEURL_API/address/delinfo")
            .params("id", id)
            .execute(callback)
    }

    /**
     * 申请退款
     */
    fun applyRefund(
        orderId: String,
        explain: String,
        imgs: String,
        goodsId: String,
        callback: JsonCallback<BaseResponse>
    ) {
        OkGo.post<BaseResponse>("$BASEURL_API/orders/services")
            .params("order_id", orderId)
            .params("explain", explain)
            .params("imgs", imgs)
            .params("good_id", goodsId)
            .execute(callback)
    }

    /**
     * 获取快递公司
     */
    fun getExpress(callback: JsonCallback<GetExpressResponse>) {
        OkGo.post<GetExpressResponse>("$BASEURL_API/public/getExpress")
            .execute(callback)
    }

    /**
     * 提交快递信息
     */
    fun pushExpress(
        express: String,
        expressNumber: String,
        orderId: String,
        callback: JsonCallback<BaseResponse>
    ) {
        OkGo.post<BaseResponse>("$BASEURL_API/public/pushExpress")
            .params("express", express)
            .params("express_number", expressNumber)
            .params("order_id", orderId)
            .execute(callback)
    }

    /**
     * 三级联动
     * @param parent_id 默认0
     */
    fun getCity(parent_id: Int, callback: JsonCallback<GetCityResponse>) {
        OkGo.post<GetCityResponse>("$BASEURL_API/public/getarea")
            .params("parent_id", parent_id)
            .execute(callback)
    }

    /**
     * 意见反馈
     */
    fun feedback(content: String, contact: String, callback: JsonCallback<BaseResponse>) {
        OkGo.post<BaseResponse>("$BASEURL_API/public/gbook")
            .params("content", content)
            .params("contact", contact)
            .execute(callback)
    }

    /**
     * 获取分享数据
     * @param type 1:商品  2 ：贴吧
     */
    fun getShareData(id: String, type: Int, callback: JsonCallback<GetShareDataResponse>) {
        OkGo.post<GetShareDataResponse>("$BASEURL_API/public/get_download")
            .params("id", id)
            .params("type", type)
            .execute(callback)
    }


    fun cancelRequest(tag: String) {
        OkGo.getInstance()
            .cancelTag(tag)
    }
}
