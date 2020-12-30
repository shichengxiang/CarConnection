package com.linruan.carconnection.ui.user

import android.Manifest
import android.graphics.Color
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.animation.AnimationUtils
import cn.jiguang.share.android.api.AuthListener
import cn.jiguang.share.android.api.JShareInterface
import cn.jiguang.share.android.api.Platform
import cn.jiguang.share.android.model.BaseResponseInfo
import cn.jiguang.share.android.model.UserInfo
import cn.jiguang.share.wechat.Wechat
import cn.jpush.android.api.JPushInterface
import cn.smssdk.EventHandler
import cn.smssdk.SMSSDK
import com.google.gson.Gson
import com.linruan.carconnection.*
import com.linruan.carconnection.dialog.LoadingDialog
import com.linruan.carconnection.dialog.TipDialog
import com.linruan.carconnection.entities.MessageEvent
import com.linruan.carconnection.moudle.LoginPresenter
import com.linruan.carconnection.moudle.LoginView
import com.linruan.carconnection.ui.MainActivity
import com.linruan.carconnection.ui.WebViewActivity
import com.linruan.carconnection.ui.master.MasterAuthActivity
import com.mob.MobSDK
import kotlinx.android.synthetic.main.activity_login.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import kotlin.collections.HashMap

class LoginActivity : BaseActivity(), LoginView, EasyPermissions.PermissionCallbacks {

    private var timer: CountDownTimer? = null
    private var isCountDown = false
    private var hasSms = true
    private var mPresenter: LoginPresenter? = null
    private val ROLETIP_MASTER = "修车师傅端登录"
    private val ROLETIP_CUSTOMER = "手机快速登录"
    private val ROLETIP_TOMASTER = "修车师傅登录"
    private val ROLETIP_TOCUSTOMER = "返回用户登录"
    private var isSelectWorker = false //仅用于标记登录时的用户选择
    private var permissions = arrayOf(
//        Manifest.permission.READ_SMS,
//        Manifest.permission.READ_PHONE_NUMBERS,
//        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    companion object {
        val MESSAGE_RECEIVED_ACTION = "com.linruan.carconnection.MESSAGE_RECEIVED_ACTION"
        val KEY_TITLE = "title"
        val KEY_MESSAGE = "message"
        val KEY_EXTRAS = "extras"
    }

