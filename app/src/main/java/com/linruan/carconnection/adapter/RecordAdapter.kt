package com.linruan.carconnection.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.linruan.carconnection.R
import com.linruan.carconnection.Router
import com.linruan.carconnection.UserManager
import com.linruan.carconnection.entities.net.GetConsumeRecordResponse
import com.linruan.carconnection.ui.master.MasterRepairDetailActivity
import com.linruan.carconnection.ui.mine.RepairCarOrderDetailActivity
import com.linruan.carconnection.ui.store.GoodsOrderDetailActivity
import com.linruan.carconnection.utils.Util
import kotlinx.android.synthetic.main.layout_item_record.view.*
import java.lang.StringBuilder

class RecordAdapter : RecyclerView.Adapter<RecordAdapter.Holder> {
    private var mOnItemClickListener: OnItemClickListener? = null
    private var mData = ArrayList<GetConsumeRecordResponse.ConsumeBean>()
    private var mDayContainer = StringBuilder()

    constructor(context: Context) : super() {
        mCxt = context
    }

    public fun putData(list: ArrayList<GetConsumeRecordResponse.ConsumeBean>?) {
        if (list != null)
            mData = list
        else {
            mData.clear()
        }
        mDayContainer.clear()
        notifyDataSetChanged()
    }

    fun addData(list: ArrayList<GetConsumeRecordResponse.ConsumeBean>?) {
        if (!list.isNullOrEmpty()) {
            mData.addAll(list)
            notifyDataSetChanged()
        }
    }

    fun clear() {
        mData.clear()
        notifyDataSetChanged()
    }

    fun getData() = mData

    private var mCxt: Context? = null

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle = itemView.tvCategory
        var tvContent = itemView.tvContent
        var tvMoney = itemView.tvMoney
        var tvDay = itemView.tvDay
        var itemRoot=itemView.itemRoot
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(mCxt).inflate(R.layout.layout_item_record, null)
        return Holder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount() = mData.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        var bean = mData[position]
        holder.tvTitle.text = bean.title ?: ""
        holder.tvContent.text = bean.content ?: ""
        holder.tvMoney.text = bean.money
        var day = Util.getDataToString(bean.create_time, "MM月dd日")
        if (mDayContainer.contains(day, true) && !bean.isShowDay) {
            holder.tvDay.visibility = View.GONE
        } else {
            if (day != ""){
                bean.isShowDay=true
                mDayContainer.append(day)
            }
            holder.tvDay.text = day
            holder.tvDay.visibility = View.VISIBLE
        }
        holder.itemRoot.setOnClickListener {
            if(bean.table=="repair"){
                if(UserManager.getUser()?.isMaster==true){
                    Router.openUI(mCxt, MasterRepairDetailActivity::class.java, HashMap<String, String>().apply {
                        put("repairId",bean.content_id?:"")
                    })
                }else{
                    Router.openUI(mCxt, RepairCarOrderDetailActivity::class.java, java.util.HashMap<String, String>().apply {
                        put(RepairCarOrderDetailActivity.REPAIRID_KEY,bean.content_id?:"")
                    })
                }
            }else if(bean.table=="orders"){
                Router.openUI(mCxt, GoodsOrderDetailActivity::class.java, HashMap<String, String>().apply {
                    put(GoodsOrderDetailActivity.ORDERIDKEY, bean.content_id ?: "")
                })
            }
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mOnItemClickListener = listener
    }
}
