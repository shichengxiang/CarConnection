package com.linruan.carconnection.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.linruan.carconnection.R
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import kotlinx.android.synthetic.main.activity_confirmorder.*

/**
 * 原因选择 flowlayout
 */
open class FlowTagAdapter : TagAdapter<String> {
    private var mContext: Context
    private var selectEnable = true

    constructor(context: Context, data: ArrayList<String>) : super(data) {
        mContext = context
    }

    override fun getView(parent: FlowLayout?, position: Int, t: String?): View {
        var tv = LayoutInflater.from(mContext)
                .inflate(R.layout.layout_flow_item, parent, false) as TextView
        tv.text = t ?: ""
        return tv
    }

    override fun setSelected(position: Int, t: String?): Boolean {
        if (!selectEnable) return false
        return super.setSelected(position, t)
    }

    override fun onSelected(position: Int, view: View?) {
        if (!selectEnable) return
        view?.isSelected = true
    }

    fun setCanSelect(selectEnable: Boolean) {
        this.selectEnable = selectEnable
    }
}