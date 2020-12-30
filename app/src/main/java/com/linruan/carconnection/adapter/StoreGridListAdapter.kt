package com.linruan.carconnection.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.linruan.carconnection.R
import com.linruan.carconnection.Router
import com.linruan.carconnection.entities.Goods
import com.linruan.carconnection.ui.store.GoodsDetailActivity
import com.linruan.carconnection.utils.Util

class StoreGridListAdapter : RecyclerView.Adapter<StoreGridListAdapter.Holder> {
    private var mData = arrayListOf<Goods>()

    constructor(context: Context) : super() {
        mCxt = context
    }

    fun setData(list: ArrayList<Goods>?) {
        if (list.isNullOrEmpty()) {
            this.mData.clear()
        } else {
            this.mData = list
        }
        notifyDataSetChanged()
    }

    private var mCxt: Context? = null

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView = itemView.findViewById<ImageView>(R.id.ivImage)
        var root = itemView.findViewById<View>(R.id.itemContent)
        var tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
        var tvPrice = itemView.findViewById<TextView>(R.id.tvPrice)
        var tvSaleNum = itemView.findViewById<TextView>(R.id.tvSaleNum)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var view = LayoutInflater.from(mCxt)
                .inflate(R.layout.item_rv_bottomlist, null)
        return Holder(view)
    }

    override fun getItemCount() = mData.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        var bean = mData[position]
        Glide.with(mCxt!!)
                .load(bean.cover ?: "")
                .error(R.mipmap.img_product_demo)
            .dontAnimate()
                .into(holder.imageView)
        holder.tvTitle.text = bean.title ?: ""
        holder.tvPrice.text = "${bean.price ?: ""}"
        holder.tvSaleNum.text = "月销 ${Util.numToW(bean.sales)}笔"
        holder.root.setOnClickListener {
            Router.openUI(mCxt, GoodsDetailActivity::class.java, HashMap<String, Int>().apply {
                put("goodsId", bean.id.toInt())
            })
        }
    }
}