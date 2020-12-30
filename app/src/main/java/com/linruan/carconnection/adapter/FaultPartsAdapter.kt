package com.linruan.carconnection.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.linruan.carconnection.MCallBack
import com.linruan.carconnection.R
import com.linruan.carconnection.dialog.EditPriceDialog
import com.linruan.carconnection.entities.FaultPart
import com.linruan.carconnection.entities.Master
import com.linruan.carconnection.logE
import com.linruan.carconnection.tryCatch
import com.linruan.carconnection.utils.Util
import kotlinx.android.synthetic.main.item_rv_troubleparts.view.*
import java.math.BigDecimal

class FaultPartsAdapter : RecyclerView.Adapter<FaultPartsAdapter.Holder> {
    private var mData = ArrayList<FaultPart>()
    private var editEnable = false

    constructor(context: Context, editEnable: Boolean = false) : super() {
        mCxt = context
        this.editEnable = editEnable
    }

    fun setData(list: ArrayList<FaultPart>) {
        if (list != null)
            mData = list
        else {
            mData.clear()
        }
        notifyDataSetChanged()
    }

    fun setCanEdit(editEnable: Boolean) {
        this.editEnable = editEnable
        notifyDataSetChanged()
    }

    @Synchronized
    fun addData(fault: FaultPart) {
        mData.add(fault)
        //        notifyDataSetChanged()
        notifyItemChanged(mData.size - 1)
    }

    @Synchronized
    fun clearData() {
        mData.clear()
        notifyDataSetChanged()
    }

    @Synchronized
    fun removeData(title: String) {
        val listIterator = mData.listIterator()
        while (listIterator.hasNext()) {
            val next = listIterator.next()
            if (next.title == title) {
                listIterator.remove()
            }
        }
        notifyDataSetChanged()
    }

    /**
     * 更新价格数据
     */
    fun refreshPrice(id: String?, price: String?) {
        mData.forEach {
            tryCatch {
                if (it.id == id?.toInt()) it.price = Util.stringToDouble(price)
            }
        }
        notifyDataSetChanged()
    }

    fun getData() = mData

    private var mCxt: Context? = null

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvPartName = itemView.tvPartName
        var tvPartPrice = itemView.tvPartPrice
        var llRight = itemView.llRight
        var ivEdit = itemView.ivEdit
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var view = LayoutInflater.from(mCxt)
            .inflate(R.layout.item_rv_troubleparts, null)
        return Holder(view)
    }

    override fun getItemCount() = mData.size + 1

    override fun onBindViewHolder(holder: Holder, position: Int) {
        if (position < mData.size) {
            var fault = mData[position]
            holder.tvPartName.text = fault.title
            holder.tvPartPrice.setText("¥ ${fault.price}")
            holder.tvPartPrice.setTextColor(ContextCompat.getColor(mCxt!!, R.color.black_2))
            //            holder.tvPartPrice.isEnabled = editEnable
            //            holder.tvPartPrice.addTextChangedListener(object : MTextWatcher() {
            //                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            //                    fault.price = Util.stringToDouble(s?.toString())
            //                    //                    notifyItemChanged(mData.size)
            //                }
            //            })
            holder.tvPartPrice.isEnabled = editEnable
            holder.ivEdit.visibility = View.VISIBLE
        } else {
            holder.tvPartName.text = "总价"
            holder.tvPartPrice.setTextColor(Color.parseColor("#FD4646"))
            var total = 0.0
            mData?.forEach {
                total += it.price
            }
            holder.tvPartPrice.setText("${Util.decimalFormat2(total)}")
            holder.tvPartPrice.isEnabled = false
            holder.ivEdit.visibility = View.GONE
        }
        if (!editEnable) holder.ivEdit.visibility = View.GONE
        holder.llRight.setOnClickListener {
            if (!editEnable) return@setOnClickListener
            if (position < mData.size) {
                val fault = mData[position]
                if (mCxt is Activity && !(mCxt as Activity).isDestroyed)
                    EditPriceDialog.show(mCxt, object : MCallBack<String> {
                        override fun call(t: String, position: Int) {
                            fault.price = Util.stringToDouble(t)
                            holder.tvPartPrice.setText("¥ $t")
                            refreshTotal()
                        }
                    }, "")
            }
        }
    }

    /**
     * 获取总额
     */
    fun getTotalPrice(): Double {
        var total = 0.0
        mData?.forEach {
            total += it.price
        }
        return total
    }

    /**
     * 刷新总价
     */
    fun refreshTotal() {
        notifyItemChanged(mData.size)
    }

}
