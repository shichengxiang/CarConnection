package com.linruan.carconnection.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.linruan.carconnection.MCallBack
import com.linruan.carconnection.R
import com.linruan.carconnection.entities.City
import com.linruan.carconnection.entities.Master
import kotlinx.android.synthetic.main.layout_item_master_show.view.*

class SelectCityAdapter : RecyclerView.Adapter<SelectCityAdapter.Holder> {

    private var mData = listOf<City>()
    private var mCallBack: MCallBack<City>? = null
    private var mCurrentType=0
    constructor(context: Context,callback: MCallBack<City>) : super() {
        mCxt = context
        mCallBack = callback
    }
    fun getType()=mCurrentType
    fun setData(list:ArrayList<City>,type:Int){
        this.mData=list
        this.mCurrentType=type
        notifyDataSetChanged()
    }


    private var mCxt: Context? = null

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv = itemView.findViewById<TextView>(R.id.tvTxt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var view = LayoutInflater.from(mCxt).inflate(R.layout.item_text_list, null)
        return Holder(view)
    }

    override fun getItemCount() = mData.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.tv.text = mData[position].name
        holder.tv.setOnClickListener { mCallBack?.call(mData[position],mCurrentType) }
    }

}