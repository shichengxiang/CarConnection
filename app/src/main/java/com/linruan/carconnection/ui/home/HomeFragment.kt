package com.linruan.carconnection.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.PagerAdapter
import cn.jpush.android.api.JPushInterface
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MyLocationStyle
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.route.*
import com.blankj.utilcode.util.LogUtils
import com.bumptech.glide.Glide
import com.jaeger.library.StatusBarUtil
import com.linruan.carconnection.*
import com.linruan.carconnection.adapter.HomeMasterAdapter
import com.linruan.carconnection.adapter.OnItemClickListener
import com.linruan.carconnection.dialog.TipDialog
import com.linruan.carconnection.entities.Master
import com.linruan.carconnection.entities.MessageEvent
import com.linruan.carconnection.entities.net.GetRunningOrderResponse
import com.linruan.carconnection.map.DrivingRouteOverLay
import com.linruan.carconnection.moudle.HomePresenter
import com.linruan.carconnection.moudle.HomeView
import com.linruan.carconnection.ui.MainActivity
import com.linruan.carconnection.ui.WebViewActivity
import com.linruan.carconnection.ui.mine.RePairCarOrderActivity
import com.linruan.carconnection.ui.mine.RepairCarOrderDetailActivity
import com.linruan.carconnection.utils.MapUtil
import com.linruan.carconnection.utils.Util
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_item_master_show.*
import kotlinx.android.synthetic.main.layout_placeorder_wait.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.ref.SoftReference

class HomeFragment : BaseFragment(), HomeView {

    private var starty = 0
    private var originHeight = 0
    private var aMap: AMap? = null
    private var mMasterAdapter: HomeMasterAdapter? = null
    val ORDERSTATUS_NO = 101 //未下单
    val ORDERSTATUS_WAITRECEIVE = 102 // 等待接单中
    val ORDERSTATUS_TIMEOUT = 103 //接单超时
    val ORDERSTATUS_SUCCESS = 104 //接单成功 途中
    var waitTimer: CountDownTimer? = null
    var mSelectedMaster: Master? = null
    var myLatitude = 0.0
    var myLongitude = 0.0
    var isRoutePlanning = false
    var mPresenter: HomePresenter? = null
    var orderReceiver: OrderReceiver? = null //监听订单状态改变
    var mCurrentOrderId = ""// 进行中订单ID
    var mCurrentOrderStatus = ORDERSTATUS_NO //当前订单状态

