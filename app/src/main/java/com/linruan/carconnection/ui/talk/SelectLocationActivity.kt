package com.linruan.carconnection.ui.talk

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.navi.model.AMapModeCrossOverlay
import com.linruan.carconnection.BaseActivity
import com.linruan.carconnection.MCallBack
import com.linruan.carconnection.R
import com.linruan.carconnection.UserManager
import com.linruan.carconnection.utils.MapUtil
import kotlinx.android.synthetic.main.activity_selectlocation.*
import kotlinx.android.synthetic.main.item_select_location.view.*

/**
 * 项目名称：CarConnection
 * 类描述：
 * 创建人：shichengxiang
 * 创建时间：2020/7/31 1:02
 * @version
 */
class SelectLocationActivity : BaseActivity() {
    override fun getLayout() = R.layout.activity_selectlocation

    override fun initView() {
        ivClose.setOnClickListener { onBackPressed() }
        rvLocation.layoutManager = LinearLayoutManager(this)
        var info=MapUtil.getAddress(this,UserManager.currentLocation[0],UserManager.currentLocation[1])
        rvLocation.adapter = LocationAdapter(this.applicationContext,info.area,info.road,object:MCallBack<String>{
            override fun call(t: String, position: Int) {
                setResult(1, Intent().putExtra("address",t))
                finish()
            }
        })
    }

    private class LocationAdapter : RecyclerView.Adapter<LocationAdapter.ViewHolder> {
        private var context: Context? = null
        private var mCallBack:MCallBack<String>?= null
        private var mArea:ArrayList<String>?=null
        private var mRoad=""
        constructor(context: Context,location: ArrayList<String>?,road:String,callback: MCallBack<String>) : super() {
            this.context=context
            this.mCallBack=callback
            this.mArea=location
            this.mRoad=road
        }


        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var vContent = itemView.vContent
            var rb = itemView.rb
            var tvLocation = itemView.tvLocation
            var tvRoad = itemView.tvRoad
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            var view = View.inflate(context, R.layout.item_select_location, null)
            return ViewHolder(view)
        }

        override fun getItemCount() = mArea?.size?:0

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.vContent.setOnClickListener {
                holder.rb.isChecked = true
                mCallBack?.call(mArea!![position],0)
            }
            holder.tvLocation.text=mArea!![position]
            holder.tvRoad.text=mRoad
        }
    }
}
