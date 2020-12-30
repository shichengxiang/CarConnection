package com.linruan.carconnection.ui.store

import android.content.Intent
import android.os.Handler
import androidx.recyclerview.widget.LinearLayoutManager
import com.linruan.carconnection.*
import com.linruan.carconnection.adapter.ConfirmGoodsOrderAdapter
import com.linruan.carconnection.dialog.LoadingDialog
import com.linruan.carconnection.dialog.PayDialog
import com.linruan.carconnection.entities.Address
import com.linruan.carconnection.entities.Goods
import com.linruan.carconnection.entities.MessageEvent
import com.linruan.carconnection.listener.OnPayResultListener
import com.linruan.carconnection.moudle.ConfirmGoodsOrderPresenter
import com.linruan.carconnection.moudle.ConfirmGoodsOrderView
import com.linruan.carconnection.ui.mine.StoreOrderActivity
import com.linruan.carconnection.utils.Util
import kotlinx.android.synthetic.main.activity_confirmgoodsorder.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.math.BigDecimal

/**
 * author：shichengxiang on 2020/6/12 17:07
 * email：1328911009@qq.com
 */
class ConfirmGoodsOrderActivity : BaseActivity(), ConfirmGoodsOrderView {
    companion object {
        const val CARTLIST = "CARTLIST"
        const val ORDERTYPE = "ordertype" //订单模式  0 购物车下单  1 商品详情下单
        const val GOODSKEY = "goods"
    }

    private var mPresenter: ConfirmGoodsOrderPresenter? = null
    private var mCurrentAddressId = ""
    private var mGoodsList = arrayListOf<Goods>() //购物车列表
    private var mOrderType = 0
    private var mAdapter: ConfirmGoodsOrderAdapter? = null
    private var mPayDialog: PayDialog? = null
    override fun getLayout() = R.layout.activity_confirmgoodsorder

    override fun initView() {
        mPresenter = ConfirmGoodsOrderPresenter(this)
        toolbar.setTitle("确认订单")
        llAddressLayout.setOnClickListener {
            Router.openUIForResult(
                this,
                AddressManagerActivity::class.java,
                101
            )
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = ConfirmGoodsOrderAdapter(this)
        recyclerView.adapter = mAdapter
        tvGoPay.setOnClickListener {
            if (mCurrentAddressId.isNullOrBlank()) {
                toast("请选择收货地址")
            } else {
                placeOrder()
            }
        }
        initData(intent)
    }

    private fun initData(intent: Intent) {
        if (intent.hasExtra(CARTLIST)) {
            mOrderType = intent.extras?.getInt(ORDERTYPE, 0) ?: 0
            mGoodsList = intent.extras?.getSerializable(CARTLIST) as ArrayList<Goods>
        }

        mAdapter?.setData(mGoodsList)
        tvShouldAmount.text = mAdapter?.getTotalAmount()
            ?.setScale(2, BigDecimal.ROUND_DOWN)
            ?.toString() ?: ""
        mPresenter?.getDefaultAddress()
    }

    override fun onGetAddress(address: Address?) {
        if (address != null) {
            mCurrentAddressId = address.id ?: ""
            tvAddressName.text = "${address.name}   ${address.phone}"
            tvArea.text = "${address.province} ${address.city} ${address.area} ${address.address}"
        }
    }

    override fun onPlaceOrderSuccess(orderNo: String) {
        toast("下单成功")
        //        mPresenter?.pay(orderNo,)
        if (mPayDialog == null) {
            mPayDialog = PayDialog(this)
        }
        mPayDialog?.setCallback(object : MCallBack<String> {
            override fun call(t: String, position: Int) {
                LoadingDialog.loading(this@ConfirmGoodsOrderActivity)
                Util.goodsPay(
                    this@ConfirmGoodsOrderActivity,
                    orderNo,
                    t,
                    object : OnPayResultListener {
                        override fun onSuccess() {
                            toast("购买成功")//购买成功跳转待发货
                            mPayDialog?.dismiss()
                            Router.openUI(
                                this@ConfirmGoodsOrderActivity,
                                StoreOrderActivity::class.java,
                                HashMap<String, Int>().apply {
                                    put(
                                        StoreOrderActivity.ORDERTAG,
                                        2
                                    )
                                })
                            finish()
                        }

                        override fun onFail(err: String) {
                            toast(err)
                        }

                        override fun onCancel() {
                            toast("支付取消")
                        }

                    })
//                mPresenter?.pay(orderNo, t)
            }
        })
//        mPayDialog?.setOnDismissListener {
//            onBackPressed()
//        }
        if (mPayDialog?.isShowing != true) {
            mPayDialog?.show()
        }
    }

    override fun onPaySuccess(res: String) {
        toast("支付成功")
        setResult(111)
        Handler().postDelayed({ mPayDialog?.dismiss() }, 1500)
    }

    override fun onError(err: String?) {
        toast(err ?: "")
    }

    /**
     * 下单
     */
    fun placeOrder() {
        var ids = StringBuffer()
        mGoodsList.forEach {
            ids.append(it.id)
                .append(",")
        }
        if (mOrderType == 0) {
            mPresenter?.placeGoodsOrder(ids.substring(0, ids.length - 1), mCurrentAddressId)
        } else {
            var goods: Goods? = mGoodsList[0]
            mPresenter?.placeGoodsOrderInDetail(
                goods?.id ?: "",
                goods?.sku_id ?: "",
                goods?.num ?: 1,
                mCurrentAddressId
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == AddressManagerActivity.RESULTCODE_SELECTEADDRESS) {
            var address = data?.extras?.getSerializable("address") as Address?
            if (address != null) {
                mCurrentAddressId = address.id ?: ""
                tvAddressName.text = "${address.name}   ${address.phone}"
                tvArea.text =
                    "${address.province} ${address.city} ${address.area} ${address.address}"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.onDestory()
    }

}