    companion object {
        val REQUESTCODE_GOORDER = 1004
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            StatusBarUtil.setDarkMode(activity)
            findLocation()
            mPresenter?.getRunningOrder()
        }
    }

    override fun onPause() {
        super.onPause()
        slideLayout.shrinkMasterList()
        mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
        unRegisterOrderReceiver()
        waitTimer?.cancel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun getLayout() = R.layout.fragment_home
    override fun initView(view: View, savedInstanceState: Bundle?) {
        mPresenter = HomePresenter(this)
        mapView.onCreate(savedInstanceState)
        btnOrder.setOnClickListener {
            if(!UserManager.isLoginAndSkip(activity)){
                return@setOnClickListener
            }
            if (mSelectedMaster != null) {
                var bundle = Bundle().apply {
                    putSerializable("master", mSelectedMaster)
                }
                Router.openUIForResult(
                    activity,
                    ConfirmRepairOrderActivity::class.java,
                    REQUESTCODE_GOORDER,
                    bundle
                )
            } else {
                Router.openUIForResult(
                    activity,
                    ConfirmRepairOrderActivity::class.java,
                    REQUESTCODE_GOORDER
                )
            }
        }
        tvTimeOut_cancleOrder.setOnClickListener {
            activity?.let { it1 ->
                TipDialog(it1).apply {
                    setMode(2)
                    getTitleView()?.text = "温馨提示"
                    getDescView()?.text = "确定取消该订单吗？"
                    getLeftView()?.apply {
                        text = "取消"
                        setOnClickListener { dismiss() }
                    }
                    getRightView()?.apply {
                        text = "确定"
                        setOnClickListener {
                            dismiss()
                            mPresenter?.cancelRepairOrder(mCurrentOrderId)
                        }
                    }
                }
                    .show()
            }
        }
//        tvTimeOut_cancleOrder.setOnClickListener { changeUiWhenPlaceOrder(ORDERSTATUS_NO) }
        tvTimeOut_replaceOrder.setOnClickListener {
            mPresenter?.goonWait(mCurrentOrderId)
//            changeUiWhenPlaceOrder(ORDERSTATUS_WAITRECEIVE)
        }
        tvSuccess_callService.setOnClickListener {
            Util.callPhone(
                activity,
                Constant.CUSTOMERSERVICE_MOBILE
            )
        }
        //        //广告位
        //        vpHome.adapter = activity?.let { HomePageAdapter(it) }
        //        circleIndicator.setViewPager(vpHome)
        //拖动展开
        originHeight = llDragView.height
        slideLayout.setOnChangeListener {
            if (it) {
                rvHomeMasterList.visibility = View.VISIBLE
                flContainer_master.visibility = View.GONE
                btnOrder.visibility = View.GONE
                tvMore.visibility = View.GONE
            } else {
                rvHomeMasterList.visibility = View.GONE
                flContainer_master.visibility = View.VISIBLE
                btnOrder.visibility = View.VISIBLE
                tvMore.visibility = View.VISIBLE
            }
        }
        // 师傅列表
        rvHomeMasterList.layoutManager = LinearLayoutManager(activity)
        mMasterAdapter = context?.let { HomeMasterAdapter(it) }
        rvHomeMasterList.adapter = mMasterAdapter
        mMasterAdapter?.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClickListener(position: Int) {
                slideLayout.shrinkMasterList()
                mSelectedMaster = mMasterAdapter!!.getData()[position]
                handleMasterView(mSelectedMaster!!)
            }
        })
        registerOrderReceiver()


        // 高德地图
        if (aMap == null)
            aMap = mapView.map
        ivLocation.setOnClickListener { findLocation() }
        ivCallService.setOnClickListener {
            Util.callPhone(
                activity,
                Constant.CUSTOMERSERVICE_MOBILE
            )
        }
        tvGoRepairOrderList.setOnClickListener {
            Router.openUI(
                activity,
                RepairCarOrderDetailActivity::class.java,
                HashMap<String, String>().apply { put(RepairCarOrderDetailActivity.REPAIRID_KEY, mCurrentOrderId) })
        }
        //修车师傅列表 设置空状态
        var emptyView = View.inflate(activity, R.layout.layout_emptyview_master, null)
        rvHomeMasterList.setEmptyView(emptyView)

