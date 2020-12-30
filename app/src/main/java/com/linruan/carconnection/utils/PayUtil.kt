package com.linruan.carconnection.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.LogUtils
import com.linruan.carconnection.AliPayActivity
import com.linruan.carconnection.entities.MessageEvent
import com.linruan.carconnection.entities.WeixinPayRes
import com.linruan.carconnection.listener.OnPayResultListener
import com.linruan.carconnection.moudle.MessageView
import com.linruan.carconnection.toast
import com.tencent.mm.opensdk.modelmsg.*
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage.IMediaObject
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * author：shichengxiang on 2020/6/13 10:18
 * email：1328911009@qq.com
 */
object PayUtil {
    object Platform {
        const val platform_weixin_session = SendMessageToWX.Req.WXSceneSession
        const val platform_weixin_friend = SendMessageToWX.Req.WXSceneTimeline
    }

    const val APP_ID = "wx99c91d95427d9d09"
    const val APP_SECRET = "7eca6f54f60dc1da71cd8f923bd8ab5d"
    private var mOnPayResultListener:OnPayResultListener?=null
    var mApi: IWXAPI? = null
    fun login(context: Context) {
        mApi = WXAPIFactory.createWXAPI(context, APP_ID, false)
        mApi?.registerApp(APP_ID)
        if (mApi?.isWXAppInstalled == false) {
            Util.toast("未找到微信")
            return
        }
        //授权登录
        var req = SendAuth.Req()
            .apply {
                scope = "snsapi_userinfo"
                state = "${System.currentTimeMillis()}linruan"
            }
        mApi?.sendReq(req)
    }

    /**
     * 分享
     */
    fun weixinShare(
        context: Context,
        title: String,
        txt: String,
        desc: String,
        image: Int = 0,
        skipUrl: String = "",
        platform: Int
    ) {
        if (mApi == null) {
            mApi = WXAPIFactory.createWXAPI(context, APP_ID, false)
            mApi?.registerApp(APP_ID)
        }
        var transaction = ""
        var obj: IMediaObject? = null
        var resBitmap: Bitmap? = null
        if (!skipUrl.isNullOrBlank()) {
            transaction = "webpage${System.currentTimeMillis()}"
            resBitmap = BitmapFactory.decodeResource(context.resources, image)
            obj = WXWebpageObject().apply {
                webpageUrl = skipUrl
            }
        } else if (image != 0) {
            transaction = "img${System.currentTimeMillis()}"
            resBitmap = BitmapFactory.decodeResource(context.resources, image)
            obj = WXImageObject(resBitmap)
        } else {
            transaction = "text${System.currentTimeMillis()}"
            obj = WXTextObject(txt)
        }
        var media = WXMediaMessage(obj).apply {
            this.title = title
            this.description = desc
            this.mediaObject = obj
            if (resBitmap != null) {
                this.thumbData = ImageUtils.bitmap2Bytes(resBitmap, Bitmap.CompressFormat.JPEG, 60)
            }
        }
        resBitmap?.recycle()
        var req = SendMessageToWX.Req()
            .apply {
                transaction = "image${System.currentTimeMillis()}"
                message = media
                scene = platform
                //            userOpenId
            }
        mApi?.sendReq(req)
    }

    fun wxPay(context: Context, wxPayRes: WeixinPayRes,onPayResultListener: OnPayResultListener?) {
        this.mOnPayResultListener=onPayResultListener
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this)
        }
        if (mApi == null) {
            mApi = WXAPIFactory.createWXAPI(context, APP_ID, false)
            mApi?.registerApp(APP_ID)
        }
        if (wxPayRes.appid.isNullOrEmpty() || wxPayRes.partnerid.isNullOrEmpty() || wxPayRes.prepayid.isNullOrEmpty() || wxPayRes.packagevalue.isNullOrEmpty() || wxPayRes.noncestr.isNullOrEmpty() || wxPayRes.timestamp.isNullOrEmpty() || wxPayRes.sign.isNullOrEmpty()) {
            context.toast("参数异常")
            return
        }
        var payReq = PayReq().apply {
            appId = wxPayRes.appid
            partnerId = wxPayRes.partnerid
            prepayId = wxPayRes.prepayid
            packageValue = wxPayRes.packagevalue
            nonceStr = wxPayRes.noncestr
            timeStamp = wxPayRes.timestamp
            sign = wxPayRes.sign
        }
        mApi?.sendReq(payReq)
    }
    fun aliPay(activity: Activity?, orderInfo:String,onPayResultListener: OnPayResultListener?){
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this)
        }
        this.mOnPayResultListener=onPayResultListener
        AliPayActivity.startPay(activity, orderInfo)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPayResult(event:MessageEvent){
        LogUtils.e("支付回调了")
        if(event.code==MessageEvent.WXPAY_RESULT){
            var result=event.message[0]
            when(result){
                "0"-> mOnPayResultListener?.onSuccess()
                "-2"-> mOnPayResultListener?.onCancel()
                else -> mOnPayResultListener?.onFail(event.message[1])
            }
        }else if(event.code==MessageEvent.ALIPAY_RESULT){
            var result=event.message[0]
            if("9000"==result){
                mOnPayResultListener?.onSuccess()
            }else if("6001"==result){
                mOnPayResultListener?.onCancel()
            }else{
                mOnPayResultListener?.onFail("支付异常")
            }
        }
    }

}
