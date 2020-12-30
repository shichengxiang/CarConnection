package com.linruan.carconnection.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.linruan.carconnection.MCallBack
import com.linruan.carconnection.R
import com.linruan.carconnection.Router
import com.linruan.carconnection.entities.Address
import com.linruan.carconnection.ui.store.EditAddressActivity
import kotlinx.android.synthetic.main.item_address_list.view.*

class AddressListAdapter : RecyclerView.Adapter<AddressListAdapter.Holder> {
    private var mData = arrayListOf<Address>()

    constructor(context: Context) : super() {
        mCxt = context
    }

    fun setData(list: ArrayList<Address>?) {
        if (list.isNullOrEmpty()) {
            mData.clear()
        } else {
            mData = list
        }
        notifyDataSetChanged()
    }

    fun removeData(position: Int) {
        if (mData.size >= position)
            mData.removeAt(position)
        notifyDataSetChanged()
    }
    fun getData()=mData

    private var mCxt: Context? = null

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivGoEdit = itemView.ivGoEdit
        var tvName = itemView.tvNamePhone
        var tvArea = itemView.tvArea
        var tvDelete = itemView.tvDelete
        var rlRoot=itemView.rlRoot
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var view = LayoutInflater.from(mCxt)
                .inflate(R.layout.item_address_list, parent, false)
        return Holder(view)
    }

    override fun getItemCount() = mData.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        var address = mData[position]
        holder.tvName.text = "${address.name}       ${address.phone}"
        holder.tvArea.text = "${address.getCountry()} ${address.address}"
        holder.ivGoEdit.setOnClickListener { Router.openUI(mCxt, EditAddressActivity::class.java, Bundle().apply { putSerializable("address", address) }) }
        holder.tvDelete.setOnClickListener {
            mCallBack?.call(address, position)
        }
        holder.rlRoot.setOnClickListener {
            mOnItemClickListener?.onItemClickListener(position)
        }
    }

    private var mCallBack: MCallBack<Address>? = null
    fun setCallBack(callback: MCallBack<Address>) {
        this.mCallBack = callback
    }
    private var mOnItemClickListener:OnItemClickListener?=null
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener){
        mOnItemClickListener=onItemClickListener
    }
}