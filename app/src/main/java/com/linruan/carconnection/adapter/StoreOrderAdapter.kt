package com.linruan.carconnection.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.linruan.carconnection.*
import com.linruan.carconnection.dialog.CommonDialog
import com.linruan.carconnection.dialog.LoadingDialog
import com.linruan.carconnection.dialog.TipDialog
import com.linruan.carconnection.entities.GoodsOrder
import com.linruan.carconnection.net.BaseResponse
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.linruan.carconnection.ui.mine.StoreOrderActivity
import com.linruan.carconnection.ui.store.EvaluateListActivity
import com.linruan.carconnection.ui.store.GoodsOrderDetailActivity
import com.linruan.carconnection.ui.store.RefundApplyActivity
import com.linruan.carconnection.ui.store.RefundedDetailActivity
import com.linruan.carconnection.utils.Util
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.item_storeorder.view.*

class StoreOrderAdapter : RecyclerView.Adapter<StoreOrderAdapter.Holder> {

    private var mCxt: Context? = null
    private var mData = arrayListOf<GoodsOrder>()
    private var currentType = GoodsOrder.STATE_ALL
    private var mExpressArray = arrayOf("1")//获取物流公司信息
    private var mCallBack: AdapterListener? = null

    constructor(context: Context?, state: Int, callback: AdapterListener?) : super() {
        mCxt = context
        currentType = state
        mCallBack = callback
    }

    fun setData(list: ArrayList<GoodsOrder>?) {
        if (list.isNullOrEmpty()) {
            this.mData.clear()
        } else {
            this.mData = list
        }
        notifyDataSetChanged()
    }

    fun addData(list: ArrayList<GoodsOrder>?) {
        if (!list.isNullOrEmpty()) {
            mData.addAll(list)
        }
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        LoadingDialog.loading(mCxt)
        Client.getInstance()
            .deleteGoodsOrder(mData[position].id ?: "", object : JsonCallback<BaseResponse>() {
                override fun onFinish() {
                    LoadingDialog.dismiss()
                }

                override fun onSuccess(response: Response<BaseResponse?>?) {
                    var body = response?.body()
                    if (body?.isSuccess() == true) {
                        mData.removeAt(position)
                        notifyItemRemoved(position)
                        mCxt?.toast("删除成功")
                    } else {
                        mCxt?.toast(body?.msg ?: "删除异常")
                    }
                }
            })
    }

    class Holder(item: View) : RecyclerView.ViewHolder(item) {
        var tvOrderState = item.tvOrderState
        var llState_after = item.llState_afterSale

        //        var tvReturnTag = item.tvReturnTag
        var tvLookReturnDetail = item.tvLookReturnDetail
        var tvDeleteOrder = item.tvDeleteOrder
        var tvGoPay = item.tvGoPay
        var tvRemindExpress = item.tvRemindExpress
        var tvCancelOrder = item.tvCancelOrder
        var tvContactService = item.tvContactService
        var tvConfirmReceived = item.tvConfirmReceived
        var tvGoEvaluat = item.tvGoEvaluate
        var tvApplyRefund = item.tvApplyRefund//申请退款
        var tvRefundTag = item.tvReturnTag//退款状态
        var tvRefundInfo = item.tvRefundInfo//退款进度描述
        var tvFillExpress = item.tvFillExpress //填写物流

        //        var rlGoOrderDetail = item.rlGoOrderDetail
        var rlGoDetail = item.rlGoDetail


        var tvOrderNo = item.tvOrderNo
        var rvGoodsList = item.rvGoodsList

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var view = LayoutInflater.from(mCxt)
            .inflate(R.layout.item_storeorder, null)
        return Holder(view)
    }

