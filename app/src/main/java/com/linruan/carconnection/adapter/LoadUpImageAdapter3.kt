package com.linruan.carconnection.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.linruan.carconnection.R
import com.linruan.carconnection.entities.ImageLoadUp
import com.linruan.carconnection.utils.Util

class LoadUpImageAdapter3 : RecyclerView.Adapter<LoadUpImageAdapter3.Holder> {
    private var mOnItemClickListener: OnItemClickListener? = null
    private var mData = ArrayList<ImageLoadUp>()
    private var canAdd = true
    private var maxCount = 9
    private var canDelete = true
    private var mColumns = 3 //默认3列

    constructor(context: Context, maxCount: Int, columns: Int = 3) : super() {
        mCxt = context
        this.maxCount = maxCount
        this.mColumns = columns
    }

    public fun putData(list: ArrayList<ImageLoadUp>) {
        if (list != null)
            mData = list
        else {
            mData.clear()
        }
        canAdd = mData.size < maxCount
        notifyDataSetChanged()
    }

    public fun addData(url: ImageLoadUp) {
        mData.add(url)
        canAdd = mData.size < maxCount
        notifyItemChanged(mData.size - 1)
    }

    public fun addData(list: ArrayList<ImageLoadUp>?) {
        if (list != null)
            mData.addAll(list)
        canAdd = mData.size < maxCount
        notifyItemChanged(mData.size - 1)
    }

    fun canDelete(can: Boolean) {
        this.canDelete = can
    }

    fun getData() = mData

    private var mCxt: Context? = null

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView? = itemView.findViewById<ImageView>(R.id.imageView)
        var ivClose: ImageView? = itemView.findViewById<ImageView>(R.id.ivClose)
        var llAdd: View? = itemView.findViewById(R.id.llAdd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = if (viewType == -1 && canAdd) {
            LayoutInflater.from(mCxt)
                .inflate(R.layout.layout_item_rv_add_match, null)
        } else {
            LayoutInflater.from(mCxt)
                .inflate(R.layout.layout_item_rv_loadupimage_match, null)
        }
        var width = Util.getScreenWidth() / mColumns - Util.px2dp(15)
        var params = ViewGroup.LayoutParams(width, width)
        view.layoutParams = params
        return Holder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position >= mData.size) -1 else 0
    }

    override fun getItemCount() = if (canAdd) mData.size + 1 else mData.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        if (position >= mData.size) {
            holder.llAdd?.setOnClickListener {
                mOnItemClickListener?.onItemClickListener(
                    position
                )
            }
            return
        }
        var bean = mData[position]
        if (canDelete) {
            holder.ivClose?.setOnClickListener {
                mData.remove(bean)
                canAdd = mData.size < maxCount
                notifyDataSetChanged()
            }
        } else {
            holder.ivClose?.visibility = View.GONE
        }
        //        if(holder.imageView!=null){
        //            var params=holder.imageView?.layoutParams
        //            params?.height=mCxt!!.resources.displayMetrics.widthPixels/3 -Util.px2dp(36)
        //            holder.imageView?.layoutParams=params
        //        }
        holder.imageView?.let {
            Glide.with(mCxt!!)
                .load(bean.filepath)
                .error(R.mipmap.img_goods_big_default)
                .dontAnimate()
                .into(it)
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mOnItemClickListener = listener
    }
}