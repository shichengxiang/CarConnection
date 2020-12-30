package com.linruan.carconnection.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.linruan.carconnection.R
import com.linruan.carconnection.entities.net.GetGoodsDetailResponse
import com.linruan.carconnection.utils.Util
import kotlinx.android.synthetic.main.item_rv_goodsevaluate.view.*


class EvaluateAdapter : RecyclerView.Adapter<EvaluateAdapter.Holder> {
    private var mData = ArrayList<GetGoodsDetailResponse.Comment>()

    constructor(context: Context) : super() {
        mCxt = context
    }

    public fun setData(list: ArrayList<GetGoodsDetailResponse.Comment>?) {
        if (list != null)
            mData = list
        else {
            mData.clear()
        }
        notifyDataSetChanged()
    }

    fun getData() = mData

    private var mCxt: Context? = null

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivHead=itemView.ivHead
        var tvName=itemView.tvName
        var tvTime=itemView.tvTime
        var tvContent=itemView.tvEvaluate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var view = LayoutInflater.from(mCxt)
                .inflate(R.layout.item_rv_goodsevaluate, parent, false)
        return Holder(view)
    }

    override fun getItemCount() = mData.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        var bean=mData[position]
        holder.tvName.text=bean.name?:""
        holder.tvTime.text=Util.getDataToString(bean.create_time,"yyyy-MM-dd HH:mm")
        holder.tvContent.text=bean.content?:""
        mCxt?.let { Glide.with(it).load(bean.avatar?:"").placeholder(R.mipmap.img_default_head).error(R.mipmap.img_default_head).dontAnimate().into(holder.ivHead) }
    }

}