package com.linruan.carconnection.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.linruan.carconnection.R
import com.linruan.carconnection.entities.net.GetRepairType
import kotlinx.android.synthetic.main.item_choose_cartype.view.*

class CarTypeAdapter : RecyclerView.Adapter<CarTypeAdapter.Holder> {
    private var mData = ArrayList<GetRepairType.DataBean.ListBean>()

    constructor(context: Context, list: ArrayList<GetRepairType.DataBean.ListBean>?) : super() {
        mCxt = context
        if (list != null)
            mData = list
    }

    fun getData() = mData

    private var mCxt: Context? = null

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cbLine = itemView.cbLine
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(mCxt).inflate(R.layout.item_choose_cartype, null)
        return Holder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount() = mData.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        var bean = mData[position]
        holder.cbLine.text = bean.title
        holder.cbLine.setOnCheckedChangeListener { btn, isChecked ->
            bean.isChecked = isChecked
        }
    }
}