    private var eventHandler: EventHandler? = object : EventHandler() {
        override fun afterEvent(event: Int, result: Int, data: Any?) {
            logE(result.toString() + Gson().toJson(data))
            if (result == SMSSDK.RESULT_COMPLETE) {
                runOnUiThread { tvGetCode.isEnabled = true }
                //回调完成
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功
                    //登录
                    mPresenter?.login(
                        etPhone.text.toString(),
                        if (isSelectWorker) 2 else 1,
                        "",
                        "",
                        ""
                    )
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //获取验证码成功
                    runOnUiThread {
                        hasSms = true
                        toast("验证码发送成功")
                        //                        SMSSDK.setAskPermisionOnReadContact(true)// 读取短信
                    }
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    //返回支持发送验证码的国家列表
//                    LoadingDialog.dismiss()
                }
            } else {
                runOnUiThread {
                    tvGetCode.isEnabled = true
                    LoadingDialog.dismiss()
                    toast((data as Throwable).message ?: "验证码错误")
                    finishCountDown()
                }
            }
        }
    }

    override fun getLayout() = R.layout.activity_login
    override fun initView() {
//        EventBus.getDefault()
//            .register(this)
        mPresenter = LoginPresenter(this)
        ivClose.setOnClickListener {
            onBackPressed()
//            UserManager.setIsMaster(isMaster)
//            Router.openUI(this, MainActivity::class.java)
//            finish()
        }
        btnLogin.setOnClickListener {
            checkLogin()
        }
        tvUsePasswordLogin.setOnClickListener {
            Router.openUI(this, PasswordLoginActivity::class.java)
            finish()
        }
        var vs = getString(R.string.userprotol_privacypolicy)
        var span = SpannableString(vs)
        val start1 = vs.indexOf("《")
        val end1 = vs.indexOf("》", start1) + 1
        val start2 = vs.indexOf("《", end1)
        val end2 = vs.indexOf("》", start2) + 1
        span.setSpan(
            ForegroundColorSpan(Color.parseColor("#FF0ED4D4")),
            start1,
            end1,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        span.setSpan(
            ForegroundColorSpan(Color.parseColor("#FF0ED4D4")),
            start2,
            end2,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        span.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
//                showProtocolDialog()
                WebViewActivity.openWebView(
                    this@LoginActivity,
                    Constant.URL_USERPROTOCOL,
                    "用户协议"
                )
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.bgColor = Color.TRANSPARENT
                ds.isUnderlineText = false
            }
        }, start1, end1, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        span.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
//                showProtocolDialog()
                WebViewActivity.openWebView(
                    this@LoginActivity,
                    Constant.URL_PRIVACY,
                    "隐私政策"
                )
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.bgColor = Color.TRANSPARENT
                ds.isUnderlineText = false
            }
        }, start2, end2, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvShowProtocol.movementMethod = LinkMovementMethod.getInstance()
        tvShowProtocol.text = span
        tvGetCode.setOnClickListener { getSms() }
        var id = JPushInterface.getRegistrationID(this)
        switchRole.setOnClickListener { switchRole() }
        withWxLogin.setOnClickListener {
            //            WeixinUtil.login(this)
            if (JShareInterface.isAuthorize(Wechat.Name)) {
                getUserInfo()
            } else {
                JShareInterface.authorize(Wechat.Name, object : AuthListener {
                    override fun onComplete(p0: Platform?, p1: Int, p2: BaseResponseInfo?) {
                        getUserInfo()
                    }

                    override fun onCancel(p0: Platform?, p1: Int) {
                        toast("授权取消")
                    }

                    override fun onError(p0: Platform?, p1: Int, errorCode: Int, p3: Throwable?) {
                        toast("授权异常：${errorCode}")
                    }

                })
            }
        }
        requestPermission()
    }

    //请求验证码
    private fun getSms() {
        if (etPhone.text.toString()
                .isBlank()
        ) {
            toast("手机号不能为空")
            return
        }
        hasSms = false
        MobSDK.submitPolicyGrantResult(false, null) // 默认未查看mob协议
        SMSSDK.registerEventHandler(eventHandler) //注册监听
        SMSSDK.getVerificationCode("86", etPhone.text.toString()) //发送验证码
        countDown()
    }

    /**
     * 验证验证码并登录
     */
    private fun checkLogin() {
        if (!cbRedProtocol.isChecked) {
            toast("请阅读并同意用户协议与隐私政策")
            return
        }
        val sms = etInputSms.text.toString()
        val phone = etPhone.text.toString()
        if (phone.isBlank()) {
            toast("手机号不能为空")
            return
        }
        if (sms.isBlank()) {
            toast("验证码不能为空")
            return
        }
        if (!hasSms) {
            toast("验证码验证失败")
            return
        }
        LoadingDialog.loading(this)
        SMSSDK.submitVerificationCode("86", phone, sms)

    }

    /**
     * 开启倒计时
     */
    private fun countDown() {
        if (isCountDown) {
            return
        }
        timer = object : CountDownTimer(60 * 1000, 1000) {
            override fun onFinish() {
                finishCountDown()
                tvGetCode.isEnabled = true
                isCountDown = false
            }

            override fun onTick(millisUntilFinished: Long) {
                var seconds = millisUntilFinished / 1000
                var result = "重新获取（$seconds ）"
                var span = SpannableString(result)
                var index = result.indexOf("（") + 1
                span.setSpan(
                    ForegroundColorSpan(Color.parseColor("#FF0ED4D4")),
                    index,
                    index + seconds.toString().length,
                    SpannableString.SPAN_INCLUSIVE_INCLUSIVE
                )
                tvGetCode.text = span
                tvGetCode.isEnabled = false
            }

        }
        timer?.start()
        isCountDown = true
    }

    private fun showProtocolDialog() {
        TipDialog(this).apply {
            getLeftView()?.apply {
                text = "不同意"
                setOnClickListener { dismiss() }
            }
            getRightView()?.apply {
                text = "同意"
                setOnClickListener { dismiss() }
                cbRedProtocol?.isChecked = true
            }
            getTitleView()?.text = "车联益众用户注册与隐私协议"
        }
    }

    private fun finishCountDown() {
        timer?.cancel()
        timer = null
        tvGetCode.text = "获取验证码"
    }


    override fun onDestroy() {
        super.onDestroy()
        SMSSDK.unregisterEventHandler(eventHandler)
        eventHandler = null
//        EventBus.getDefault()
//            .unregister(this)
    }

    fun requestPermission() {
        if (!EasyPermissions.hasPermissions(this, *permissions)) {
            EasyPermissions.requestPermissions(this, "权限申请", 101, *permissions)
        }
    }

    fun switchRole() {
        if (isSelectWorker) {
            tvRoleTip.text = ROLETIP_CUSTOMER
            tvToRole.text = ROLETIP_TOMASTER
            ivGoBackUser.setImageResource(R.mipmap.img_zhifubao_client)
        } else {
            ivGoBackUser.setImageResource(R.mipmap.img_arrow_circle)
            tvRoleTip.text = ROLETIP_MASTER
            tvToRole.text = ROLETIP_TOCUSTOMER
        }
        isSelectWorker = !isSelectWorker
        var anim = AnimationUtils.loadAnimation(this, R.anim.anim_adert_fromright_enter)
        tvRoleTip.startAnimation(anim)
    }

    fun onWeixinAuth(event: MessageEvent) {
        //微信登录成功 获取 name avator
        var nick = event.message[0]
        var avatar = event.message[1]
        var openId = event.message[2]
        mPresenter?.wxLogin(nick, avatar, openId)
    }

    private fun getUserInfo() {
        runOnUiThread {
            LoadingDialog.loading(this)
            JShareInterface.getUserInfo(Wechat.Name, object : AuthListener {
                override fun onComplete(platform: Platform?, action: Int, data: BaseResponseInfo?) {
                    when (action) {
                        Platform.ACTION_USER_INFO -> {
                            if (data is UserInfo) {
                                var openId = data.openid
                                var name = data.name
                                var avatar = data.imageUrl
                                mPresenter?.wxLogin(name, avatar, openId)
                            }
                        }
                    }

                }

                override fun onCancel(p0: Platform?, p1: Int) {
                }

                override fun onError(p0: Platform?, p1: Int, p2: Int, p3: Throwable?) {
                }

            })
        }
    }

    override fun onLoginSuccess(isIdentified: Boolean, identify: String) {
        //登录成功设置别名 alias
        UserManager.handleDataWhenLoginSuccess(this)
        if (!isIdentified) {
            //未认证成为师傅
            if (!isSelectWorker) {
                //如果用户选择的是用户登录
                Router.openUI(this@LoginActivity, MainActivity::class.java)
                finish()
            } else {
                //如果用户选择的是师傅登录
                if (identify == "0") {
                    //未申请认证师傅
                    Router.openUI(this@LoginActivity, MasterAuthActivity::class.java)
                    finish()
                } else if (identify == "1") UserManager.logout()
                //认证过审核中
                if (!isDestroyed)
                    TipDialog(this).apply {
                        setMode(1)
                        mSingleBtn?.apply {
                            text = "确定"
                            setOnClickListener { dismiss() }
                        }
                        getDescView()?.text = "已提交认证信息等待后台审核"
                        getTitleView()?.text = "提示"
                    }.show()
            }
        } else {
            //已经认证成为师傅
            Router.openUI(this@LoginActivity, MainActivity::class.java)
            finish()
        }
    }

    override fun onUnbindMobile(nick: String, avatar: String, openId: String) {
        Router.openUI(this, BindMobileActivity::class.java, HashMap<String, Any>().apply {
            put(BindMobileActivity.NICK_KEY, nick)
            put(BindMobileActivity.AVATAR_KEY, avatar)
            put(BindMobileActivity.OPENID_KEY, openId)
            put(BindMobileActivity.ISSELECTWORKER_KEY, isSelectWorker)
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onError(err: String?) {
        err?.let { toast(err) }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            //用户勾选了“不再询问”，引导用户去设置页面打开权限
            AppSettingsDialog.Builder(this)
                .setTitle("权限申请")
                .setRationale("应用程序运行缺少必要的权限，请前往设置页面打开")
                .setPositiveButton("去设置")
                .setNegativeButton("取消")
                .setRequestCode(102)
                .build()
                .show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }
}
