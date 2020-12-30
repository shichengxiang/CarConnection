package com.linruan.carconnection.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.linruan.carconnection.R
import com.linruan.carconnection.Router
import com.linruan.carconnection.entities.Goods
import com.linruan.carconnection.ui.store.GoodsEvaluateActivity
import com.linruan.carconnection.utils.Util
import kotlinx.android.synthetic.main.item_evaluate_goods.view.*
import java.math.BigDecimal

class EvaluateGoodsAfterAdapter : RecyclerView.Adapter<EvaluateGoodsAfterAdapter.Holder> {

    private var mCxt: Context? = null
    private var mData = arrayListOf<Goods>()
    private var mOrderNo=""
    private var mOrderId=""
    constructor(context: Context?) : super() {
        mCxt = context
    }

    constructor(context: Context?, list: ArrayList<Goods>?,orderNo:String,orderId:String) : super() {
        mCxt = context
        mOrderNo=orderNo
        mOrderId=orderId
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
        var tvOrderNo = item.tvOrderNo
        var ivImageView=item.ivGoodsImage
        var tvGoodsName = item.tvGoodsName
        var tvGoodsAmount = item.tvGoodsAmount
        var tvGoEvaluate=item.tvGoEvaluate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var view = LayoutInflater.from(mCxt)
                .inflate(R.layout.item_evaluate_goods, parent,false)
        return Holder(view)
    }

    override fun getItemCount() = mData.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        var goods = mData[position]
        holder.tvOrderNo.text="订单编号：${mOrderNo}"
        Glide.with(mCxt!!)
                .load(goods.goods_cover)
                .error(R.mipmap.img_default_image)
            .dontAnimate()
                .into(holder.ivImageView)
        holder.tvGoodsName.text = goods.goods_title ?: ""
        holder.tvGoodsAmount.text = Util.moneyformat(goods.price.setScale(2, BigDecimal.ROUND_DOWN))
        holder.tvGoEvaluate.setOnClickListener {
            Router.openUI(mCxt,GoodsEvaluateActivity::class.java, Bundle().apply {
                putSerializable("goods",goods)
                putString("orderNo",mOrderNo)
                putString("orderId",mOrderId)
            })
        }
    }
}