package com.linruan.carconnection.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.linruan.carconnection.R
import com.linruan.carconnection.Router
import com.linruan.carconnection.entities.net.GetGoodsIndex
import com.linruan.carconnection.ui.store.SearchGoodsListActivity
import java.util.HashMap

class StoreCategoryAdapter : RecyclerView.Adapter<StoreCategoryAdapter.Holder> {
    var cars = arrayListOf<String>("电动车", "自行车", "摩托车", "充电器", "电动车配件", "电瓶")
    var carImages = arrayListOf<Int>(
        R.mipmap.img_electromobile,
        R.mipmap.img_mobile,
        R.mipmap.img_motorbike,
        R.mipmap.img_charger,
        R.mipmap.img_electromobile,
        R.mipmap.img_charger)
    var ids = arrayOf(438, 439, 440, 441, 453, 454)

    private var mData = arrayListOf<GetGoodsIndex.ItemListBean>()

    constructor(context: Context) : super() {
        mCxt = context
    }

    fun setData(list: ArrayList<GetGoodsIndex.ItemListBean>?) {
        if (list.isNullOrEmpty()) {
            return
        }
        mData = list
        notifyDataSetChanged()
    }

    private var mCxt: Context? = null

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView = itemView.findViewById<ImageView>(R.id.ivImage)
        var categoryName = itemView.findViewById<TextView>(R.id.tvCategoryName)
        var root = itemView.findViewById<View>(R.id.rootContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var view = LayoutInflater.from(mCxt)
                .inflate(R.layout.item_gv_category, null)
        return Holder(view)
    }

    override fun getItemCount() = if (mData.isNullOrEmpty()) cars.size else mData.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        if (mData.isNullOrEmpty()) {
            holder.imageView.setImageResource(carImages[position])
            holder.categoryName.text = cars[position]
            holder.root.setOnClickListener {
                Router.openUI(mCxt, SearchGoodsListActivity::class.java, HashMap<String, Any>().apply {
                    put("keyword", cars[position])
                    put("id", ids[position])
                })
            }
        } else {
            var bean = mData[position]
            Glide.with(mCxt!!)
                    .load(bean.cover ?: "")
                .dontAnimate()
                    .into(holder.imageView)
            holder.categoryName.text = bean.title ?: ""
            holder.root.setOnClickListener {
                Router.openUI(mCxt, SearchGoodsListActivity::class.java, HashMap<String, Any>().apply {
                    put("keyword", bean.title ?: "")
                    put("id", bean.id)
                })
            }
        }
    }
}
