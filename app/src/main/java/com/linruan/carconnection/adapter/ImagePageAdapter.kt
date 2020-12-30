package com.linruan.carconnection.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.linruan.carconnection.R
import java.lang.ref.SoftReference

class ImagePageAdapter : PagerAdapter {
    private var mContext: SoftReference<Context?>? = null
    private var mData = arrayListOf<String>()

    constructor(context: Context?) : super() {
        mContext = SoftReference(context)
    }

    constructor(context: Context?, list: ArrayList<String>) : super() {
        mContext = SoftReference(context)
        this.mData = list
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var view = LayoutInflater.from(mContext?.get())
                .inflate(R.layout.item_vp_img_match_centercrop, null)
        var imageView = view.findViewById<ImageView>(R.id.iv_advert)
        var url = mData[position]
        Glide.with(mContext?.get()!!)
                .load(url)
                .error(R.mipmap.img_goods_big_default)
                .placeholder(R.mipmap.img_goods_big_default)
            .dontAnimate()
                .into(imageView)
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount() = mData.size

}