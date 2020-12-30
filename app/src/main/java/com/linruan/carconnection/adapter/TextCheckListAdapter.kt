package com.linruan.carconnection.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.linruan.carconnection.MCallBack
import com.linruan.carconnection.R

class TextCheckListAdapter : RecyclerView.Adapter<TextCheckListAdapter.Holder> {

    private var mData = listOf<String>()
    private var mCallBack: MCallBack<String>? = null
    private var mCurrentIndex=-1

    constructor(context: Context, s: List<String>, callback: MCallBack<String>) : super() {
        mCxt = context
        this.mData = s
        mCallBack = callback
    }
    fun clear(){
        mCurrentIndex=-1
        notifyDataSetChanged()
    }


    private var mCxt: Context? = null

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv = itemView.findViewById<TextView>(R.id.tvText)
        var ivArrow=itemView.findViewById<View>(R.id.ivArrow)
        var rlRoot=itemView.findViewById<View>(R.id.rlRoot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var view = LayoutInflater.from(mCxt).inflate(R.layout.item_layout_checklist_text, null)
        return Holder(view)
    }

    override fun getItemCount() = mData.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.ivArrow.isSelected = position==mCurrentIndex
        holder.tv.isSelected = position==mCurrentIndex
        holder.tv.text = mData[position]
        holder.rlRoot.setOnClickListener {
            mCurrentIndex=position
            mCallBack?.call("",position)
            notifyDataSetChanged()
        }
    }

}