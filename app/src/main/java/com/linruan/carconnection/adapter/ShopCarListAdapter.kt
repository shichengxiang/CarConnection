package com.linruan.carconnection.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.linruan.carconnection.MCallBack
import com.linruan.carconnection.R
import com.linruan.carconnection.Router
import com.linruan.carconnection.dialog.LoadingDialog
import com.linruan.carconnection.entities.Goods
import com.linruan.carconnection.isSuccess
import com.linruan.carconnection.net.BaseResponse
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.linruan.carconnection.ui.store.GoodsDetailActivity
import com.linruan.carconnection.utils.Util
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.item_shopcarlist.view.*
import java.math.BigDecimal

class ShopCarListAdapter : RecyclerView.Adapter<ShopCarListAdapter.Holder> {
    private var mData = ArrayList<Goods>()
    private var mOnChangeCallback: OnPriceChangeCallback? = null
    private var isAllSelected = false

    constructor(context: Context, onPriceChangeCallback: OnPriceChangeCallback) : super() {
        mCxt = context
        mOnChangeCallback = onPriceChangeCallback
    }

    fun setData(list: ArrayList<Goods>?) {
        if (list == null) {
            mData.clear()
        } else {
            this.mData = list
        }
        notifyDataSetChanged()
    }

    fun getData() = mData
    fun chooseAll(isSelected: Boolean) {
        mData.forEach {
            it.isSelected = isSelected
        }
        notifyDataSetChanged()
    }

    private var mCxt: Context? = null

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvProductName = itemView.tvProductName
        var imageView = itemView.findViewById<ImageView>(R.id.ivImageOfProduct)
        var ivIsSelected = itemView.ivIsSelected
        var tvPrice = itemView.tvPrice
        var tvNum = itemView.tvGoodsNum
        var tvSub = itemView.ivSubtract
        var tvAdd = itemView.ivAdd
        var tvSku = itemView.tvSku
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var view = LayoutInflater.from(mCxt)
            .inflate(R.layout.item_shopcarlist, null)
        return Holder(view)
    }

    override fun getItemCount() = mData.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        var goods = mData.get(position)
        Glide.with(mCxt!!)
            .load(goods.cover ?: "")
            .error(R.mipmap.img_default_image)
            .dontAnimate()
            .into(holder.imageView)
        holder.tvProductName.text = goods.title ?: ""
        holder.ivIsSelected.isSelected = goods.isSelected
        holder.tvNum.text = if (goods.num > 0) goods.num.toString() else "1"
        holder.tvPrice.text = Util.moneyformat(goods.price)
        holder.tvSku.text = "规格：${goods.sku}"
        holder.ivIsSelected.setOnClickListener {
            it.isSelected = !it.isSelected
            mData[position].isSelected = it.isSelected
            mOnChangeCallback?.onchange(calculatePrice(), isAllSelected)
        }
        holder.tvSub.setOnClickListener {
            var num = holder.tvNum.text.toString()
                .toInt()
            if (num > 1) {
                num--
                requestNumberReduce(goods.goods_id.toString(),goods.sku_id?:"",1,object :MCallBack<Int>{
                    override fun call(t: Int, position: Int) {
                        if(t==0){
                            holder.tvNum.text = "$num"
                            goods.num = num
                            mOnChangeCallback?.onchange(calculatePrice(), isAllSelected)
                        }
                    }
                })
            }
        }
        holder.tvAdd.setOnClickListener {
            var num = holder.tvNum.text.toString()
                .toInt()
            if (num < 99) {
                num++
                requestNumberAdd(goods.goods_id.toString(),goods.sku_id?:"",1,object :MCallBack<Int>{
                    override fun call(t: Int, position: Int) {
                        if(t==0){
                            holder.tvNum.text = "$num"
                            goods.num = num
                            mOnChangeCallback?.onchange(calculatePrice(), isAllSelected)
                        }
                    }
                })
            }
        }
        holder.tvNum.text = "${goods.num}"
        holder.imageView.setOnClickListener {
            if (goods.goods_id == 0) {
                return@setOnClickListener
            }
            Router.openUI(mCxt, GoodsDetailActivity::class.java, HashMap<String, Int>().apply {
                put("goodsId", goods.goods_id)
            })
        }
    }

    /**
     * 删除被选中的商品
     */
    fun removeSelectedView() {
        val listIterator = mData.listIterator()
        while (listIterator.hasNext()) {
            val next = listIterator.next()
            if (next.isSelected) {
                listIterator.remove()
            }
        }
        notifyDataSetChanged()
    }

    /**
     * 计算总价
     */
    fun calculatePrice(): BigDecimal {
        var total = BigDecimal.ZERO
        isAllSelected = true
        mData.forEach {
            if (it.isSelected) {
                val price = it.price.multiply(BigDecimal(it.num))
                total = total.add(price)
            } else {
                isAllSelected = false
            }
        }
        return total.setScale(2, BigDecimal.ROUND_DOWN)
    }
    fun requestNumberReduce(goodsId:String,skuId:String,number:Int,callback:MCallBack<Int>){
        LoadingDialog.loading(mCxt)
        Client.getInstance().reduceCartNum(goodsId,skuId,number,object :JsonCallback<BaseResponse>(){
            override fun onFinish() {
                LoadingDialog.dismiss()
            }
            override fun onSuccess(response: Response<BaseResponse?>?) {
                if(response?.body().isSuccess()){
                    callback.call(0,0)
                }else{
                    callback.call(-1,0)
                    ToastUtils.showShort(response?.body()?.msg?:"修改数量异常")
                }
            }
        })
    }
    fun requestNumberAdd(goodsId:String,skuId:String,number:Int,callback:MCallBack<Int>){
        LoadingDialog.loading(mCxt)
        Client.getInstance().addGoodsToCar(goodsId,skuId,number,object :JsonCallback<BaseResponse>(){
            override fun onFinish() {
                LoadingDialog.dismiss()
            }
            override fun onSuccess(response: Response<BaseResponse?>?) {
                if(response?.body().isSuccess()){
                    callback.call(0,0)
                }else{
                    callback.call(-1,0)
                    ToastUtils.showShort(response?.body()?.msg?:"修改数量异常")
                }
            }
        })
    }

    interface OnPriceChangeCallback {
        fun onchange(total: BigDecimal, isAllSelected: Boolean)
    }
}
