package com.linruan.carconnection.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.linruan.carconnection.R
import com.linruan.carconnection.entities.Master


class ImageWrapAdapter : RecyclerView.Adapter<ImageWrapAdapter.Holder> {
    private var mData = ArrayList<String>()

    constructor(context: Context) : super() {
        mCxt = context
    }

    public fun setData(list: ArrayList<String>?) {
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
        var imageView=itemView.findViewById<ImageView>(R.id.ivImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var view = LayoutInflater.from(mCxt)
                .inflate(R.layout.item_show_image_wrap, parent, false)
        return Holder(view)
    }

    override fun getItemCount() = mData.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val layoutParams: ViewGroup.LayoutParams = holder.itemView.getLayoutParams()
        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
        Glide.with(mCxt!!).load(mData[position]).dontAnimate().into(holder.imageView)
    }

}