    override fun getItemCount() = mData.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        var order = mData[position]
        var goodsList = order.ordersGoods
        holder.tvOrderNo.text = "订单编号  ${order.orderno}"
        holder.tvGoEvaluat.visibility = View.GONE
        holder.tvRemindExpress.visibility = View.GONE
        holder.tvCancelOrder.visibility = View.GONE
        holder.tvContactService.visibility = View.GONE
        holder.tvDeleteOrder.visibility = View.GONE
        holder.tvConfirmReceived.visibility = View.GONE
        holder.tvLookReturnDetail.visibility = View.GONE
        holder.tvGoPay.visibility = View.GONE
        holder.tvApplyRefund.visibility = View.GONE
        holder.tvRefundTag.visibility = View.GONE
        holder.tvFillExpress.visibility = View.GONE
        when (order.status) {
            GoodsOrder.STATE_WAITPAY -> {
                holder.tvOrderState.text = "等待付款"

                holder.tvGoPay.visibility = View.VISIBLE
                holder.tvDeleteOrder.visibility = View.VISIBLE
            }
            GoodsOrder.STATE_WAITEXPRESS -> {
                holder.tvOrderState.text = "等待发货"

                holder.tvRemindExpress.visibility = View.VISIBLE
                holder.tvCancelOrder.visibility = View.VISIBLE
            }
            GoodsOrder.STATE_WAITRECEIVE -> {
                holder.tvOrderState.text = "等待收货"

                holder.tvContactService.visibility = View.VISIBLE
                holder.tvConfirmReceived.visibility = View.VISIBLE
            }
            else -> {
                holder.tvOrderState.text = "退款/售后"

                when (order.step) {
                    GoodsOrder.STEP_COMPLETED -> {
                        holder.tvOrderState.text = "已完成"
                        holder.tvGoEvaluat.visibility = View.VISIBLE
                        holder.tvDeleteOrder.visibility = View.VISIBLE
                        holder.tvApplyRefund.visibility = View.VISIBLE
                    }
                    GoodsOrder.STEP_CANCELED -> {
                        holder.tvOrderState.text = "已取消"
                        holder.tvDeleteOrder.visibility = View.VISIBLE
                        holder.tvDeleteOrder.visibility = View.VISIBLE
                    }
                    GoodsOrder.STEP_RETURNING -> {
                        holder.tvRefundTag.text = "退款中"
                        holder.tvRefundTag.visibility = View.VISIBLE
                        holder.tvRefundInfo.visibility = View.VISIBLE
                        holder.tvLookReturnDetail.visibility = View.VISIBLE
                        when (order.refund?.status) {
                            GoodsOrder.REFUND_STATUS_APPLING -> {
                                holder.tvRefundInfo.text = "退款申请已提交，等待后台审核"
                            }
                            GoodsOrder.REFUND_STATUS_AGREE_NOEXPRESS -> {
                                holder.tvRefundInfo.text = "审核已完成，请填写物流信息"
                                holder.tvFillExpress.visibility = View.VISIBLE
                            }
                            GoodsOrder.REFUND_STATUS_AGREE_HASEXPRESS -> {
                                holder.tvRefundInfo.text = "物流信息已填写，等待卖家收货"
                            }
                            GoodsOrder.REFUND_STATUS_COMPLETED -> {
                                holder.tvRefundInfo.text = "退款已完成，退款金额：${order.price}"
                            }
                            GoodsOrder.REFUND_STATUS_FAIL -> {
                                holder.tvRefundInfo.text =
                                    "退款失败，失败原因：${order.refund?.failure ?: ""}"
                                holder.tvApplyRefund.text = "重新提交"
                                holder.tvApplyRefund.visibility = View.VISIBLE
                            }

                        }
                    }
                    GoodsOrder.STEP_RETURNED -> {
                        holder.tvOrderState.text = "已退款"
                        holder.tvLookReturnDetail.visibility = View.VISIBLE
                        holder.tvDeleteOrder.visibility = View.VISIBLE
                        holder.tvRefundInfo.visibility = View.VISIBLE
                        holder.tvRefundInfo.text = "退款已完成，退款金额：${order.price}"
                    }
                }
            }
        }
        holder.tvDeleteOrder.setOnClickListener { removeItem(position) }
        holder.tvGoPay.setOnClickListener {
            Router.openUI(
                mCxt,
                GoodsOrderDetailActivity::class.java,
                HashMap<String, String>().apply {
                    put(GoodsOrderDetailActivity.ORDERIDKEY, order.id ?: "")
                })
        }
        holder.tvRemindExpress.setOnClickListener { remind(order.id ?: "") }
        holder.tvCancelOrder.setOnClickListener { cancelOrderDialog(order.id ?: "", position) }
        holder.tvContactService.setOnClickListener { mCxt?.toast("联系客服") }
        holder.tvConfirmReceived.setOnClickListener {
            requestConfirmReceived(order?.id ?: "", position)
        }
        holder.tvApplyRefund.setOnClickListener {
            Router.openUI(mCxt, RefundApplyActivity::class.java, HashMap<String, String>().apply {
                put(RefundApplyActivity.ORDERID_KEY, order.id ?: "")
            })
        }
        holder.tvFillExpress.setOnClickListener {
            mCallBack?.call(StoreOrderActivity.TAG_FILLEXPRESS, position, order.id ?: "")
        }
        holder.tvGoEvaluat.setOnClickListener {
            var bundle = Bundle().apply {
                putString(EvaluateListActivity.ORDERNO_KEY, order.orderno)
                putString(EvaluateListActivity.ORDERID_KEY, order.id ?: "")
                putSerializable(EvaluateListActivity.GOODSLIST_KEY, goodsList)
            }
            Router.openUI(mCxt, EvaluateListActivity::class.java, bundle)
        }
        holder.rlGoDetail.setOnClickListener {
            if (order.status == 4 && (order.step == GoodsOrder.STEP_RETURNING || order.step == GoodsOrder.STEP_RETURNED)) {
                Router.openUI(
                    mCxt,
                    RefundedDetailActivity::class.java,
                    HashMap<String, String>().apply {
                        put(
                            RefundedDetailActivity.ORDERID_KEY,
                            order.id ?: ""
                        )
                    }
                )
            } else {
                Router.openUI(
                    mCxt,
                    GoodsOrderDetailActivity::class.java,
                    HashMap<String, String>().apply {
                        put(GoodsOrderDetailActivity.ORDERIDKEY, order.id ?: "")
                    })
            }
        }
        holder.tvLookReturnDetail.setOnClickListener {
            Router.openUI(
                mCxt,
                RefundedDetailActivity::class.java,
                HashMap<String, String>().apply {
                    put(
                        RefundedDetailActivity.ORDERID_KEY,
                        order.id ?: ""
                    )
                }
            )
        }
        //        holder.rlGoOrderDetail.setOnClickListener { Router.openUI(mCxt, GoodsOrderDetailActivity::class.java) }
        //商品列表
        holder.rvGoodsList.layoutManager = LinearLayoutManager(mCxt)
        holder.rvGoodsList.adapter = InnerGoodsAdapter(mCxt, order.status, goodsList)
    }

    private fun remind(orderId: String) {
        LoadingDialog.loading(mCxt)
        Client.getInstance()
            .remindSendGoods(orderId, object : JsonCallback<BaseResponse>() {
                override fun onFinish() {
                    LoadingDialog.dismiss()
                }

                override fun onSuccess(response: Response<BaseResponse?>?) {
                    if (response?.body()
                            ?.isSuccess() == true
                    ) {
                        mCxt?.toast("已提醒卖家发货")
                    } else {
                        mCxt?.toast(response?.body()?.msg ?: "请稍后重试")
                    }
                }

                override fun onError(response: Response<BaseResponse?>?) {
                }
            })
    }

    private fun cancelOrderDialog(orderId: String, position: Int) {
        var tipDialog = TipDialog(mCxt!!).apply {
            getTitleView()?.text = "温馨提示"
            getDescView()?.text = "确定取消该订单吗?"
            getLeftView()
                ?.apply {
                    text = "取消"
                    setOnClickListener { dismiss() }
                }
            getRightView()
                ?.apply {
                    text = "确定"
                    setOnClickListener {
                        dismiss()
                        requestCancleOrder(orderId, position)
                    }
                }
            setCancelable(true)
        }
        tipDialog.show()
    }

    /**
     * 确认收货
     */
    private fun requestConfirmReceived(orderId: String, position: Int) {
        Client.getInstance()
            .confirmReceivedGoods(orderId, object : JsonCallback<BaseResponse>() {
                override fun onFinish() {
                    LoadingDialog.dismiss()
                }

                override fun onSuccess(response: Response<BaseResponse?>?) {
                    var body = response?.body()
                    if (body?.isSuccess() == true) {
                        mData.removeAt(position)
                        notifyDataSetChanged()
                        mCxt?.toast("已确认收货")
                    } else {
                        mCxt?.toast(body?.msg ?: "发送失败")
                    }
                }
            })
    }

    /**
     * 取消订单
     */
    private fun requestCancleOrder(orderId: String, position: Int) {
        LoadingDialog.loading(mCxt)
        Client.getInstance()
            .cancelGoodsOrder(orderId, object : JsonCallback<BaseResponse>() {
                override fun onFinish() {
                    LoadingDialog.dismiss()
                }

                override fun onSuccess(response: Response<BaseResponse?>?) {
                    if (response?.body()
                            ?.isSuccess() == true
                    ) {
                        mData.removeAt(position)
                        notifyItemRemoved(position)
                        mCxt?.toast("取消订单成功")
                    } else {
                        mCxt?.toast(response?.body()?.msg ?: "取消订单异常，请点击重试")
                    }
                }
            })

    }
}
