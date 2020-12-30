package com.linruan.carconnection.ui.mine

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.linruan.carconnection.*
import com.linruan.carconnection.adapter.ShowImageAdapter
import com.linruan.carconnection.adapter.FaultPartsAdapter
import com.linruan.carconnection.decorview.SpaceItemDecoraton
import com.linruan.carconnection.dialog.CommonDialog
import com.linruan.carconnection.dialog.TipDialog
import com.linruan.carconnection.entities.FaultPart
import com.linruan.carconnection.entities.MessageEvent
import com.linruan.carconnection.entities.RepairOrder
import com.linruan.carconnection.entities.RepairOrderDetail
import com.linruan.carconnection.moudle.RepairOrderDetailPresenter
import com.linruan.carconnection.moudle.RepairOrderDetailView
import com.linruan.carconnection.ui.wallet.PayPlatformActivity
import com.linruan.carconnection.utils.Util
import kotlinx.android.synthetic.main.activity_repaircarorderdetail.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.math.BigDecimal

class RepairCarOrderDetailActivity : BaseActivity(), RepairOrderDetailView {

    companion object {
        @JvmField
        val REPAIRID_KEY = "repairId"
    }

    private var mPresenter: RepairOrderDetailPresenter? = null
    private var mRepairId = ""
    private var mPreOrderInfo: RepairOrder? = null
    private var mCurrentRepairOrderDetail: RepairOrderDetail? = null
    private var mFaultPartsAdapter: FaultPartsAdapter? = null //故障部位
    private var mFaultSupplementPartAdapter: FaultPartsAdapter? = null //另行修复部位
    private var mFaultImageAdapter: ShowImageAdapter? = null //照片
    private var mFaultSuppleImageAdapter: ShowImageAdapter? = null //另行修复照片
    private var mFailImageAdapter: ShowImageAdapter? = null //失败原因图片
    val REQUESTCODE_GOPAY = 1001
    override fun getLayout() = R.layout.activity_repaircarorderdetail

