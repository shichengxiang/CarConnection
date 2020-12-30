package com.linruan.carconnection.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.linruan.carconnection.R
import com.linruan.carconnection.entities.Master
import kotlinx.android.synthetic.main.layout_item_master_show.view.*
import java.lang.StringBuilder

class HomeMasterAdapter : RecyclerView.Adapter<HomeMasterAdapter.Holder> {
    private var mOnItemClickListener: OnItemClickListener? = null
    private var mData = ArrayList<Master>()

    constructor(context: Context) : super() {
        mCxt = context
    }

    public fun putMasters(list: ArrayList<Master>) {
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
        var itemLayout = itemView.itemLayout
        var tvName = itemView.tvMasterName
        var tvBusiness = itemView.tvMonopoly
        var ivHead = itemView.iv_head
        var tvCommentNum=itemView.tvCommentNum
        var tvOrderNum=itemView.tvOrderNum
        var tvWorkTime=itemView.tvWorkTime
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var view = LayoutInflater.from(mCxt)
                .inflate(R.layout.layout_item_master_show, null)
        return Holder(view)
    }

    override fun getItemCount() = mData.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.itemLayout.setOnClickListener { mOnItemClickListener?.onItemClickListener(position) }
        var bean = mData[position]
        holder.tvName.text = bean.name
        var leixing = StringBuilder()
        bean.leixing?.forEach {
            leixing.append(it)
                    .append("/")
        }
        holder.tvCommentNum.text="评价（${bean.comments?:""}）"
        holder.tvOrderNum.text="已接：${bean.ordernum?:0}单"
        holder.tvWorkTime.text=bean.worktime?:"9:00-18:00"
        holder.tvBusiness.text = if(leixing.length>0) leixing.substring(0, leixing.length - 1) else ""
        Glide.with(mCxt!!)
                .load(bean.avatar ?: "")
                .placeholder(R.mipmap.img_default_head)
                .error(R.mipmap.img_default_head)
            .dontAnimate()
                .into(holder.ivHead)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mOnItemClickListener = listener
    }
}