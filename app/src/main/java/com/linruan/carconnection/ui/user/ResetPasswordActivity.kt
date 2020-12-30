package com.linruan.carconnection.ui.user

import android.graphics.Color
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import cn.smssdk.EventHandler
import cn.smssdk.SMSSDK
import com.linruan.carconnection.BaseActivity
import com.linruan.carconnection.R
import com.linruan.carconnection.dialog.LoadingDialog
import com.linruan.carconnection.moudle.ResetPasswordPresenter
import com.linruan.carconnection.moudle.ResetPasswordView
import com.linruan.carconnection.toast
import com.mob.MobSDK
import kotlinx.android.synthetic.main.activity_resetpassword.*

/**
 * 项目名称：CarConnection
 * 类描述：
 * 创建人：shichengxiang
 * 创建时间：2020/9/9 19:38
 * @version
 */
class ResetPasswordActivity : BaseActivity(), ResetPasswordView {
    private var hasGetSms = false
    private var isCountDown = false //正在倒计时
    private var timer: CountDownTimer? = null
    private var mPresenter: ResetPasswordPresenter? = null

    override fun getLayout() = R.layout.activity_resetpassword

    override fun initView() {
        ivClose.setOnClickListener { onBackPressed() }
        tvGetCode.setOnClickListener { getSms() }
        mPresenter = ResetPasswordPresenter(this)
        btnReset.setOnClickListener {
            //先验证验证码 然后去重置
            checkParams()
        }
    }

    private var eventHandler: EventHandler? = object : EventHandler() {
        override fun afterEvent(event: Int, result: Int, data: Any?) {
            if (result == SMSSDK.RESULT_COMPLETE) {
                runOnUiThread { tvGetCode.isEnabled = true }
                //回调完成
                runOnUiThread {
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        //登录
                        goResetPassword()
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取验证码成功
                        hasGetSms = true
                        toast("验证码发送成功")
                        //                        SMSSDK.setAskPermisionOnReadContact(true)// 读取短信
                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                        //返回支持发送验证码的国家列表
                        LoadingDialog.dismiss()
                    }
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

    private fun getSms() {
        if (etPhone.text.toString()
                .isBlank()
        ) {
            toast("手机号不能为空")
            return
        }
        hasGetSms = false
        MobSDK.submitPolicyGrantResult(false, null) // 默认未查看mob协议
        SMSSDK.registerEventHandler(eventHandler) //注册监听
        SMSSDK.getVerificationCode("86", etPhone.text.toString()) //发送验证码
        countDown()
    }

    private fun checkParams() {
        val phone = etPhone.text.toString()
        val sms = etInputSms.text.toString()
        val password1 = etInputPwd1.text.toString()
        val password2 = etInputPwd2.text.toString()
        if (phone.isBlank() || phone.length!=11) {
            toast("手机号不能为空")
            return
        }
        if (sms.isBlank()) {
            toast("验证码不能为空")
            return
        }
        if (password1.isBlank()) {
            toast("密码不能为空")
            return
        }
        if (password1 != password2) {
            toast("请检查两次密码是否一致")
            return
        }
        LoadingDialog.loading(this)
        SMSSDK.submitVerificationCode("86", phone, sms)
    }

    /**
     * 重置
     */
    private fun goResetPassword() {
        val password1 = etInputPwd1.text.toString()
        val passsword2 = etInputPwd2.text.toString()
        val account=etPhone.text.toString()
        mPresenter?.resetPassword(account,password1, passsword2, btnReset)
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

    private fun finishCountDown() {
        timer?.cancel()
        timer = null
        tvGetCode.isEnabled=true
        tvGetCode.text = "获取验证码"
    }

    override fun onResetPasswordSuccess() {
        toast("重置密码成功")
    }

    override fun onError(err: String?) {
        toast(err ?: "")
    }
}
