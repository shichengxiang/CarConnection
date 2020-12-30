package com.linruan.carconnection.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.linruan.carconnection.R
import com.linruan.carconnection.entities.Message
import com.linruan.carconnection.utils.Util

class MsgAdapter : RecyclerView.Adapter<MsgAdapter.VHolder> {
    var mct: Context

    constructor(context: Context) : super() {
        mct = context
    }

    var mData = ArrayList<Message>()

    //刷新
    fun putMsg(list: ArrayList<Message>?) {
        if (list != null) {
            mData = list
        } else {
            mData?.clear()
        }
        notifyDataSetChanged()
    }

    //加载更多
    fun putMoreMsg(list: ArrayList<Message>?) {
        if (!list.isNullOrEmpty()) {
            mData.addAll(list)
            notifyDataSetChanged()
        }
    }
    fun clear(){
        mData.clear()
        notifyDataSetChanged()
    }

    class VHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle=itemView.findViewById<TextView>(R.id.tvMsgTitle)
        var tvContent=itemView.findViewById<TextView>(R.id.tvMsgDesc)
        var tvTime=itemView.findViewById<TextView>(R.id.tvMsgTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        var view = LayoutInflater.from(mct)
            .inflate(R.layout.item_rv_msg, null)
        return VHolder(view)
    }

    override fun getItemCount() = mData?.size

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        var msg= mData[position]
        holder.tvTitle.text=msg.title?:""
        holder.tvContent.text=msg.content?:""
        holder.tvTime.text=Util.getDataToString(msg.create_time,"yyyy-MM-dd HH:mm:ss")
    }
}