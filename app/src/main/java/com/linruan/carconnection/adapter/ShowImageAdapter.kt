package com.linruan.carconnection.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.linruan.carconnection.R
import com.linruan.carconnection.entities.Master

class ShowImageAdapter : RecyclerView.Adapter<ShowImageAdapter.Holder> {
    private var mData = ArrayList<String>()

    constructor(context: Context) : super() {
        mCxt = context
    }

    fun setData(list: ArrayList<String>) {
        if (list != null)
            mData = list
        else {
            mData.clear()
        }
        notifyDataSetChanged()
    }
    fun addData(url:String){
        mData.add(url)
        notifyItemChanged(mData.size-1)
    }
    fun getData()=mData

    private var mCxt: Context? = null

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView=itemView.findViewById<ImageView>(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var view = LayoutInflater.from(mCxt).inflate(R.layout.item_show_image_exact, null)
        return Holder(view)
    }

    override fun getItemCount() = mData.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        Glide.with(mCxt!!).load(mData[position]).error(R.mipmap.img_default_image).centerCrop().dontAnimate().into(holder.imageView)
    }

}