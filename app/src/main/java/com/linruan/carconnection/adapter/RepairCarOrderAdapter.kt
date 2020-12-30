package com.linruan.carconnection.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.linruan.carconnection.R
import com.linruan.carconnection.Router
import com.linruan.carconnection.UserManager
import com.linruan.carconnection.entities.RepairOrder
import com.linruan.carconnection.ui.mine.RePairCarOrderActivity
import com.linruan.carconnection.ui.mine.RepairCarOrderDetailActivity
import java.lang.StringBuilder
import java.util.HashMap

class RepairCarOrderAdapter : RecyclerView.Adapter<RepairCarOrderAdapter.Holder> {

    private var currentType = RepairOrder.STATE_ALL
    private var mData= arrayListOf<RepairOrder>()

    constructor(context: Context?, type: Int) : super() {
        mCxt = context
        this.currentType = type
    }

    private var mCxt: Context? = null
    fun setData(list: ArrayList<RepairOrder>?) {
        if (list.isNullOrEmpty()) {
            this.mData.clear()
        } else {
            this.mData = list
        }
        notifyDataSetChanged()
    }

    fun addData(list: ArrayList<RepairOrder>?) {
        if (!list.isNullOrEmpty()) {
            mData.addAll(list)
        }
        notifyDataSetChanged()
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var root = itemView.findViewById<View>(R.id.itemContent)
        var tvState = itemView.findViewById<TextView>(R.id.tvState)
        var tvOrderNo=itemView.findViewById<TextView>(R.id.tvOrderNo)
        var tvCarType=itemView.findViewById<TextView>(R.id.tvCarType)
        var tvFaults=itemView.findViewById<TextView>(R.id.tvFaults)
        var tvRemark=itemView.findViewById<TextView>(R.id.tvRemark)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var view = LayoutInflater.from(mCxt)
                .inflate(R.layout.layout_item_rv_repaircarorder, null)
        return Holder(view)
    }

    override fun getItemCount() = mData.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        var order=mData[position]
        holder.root.setOnClickListener {
            Router.openUIForResult(mCxt, RepairCarOrderDetailActivity::class.java,100, HashMap<String,String>().apply {
                put(RepairCarOrderDetailActivity.REPAIRID_KEY,order.id)
            })
        }
        when (order.status) {
            RepairOrder.STATE_WAITPAY -> holder.tvState.text = "待付款"
            RepairOrder.STATE_RUNNING -> {
                holder.tvState.text = "进行中"
                if(order.step==7){
                    holder.tvState.text="维修失败待确认"
                }
            }
            RepairOrder.STATE_COMPLETED -> holder.tvState.text  = if(order.step==6) "维修失败" else "已完成"
            RepairOrder.STATE_CANCELED -> holder.tvState.text = "已取消"
        }
        holder.tvOrderNo.text="订单编号：${order.orderno}"
        //类型
        var carType=""
        UserManager.mCarTypes?.forEach {
            if(order.leixing_id==it.id){
                carType=it.title?:""
                return@forEach
            }
        }
        holder.tvCarType.text=carType
        //错误
        var errReason=StringBuilder()
        UserManager.mFaults?.forEach {
            if(order.fault_ids.contains(it.id.toString())){
                errReason.append(it.title).append(" ")
            }
        }
        holder.tvFaults.text=errReason.toString()
        holder.tvRemark.text=if(order.intro.isNullOrBlank()) "暂无备注" else order.intro

    }
}
