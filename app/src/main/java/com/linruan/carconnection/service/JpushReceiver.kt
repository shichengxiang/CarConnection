package com.linruan.carconnection.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import cn.jpush.android.api.*
import cn.jpush.android.service.JPushMessageReceiver
import com.blankj.utilcode.util.LogUtils
import com.google.gson.Gson
import com.linruan.carconnection.Constant
import com.linruan.carconnection.UserManager
import com.linruan.carconnection.entities.MessageEvent
import com.linruan.carconnection.entities.PushMessage
import com.linruan.carconnection.logE
import com.linruan.carconnection.toast
import com.linruan.carconnection.ui.MainActivity
import com.linruan.carconnection.utils.NotificationUtil
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

/**
 * author：shichengxiang on 2020/6/2 09:23
 * email：1328911009@qq.com
 */
class JpushReceiver : JPushMessageReceiver() {
    override fun onMessage(context: Context, customMessage: CustomMessage) {
        super.onMessage(context, customMessage)
        logE("onMessage  " + Gson().toJson(customMessage))
        var extra = customMessage.extra.replace("\\", "")
        var json = JSONObject()
        var title = customMessage.title
        var msg = customMessage.message
        var type = 0
        var id=""
        //接单
//        NotificationUtils.notify(1, object : NotificationCompat.Builder(context, "1"),
//            Utils.Consumer<NotificationCompat.Builder> {
//            override fun accept(t: NotificationCompat.Builder?) {
//
//                t?.setContentTitle("是圣诞节饭")
//                t?.setContentText("是圣诞节饭")
//            }
//        })
        if (extra.isNotBlank()) {
            json = JSONObject(extra)
            type = json.optInt("type", 0)
            id=json.optString("content_id","")
        }
        var pushMsg = PushMessage().apply {
            this.type = type
            this.title = title
            this.content = msg
            this.id=id
        }
        NotificationUtil.sendSimpleNotification(context, pushMsg)
        when (type) {
            Constant.NOTIFICATION_TYPE_BATTLED -> {
                //已经被接单
//                context.toast("您已被接单")
                EventBus.getDefault()
                    .post(MessageEvent(MessageEvent.REFRESH_HOMEFRAGMENTRUNNINGORDER))
            }
            Constant.NOTIFICATION_TYPE_MASTERISRECEIVED -> {
                EventBus.getDefault()
                    .post(MessageEvent(MessageEvent.REFRESH_HOMEFRAGMENTRUNNINGORDER))
            }
            Constant.NOTIFICATION_TYPE_REPAIRORDER_WAITCONFIRM -> {
                LogUtils.i("确认待更新")
                EventBus.getDefault()
                    .post(MessageEvent(MessageEvent.REFRESH_REPAIRORDERD_TOCUSTOMER))
            }
            Constant.NOTIFICATON_TYPE_CANCELORDER->{
                //取消订单
                LogUtils.i("取消订单通知")
                EventBus.getDefault().post(MessageEvent.REFRESH_HOMEFRAGMENTRUNNINGORDER)
            }
            Constant.NOTIFICATION_TYPE_REPAIRSUCCESS, Constant.NOTIFICATION_TYPE_REPAIRFAIL -> {
                LogUtils.i("维修结果发送更新消息")
                EventBus.getDefault()
                    .post(MessageEvent(MessageEvent.REFRESH_REPAIRORDERD_TOMASTER))
            }
            Constant.NOTIFICATION_TYPE_SENDEDGOODS,Constant.NOTIFICATION_TYPE_RETURNGOODS->{
                //发货通知 退货通知
                EventBus.getDefault()
                    .post(MessageEvent(MessageEvent.REFRESH_STOREORDERACTIVITY))
            }
        }
    }

    override fun onConnected(p0: Context?, p1: Boolean) {
        super.onConnected(p0, p1)
        logE("onConnected== $p1")
    }

    override fun onNotifyMessageOpened(context: Context?, message: NotificationMessage?) {
        try { //打开自定义的Activity
            val i = Intent(context, MainActivity::class.java)
            val bundle = Bundle()
            bundle.putString(JPushInterface.EXTRA_NOTIFICATION_TITLE, message?.notificationTitle)
            bundle.putString(JPushInterface.EXTRA_ALERT, message?.notificationContent)
            i.putExtras(bundle)
            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            context?.startActivity(i)
        } catch (throwable: Throwable) {
        }
    }

    override fun onCommandResult(p0: Context?, p1: CmdMessage?) {
        super.onCommandResult(p0, p1)
        logE("onCommandResult" + Gson().toJson(p1))
    }

    override fun onAliasOperatorResult(p0: Context?, p1: JPushMessage?) {
        super.onAliasOperatorResult(p0, p1)
        logE("onAliasOperatorResult" + Gson().toJson(p1))
    }

    override fun onTagOperatorResult(p0: Context?, p1: JPushMessage?) {
        super.onTagOperatorResult(p0, p1)
        logE("onTagOperatorResult" + Gson().toJson(p1))
    }

    override fun onCheckTagOperatorResult(p0: Context?, p1: JPushMessage?) {
        super.onCheckTagOperatorResult(p0, p1)
        logE("onCheckTagOperatorResult" + Gson().toJson(p1))
    }

    private fun handleMessage(context: Context, msg: String) {

    }
}
