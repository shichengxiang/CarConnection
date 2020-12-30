package com.linruan.carconnection.utils

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.maps.AMap
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.amap.api.navi.AMapNavi
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.route.*
import com.google.gson.Gson
import com.linruan.carconnection.*
import com.linruan.carconnection.entities.LocationInfo
import com.linruan.carconnection.map.DrivingRouteOverLay
import kotlinx.android.synthetic.main.layout_mark_master.view.*
import java.io.IOException
import java.lang.Exception
import java.util.*


object MapUtil {
    private var mNavi: AMapNavi? = null

    /**
     * 显示蓝点样式
     */
    fun showBluePoint(aMap: AMap?) {
        val myLocationStyle = MyLocationStyle().apply {
            //初始化定位蓝点样式类
            myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE) //定位模式
            showMyLocation(true)
            anchor(0.5f, 0.5f)
            myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bluepoint))
            strokeWidth(0f)
            strokeColor(Color.TRANSPARENT)
            radiusFillColor(Color.TRANSPARENT)
            //            interval(2000) //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        }
        aMap!!.myLocationStyle = myLocationStyle //设置定位蓝点的Style
    }

    /**
     * 添加圆形面范围标识
     */
    fun drawCircle(aMap: AMap?, location: LatLng) {
        var circleOptions = CircleOptions()
        circleOptions.center(location)
        circleOptions.strokeWidth(0f)
        circleOptions.fillColor(Color.parseColor("#AADAEAF7"))
        circleOptions.radius(200.0)
        aMap?.addCircle(circleOptions)
    }

    /**
     * 添加 mark
     */
    fun addMark(
        activity: Activity,
        aMap: AMap?,
        latLng: LatLng,
        name: String,
        leixing: String,
        title: String
    ) {
        var markOption =
            MarkerOptions().icon(
                BitmapDescriptorFactory.fromView(
                    getMarkView(
                        activity,
                        name,
                        leixing,
                        false
                    )
                )
            )
                .anchor(0.5f, 0.5f)
                .position(latLng)
                .draggable(false)
                .title(title)
        aMap?.addMarker(markOption)
    }

    fun addRouteEndMark(
        activity: Context?,
        aMap: AMap?,
        latLng: LatLng,
        name: String,
        leixing: String,
        title: String,
        distance: String
    ) {
        var view = getMarkView(activity, name, leixing, false)
        var span = SpannableString("距您  $distance")
        span.setSpan(
            ForegroundColorSpan(Color.parseColor("#FF00D2D2")),
            4,
            4 + distance.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        view.findViewById<TextView>(R.id.tvDistance)
            .apply {
                text = span
                visibility = View.VISIBLE
            }
        var markOption =
            MarkerOptions().icon(
                BitmapDescriptorFactory.fromView(view)
            )
                .anchor(0.5f, 1f)
                .position(latLng)
                .draggable(false)
                .title(title)
        aMap?.addMarker(markOption)
    }

    fun addEndMark(
        activity: Context?,
        aMap: AMap?,
        latLng: LatLng,
        title: String,
        distance: String
    ) {
        var view = getMarkView(activity, "", "", false)
        var span = SpannableString("距您  $distance")
        span.setSpan(
            ForegroundColorSpan(Color.parseColor("#FF00D2D2")),
            4,
            4 + distance.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        view.findViewById<TextView>(R.id.tvDistance)
            .apply {
                text = span
                visibility = View.VISIBLE
            }
        var markOption =
            MarkerOptions().icon(
                BitmapDescriptorFactory.fromView(view)
            )
                .anchor(0.5f, 1f)
                .position(latLng)
                .draggable(false)
                .title(title)
        aMap?.addMarker(markOption)
    }

    fun addRouteStartMark(activity: Context?, aMap: AMap?, latLng: LatLng) {
        var markOption =
            MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bluepoint))
                .anchor(0.5f, 1f)
                .position(latLng)
                .draggable(false)
                .title("start")
        aMap?.addMarker(markOption)
    }

    fun getMarkView(
        activity: Context?,
        name: String,
        business: String,
        showDistance: Boolean
    ): View {
        var view = LayoutInflater.from(activity)
            .inflate(R.layout.layout_mark_master, null)
        view.tvMarkName.text = name
        view.tvMarkBusiness.text = business
        if (showDistance) {
            view.tvDistance.visibility = View.VISIBLE
        }
        if (name.isNullOrBlank()) {
            view.tvMarkName.visibility = View.GONE
        }
        if (business.isNullOrBlank()) {
            view.tvMarkBusiness.visibility = View.GONE
        }
        return view
    }

    /**
     * 坐标计算距离
     */
    fun calculateDistance(
        resLat: Double?,
        resLng: Double?,
        desLat: Double?,
        desLng: Double?
    ): Float {
        logI("坐标计算 $resLat $resLng   $desLat  $desLng")
        return if (resLat == null || resLng == null || desLat == null || desLng == null) 0f else AMapUtils.calculateLineDistance(
            LatLng(resLat, resLng),
            LatLng(desLat, desLng)
        )
    }

    /**
     * 获取所处位置信息
     */
    fun getInfoLocation(context: Context?, callback: MCallBack<AMapLocation>) {
        val aMapLocationClientOption = AMapLocationClientOption().apply {
            setOnceLocation(true)
        }
        aMapLocationClientOption.setNeedAddress(true)
        var client = AMapLocationClient(context)
        client.setLocationOption(aMapLocationClientOption)
        client.setLocationListener { location ->
            callback.call(location, 0)
            client.stopLocation()
            logE(Gson().toJson(location))
            logE("${location.address}  ${location.district} ${location.city} ${location.aoiName} ${location.poiName}")
            logE("${location.coordType}  ${location.latitude}  ${location.longitude}")
        }
        client.stopLocation()
        client.startLocation()
    }

    /**
     * 获取位置信息
     */
    fun findLocation(aMap: AMap?, callback: MCallBack<Location>?) {
        //具体信地址信息
        //蓝点
        val myLocationStyle = MyLocationStyle().apply {
            //初始化定位蓝点样式类
            myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW) //只定位一次
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
                callback?.call(location, 0)
                UserManager.currentLocation = arrayOf(location.latitude, location.longitude)
                val scalePerPixel = aMap?.scalePerPixel
                aMap?.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(location.latitude, location.longitude),
                        16f
                    )
                ) //设置比例尺
            }
        }
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
    }

    /**
     * 根据经纬度获取位置信息
     */
    fun getAddress(context: Context?, latitude: Double, longitude: Double): LocationInfo {
        val geocoder = Geocoder(context, Locale.CHINA)
        try {
            if (latitude > 90) {
                return LocationInfo().apply {
                    area = arrayListOf("未知位置")
                }
            }
            val addresses: List<Address> = geocoder.getFromLocation(
                latitude,
                longitude, 100
            )
            if (addresses.size > 0) {
                val address: Address? = addresses[0]
                var max = address?.maxAddressLineIndex ?: 0
                var locs = arrayListOf<String>()
                if (max > 0) {
                    for (index in 1..max) {
                        var loc = address?.getAddressLine(index) ?: ""
                        locs.add(loc)
                    }
                }
                logE("位置列表  ${Gson().toJson(locs)}")
//                val data: String = address.toString()
                //                logE(data)
//                val startCity = data.indexOf("1:\"") + "1:\"".length
//                val endCity = data.indexOf("\"", startCity)
//                val city = data.substring(startCity, endCity)
                //                val startPlace = data.indexOf("feature=") + "feature=".length
                //                val endplace = data.indexOf(",", startPlace)
                //                val place = data.substring(startPlace, endplace)
                return LocationInfo().apply {
                    area = locs
                    road = address?.getAddressLine(0) ?: ""
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return return LocationInfo().apply {
            area = arrayListOf("未知位置")
        }
    }

    /**
     * 计算距离 并绘制路线图
     */
    fun drawRouter(context: Context?, aMap: AMap?, from: LatLng, to: LatLng) {
        var distanceSearch = DistanceSearch(context)
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
                    context,
                    aMap,
                    from,
                    to,
                    "$distance 公里  $minutes 分钟"
                )
            }
        }
        distanceQuery.destination = LatLonPoint(to.latitude, to.longitude)
        distanceQuery.origins = arrayListOf(LatLonPoint(from.latitude, from.longitude))
        distanceQuery.type = DistanceSearch.TYPE_DRIVING_DISTANCE
        distanceSearch.calculateRouteDistanceAsyn(distanceQuery)
    }

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     * @param context
     * @return true 表示开启
     */
    public fun isGPSEnable(context: Context): Boolean {
        var locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        var gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        var network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (gps || network) {
            return true
        }
        return false
    }

    /**
     * 强制帮用户打开GPS
     * @param context
     */
    public fun openGPS(context: Context):Boolean {
        var isOpened=false
        var GPSIntent = Intent()
        GPSIntent.setClassName(
            "com.android.settings",
            "com.android.settings.widget.SettingsAppWidgetProvider"
        );
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send()
            isOpened=true
        } catch (e: Exception) {
            isOpened=false
            e.printStackTrace()
        }
        return isOpened
    }
    /**
     *
     */

    /**
     * 显示路线规划图
     */
    private fun showRouteSearchResult(
        context: Context?,
        aMap: AMap?,
        from: LatLng,
        to: LatLng,
        distance: String
    ) {
        if (context == null)
            return
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
                            //                            addRouteStartMark(context, aMap, from) //开始
                            addRouteEndMark(context, aMap, to, "", "", "", distance) //结束

                            //缩放
                            aMap?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    from,
                                    14f
                                )
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
            LatLonPoint(from.latitude, from.longitude),
            LatLonPoint(to.latitude, to.longitude)
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
}
