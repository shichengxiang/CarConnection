package com.linruan.carconnection.ui.wallet

import android.view.Gravity
import android.view.View
import com.linruan.carconnection.*
import com.linruan.carconnection.dialog.CommonDialog
import com.linruan.carconnection.dialog.LoadingDialog
import com.linruan.carconnection.entities.net.GetOrderNoResponse
import com.linruan.carconnection.entities.net.GetPayResponse
import com.linruan.carconnection.listener.OnPayResultListener
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.linruan.carconnection.utils.CashierInputFilter
import com.linruan.carconnection.utils.PayUtil
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_recharge.*
import kotlinx.android.synthetic.main.layout_choose_paytype.*

class RechargeActivity : BaseActivity() {
    private var currentType = Constant.PAY_TYPE_WEIXIN
    override fun getLayout() = R.layout.activity_recharge

    override fun initView() {
        toolbar.setTitle(getString(R.string.recharge))
        etInputAmount.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            vLine_recharge.isSelected = hasFocus
        }
        etInputAmount.filters = arrayOf(CashierInputFilter())
        rlChooseType.setOnClickListener {
            var dialog = object : CommonDialog(this, R.layout.layout_choose_paytype) {
                override fun initView(root: View) {
                    window?.setWindowAnimations(R.style.BottomDialogAnim)
                    llChooseWeixin.setOnClickListener {
                        cbWeiXin.isChecked = true
                        cbZhiFuBao.isChecked = false
                        dismiss()
                        changePayType(Constant.PAY_TYPE_WEIXIN)
                    }
                    llChooseZhiFuBao.setOnClickListener {
                        cbZhiFuBao.isChecked = true
                        cbWeiXin.isChecked = false
                        dismiss()
                        changePayType(Constant.PAY_TYPE_ZHIFUBAO)
                    }
                    cbWeiXin.isChecked = currentType == Constant.PAY_TYPE_WEIXIN
                    cbZhiFuBao.isChecked = currentType == Constant.PAY_TYPE_ZHIFUBAO
                }
            }
            dialog.show()
            dialog.window?.setGravity(Gravity.BOTTOM)
        }
        btnRecharge.setOnClickListener {
            LoadingDialog.loading(this)
            var payWay = currentType
            Client.getInstance().recharge1(
                etInputAmount.text.toString(),
                object : JsonCallback<GetOrderNoResponse>() {
                    override fun onSuccess(response: Response<GetOrderNoResponse?>?) {
                        var body = response?.body()
                        var orderNo = response?.body()?.data ?: ""
                        if (body.isSuccess()) {
                            Client.getInstance().recharge2(
                                payWay,
                                orderNo,
                                object : JsonCallback<GetPayResponse>() {
                                    override fun onFinish() {
                                        LoadingDialog.dismiss()
                                    }

                                    override fun onSuccess(response: Response<GetPayResponse?>?) {
                                        if (body.isSuccess()) {
                                            if (payWay == Constant.PAY_TYPE_BALANCE) {
                                                toast("充值成功")
                                            } else if (payWay == Constant.PAY_TYPE_WEIXIN) {
                                                var pay = response?.body()?.data
                                                if (pay != null) {
                                                    PayUtil.wxPay(
                                                        this@RechargeActivity,
                                                        pay,
                                                        object : OnPayResultListener {
                                                            override fun onSuccess() {
                                                                toast("支付成功")
                                                            }

                                                            override fun onFail(err: String) {
                                                                toast("充值失败：$err")
                                                            }

                                                            override fun onCancel() {
                                                                toast("充值已取消")
                                                            }

                                                        })
                                                } else {
                                                    toast("微信支付异常")
                                                }
                                            } else if (payWay == Constant.PAY_TYPE_ZHIFUBAO) {
                                                var orderInfo = response?.body()?.data?.alipay
                                                PayUtil.aliPay(
                                                    this@RechargeActivity,
                                                    orderInfo ?: "",
                                                    object : OnPayResultListener {
                                                        override fun onSuccess() {
                                                            toast("支付成功")
                                                        }

                                                        override fun onFail(err: String) {
                                                            toast("充值失败：$err")
                                                        }

                                                        override fun onCancel() {
                                                            toast("充值已取消")
                                                        }

                                                    })
                                            }
                                        } else {
                                            toast(body?.msg ?: "支付失败，请联系客服")
                                        }
                                    }

                                })
                        } else {
                            toast(body?.msg ?: "支付失败，请联系客服")
                            LoadingDialog.dismiss()
                        }

                    }

                    override fun onError(response: Response<GetOrderNoResponse?>?) {
                        super.onError(response)
                        LoadingDialog.dismiss()
                    }
                })
        }
    }

    private fun changePayType(p: String) {
        currentType = p
        if (p == Constant.PAY_TYPE_WEIXIN) {
            ivRechargeType.setImageResource(R.mipmap.ic_weixin_pay)
            tvRechargeTypeResult.text = "微信支付"
        } else {
            ivRechargeType.setImageResource(R.mipmap.ic_zhifubao_pay)
            tvRechargeTypeResult.text = "支付宝支付"
        }
    }
}
