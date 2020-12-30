package com.linruan.carconnection.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.linruan.carconnection.R
import com.linruan.carconnection.entities.Goods
import com.linruan.carconnection.entities.GoodsOrder
import kotlinx.android.synthetic.main.item_inner_goods.view.*
import java.math.BigDecimal

class InnerGoodsAdapter : RecyclerView.Adapter<InnerGoodsAdapter.Holder> {

    private var mCxt: Context? = null
    private var mData = arrayListOf<Goods>()
    private var currentType = GoodsOrder.STATE_ALL

    constructor(context: Context?, state: Int) : super() {
        mCxt = context
        currentType = state
    }

    constructor(context: Context?, state: Int, list: ArrayList<Goods>?) : super() {
        mCxt = context
        currentType = state
        if (list.isNullOrEmpty()) {
            this.mData.clear()
        } else {
            this.mData = list
        }
    }

    fun setData(list: ArrayList<Goods>?) {
        if (list.isNullOrEmpty()) {
            this.mData.clear()
        } else {
            this.mData = list
        }
        notifyDataSetChanged()
    }

    fun addData(list: ArrayList<Goods>?) {
        if (!list.isNullOrEmpty()) {
            mData.addAll(list)
        }
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        mData.removeAt(position)
        notifyItemRemoved(position)
    }

    class Holder(item: View) : RecyclerView.ViewHolder(item) {
        var ivGoodsImage = item.ivGoodsImage
        var tvGoodsName = item.tvGoodsName
        var tvGoodsAmount = item.tvGoodsAmount
        var tvGoodsNum=item.tvGoodsNumber
        var tvReturnTag=item.tvReturnTag
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var view = LayoutInflater.from(mCxt)
                .inflate(R.layout.item_inner_goods, null)
        return Holder(view)
    }

    override fun getItemCount() = mData.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        var goods = mData[position]
        Glide.with(mCxt!!)
                .load(goods.goods_cover)
                .error(R.mipmap.img_default_image)
            .dontAnimate()
                .into(holder.ivGoodsImage)
        holder.tvGoodsName.text = goods.goods_title ?: ""
        holder.tvGoodsAmount.text = goods.price.setScale(2, BigDecimal.ROUND_DOWN)
                .toString()
        holder.tvGoodsNum.text="购买数量：${goods.num} 件"
    }
}
