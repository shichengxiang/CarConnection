package com.linruan.carconnection.utils

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.ColorMatrixColorFilter
import android.net.Uri
import android.telephony.TelephonyManager
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import cn.jiguang.share.android.api.JShareInterface
import cn.jiguang.share.android.api.PlatActionListener
import cn.jiguang.share.android.api.ShareParams
import cn.jiguang.share.qqmodel.QQ
import cn.jiguang.share.qqmodel.QZone
import cn.jiguang.share.wechat.Wechat
import cn.jiguang.share.wechat.WechatMoments
import com.amap.api.maps.CoordinateConverter
import com.amap.api.maps.model.LatLng
import com.blankj.utilcode.util.LogUtils
import com.linruan.carconnection.*
import com.linruan.carconnection.dialog.CommonDialog
import com.linruan.carconnection.entities.net.GetPayResponse
import com.linruan.carconnection.listener.OnPayResultListener
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.lzy.okgo.model.Response
import java.lang.Exception
import java.lang.StringBuilder
import java.math.BigDecimal
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.floor

object Util {
    private var mApplication: Context? = null
    fun init(context: Context?) {
        mApplication = context
    }

    fun sHA1(context: Context): String? {
        try {
            val info = context.packageManager.getPackageInfo(
                context.packageName, PackageManager.GET_SIGNATURES
            )
            val cert = info.signatures[0].toByteArray()
            val md = MessageDigest.getInstance("SHA1")
            val publicKey = md.digest(cert)
            val hexString = StringBuffer()
            for (i in publicKey.indices) {
                val appendString =
                    Integer.toHexString(0xFF and publicKey[i].toInt())
                        .toUpperCase(Locale.US)
                if (appendString.length == 1) hexString.append("0")
                hexString.append(appendString)
                hexString.append(":")
            }
            val result = hexString.toString()
            return result.substring(0, result.length - 1)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return null
    }

    fun stringToDouble(str: String?): Double {
        var res = 0.0
        if (!str.isNullOrBlank()) {
            try {
                res = str.toDouble()
            } catch (e: Exception) {
            }
        }
        return res
    }

    /**
     * 秒 转 时间格式
     */
    fun exchangeSecondToTimeFormat(seconds: Int): String {
        var stringBuilder = StringBuilder()
        var formater = Formatter(stringBuilder, Locale.CHINA)
        var second = seconds % 60
        var minute = seconds / 60 % 60
        return formater.format("%02d:%02d", minute, second)
            .toString()
    }

    fun callPhone(context: Context?, phone: String) {
        var intent = Intent(Intent.ACTION_DIAL);
        var data = Uri.parse("tel:$phone");
        intent.data = data;
        context?.startActivity(intent);
    }

    /**
     * 切换buttons 状态
     */
    fun switchButtonsSelected(selected: View, vararg views: View) {
        selected.isSelected = true
        if (views.isNotEmpty()) {
            views.forEach {
                if (it != selected)
                    it.isSelected = false
            }
        }
    }

    /**
     * 切换buttons 是否可见
     */
    fun switchButtonsVisibility(selected: View, vararg views: View) {
        selected.visibility = View.VISIBLE
        if (views.isNotEmpty()) {
            views.forEach {
                if (it != selected)
                    it.visibility = View.GONE
            }
        }
    }

    /**
     * 转化 金币 为￥22.00格式
     */
    fun moneyformat(money: BigDecimal): SpannableString {
        var sb = StringBuilder()
        sb.append("¥")
        val str = money.setScale(2, BigDecimal.ROUND_DOWN)
            .toString()
        var a = str.substring(0, str.length - 2)
        var b = str.substring(str.length - 2)
        sb.append(a)
            .append(b)
        var span = SpannableString(sb.toString())
        span.setSpan(AbsoluteSizeSpan(10, true), 0, 1, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        span.setSpan(
            AbsoluteSizeSpan(10, true),
            sb.length - 2,
            sb.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return span
    }

    fun setIconColor(imageView: ImageView, r: Float, g: Float, b: Float, a: Float) {
        var colorMatrix = floatArrayOf(
            0f, 0f, 0f, 0f, r,
            0f, 0f, 0f, 0f, g,
            0f, 0f, 0f, 0f, b,
            0f, 0f, 0f, a / 255, 0f
        )
        imageView.setColorFilter(ColorMatrixColorFilter(colorMatrix))
    }

    fun px2dp(px: Int): Int {
        return ((mApplication?.resources?.displayMetrics?.density ?: 2f) * px).toInt()
    }

    fun getScreenWidth(): Int {
        return mApplication?.resources?.displayMetrics?.widthPixels ?: 0
    }

    fun getScreenHeight(): Int {
        return mApplication?.resources?.displayMetrics?.heightPixels ?: 0
    }

    /**
     * tag 标签
     */
    fun getReasonTags(context: Context?, txt: String): View {
        var view = LayoutInflater.from(context)
            .inflate(R.layout.item_tag_troublereason, null)
        view.findViewById<TextView>(R.id.tvReason).text = txt
        return view
    }

    fun isAppInstalled(context: Context?, packageName: String): Boolean {
        var packageInfo: PackageInfo? = null
        try {
            packageInfo=context?.packageManager?.getPackageInfo(packageName, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return packageInfo != null
    }

    /**
     * 开启第三方导航
     */
    fun startThirdNavi(
        context: Context?,
        packageName: String,
        fromLat: Double,
        fromLng: Double,
        toLat: Double,
        toLng: Double,
        fromName: String,
        toName: String
    ) {
        try {
            when (packageName) {
                Constant.PACKAGENAME_GAODE -> {
                    val intent = Intent().apply {
                        setPackage(packageName)
                        action = Intent.ACTION_VIEW
                        addCategory(Intent.CATEGORY_DEFAULT)
                        data =
                            Uri.parse("amapuri://route/plan/?sid=&slat=$fromLat&slon=$fromLng&sname=$fromName&did=&dlat=$toLat&dlon=$toLng&dname=$toName&dev=0&t=0")
                    }
                    context?.startActivity(intent)
                }
                Constant.PACKAGENAME_BAIDU -> {
                    val toLatLng =
                        CoordinateConverter(context).from(CoordinateConverter.CoordType.MAPABC)
                            .coord(LatLng(toLat, toLng))
                            .convert()
                    val intent = Intent().apply {
                        setPackage(packageName)
                        action = Intent.ACTION_VIEW
                        addCategory(Intent.CATEGORY_DEFAULT)
                        data =
                            Uri.parse("baidumap://map/direction?destination=latlng:${toLatLng.latitude},${toLatLng.longitude}|name:$toName&coord_type=gcj02&mode=driving")
                    }
                    context?.startActivity(intent)
                }
                Constant.PACKAGENAME_TENCENT -> {
                    val intent = Intent()
                    val toLatLng =
                        CoordinateConverter(context).from(CoordinateConverter.CoordType.MAPABC)
                            .coord(LatLng(toLat, toLng))
                            .convert()
                    intent.setData(Uri.parse("qqmap://map/routeplan?type=drive&to=$toName&tocoord=${toLatLng.latitude},${toLatLng.longitude}&policy=1&referer=车联益众"))
                    context?.startActivity(intent)
                }
            }
        } catch (e: Exception) {
            LogUtils.e(e.message)
            context?.toast("地图打开失败")
        }

    }

    fun toast(msg: String?) {
        if (!msg.isNullOrBlank()) {
            mApplication?.toast(msg)
        }
    }

    fun sharePlatform(
        context: Context?,
        shareParams: ShareParams,
        actionListener: PlatActionListener?
    ) {
        var shareDialog = object : CommonDialog(context!!, R.layout.layout_dialog_share_platform) {
            override fun initView(root: View) {
                window?.setWindowAnimations(R.style.BottomDialogAnim)
                window?.setGravity(Gravity.BOTTOM)
                if (shareParams.title?.length ?: 0 >= 30) {
                    shareParams.title = shareParams.title.substring(0, 25)
                }
                if (shareParams.text?.length ?: 0 >= 40) {
                    shareParams.text = shareParams.text.substring(0, 35)
                }
                root.findViewById<View>(R.id.btnCancel)
                    .setOnClickListener { dismiss() }
                root.findViewById<View>(R.id.ivWeixin)
                    .setOnClickListener {
                        dismiss()
                        JShareInterface.share(Wechat.Name, shareParams, actionListener)
                    }
                root.findViewById<View>(R.id.ivWeixinFriend).setOnClickListener {
                    dismiss()
                    JShareInterface.share(WechatMoments.Name, shareParams, actionListener)
                }
                root.findViewById<View>(R.id.ivQQ).setOnClickListener {
                    dismiss()
                    JShareInterface.share(QQ.Name, shareParams, actionListener)
                }
                root.findViewById<View>(R.id.ivZone).setOnClickListener {
                    dismiss()
                    JShareInterface.share(QZone.Name, shareParams, actionListener)
                }
                root.findViewById<View>(R.id.ivCopyLink).setOnClickListener {
                    dismiss()
                    if (context != null) {
                        clipboard(context, shareParams.text)
                        toast("复制链接成功")
                    }
                }
            }
        }
        shareDialog?.show()
    }

    /**
     * 时间戳转时间格式
     */
    fun getDataToString(millSecond: String?, pattern: String): String {
        var result = ""
        tryCatch {
            var mill = ""
            if (millSecond?.length ?: 0 < 11) {
                mill = "${millSecond ?: ""}000"
            }
            var format = SimpleDateFormat(pattern, Locale.CHINA)
            result = format.format(Date(mill.toLong()))
        }
        return result
    }

    /**
     * 计算时间戳与当前时间差
     */
    fun getDiffTimeToCurrent(milSecond: Long): String {
        var result = ""
        var currentTime = System.currentTimeMillis()
        var diff = (currentTime - milSecond) / 1000
        var hour = floor((diff / 3600).toDouble()).toInt()
        var day = floor((diff / (3600 * 24)).toDouble()).toInt()
        var month = floor((diff / (3600 * 24 * 30)).toDouble()).toInt()
        if (hour > 0) {
            result = "$hour 小时前"
        }
        if (month > 0) {
            result = "$month 月前"
        } else if (day > 0) {
            result = "$day 天前"
        } else if (hour > 0) {
            result = "$hour 小时前"
        } else {
            result = "刚刚"
        }
        return result
    }

    /**
     * 保留两位小数
     */
    fun decimalFormat2(d: Double): String {
        var format = DecimalFormat("0.00")
        return format.format(d)
    }

    /**
     * 判断服务是否已经正在运行
     * @param mContext   上下文对象
     * @param className  Service类的全路径类名 "包名+类名" 如com.demo.test.MyService
     * @return
     */
    fun isServiceRunning(mContext: Context, className: String): Boolean {
        var myManager = mContext
            .getApplicationContext().getSystemService(
                Context.ACTIVITY_SERVICE
            ) as ActivityManager
        var runningService = myManager
            .getRunningTasks(30) as ArrayList<ActivityManager.RunningServiceInfo>
        for (i in 0..runningService.size) {
            LogUtils.i(className + "》》》》" + runningService.get(i).service.getClassName())
            if (runningService.get(i).service.getClassName().toString() == className) {
                return true
            }
        }
        return false;
    }


    /**
     * 判断某个界面是否在前台
     *
     * @param context
     * @param className   某个界面名称
     *
     */
    fun isActivityForeground(context: Context, className: String): Boolean {
        if (context == null || className.isBlank()) {
            return false;
        }

        var am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        var list = am.getRunningTasks(1) as List<ActivityManager.RunningTaskInfo>?
        if (list != null && list.isNotEmpty()) {
            var cpn = list.get(0).topActivity
            LogUtils.i(className + "》》》》" + cpn?.className)
            if (className == cpn?.className) {
                return true
            }
        }

        return false;
    }


    /**
     * 保留一位小数
     */
    fun decimalFormat1(d: Double): String {
        var format = DecimalFormat("0.0")
        return format.format(d)
    }

    /**
     * 商品支付
     */
    fun goodsPay(
        context: Context,
        orderNo: String,
        payway: String,
        listener: OnPayResultListener?
    ) {
        Client.getInstance()
            .goodsOrderPay(orderNo, payway, object : JsonCallback<GetPayResponse>() {
                override fun onSuccess(response: Response<GetPayResponse?>?) {
                    var body = response?.body()
                    if (body?.isSuccess() == true) {
                        if (payway == Constant.PAY_TYPE_BALANCE) {
                            listener?.onSuccess()
                        } else if (payway == Constant.PAY_TYPE_WEIXIN) {
                            var pay = body?.data
                            if (pay != null) {
                                PayUtil.wxPay(context, pay, listener)
                            } else {
                                listener?.onFail("微信支付异常")
                            }
                        } else if (payway == Constant.PAY_TYPE_ZHIFUBAO) {
                            var orderInfo = response?.body()?.data?.alipay
                            PayUtil.aliPay(context as Activity?, orderInfo ?: "", listener)
                        }

                    } else {
                        listener?.onFail(body?.msg ?: "支付失败，请联系客服")
                    }
                }
            })
    }

    fun getPhone(context: Context?): String {
        var number = ""
        try {
            val manager = context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_SMS
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_PHONE_NUMBERS
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                number = manager?.line1Number
                if (number.length > 11) {
                    number = number.substring(number.length - 11, number.length)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return number
    }

    fun sizeImageHeight(expectWidth: Float, realWidth: Float, realHeight: Float): Float {
        return expectWidth * realHeight / realWidth
    }

    /**
     * 复制文字到剪切板
     */
    fun clipboard(context: Context, txt: String) {
        //获取剪贴板管理器：
        var cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
// 创建普通字符型ClipData
        var mClipData = ClipData.newPlainText("Label", txt);
// 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData)
    }

    /**
     * <1000 不转
     * 数量转K为单位 并保留两位
     */
    fun numToK(num: Int, unit: String = "k"): String {
        return if (num < 1000) "$num" else "${decimalFormat2(num.toDouble() / 1000)}$unit"
    }

    fun numToW(num: Int, unit: String = "万"): String {
        return if (num < 10000) "$num" else "${decimalFormat2(num.toDouble() / 10000)}$unit"
    }
}
