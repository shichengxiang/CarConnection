package com.linruan.carconnection.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.SoundPool
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.linruan.carconnection.R
import com.linruan.carconnection.entities.PushMessage
import com.linruan.carconnection.service.MyClickReceiver


/**
 * 项目名称：CarConnection
 * 类描述：
 * 创建人：shichengxiang
 * 创建时间：2020/8/3 16:30
 * @version
 */
object NotificationUtil {
    var channelId=100
    fun sendSimpleNotification(context: Context,pushMessage:PushMessage){
        var nm=context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            channelId++
            createNotificationChannel(context,nm, channelId.toString(),"chelianyizhong")
        }
        var builder= createNotificationBuilder(context,pushMessage,channelId.toString())
        nm.notify(0,builder.build())
//        nm.notify(0,)
    }

    fun createNotificationBuilder(context: Context,pushMessage:PushMessage,channelId: String):NotificationCompat.Builder{
        // 通知栏点击接收者
        val i = Intent(context, MyClickReceiver::class.java)
        i.action = MyClickReceiver.ACTION
        i.putExtra("data", pushMessage)
        val pendingIntent =
            PendingIntent.getBroadcast(context, 101, i, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context,channelId)
        builder.setContentIntent(pendingIntent)
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setContentTitle(pushMessage.title)
        builder.setContentText(pushMessage.content)
        builder.setAutoCancel(true)
        builder.setDefaults(Notification.DEFAULT_ALL)
        val sound= Uri.parse("android.resource://" + context.packageName
                + "/" + R.raw.alarm)
        builder.setSound(sound)
//        val stoneRing = "通知声音地址"
//        if (!TextUtils.isEmpty(stoneRing)) {
//            builder.setSound(Uri.parse(stoneRing))
//        }
//        builder.setDefaults(Notification.DEFAULT_SOUND)
        return builder
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(context: Context, nm:NotificationManager, id:String, name:String){
        var channel=NotificationChannel(id,name,NotificationManager.IMPORTANCE_HIGH)
        channel.enableLights(true)//开启指示灯
        channel.enableVibration(true) //开启震动
        channel.lightColor=Color.RED//指示灯颜色
        channel.lockscreenVisibility=Notification.VISIBILITY_PRIVATE//设置是否在锁屏屏幕上显示该通知
        channel.setShowBadge(true)//是否显示角标
        channel.setBypassDnd(true) //是否绕过免打扰
        channel.vibrationPattern= longArrayOf(100,200,300,400)
        //最后在manager中创建该通知渠道
        nm.createNotificationChannel(channel)
    }
}
