package com.linruan.carconnection.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.linruan.carconnection.R
import com.linruan.carconnection.utils.Util
import kotlinx.android.synthetic.main.layout_item_rv_loadupimage.view.*

class LoadUpImageAdapter2 : RecyclerView.Adapter<LoadUpImageAdapter2.Holder> {
    private var mOnItemClickListener: OnItemClickListener? = null
    private var mData = ArrayList<String>()
    private var canAdd = true
    private var maxCount = 6
    private var canDelete = true

    constructor(context: Context, maxCount: Int) : super() {
        mCxt = context
        this.maxCount = maxCount
    }

    public fun putData(list: ArrayList<String>) {
        if (list != null)
            mData = list
        else {
            mData.clear()
        }
        canAdd = mData.size < maxCount
        notifyDataSetChanged()
    }

    public fun addData(url: String) {
        mData.add(url)
        canAdd = mData.size < maxCount
        notifyDataSetChanged()
    }

    fun canDelete(can: Boolean) {
        this.canDelete = can
    }

    fun getData() = mData

    private var mCxt: Context? = null

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView? = itemView.findViewById<ImageView>(R.id.imageView)
        var ivClose: ImageView? = itemView.findViewById<ImageView>(R.id.ivClose)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = if (viewType == -1) {
            LayoutInflater.from(mCxt)
                    .inflate(R.layout.layout_item_rv_add, null)
        } else {
            LayoutInflater.from(mCxt)
                    .inflate(R.layout.layout_item_rv_loadupimage, null)
        }
        var width = Util.getScreenWidth() / 5 - Util.px2dp(10)
        var params = ViewGroup.LayoutParams(width, width)
        view.layoutParams = params
        return Holder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 && canAdd) -1 else 0
    }

    override fun getItemCount() = if (canAdd) mData.size + 1 else mData.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        if (position == 0 && canAdd) {
            holder.imageView?.setOnClickListener {
                mOnItemClickListener?.onItemClickListener(
                    position
                                                         )
            }
            return
        }
        var url = mData[if (canAdd) position - 1 else position]
        if (canDelete) {
            holder.ivClose?.setOnClickListener {
                mData.remove(url)
                canAdd = mData.size < maxCount
                notifyDataSetChanged()
            }
        } else {
            holder.ivClose?.visibility = View.GONE
        }
        holder.imageView?.let {
            Glide.with(mCxt!!)
                    .load(url)
                    .error(R.mipmap.img_default_image)
                .dontAnimate()
                    .into(it)
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mOnItemClickListener = listener
    }
}