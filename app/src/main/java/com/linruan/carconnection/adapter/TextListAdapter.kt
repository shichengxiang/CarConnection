package com.linruan.carconnection.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.linruan.carconnection.MCallBack
import com.linruan.carconnection.R
import com.linruan.carconnection.entities.Master
import kotlinx.android.synthetic.main.layout_item_master_show.view.*

class TextListAdapter : RecyclerView.Adapter<TextListAdapter.Holder> {

    private var mData = listOf<String>()
    private var mCallBack: MCallBack<String>? = null

    constructor(context: Context, s: List<String>, callback: MCallBack<String>) : super() {
        mCxt = context
        this.mData = s
        mCallBack = callback
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
        holder.tv.text = mData[position]
        holder.tv.setOnClickListener { mCallBack?.call(holder.tv.text.toString(),position) }
    }

}