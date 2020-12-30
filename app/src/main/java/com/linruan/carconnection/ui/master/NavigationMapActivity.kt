package com.linruan.carconnection.ui.master

import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.View
import com.amap.api.maps.AMap
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MyLocationStyle
import com.linruan.carconnection.*
import com.linruan.carconnection.dialog.CommonDialog
import com.linruan.carconnection.dialog.TipDialog
import com.linruan.carconnection.entities.MessageEvent
import com.linruan.carconnection.entities.RepairOrder
import com.linruan.carconnection.moudle.NavigationMapPresenter
import com.linruan.carconnection.moudle.NavigationMapView
import com.linruan.carconnection.utils.MapUtil
import com.linruan.carconnection.utils.Util
import kotlinx.android.synthetic.main.activity_navigationmap.*
import org.greenrobot.eventbus.EventBus

/**
 * author：shichengxiang on 2020/6/3 15:53
 * email：1328911009@qq.com
 */
class NavigationMapActivity : BaseActivity(), NavigationMapView {

    private var aMap: AMap? = null
    private var currentType = 0
    private var mRepairOrder: RepairOrder? = null
    private var mPresenter: NavigationMapPresenter? = null

    companion object {
        const val REPAIRORDER_KEY = "repairOrder"
    }

    override fun getLayout() = R.layout.activity_navigationmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapView.onCreate(savedInstanceState)
    }

    override fun initView() {
        mPresenter = NavigationMapPresenter(this)
        aMap = mapView.map
        mRepairOrder = intent.extras?.getSerializable(REPAIRORDER_KEY) as RepairOrder
        currentType = intent.extras?.getInt("type", 0) ?: 0
        //去导航 35.133832  118.304132
        tvGoNavi.setOnClickListener {
            var gaode = Util.isAppInstalled(this, Constant.PACKAGENAME_GAODE)
            var baidu = Util.isAppInstalled(this, Constant.PACKAGENAME_BAIDU)
            var tencent = Util.isAppInstalled(this, Constant.PACKAGENAME_TENCENT)
            if (!gaode && !baidu && !tencent) {
                toast("请检查是否安装地图应用")
                return@setOnClickListener
            }
            object : CommonDialog(this, R.layout.dialog_choose_map) {
                override fun initView(root: View) {
                    window?.setWindowAnimations(R.style.BottomDialogAnim)
                    window?.setGravity(Gravity.BOTTOM)
                    var mTvGaode = root.findViewById<View>(R.id.tvGaode)
                    var mTvBaidu = root.findViewById<View>(R.id.tvBaidu)
                    var mTvTencent = root.findViewById<View>(R.id.tvTencent)
                    mTvGaode.visibility = if (gaode) View.VISIBLE else View.GONE
                    mTvBaidu.visibility = if (baidu) View.VISIBLE else View.GONE
                    mTvTencent.visibility = if (tencent) View.VISIBLE else View.GONE
                    mTvGaode.setOnClickListener {
                        dismiss()
                        naviWithMap(Constant.PACKAGENAME_GAODE)
                    }
                    mTvBaidu.setOnClickListener {
                        dismiss()
                        naviWithMap(Constant.PACKAGENAME_BAIDU)
                    }
                    mTvTencent.setOnClickListener {
                        dismiss()
                        naviWithMap(Constant.PACKAGENAME_TENCENT)
                    }
                    root.findViewById<View>(R.id.btnCancel).setOnClickListener { dismiss() }
                }
            }.show()
        }
        tvGoCall.setOnClickListener {
            Util.callPhone(this, mRepairOrder?.phone ?: "")
        }
        MapUtil.findLocation(aMap, object : MCallBack<Location> {
            override fun call(t: Location, position: Int) {
                UserManager.currentLocation = arrayOf(t.latitude, t.longitude)
            }
        })
        handleUI()
    }

    private fun naviWithMap(packageName: String) {
        Util.startThirdNavi(
            this,
            packageName,
            UserManager.currentLocation[0],
            UserManager.currentLocation[1],
            Util.stringToDouble(mRepairOrder?.lat)
            ,
            Util.stringToDouble(mRepairOrder?.lng),
            "我的位置",
            mRepairOrder?.area ?: "客户位置"
        )
    }

    private fun handleUI() {
        tvTitle.apply {
            if (currentType == 0) {
                text = "师傅抢单"
                llGoingOrderLayout.visibility = View.GONE
//                tvGoNavi.visibility = View.VISIBLE
//                tvGoCall.visibility = View.VISIBLE
//                llGoingOrderLayout.visibility = View.VISIBLE
            } else {
                tvGoNavi.visibility = View.VISIBLE
                tvGoCall.visibility = View.VISIBLE
                llGoingOrderLayout.visibility = View.VISIBLE
                text = "进行中"
            }
            setOnClickListener { finish() }
        }
        tvAddress.text = mRepairOrder?.area ?: ""
        tvDistance.text = "${mRepairOrder?.road ?: ""}"
        tvFaults.text = mRepairOrder?.faultstr ?: ""
        tvReachDestination.setOnClickListener {
            mPresenter?.reachDestination(mRepairOrder?.id ?: "")
        }
        tvCancelOrder.setOnClickListener {
            TipDialog(this).apply {
                getTitleView()?.text = "提示"
                getDescView()?.text = "是否确定取消该订单？"
                getLeftView()?.apply {
                    text = "否"
                    setOnClickListener { dismiss() }
                }
                getRightView()?.apply {
                    text = "是"
                    setOnClickListener {
                        dismiss()
                        mPresenter?.cancelRepairOrder(mRepairOrder?.id ?: "")
                    }
                }
            }.show()
        }
        //绘制路线图
        MapUtil.drawRouter(
            this,
            aMap,
            LatLng(UserManager.currentLocation[0], UserManager.currentLocation[1]),
            LatLng(
                Util.stringToDouble(mRepairOrder?.lat),
                Util.stringToDouble(mRepairOrder?.lng)
            )
        )
        MapUtil.drawCircle(
            aMap,
            LatLng(UserManager.currentLocation[0], UserManager.currentLocation[1])
        )
        MapUtil.showBluePoint(aMap)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        mPresenter?.onDestory()
        mPresenter = null
        aMap = null
    }

    override fun onReachDestinationSuccess() {
//        toast("已上传")
        Handler().postDelayed({
            setResult(BattleFragment.REQUESTCODE_ISRECEIVED)
            finish()
        }, 1500)
    }

    override fun onCancelRepairOrderSuccess() {
        toast("已取消订单")
        EventBus.getDefault().post(MessageEvent(MessageEvent.REFRESH_BATTLEFRAGMENT))
        Handler().postDelayed({
            finish()
        }, 1500)
    }

    override fun onError(err: String?) {
        toast(err ?: "")
    }
}
