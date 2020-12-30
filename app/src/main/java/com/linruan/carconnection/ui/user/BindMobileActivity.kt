package com.linruan.carconnection.ui.user

import android.graphics.Color
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.KeyEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.CheckBox
import cn.smssdk.EventHandler
import cn.smssdk.SMSSDK
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.linruan.carconnection.*
import com.linruan.carconnection.dialog.LoadingDialog
import com.linruan.carconnection.dialog.TipDialog
import com.linruan.carconnection.moudle.LoginPresenter
import com.linruan.carconnection.moudle.LoginView
import com.linruan.carconnection.ui.MainActivity
import com.linruan.carconnection.ui.WebViewActivity
import com.linruan.carconnection.ui.master.MasterAuthActivity
import com.mob.MobSDK
import kotlinx.android.synthetic.main.activity_bindmobile.*
import org.json.JSONObject

/**
 * author：shichengxiang on 2020/6/17 14:53
 * email：1328911009@qq.com
 */
class BindMobileActivity : BaseActivity(), LoginView {

    companion object {
        const val NICK_KEY = "nick"
        const val AVATAR_KEY = "avatar"
        const val OPENID_KEY = "openid"
        const val ISSELECTWORKER_KEY="isSelectWorker"
    }

    private var mNick = ""
    private var mAvatar = ""
    private var mOpenId = ""
    private var mCurrentPhone = ""
    private var isSelectWorker=false
    private var mCbProtocol: CheckBox? = null
    private var countDownTimer: CountDownTimer? = null
    private var mPresenter: LoginPresenter? = null
    private var eventHandler: EventHandler? = object : EventHandler() {
        override fun afterEvent(event: Int, result: Int, data: Any?) {
            if (result == SMSSDK.RESULT_COMPLETE) {
                //回调完成
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功
                    //登录
                    mPresenter?.login(
                        etPhone.text.toString(),
                        if (isSelectWorker) 2 else 1,
                        mNick,
                        mAvatar,
                        mOpenId
                    )
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //获取验证码成功
                    runOnUiThread {
                        toast("验证码发送成功")
                        goNextVerify()
                        timerCount()
                    }
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    //返回支持发送验证码的国家列表
                    LoadingDialog.dismiss()
                    toast("手机号暂时不支持")
                }
            } else {
                runOnUiThread {
                    LoadingDialog.dismiss()
                    var obj = JSONObject((data as Throwable).message ?: "")
                    var err: String? = obj.optString("detail")
                    toast(err ?: "获取手机号失败")
                }
            }
        }
    }

    override fun getLayout() = R.layout.activity_bindmobile

    override fun initView() {
        mNick = intent.getStringExtra(NICK_KEY)?:""
        mAvatar = intent.getStringExtra(AVATAR_KEY)?:""
        mOpenId = intent.getStringExtra(OPENID_KEY)?:""
        isSelectWorker=intent.getBooleanExtra(ISSELECTWORKER_KEY,false)
        mPresenter = LoginPresenter(this)
        mCbProtocol = cbRedProtocol
        ivClose.setOnClickListener { onBackPressed() }
        ivClear.setOnClickListener { etPhone.setText("") }
        var vs = getString(R.string.userprotol_privacypolicy)
        var span = SpannableString(vs)
        val indexOf = vs.indexOf("《")
        span.setSpan(
            ForegroundColorSpan(Color.parseColor("#FF0ED4D4")),
            indexOf,
            vs.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        span.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                WebViewActivity.openWebView(
                    this@BindMobileActivity,
                    Constant.URL_PRIVACY,
                    "用户协议和隐私政策"
                )
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.bgColor = Color.TRANSPARENT
                ds.isUnderlineText = false
            }
        }, indexOf, vs.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvShowProtocol.movementMethod = LinkMovementMethod.getInstance()
        tvShowProtocol.text = span
        btnSendSms.setOnClickListener {
            if (etPhone.text.toString()
                    .isBlank()
            ) {
                toast("手机号不能为空")
                return@setOnClickListener
            }
            if (etPhone.text.length != 11) {
                toast("请检查手机号是否正确")
                return@setOnClickListener
            }
            mCurrentPhone = etPhone.text.toString()
            MobSDK.submitPolicyGrantResult(false, null) // 默认未查看mob协议
            SMSSDK.registerEventHandler(eventHandler) //注册监听
            SMSSDK.getVerificationCode("86", mCurrentPhone) //发送验证码
        }
        tvPhone.text = "短信已发送至+86$mCurrentPhone"
        llEdit.setOnClickListener { KeyboardUtils.showSoftInput() }
        tvSmsCounter.setOnClickListener {
            SMSSDK.getVerificationCode("86", mCurrentPhone) //发送验证码
        }
    }

    private fun showProtocolDialog() {
        TipDialog(this).apply {
            getLeftView()?.apply {
                text = "不同意"
                setOnClickListener { dismiss() }
            }
            getRightView()?.apply {
                text = "同意"
                mCbProtocol?.isChecked = true
                setOnClickListener { dismiss() }
            }
            getTitleView()?.text = "车联益众用户注册与隐私协议"
        }
    }

    override fun onBackPressed() {
        if (secondPage.visibility == View.VISIBLE) {
            smsBuffer.setLength(0)
            backInsertPhone()
        } else {
            super.onBackPressed()
        }
    }

    private var smsBuffer = StringBuffer()
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9 && smsBuffer.length < 6) {
            smsBuffer.append(keyCode - 7)
            when (smsBuffer.length) {
                1 -> etNum1.text = smsBuffer[0].toString()
                2 -> etNum2.text = smsBuffer[1].toString()
                3 -> etNum3.text = smsBuffer[2].toString()
                4 -> etNum4.text = smsBuffer[3].toString()
                5 -> etNum5.text = smsBuffer[4].toString()
                6 -> etNum6.text = smsBuffer[5].toString()
            }
            if (smsBuffer.length == 6) {
                logE("$mCurrentPhone  ${smsBuffer}")
                SMSSDK.submitVerificationCode("86", mCurrentPhone, smsBuffer.toString())
                KeyboardUtils.hideSoftInput(etNum1)
            }
        }
        if (keyCode == KeyEvent.KEYCODE_DEL && smsBuffer.isNotEmpty()) {
            smsBuffer = smsBuffer.deleteCharAt(smsBuffer.length - 1)
            when (smsBuffer.length) {
                5 -> etNum6.text = ""
                4 -> etNum5.text = ""
                3 -> etNum4.text = ""
                2 -> etNum3.text = ""
                1 -> etNum2.text = ""
                0 -> etNum1.text = ""
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun timerCount() {
        tvSmsCounter.isEnabled = false
        countDownTimer = object : CountDownTimer(60 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tvSmsCounter.text = "${millisUntilFinished / 1000}s后重新获取"
            }

            override fun onFinish() {
                tvSmsCounter.text = "重新获取"
            }
        }
        countDownTimer?.start()
    }

    /**
     * 返回输入手机号
     */
    private fun backInsertPhone() {
        var anim = AnimationUtils.loadAnimation(this, R.anim.anim_adert_fromleft_enter)
        secondPage.visibility = View.GONE
        firstPage.visibility = View.VISIBLE
        firstPage.startAnimation(anim)
    }

    /**
     * 输入验证码
     */
    private fun goNextVerify() {
        var anim = AnimationUtils.loadAnimation(this, R.anim.anim_adert_fromright_enter)
        firstPage.visibility = View.GONE
        secondPage.visibility = View.VISIBLE
        secondPage.startAnimation(anim)
        KeyboardUtils.showSoftInput(etNum1)
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

    override fun onLoginSuccess(isIdentified: Boolean, identify: String) {
        //登录成功设置别名 alias
        UserManager.handleDataWhenLoginSuccess(this)
        ActivityUtils.finishActivity(LoginActivity::class.java)//关闭登录界面
        if (!isIdentified) {
            //未认证成为师傅
            if (!isSelectWorker) {
                //如果用户选择的是用户登录
                Router.openUI(this, MainActivity::class.java)
                finish()
            } else {
                //如果用户选择的是师傅登录
                if (identify == "0") {
                    //未申请认证师傅
                    Router.openUI(this, MasterAuthActivity::class.java)
                    finish()
                } else if (identify == "1") UserManager.logout()
                //认证过审核中
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
            Router.openUI(this, MainActivity::class.java)
            finish()
        }
    }

    override fun onUnbindMobile(nick: String, avatar: String, openId: String) {
        //nothing
    }

    override fun onError(err: String?) {
        toast(err ?: "")
    }
}
