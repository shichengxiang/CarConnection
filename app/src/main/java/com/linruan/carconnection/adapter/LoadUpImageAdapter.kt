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
import com.linruan.carconnection.view.PieProgressBar

class LoadUpImageAdapter : RecyclerView.Adapter<LoadUpImageAdapter.Holder> {
    private var mOnItemClickListener: OnItemClickListener? = null
    private var mData = ArrayList<ImageLoadUp>()
    private var canAdd = true
    private var maxCount = 9
    private var canDelete = true

    constructor(context: Context, maxCount: Int) : super() {
        mCxt = context
        this.maxCount = maxCount
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

    public fun addData(image: ImageLoadUp) {
        mData.add(image)
        canAdd = mData.size < maxCount
        notifyItemChanged(mData.size - 1)
    }

    fun canAdd(can: Boolean) {
        this.canAdd = can
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
        var pieProgressBar: PieProgressBar? = itemView.findViewById<PieProgressBar>(R.id.pieProgressBar)
        var tvUploadSuccess: View? = itemView.findViewById(R.id.tvUploadSuccess)
        var tvError: View? = itemView.findViewById(R.id.tvError)
        fun setProgress(p: Int) {
            if (p < 100) {
                tvUploadSuccess?.visibility = View.GONE
                pieProgressBar?.visibility = View.VISIBLE
                pieProgressBar?.progress = p
            } else {
                pieProgressBar?.visibility = View.GONE
                tvUploadSuccess?.visibility = View.VISIBLE
            }
        }

        fun onError() {
            tvError?.visibility = View.VISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = if (viewType == -1) {
            LayoutInflater.from(mCxt)
                    .inflate(R.layout.layout_item_rv_loadupimage_default, null)
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
        return if (canAdd && position == mData.size) -1 else 0
    }

    override fun getItemCount() = if (canAdd) mData.size + 1 else mData.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        if (canAdd && position >= mData.size) {
            holder.imageView?.setOnClickListener {
                mOnItemClickListener?.onItemClickListener(
                    position
                                                         )
            }
            return
        }
        var imageLoadUp = mData[position]
        imageLoadUp.position = position
        var url = imageLoadUp.filepath
        if (!canDelete) {
            holder.ivClose?.visibility = View.GONE
        } else {
            holder.ivClose?.setOnClickListener {
                mData.remove(imageLoadUp)
                canAdd = mData.size < maxCount
                notifyDataSetChanged()
            }
        }
        holder.imageView?.let {
            Glide.with(mCxt!!)
                    .load(url)
                    .error(R.mipmap.img_default_image)
                .dontAnimate()
                    .into(it)
        }
        holder.tvError?.setOnClickListener {
            imageLoadUp.isError = false
            it?.visibility = View.GONE
            mOnReloadUpListener?.onReloadup(position)
        }
        //状态
        if (imageLoadUp.isError) {
            holder.tvError?.visibility = View.VISIBLE
        } else {
            holder.tvError?.visibility = View.GONE
        }
        if (imageLoadUp.isLoaded) {
            holder.tvUploadSuccess?.visibility = View.VISIBLE
        } else {
            holder.tvUploadSuccess?.visibility = View.GONE
        }

    }

    fun getDataAt(position: Int) = mData[position]

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mOnItemClickListener = listener
    }

    private var mOnReloadUpListener: OnReloadUpListener? = null
    fun setOnReLoadUpListener(onReloadUpListener: OnReloadUpListener) {
        this.mOnReloadUpListener = onReloadUpListener
    }

    interface OnReloadUpListener {
        fun onReloadup(position: Int)
    }
}