//        JPushInterface.setTags(activity, setOf("yonghu")) { code, p1, p2 ->
//            if (code != 0) logE("用户推送tag设置失败$code")
//        }
//        JPushInterface.setTags(activity, 1, setOf("yonghu"))
        //监听订单状态变化
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun getData() {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOrderStatusChange(event: MessageEvent) {
        if(event.code==MessageEvent.REFRESH_HOMEFRAGMENTRUNNINGORDER){
            mPresenter?.getRunningOrder()
        }
//        if (event.code == MessageEvent.REFRESH_WHENMASTEISRECEIVED) {
//            changeUiWhenPlaceOrder(ORDERSTATUS_NO)
//        } else if (event.code == MessageEvent.REFRESH_WHENMASTERBATTLE) {
//            changeUiWhenPlaceOrder(ORDERSTATUS_SUCCESS)
//        }
    }

    class HomePageAdapter : PagerAdapter {
        private var mContext: SoftReference<Context?>? = null
        private var mData = arrayListOf<GetRunningOrderResponse.SlideBean>()

        constructor(
            context: Context,
            list: ArrayList<GetRunningOrderResponse.SlideBean>?
        ) : super() {
            mContext = SoftReference(context)
            if (list.isNullOrEmpty()) {
                mData.clear()
            } else {
                mData = list
            }
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            var bean = mData[position]
            var view =
                LayoutInflater.from(mContext?.get())
                    .inflate(R.layout.item_vp_homeadvert, null)
            var iv = view.findViewById<ImageView>(R.id.iv_advert)
            //有风险 先不管
            mContext?.get()?.let {
                Glide.with(it)
                    .load(bean.cover)
                    .placeholder(R.mipmap.img_home_banner_default)
                    .error(R.mipmap.img_home_banner_default)
                    .dontAnimate()
                    .into(iv)
            }
            iv.setOnClickListener {
                WebViewActivity.openWebView(mContext?.get(), bean.url ?: "", bean.title)
            }
            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View?)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun getCount() = mData.size

    }

    private fun findLocation() {
        //具体信地址信息
        //蓝点
        val myLocationStyle = MyLocationStyle().apply {
            //初始化定位蓝点样式类
            myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW) //只一次定位
            showMyLocation(true)
            //            interval(2000) //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        }
        aMap!!.myLocationStyle = myLocationStyle //设置定位蓝点的Style

        aMap?.uiSettings?.apply {
            isMyLocationButtonEnabled = false //设置默认定位按钮是否显示，非必需设置。\
            //            logoPosition=AMapOptions.LOGO_POSITION_BOTTOM_CENTER
            //            zoomPosition=AMapOptions.ZOOM_POSITION_RIGHT_CENTER
            isScaleControlsEnabled = false
            isZoomControlsEnabled = false
        }
        aMap?.isMyLocationEnabled = true // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap?.setOnMyLocationChangeListener { location ->
            if (location != null) {
                myLatitude = location.latitude //维度
                myLongitude = location.longitude //经度
                UserManager.currentLocation = arrayOf(myLatitude, myLongitude)
                aMap?.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(myLatitude, myLongitude),
                        16f
                    )
                ) //设置比例尺
//                MapUtil.drawCircle(aMap, LatLng(location.latitude, location.longitude))
                mPresenter?.getNearbyMasters(myLongitude.toString(), myLatitude.toString())
            }
        }
        //mark点击事件
        aMap?.setOnMarkerClickListener { mark ->
            val data = mMasterAdapter?.getData()
            data?.forEach {
                if (it.id == mark.title) {
                    mSelectedMaster = it
                    handleMasterView(it)
                }
            }
            return@setOnMarkerClickListener true
        }
        //        MapUtil.findLocation(aMap,object :MCallBack<Location>{
        //            override fun call(t: Location, position: Int) {
        //                myLatitude = t.latitude //维度
        //                myLongitude = t.longitude //经度
        //                UserManager.currentLocation = arrayOf(myLatitude, myLongitude)
        //                mPresenter?.getNearbyMasters(myLongitude.toString(), myLatitude.toString())
        //            }
        //        })
        //        //声明mlocationClient对象
        //        var mlocationClient = AMapLocationClient(activity)
        //        //声明mLocationOption对象
        //        var mLocationOption = AMapLocationClientOption()
        //        //初始化定位参数
        //        //设置定位监听
        //        mlocationClient.setLocationListener(this);
        //        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        //        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //        //设置定位间隔,单位毫秒,默认为2000ms
        //        mLocationOption.setInterval(2000);
        //        //设置定位参数
        //        mlocationClient.setLocationOption(mLocationOption)
        //        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        //        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        //        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        //        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //        //启动定位
        //        mlocationClient.startLocation()
        //拉取当前是否有可进行订单
    }

    private fun addMastersMark() {
        if (isRoutePlanning)
            return
        //        aMap?.mapScreenMarkers?.forEach {
        //            if(!it.isRemoved)
        //                it.remove()
        //        }
        //添加mark
        val data = mMasterAdapter?.getData()
        if (!data.isNullOrEmpty() && activity != null) {
            data.forEach {
                MapUtil.addMark(
                    activity!!,
                    aMap,
                    LatLng(it.lat, it.lng),
                    it.name,
                    it.leixingToString(),
                    it.id
                )
            }
        }
    }

    public fun changeUiWhenPlaceOrder(tag: Int, limitTime: Long = UserManager.limitTime) {
        waitTimer?.cancel()
        mCurrentOrderStatus = tag
        when (tag) {
            ORDERSTATUS_NO -> {
                llDragView.visibility = View.VISIBLE
                layoutPop.visibility = View.GONE
                isRoutePlanning = false
                addMastersMark()
            }
            ORDERSTATUS_WAITRECEIVE -> {
                llDragView.visibility = View.GONE
                layoutPop.visibility = View.VISIBLE
                llWaitLayout.visibility = View.GONE
                tvWaitTip.visibility = View.VISIBLE
                llTimeOutLayout.visibility = View.GONE
                tvTimeOutTip.visibility = View.GONE
                llSuccessLayout.visibility = View.GONE
                llSuccessBottomLayout.visibility = View.GONE
                llTimeLayout.visibility = View.VISIBLE
                llBefore_topTip.visibility = View.VISIBLE
                tvSuccess_topTip.visibility = View.GONE
                waitTimer = object : CountDownTimer(limitTime * 1000, 1000) {
                    override fun onFinish() {
                        waitTimer?.cancel()
                        changeUiWhenPlaceOrder(ORDERSTATUS_TIMEOUT)
                        //TODO 默认开启导航
                        //                        caculateDistance()
                    }

                    override fun onTick(millisUntilFinished: Long) {
                        tvCountDown?.text =
                            Util.exchangeSecondToTimeFormat((millisUntilFinished / 1000).toInt())
                    }
                }
                waitTimer?.start()
            }
            ORDERSTATUS_TIMEOUT -> {
                llDragView?.visibility = View.GONE
                layoutPop?.visibility = View.VISIBLE
                llWaitLayout?.visibility = View.GONE
                tvWaitTip?.visibility = View.GONE
                llSuccessLayout?.visibility = View.GONE
                llSuccessBottomLayout?.visibility = View.GONE
                llTimeOutLayout?.visibility = View.VISIBLE
                tvTimeOutTip?.visibility = View.VISIBLE
                llTimeLayout?.visibility = View.VISIBLE
                llBefore_topTip?.visibility = View.VISIBLE
                tvSuccess_topTip?.visibility = View.GONE
            }
            ORDERSTATUS_SUCCESS -> {
                llDragView.visibility = View.GONE
                layoutPop.visibility = View.VISIBLE
                llWaitLayout.visibility = View.GONE
                tvWaitTip.visibility = View.VISIBLE
                llTimeOutLayout.visibility = View.GONE
                tvTimeOutTip.visibility = View.GONE
                llSuccessLayout.visibility = View.VISIBLE
                llSuccessBottomLayout.visibility = View.VISIBLE
                llBefore_topTip.visibility = View.GONE
                tvSuccess_topTip.visibility = View.VISIBLE
                llTimeLayout.visibility = View.GONE
                caculateDistance()
            }
        }

    }

    /**
     * 显示路线规划图
     */
    private fun showRouteSearchResult(
        context: Context?,
        from: LatLonPoint,
        to: LatLonPoint,
        distance: String
    ) {
        if (context == null)
            return
        isRoutePlanning = true
        var routeSearch = RouteSearch(context)
        routeSearch.setRouteSearchListener(object : RouteSearch.OnRouteSearchListener {
            override fun onDriveRouteSearched(result: DriveRouteResult?, errorCode: Int) {
                if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
                    if (result?.paths != null) {
                        if (result.paths.size > 0) {
                            aMap?.clear(true)
                            val drivePath: DrivePath = result.getPaths()
                                .get(0)
                            val drivingRouteOverlay =
                                DrivingRouteOverLay(
                                    context, aMap, drivePath,
                                    result.startPos,
                                    result.targetPos, null
                                )
                            drivingRouteOverlay.setNodeIconVisibility(true) //设置节点marker是否显示
                            drivingRouteOverlay.setIsColorfulline(true) //是否用颜色展示交通拥堵情况，默认true
                            drivingRouteOverlay.removeFromMap()
                            drivingRouteOverlay.addToMap()
                            drivingRouteOverlay.zoomToSpan()
                            MapUtil.addRouteEndMark(
                                activity?.applicationContext,
                                aMap,
                                LatLng(
                                    mSelectedMaster?.lat ?: 0.0,
                                    mSelectedMaster?.lng ?: 0.0
                                ),
                                mSelectedMaster?.name ?: "",
                                mSelectedMaster?.leixingToString() ?: "",
                                mSelectedMaster?.name ?: "",
                                distance
                            )
                        } else if (result != null && result.getPaths() == null) {
                            logE("没有搜索到相关数据")
                        }
                    } else {
                        logE("没有搜索到相关数据")
                    }
                } else {
                    logE("没有搜索到相关数据 $errorCode")
                }
            }

            override fun onBusRouteSearched(p0: BusRouteResult?, p1: Int) {
            }

            override fun onRideRouteSearched(p0: RideRouteResult?, p1: Int) {
            }

            override fun onWalkRouteSearched(p0: WalkRouteResult?, p1: Int) {
            }
        })
        val fromAndTo = RouteSearch.FromAndTo(
            from, to
        )
        var query = RouteSearch.DriveRouteQuery(
            fromAndTo,
            RouteSearch.DRIVEING_PLAN_NO_HIGHWAY,
            null,
            null,
            null
        )
        routeSearch.calculateDriveRouteAsyn(query)
    }

    private fun caculateDistance() {
        if (mSelectedMaster == null) {
            activity?.toast("未指派师傅")
            return
        }
        var distanceSearch = DistanceSearch(activity)
        var distanceQuery = DistanceSearch.DistanceQuery()
        distanceSearch.setDistanceSearchListener { distanceResult, i ->
            if (i === 1000) {
                val time_string: String
                //距离米
                val distance: String = "${distanceResult.distanceResults[0].distance / 1000}"
                //时间秒 转分钟
                /*int time = (int) distanceResult.getDistanceResults().get(0).getDuration() / 60;
            Log.d("距离", "onDistanceSearched: " + distance + "   " + time);
            int hours = (int) Math.floor(time / 60);
            int minute = time % 60;
            if (hours > 0) {
                time_string = time + "小时" + minute + "分钟";
            } else {
                time_string = minute + "分钟";
            }*/
                var second =
                    distanceResult.distanceResults[0].duration.toLong()
                val days = second / 86400 //转换天数
                second = second % 86400 //剩余秒数
                val hours = second / 3600 //转换小时
                second = second % 3600 //剩余秒数
                val minutes = second / 60 //转换分钟
                second = second % 60
                showRouteSearchResult(
                    activity,
                    LatLonPoint(myLatitude, myLongitude),
                    LatLonPoint(
                        mSelectedMaster?.lat ?: 0.0,
                        mSelectedMaster?.lng ?: 0.0
                    ),
                    "$distance 公里  $minutes 分钟"
                )
            }
        }
        distanceQuery.destination =
            LatLonPoint(mSelectedMaster?.lat ?: 0.0, mSelectedMaster?.lng ?: 0.0)
        distanceQuery.origins = arrayListOf(LatLonPoint(myLatitude, myLongitude))
        distanceQuery.type = DistanceSearch.TYPE_DRIVING_DISTANCE
        distanceSearch.calculateRouteDistanceAsyn(distanceQuery)
    }

    private fun handleMasterView(master: Master) {
        if (flContainer_master.childCount == 0) {
            var vMaster =
                layoutInflater.inflate(R.layout.layout_item_master_show, flContainer_master)
        }
        flContainer_master.visibility = View.VISIBLE
        tvMasterName.text = master.name
        var leixing = StringBuilder()
        master.leixing?.forEach {
            leixing.append(it)
                .append("/")
        }
        tvCommentNum.text = "评价（${master.comments ?: ""}）"
        tvOrderNum.text = "已接：${master.ordernum ?: 0}单"
        tvWorkTime.text = master.worktime ?: "9:00-18:00"
        tvMonopoly.text = if (leixing.length > 0) leixing.substring(0, leixing.length - 1) else ""
        Glide.with(this)
            .load(master.avatar ?: "")
            .placeholder(R.mipmap.img_default_head)
            .error(R.mipmap.img_default_head)
            .dontAnimate()
            .into(iv_head)
        btnOrder.text = resources.getString(R.string.placeorder_now)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUESTCODE_GOORDER) {
            mPresenter?.getRunningOrder()
//            var b=data?.getStringExtra("isSuccess")
//            var success = data?.extras?.getBoolean("isSuccess")
//            if (success == true) {
//                changeUiWhenPlaceOrder(ORDERSTATUS_WAITRECEIVE)
//            }
        }else if(requestCode==MainActivity.REQUESTCODE_PERMISSION){
            findLocation()
            mPresenter?.getRunningOrder()
        }
    }

    override fun onGetMasterList(masters: ArrayList<Master>?) {
        if (masters.isNullOrEmpty()) {
            activity?.toast("附近未有修车师傅营业")
            return
        }
        mMasterAdapter?.putMasters(masters)
        addMastersMark()
    }

    override fun onPlaceOrderSuccess() {
        //等待接单
        changeUiWhenPlaceOrder(ORDERSTATUS_WAITRECEIVE)
    }

    override fun onError(err: String?) {
        activity?.toast(err ?: "")
    }

    override fun onGetRunningOrder(
        slides: ArrayList<GetRunningOrderResponse.SlideBean>?,
        order: GetRunningOrderResponse.RunningOrder?,
        worker: GetRunningOrderResponse.WorkerBean?
    ) {
        //广告位  进行中订单
        //广告位
//        var vpImgs = arrayListOf<String>()
//        slides?.forEach {
//            vpImgs.add(it.cover)
//        }
        vpHome.adapter = activity?.let { HomePageAdapter(it, slides) }
        circleIndicator.setViewPager(vpHome)
        if (slides.isNullOrEmpty()) {
            rlHomeAderts.visibility = View.GONE
        }
        if (order != null && !order.id.isNullOrBlank()) {
            mCurrentOrderId = order.id ?: ""
//            1等待师傅接单 2师傅已接单 3师傅已到达 4维修完成待确认 5用户已确认  6订单失败
            //step  1:等待抢单 2 已抢单赶来的路上  3：已到达
            when (order.step) {
                1 -> {
                    if (order.remaining > 0) {
                        changeUiWhenPlaceOrder(ORDERSTATUS_WAITRECEIVE, order.remaining.toLong())
                    } else {
                        changeUiWhenPlaceOrder(ORDERSTATUS_TIMEOUT)
                    }
                    tvGoRepairOrderList.visibility = View.GONE
                }
                2 -> {
                    changeUiWhenPlaceOrder(ORDERSTATUS_SUCCESS)
                    //师傅头像
                    activity?.let {
                        if (ivSuccessHead != null) Glide.with(it).load(
                            worker?.avatar ?: ""
                        ).placeholder(R.mipmap.img_default_head).error(R.mipmap.img_default_head).dontAnimate().into(
                            ivSuccessHead
                        )
                    }
                    tvSuccess_masterName?.let { it.text = worker?.name ?: "" }
                    tvSuccess_OrderNum?.let { it.text = "已接： ${worker?.repair_count ?: ""}单" }
                    tvSuccess_masterBusiness?.let {
                        it.text = "本店专营：${worker?.leixing?.replace(",", "/")}"
                    }
                    tvCommentNums?.let { it.text = "评论 (${worker?.comments_count})" }
                    tvGoRepairOrderList.visibility = View.GONE
                }
                else -> {
                    changeUiWhenPlaceOrder(ORDERSTATUS_NO)
                    tvGoRepairOrderList.visibility = View.VISIBLE
                }
            }
        } else {
            changeUiWhenPlaceOrder(ORDERSTATUS_NO)
            tvGoRepairOrderList.visibility = View.GONE
        }
    }

    override fun onCancelOrderSuccess() {
        //取消订单
        activity?.toast("已取消")
        changeUiWhenPlaceOrder(ORDERSTATUS_NO)
    }

    override fun onGoonWaitSuccess() {
        //继续等待
        changeUiWhenPlaceOrder(ORDERSTATUS_WAITRECEIVE)
    }

    /**
     * 监听推送消息
     */
    private fun registerOrderReceiver() {
        orderReceiver = OrderReceiver()
        var filter = IntentFilter().apply {
            addAction(Constant.BROADCAST_ACTION_PLACEORDER_RECEIVED)
        }
        orderReceiver?.setCallback(object : MCallBack<Master> {
            override fun call(t: Master, position: Int) {
                changeUiWhenPlaceOrder(ORDERSTATUS_SUCCESS)
            }
        })
        activity?.registerReceiver(orderReceiver, filter)
    }

    private fun unRegisterOrderReceiver() {
        activity?.unregisterReceiver(orderReceiver)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    class OrderReceiver : BroadcastReceiver() {
        companion object {
            val DATA_KEY = "placeOrder"
        }

        private var mCallBack: MCallBack<Master>? = null
        override fun onReceive(context: Context?, intent: Intent?) {
            var master = intent?.extras?.getSerializable(DATA_KEY) as Master
            mCallBack?.call(master, 0)
        }

        fun setCallback(callback: MCallBack<Master>) {
            this.mCallBack = callback
        }

    }
}
