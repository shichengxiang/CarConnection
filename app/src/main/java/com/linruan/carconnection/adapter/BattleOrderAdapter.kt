package com.linruan.carconnection.adapter

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.linruan.carconnection.*
import com.linruan.carconnection.dialog.CommonDialog
import com.linruan.carconnection.dialog.LoadingDialog
import com.linruan.carconnection.entities.Master
import com.linruan.carconnection.entities.MessageEvent
import com.linruan.carconnection.entities.RepairOrder
import com.linruan.carconnection.entities.net.GetBattleRepairResponse
import com.linruan.carconnection.net.BaseResponse
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.linruan.carconnection.ui.master.BattleFragment
import com.linruan.carconnection.ui.master.MasterRepairDetailActivity
import com.linruan.carconnection.ui.master.NavigationMapActivity
import com.linruan.carconnection.ui.wallet.PayPlatformActivity
import com.linruan.carconnection.utils.MapUtil
import com.linruan.carconnection.utils.Util
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.customer_notitfication_layout.view.*
import kotlinx.android.synthetic.main.item_battle_list.view.*
import org.greenrobot.eventbus.EventBus
import java.lang.StringBuilder

class BattleOrderAdapter : RecyclerView.Adapter<BattleOrderAdapter.Holder> {
    /**
     * 师傅抢单列表
     *  status 1 待付款 2进行中  3已完成 4已取消
     *  step 1等待师傅接单 2师傅已接单 3师傅已到达 4维修完成待确认 5用户已确认  6订单失败
     */
    private var mData = ArrayList<RepairOrder>()
    private var currentType = 0 // 0:抢单  else 根据 status step判断
    private var mCallBack: AdapterListener? = null

    companion object {
        const val TAG_DELETEORDER = 11
    }

    constructor(context: Context?, type: Int) : super() {
        mCxt = context
        this.currentType = type
    }

    fun setListener(listener: AdapterListener?) {
        this.mCallBack = listener
    }

    fun setData(list: ArrayList<RepairOrder>?, type: Int) {
        if (list.isNullOrEmpty())
            mData.clear()
        else
            mData = list
        this.currentType = type
        notifyDataSetChanged()
    }

    fun addData(list: ArrayList<RepairOrder>?) {
        if (!list.isNullOrEmpty()) {
            mData.addAll(list)
        }
        notifyDataSetChanged()
    }

    fun getData() = mData

    private var mCxt: Context? = null

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvState = itemView.tvState
        var layoutFinished = itemView.layoutFinished
        var tvTime = itemView.tvTime
        var btnBattle = itemView.btnBattle
        var root = itemView.findViewById<View>(R.id.llRoot)
        var llDistanceTag = itemView.findViewById<View>(R.id.llDistanceTag)
        var tvDistance = itemView.findViewById<TextView>(R.id.tvDistance)
        var tvAddressName = itemView.findViewById<TextView>(R.id.tvAddressName)
        var tvLeixing = itemView.findViewById<TextView>(R.id.tvLeixing)
        var tvTroublePart = itemView.findViewById<TextView>(R.id.tvTroubleParts)
        var tvDelete = itemView.findViewById<View>(R.id.tvDeleteRepairOrder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var view = LayoutInflater.from(mCxt)
            .inflate(R.layout.item_battle_list, null)
        return Holder(view)
    }

    override fun getItemCount() = mData.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        var order = mData[position]
        holder.root.setOnClickListener {
            if (currentType == 0 || currentType == 1) {
                var bundle = Bundle().apply {
                    putSerializable(NavigationMapActivity.REPAIRORDER_KEY, order)
                    putInt("type", currentType)
                }
                Router.openUIForResult(
                    mCxt,
                    NavigationMapActivity::class.java,
                    BattleFragment.REQUESTCODE_ISRECEIVED,
                    bundle
                )
            } else {
                Router.openUI(
                    mCxt,
                    MasterRepairDetailActivity::class.java,
                    HashMap<String, String>().apply {
                        put("type", currentType.toString())
                        put("repairId", order.id)
                    })
            }
        }
        holder.tvDelete.setOnClickListener { mCallBack?.call(TAG_DELETEORDER, position, order.id) }
//        var address = MapUtil.getAddress(mCxt, order.lat.toDouble(), order.lng.toDouble())
        order.area = order.address ?: ""
        order.road = ""
        order.distance = order.juli ?: ""
        holder.tvAddressName.text = order.area
        holder.tvTime.text =
            "抢单时间：${Util.getDataToString(order.create_time, "yyyy-MM-dd HH:mm:ss")}"
        var leixing = ""
        UserManager.mCarTypes?.forEach {
            if (order.leixing_id == it.id) {
                leixing = it.title ?: ""
            }
        }
        var faults = StringBuffer()
        UserManager.mFaults?.forEach {
            if (order.fault_ids.contains(it.id.toString(), false)) {
                faults.append(it.title + " ")
            }
        }
        order.leixingstr = leixing
        order.faultstr = faults.toString()
        holder.tvLeixing.text = "车型：$leixing"
        holder.tvTroublePart.text = "损坏部位：${faults}"