    override fun initView() {
        if (intent?.extras != null) {
            mPreOrderInfo = intent.extras?.getSerializable("order") as RepairOrder?
            mRepairId = mPreOrderInfo?.id ?: ""
        }
        if (intent?.hasExtra(REPAIRID_KEY) == true) {
            mRepairId = intent?.getStringExtra(REPAIRID_KEY) ?: ""
        }
        mPresenter = RepairOrderDetailPresenter(this)
        toolbar.setTitle("订单详情")
        //价格
        rvFaultPrice.layoutManager = LinearLayoutManager(this)
        mFaultPartsAdapter = FaultPartsAdapter(this)
        rvFaultPrice.adapter = mFaultPartsAdapter
        //照片
        rvFaultPhotosOfCustomer.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        rvFaultPhotosOfCustomer.addItemDecoration(
            SpaceItemDecoraton(
                Util.px2dp(5),
                0,
                0,
                0,
                RecyclerView.HORIZONTAL
            )
        )
        mFaultImageAdapter = ShowImageAdapter(this)
        rvFaultPhotosOfCustomer.adapter = mFaultImageAdapter
        //价格
        rvSupplementFaultPartPrice.layoutManager = LinearLayoutManager(this)
        mFaultSupplementPartAdapter = FaultPartsAdapter(this)
        rvSupplementFaultPartPrice.adapter = mFaultSupplementPartAdapter
        //照片
        rvSupplementFaultPhoto.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        rvSupplementFaultPhoto.addItemDecoration(
            SpaceItemDecoraton(
                Util.px2dp(5),
                0,
                0,
                0,
                RecyclerView.HORIZONTAL
            )
        )
        mFaultSuppleImageAdapter = ShowImageAdapter(this)
        rvSupplementFaultPhoto.adapter = mFaultSuppleImageAdapter
        mFailImageAdapter = ShowImageAdapter(this)
        rvFailPhotos.layoutManager = GridLayoutManager(this, 3)
        rvFailPhotos.adapter = mFailImageAdapter
        btnRepairSuccess.setOnClickListener { mPresenter?.confirmRepairSuccess(mRepairId) }
        btnFail.setOnClickListener {
            Router.openUI(this, RepairFailActivity::class.java, HashMap<String, String>().apply {
                put(RepairFailActivity.REPAIRID_KEY, mCurrentRepairOrderDetail?.id ?: "")
            })
        }
        //支付
        btnGoPay.setOnClickListener {
            Router.openUIForResult(
                this,
                PayPlatformActivity::class.java,
                REQUESTCODE_GOPAY,
                HashMap<String, String>().apply {
                    put(PayPlatformActivity.ORDERNO_KEY, mCurrentRepairOrderDetail?.orderno ?: "")
                })
        }
        //取消订单
        btnCancelOrder.setOnClickListener {
            TipDialog(this).apply {
                getTitleView()?.text = "温馨提示"
                getDescView()?.text = "确定取消该订单吗？"
                getLeftView()?.apply {
                    text = "取消"
                    setOnClickListener { dismiss() }
                }
                getRightView()?.apply {
                    text = "确定"
                    setOnClickListener {
                        dismiss()
                        mPresenter?.cancelRepairOrder(mRepairId)
                    }
                }
            }
                .show()
        }
        //删除订单
        btnDeleteOrder.setOnClickListener {
            TipDialog(this).apply {
                getTitleView()?.text = "温馨提示"
                getDescView()?.text = "确定删除该订单吗？"
                getLeftView()?.apply {
                    text = "取消"
                    setOnClickListener { dismiss() }
                }
                getRightView()?.apply {
                    text = "确定"
                    setOnClickListener {
                        dismiss()
                        mPresenter?.deleteRepairOrder(mRepairId)
                    }
                }
            }
                .show()
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        refresh()
    }

    private fun refresh() {
        mPresenter?.getOrderDetail(mRepairId)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onChangeStatus(event: MessageEvent) {
        if (event.code == MessageEvent.REFRESH_REPAIRORDERD_TOCUSTOMER) {
            refresh()
        }
    }

    /**
     * 状态修改 另行修复是否隐藏
     */
    private fun handleState(status: Int, step: Int) {
        llWhenFinishOrder.visibility=View.GONE
        when (status) {
            RepairOrder.STATE_WAITPAY -> {
                rlTopTag.visibility = View.GONE
                //                tvOrderProgress.
                tvOrderState.text = "订单待付款"
                tvRemark.isEnabled = false
                llBottomWhenSuccess.visibility = View.VISIBLE
                llLayoutSupplement.visibility = View.GONE
                btnCancelOrder.visibility = View.VISIBLE
                btnGoPay.visibility = View.VISIBLE
            }
            RepairOrder.STATE_RUNNING -> {
                tvOrderProgress.text = "正在维修中"
                tvOrderState.text = "下单成功"
                btnFail.visibility = View.VISIBLE
                btnRepairSuccess.visibility = View.VISIBLE
                if (step == 2) {
                    tvOrderProgress.text = "进行中"
                }
                if (step == 4) { //维修完成 待确认
                    llBottomWhenSuccess.visibility = View.VISIBLE
                    llLayoutSupplement.visibility = View.VISIBLE
                } else {
                    llLayoutSupplement.visibility = View.GONE
                    llBottomWhenSuccess.visibility = View.GONE
                }
                if (step == 7) {
                    //客户点击维修失败
                    tvOrderProgress.text = "维修失败待确认"
                    llLayoutFailure.visibility = View.VISIBLE
                } else {
                    llLayoutFailure.visibility = View.GONE
                }
            }
            RepairOrder.STATE_COMPLETED -> {
                llWhenFinishOrder.visibility=View.VISIBLE
                tvOrderState?.text = "订单完成"
                tvArriveTime?.text = "到达时间：${Util.getDataToString(
                    mCurrentRepairOrderDetail?.arrive_time,
                    "yyyy-MM-dd HH:mm:ss"
                )}"
                tvFinishTime?.text = "完成时间：${Util.getDataToString(
                    mCurrentRepairOrderDetail?.finish_time,
                    "yyyy-MM-dd HH:mm:ss"
                )}"
                when (step) {
                    5 -> {
                        tvOrderProgress.text = "维修成功"
                        tvOrderState.text = "下单成功"
                    }
                    6 -> {
                        tvOrderProgress.text = "维修失败"
                        tvOrderState.text = "下单成功"
                    }
                }
                llBottomWhenSuccess.visibility = View.GONE
            }
            RepairOrder.STATE_CANCELED -> {
                tvOrderProgress.text = "已取消"
                tvOrderState.text = "订单已取消"
                btnDeleteOrder.visibility = View.VISIBLE
                llBottomWhenSuccess.visibility = View.VISIBLE
            }

        }
    }

    override fun onConfirmRepairedSuccess() {
        toast("提交成功,去评价")
        //刷新首页状态
        EventBus.getDefault().post(MessageEvent(MessageEvent.REFRESH_HOMEFRAGMENTRUNNINGORDER))
        Router.openUIForResult(
            this@RepairCarOrderDetailActivity,
            EvaluateMasterActivity::class.java,
            100,
            java.util.HashMap<String, String>().apply {
                put("repairId", mRepairId)
                put("orderNo",mPreOrderInfo?.orderno?:"")
            })
    }

    override fun onGetOrderDetailSuccess(repairOrderDetail: RepairOrderDetail?) {
        stateView.content()
            ?.show()
        mCurrentRepairOrderDetail = repairOrderDetail
        tvOrderTime.text =
            Util.getDataToString(repairOrderDetail?.create_time, "yyyy-MM-dd HH:mm:ss")//时间
        //待付款
        tvTotalAmount.text = "${Util.moneyformat(BigDecimal(repairOrderDetail?.price1 ?: "0"))}"
        tvOrderNum.text = "订单编号：${repairOrderDetail?.orderno ?: ""}"
        //故障原因  损坏部位价格
        flowLayoutReasons.removeAllViews()
        mFaultPartsAdapter?.clearData()
        repairOrderDetail?.fault?.forEach {
            flowLayoutReasons.addView(Util.getReasonTags(this, it.title))
            mFaultPartsAdapter?.addData(it)
        }
        tryCatch {
            // 更新 部位价格
            var price = repairOrderDetail?.price
            price?.split(",")
                ?.forEach { item ->
                    var part = item.split(":")
                    mFaultPartsAdapter?.refreshPrice(part[0], part[1])
                }
        }
        //损坏部位照片
        var imgs = arrayListOf<String>()
        repairOrderDetail?.imgs?.forEach {
            imgs.add(it.filepath)
        }
        mFaultImageAdapter?.setData(imgs)
        //备注
        tvRemark.setText(if (repairOrderDetail?.intro.isNullOrBlank()) "暂无备注" else repairOrderDetail?.intro)

        //另行维修
        tryCatch {
            //另行维修部位价格
            var supplement = repairOrderDetail?.other
            supplement?.split(",")
                ?.forEach { item ->
                    var split = item.split(":")
                    var id = split[0]
                    var price = split[1]
                    var title = ""
                    UserManager.mFaults?.forEach {
                        if (it.id.toString() == id) {
                            title = it.title
                            return@forEach
                        }
                    }
                    flowLayoutReasonsSupplement.addView(Util.getReasonTags(this, title))
                    mFaultSupplementPartAdapter?.addData(FaultPart(id.toInt(), title, price))
                }
        }
        //另行修复照片
        var otherImages = repairOrderDetail?.other_imgs
        otherImages?.forEach {
            mFaultSuppleImageAdapter?.addData(it.filepath)
        }
        //另行备注
        tvRemarkSupplement.setText(if (repairOrderDetail?.worker_intro.isNullOrBlank()) "暂无备注" else repairOrderDetail?.worker_intro)
        handleState(repairOrderDetail?.status ?: 0, repairOrderDetail?.step ?: 0)
        //根据数据是否为空判断是否隐藏另行修复
        if (repairOrderDetail?.other.isNullOrBlank() && repairOrderDetail?.other_imgs.isNullOrEmpty()) {
            llLayoutSupplement.visibility = View.GONE
        }
        //维修失败原因
        var failImages = repairOrderDetail?.fail_imgs
        failImages?.forEach {
            mFailImageAdapter?.addData(it.filepath)
        }
        tvFailReason?.setText(repairOrderDetail?.fail_reason ?: "无备注")
    }

    override fun onCancelRepairOrderSuccess() {
        toast("已取消订单")
        setResult(
            RePairCarOrderActivity.RESULTCODE_SWITCHTAB,
            Intent().apply { putExtra("tab", 4) })
        finish() //取消成功就关闭页面
    }

    override fun onDeleteRepairOrderSuccess() {
        toast("已删除订单")
        EventBus.getDefault().post(MessageEvent(MessageEvent.REFRESH_REPAIRORDERACTIVITY, "4"))
        finish()
    }

    override fun onError(err: String?) {
        toast(err ?: "")
    }

    override fun showError() {
        stateView.error()
            ?.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.onDestory()
        EventBus.getDefault().unregister(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUESTCODE_GOPAY && resultCode == PayPlatformActivity.RESULTCODE_PAYSUCCESS) {
            //支付成功关闭页面
            setResult(1, Intent().apply { putExtra("isSuccess", true) })
            finish()
        } else if (requestCode == 100) {
            setResult(RePairCarOrderActivity.RESULTCODE_SWITCHTAB)
            finish()
        }
    }
}
