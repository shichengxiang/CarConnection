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
import com.linruan.carconnection.entities.net.GetGoodsIndex
import com.linruan.carconnection.ui.store.GoodsDetailActivity

class DiscountAdapter : RecyclerView.Adapter<DiscountAdapter.Holder> {

    private var mData= arrayListOf<GetGoodsIndex.DiscountBean>()
    constructor(context: Context) : super() {
        mCxt = context
    }
    fun setData(list:ArrayList<GetGoodsIndex.DiscountBean>){
        this.mData=list
        notifyDataSetChanged()
    }
    private var mCxt: Context? = null

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemRoot=itemView.findViewById<View>(R.id.itemRoot)
        var imageView=itemView.findViewById<ImageView>(R.id.ivImage)
        var tvPrice=itemView.findViewById<TextView>(R.id.tvPrice)//现价
        var tvOrigPrice=itemView.findViewById<TextView>(R.id.tvOrigPrice) //原价
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var view = LayoutInflater.from(mCxt).inflate(R.layout.item_rv_discount, null)
        return Holder(view)
    }

    override fun getItemCount()=mData.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        var bean= mData[position]
        holder.tvPrice.text="${bean.price?:""}"
        holder.tvOrigPrice.text="原价 ¥${bean.markprice?:""}"
        Glide.with(mCxt!!).load(bean.cover?:"").error(R.mipmap.img_product_demo).dontAnimate().into(holder.imageView)
        holder.itemRoot.setOnClickListener { Router.openUI(mCxt, GoodsDetailActivity::class.java,HashMap<String,Int>().apply {
            put("goodsId",bean.id)
        }) }
    }
}