        if (currentType == 0) {
            holder.llDistanceTag.visibility = View.VISIBLE
            holder.btnBattle.visibility = View.VISIBLE
            holder.tvState.visibility = View.GONE
            holder.tvTime.visibility = View.GONE
            holder.layoutFinished.visibility = View.GONE
            holder.btnBattle.setOnClickListener {
                battleOrder(order.id, order.orderno)
            }
            holder.tvDistance.text = "距离您 ${order.distance} 公里"
        } else {
            if (order.status == 2) {
                //进行中（进行中 维修中）
                if (order.step == 2) {
                    //进行中
                    holder.tvState.apply {
                        visibility = View.VISIBLE
                        text = "进行中"
                        setTextColor(Color.parseColor("#FF8E15"))
                        setBackgroundResource(R.drawable.bg_repair_state_going)
                    }
                    holder.tvTime.apply {
                        text = "抢单时间：2020-5-10 20:10:30"
                        visibility = View.VISIBLE
                    }
                    holder.layoutFinished.visibility = View.GONE
                    holder.btnBattle.visibility = View.GONE
                } else {
                    //维修中
                    holder.tvState.apply {
                        visibility = View.VISIBLE
                        text = if (order.step == 7) "维修失败待确认" else "维修中"
                        setBackgroundResource(R.drawable.bg_repair_state_repairing)
                    }
                    holder.tvTime.apply {
                        text = "抢单时间：2020-5-10 20:10:30"
                        visibility = View.VISIBLE
                    }
                    holder.layoutFinished.visibility = View.GONE
                    holder.btnBattle.visibility = View.GONE
                }
            } else {
                holder.tvTime.text =
                    "完成时间：${Util.getDataToString(order.finish_time, "yyyy-MM-dd HH:mm:ss")}"
                //已完成
                holder.tvState.apply {
                    visibility = View.VISIBLE
                    text = if (order.step == 6) "维修失败" else "已完成"
                    setBackgroundResource(R.drawable.bg_repair_state_finished)
                    when (order.step) {
                    }
                }
                holder.tvTime.visibility = View.VISIBLE
                holder.layoutFinished.visibility = View.VISIBLE
                holder.btnBattle.visibility = View.GONE
            }
        }
    }

    private fun battleOrder(repairId: String, orderNo: String) {
        LoadingDialog.loading(mCxt)
        Client.getInstance()
            .battleOrder(repairId, object : JsonCallback<GetBattleRepairResponse>() {
                override fun onFinish() {
                    LoadingDialog.dismiss()
                }

                override fun onSuccess(response: Response<GetBattleRepairResponse?>?) {
                    var body = response?.body()
                    if (body?.isSuccess() == true) {
                        showBattleSuccessDialog(orderNo, body.data?.status ?: 0 == 1)
                    } else {
                        mCxt?.toast(body?.msg ?: "抢单失败")
                    }
                }
            })
    }

    private fun showBattleSuccessDialog(orderNo: String, needPay: Boolean) {
        var dialog = object : CommonDialog(mCxt!!, R.layout.layout_dialog_battlesuccess) {
            override fun initView(root: View) {
                window?.attributes?.width = WindowManager.LayoutParams.WRAP_CONTENT
                var mBtnGoPay = root.findViewById<Button>(R.id.btnGoPay)
                mBtnGoPay.text = if (needPay) "去支付" else "确认"
                mBtnGoPay.setOnClickListener {
                    dismiss()
                    if (!needPay) {
                        EventBus.getDefault()
                            .post(MessageEvent(MessageEvent.REFRESH_BATTLEFRAGMENT, "1"))
                        return@setOnClickListener
                    }
                    Router.openUIForResult(
                        mCxt,
                        PayPlatformActivity::class.java,
                        BattleFragment.REQUESTCODE_REFRESH,
                        HashMap<String, String>().apply {
                            put(PayPlatformActivity.ORDERNO_KEY, orderNo)
                        })
                }
                setCancelable(false)
            }
        }
        dialog.show()
    }

}
