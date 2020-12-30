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
import com.linruan.carconnection.entities.Goods
import com.linruan.carconnection.entities.GoodsOrder
import java.math.BigDecimal

class ConfirmGoodsOrderAdapter : RecyclerView.Adapter<ConfirmGoodsOrderAdapter.Holder> {
    val CONST_TYPE_SUM=2
    val CONST_TYPE_COMMON=1
    private var mTotalAmount=BigDecimal.ZERO

    private var mCxt: Context? = null
    private var mData = arrayListOf<Goods>()

    constructor(context: Context?) : super() {
        mCxt = context
    }

    fun setData(list: ArrayList<Goods>?) {
        mTotalAmount=BigDecimal.ZERO
        if(list.isNullOrEmpty()){
            this.mData.clear()
        }else{
            this.mData = list
        }
        mData.forEach {
            mTotalAmount=mTotalAmount.add(it.selectedSkuBean?.price?.multiply(BigDecimal(it.num))?:BigDecimal.ZERO)
        }
        notifyDataSetChanged()
    }
    fun addData(list: ArrayList<Goods>?){
        if(!list.isNullOrEmpty()){
            mData.addAll(list)
        }
        mTotalAmount=BigDecimal.ZERO
        mData.forEach {
            mTotalAmount=mTotalAmount.add(it.price.multiply(BigDecimal(it.num)))
        }
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        mData.removeAt(position)
        notifyItemRemoved(position)
    }

    class Holder(item: View) : RecyclerView.ViewHolder(item) {
        var imageView=item.findViewById<ImageView>(R.id.ivImageOfOrder)
        var tvGname=item.findViewById<TextView>(R.id.tvGoodsName)
        var tvGnumber=item.findViewById<TextView>(R.id.tvGoodsNumber)
        var tvGmoney=item.findViewById<TextView>(R.id.tvMoney)

        var tvTotalAmount=item.findViewById<TextView>(R.id.tvTotalAmount)
        var tvCarriage=item.findViewById<TextView>(R.id.tvCarriage)
        var tvBottomAmount=item.findViewById<TextView>(R.id.tvBottomAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        lateinit var view:View
        if(viewType==CONST_TYPE_SUM){
            view = LayoutInflater.from(mCxt)
                    .inflate(R.layout.item_confirmgoodsorder_sum, null)
        }else{
            view = LayoutInflater.from(mCxt)
                    .inflate(R.layout.item_confirmgoodsorder, null)
        }
        return Holder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return if(mData.size==position) CONST_TYPE_SUM else CONST_TYPE_COMMON
    }
    fun getTotalAmount()=mTotalAmount //

    override fun getItemCount() = mData.size+1

    override fun onBindViewHolder(holder: Holder, position: Int) {
        if(position>mData.size-1){
            holder.tvTotalAmount.text="商品总价: ¥ ${mTotalAmount.setScale(2,BigDecimal.ROUND_DOWN)}"
            holder.tvBottomAmount.text="¥ ${mTotalAmount.setScale(2,BigDecimal.ROUND_DOWN)}"
//            holder.tvCarriage.text="运费: ¥ 3.00"
            return
        }
        var goods=mData[position]
        Glide.with(mCxt!!).load(goods.cover?:"").error(R.mipmap.img_default_image).dontAnimate().into(holder.imageView)
        holder.tvGname.text=goods.title?:""
        holder.tvGnumber.text="购买数量：${goods.num} 件"
        holder.tvGmoney.text="¥${goods.selectedSkuBean?.price?:""}"

//        var order = mData[position]
    }
}
