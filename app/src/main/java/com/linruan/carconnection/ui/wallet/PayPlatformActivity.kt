package com.linruan.carconnection.ui.wallet

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import com.linruan.carconnection.*
import com.linruan.carconnection.dialog.LoadingDialog
import com.linruan.carconnection.entities.Master
import com.linruan.carconnection.entities.net.GetPayResponse
import com.linruan.carconnection.listener.OnPayResultListener
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.linruan.carconnection.utils.PayUtil
import com.linruan.carconnection.utils.Util
import kotlinx.android.synthetic.main.activity_payplatform.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*
import org.greenrobot.eventbus.EventBus

/**
 * author：shichengxiang on 2020/6/18 13:57
 * email：1328911009@qq.com
 */
class PayPlatformActivity : BaseActivity() {

    private var currentPayWay = Constant.PAY_TYPE_WEIXIN

    companion object {
        const val ORDERNO_KEY = "orderNo"
        const val RESULTCODE_PAYSUCCESS = 1
    }

    private var mOnPayResultListener = object : OnPayResultListener {
        override fun onSuccess() {
            toast("订单支付成功")
            back()
        }

        override fun onFail(err: String) {
            toast(err)
        }

        override fun onCancel() {
            toast("已取消")
        }

    }

    override fun getLayout() = R.layout.activity_payplatform

    override fun initView() {
        var orderNo = ""
        if (intent.hasExtra(ORDERNO_KEY)) {
            orderNo = intent.getStringExtra(ORDERNO_KEY)
        }
        toolbar.apply {
            getLeftImageView().setImageResource(R.drawable.ic_back_white)
            tvTitle.apply {
                text = "支付平台"
                setTextColor(ContextCompat.getColor(this@PayPlatformActivity, R.color.white))
            }
        }
        //动态平台支付设置
        if (UserManager.getUser()?.isMaster == true) {
            tvAmount.text = Util.decimalFormat2(Constant.PLATFROM_WORKER_PRICE)
        } else {
            tvAmount.text = Util.decimalFormat2(Constant.PLATFORM_USER_PRICE)
        }
        btnPay.setOnClickListener { pay(orderNo) }
        rgGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbWeixin -> currentPayWay = Constant.PAY_TYPE_WEIXIN
                R.id.rbAlipay -> currentPayWay = Constant.PAY_TYPE_ZHIFUBAO
                R.id.rbBalance -> currentPayWay = Constant.PAY_TYPE_BALANCE
            }
        }
//        if(!EventBus.getDefault().isRegistered(this)){
//            EventBus.getDefault().register(this)
//        }
        registerOrderReceiver()
    }

    /**
     * 支付
     */
    private fun pay(orderNo: String) {
        LoadingDialog.loading(this)
        Client.getInstance().repairOrderPay(
            orderNo,
            currentPayWay,
            UserManager.getUser()?.isMaster ?: false,
            object : JsonCallback<GetPayResponse>() {
                override fun onFinish() {
                    LoadingDialog.dismiss()
                }

                override fun onSuccess(response: com.lzy.okgo.model.Response<GetPayResponse?>?) {
                    var body = response?.body()
                    if (body?.isSuccess() == true) {
                        if (currentPayWay == Constant.PAY_TYPE_BALANCE) {
                            mOnPayResultListener.onSuccess()
                        } else if (currentPayWay == Constant.PAY_TYPE_WEIXIN) {
                            var pay = body?.data
                            if (pay != null) {
                                PayUtil.wxPay(this@PayPlatformActivity, pay, mOnPayResultListener)
                            } else {
                                mOnPayResultListener.onFail("微信支付参数异常")
                            }
                        } else if (currentPayWay == Constant.PAY_TYPE_ZHIFUBAO) {
                            var orderInfo = response?.body()?.data?.alipay
                            PayUtil.aliPay(
                                this@PayPlatformActivity,
                                orderInfo ?: "",
                                mOnPayResultListener
                            )
                        }
                    } else {
                        toast(body?.msg ?: "订单支付失败")
                    }
                }
            })

    }

    /**
     * 支付成功后
     */
    fun back() {
        setResult(RESULTCODE_PAYSUCCESS, Intent().apply { putExtra("isSuccess", true) })
        finish()
    }
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onEvent(event:MessageEvent){
//        logE(Gson().toJson(event))
//        if(event.code==MessageEvent.WXPAY_RESULT){
//            var msg=event.message
//            if(msg.isNotEmpty() && msg.size>0){
//                var result=msg[0].toInt()
//                if(result==0){
//                    //支付成功
////                    back()
//                    mOnPayResultListener.onSuccess()
//                }else if(result==-2){
//                    //用户取消
//                    mOnPayResultListener.onCancel()
////                    toast("已取消")
//                }else{
//                    mOnPayResultListener.onFail("支付失败")
//                    //错误
////                    toast("支付失败")
//                }
//            }
//        }
//    }
    /**
     * 监听支付消息
     */
    private var mPayReceiver: PayReceiver? = null

    private fun registerOrderReceiver() {
        mPayReceiver = PayReceiver()
        var filter = IntentFilter().apply {
            addAction(Constant.BROADCAST_ACTION_PAYRESULT_RECEIVED)
        }
//        mPayReceiver?.setCallback(object : MCallBack<Master> {
//            override fun call(t: Master, position: Int) {
//                changeUiWhenPlaceOrder(ORDERSTATUS_SUCCESS)
//            }
//        })
//        registerReceiver(mPayReceiver, filter)
    }

    private fun unRegisterOrderReceiver() {
//        unregisterReceiver(mPayReceiver)
    }

    //不用了
    inner class PayReceiver : BroadcastReceiver() {
        private var mCallBack: MCallBack<Master>? = null
        override fun onReceive(context: Context?, intent: Intent?) {
            var code = intent?.getIntExtra("code", -1)
        }

        fun setCallback(callback: MCallBack<Master>) {
            this.mCallBack = callback
        }

    }

    override fun onDestroy() {
        super.onDestroy()
//        EventBus.getDefault().unregister(this)
        unRegisterOrderReceiver()
    }

}
