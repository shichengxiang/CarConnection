package com.linruan.carconnection.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.linruan.carconnection.MCallBack
import com.linruan.carconnection.R
import com.linruan.carconnection.entities.Master
import com.linruan.carconnection.entities.net.GetPingJiaWorkerResponse
import kotlinx.android.synthetic.main.layout_item_master_show.view.*

class AdvantageAdapter : RecyclerView.Adapter<AdvantageAdapter.Holder> {

    private var mData = arrayListOf<GetPingJiaWorkerResponse.PingJia>()

    constructor(context: Context) : super() {
        mCxt = context
    }

    fun setData(list: ArrayList<GetPingJiaWorkerResponse.PingJia>?) {
        if (list.isNullOrEmpty()) {
            mData.clear()
        } else {
            mData = list
        }
        notifyDataSetChanged()
    }


    private var mCxt: Context? = null

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv = itemView.findViewById<TextView>(R.id.tvTxt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var view = LayoutInflater.from(mCxt).inflate(R.layout.item_advantage, null)
        return Holder(view)
    }

    override fun getItemCount() = mData.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        var bean = mData[position]
        holder.tv.text = bean.title ?: ""
        holder.tv.setOnClickListener {
            it.isSelected = !it.isSelected
            bean.isChecked = it.isSelected
        }
    }

    fun getSelectedAll(): String {
        var result = StringBuffer()
        mData.forEach {
            if (it.isChecked) {
                result.append(it.title).append(",")
            }
        }
        return if (result.isNotEmpty()) {
            result.substring(0, result.length - 1)
        } else {
            ""
        }
    }

}
