package com.linruan.carconnection.ui

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Process
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.linruan.carconnection.*
import com.linruan.carconnection.dialog.TipDialog
import com.linruan.carconnection.entities.MessageEvent
import com.linruan.carconnection.entities.net.GetAppVersion
import com.linruan.carconnection.moudle.MainPresenter
import com.linruan.carconnection.moudle.MainView
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.linruan.carconnection.ui.home.HomeFragment
import com.linruan.carconnection.ui.master.BattleFragment
import com.linruan.carconnection.ui.mine.MineFragment
import com.linruan.carconnection.ui.store.StoreFragment
import com.linruan.carconnection.ui.talk.TalkFragment
import com.linruan.carconnection.utils.MapUtil
import com.linruan.carconnection.utils.SharePreferenceUtil
import com.linruan.carconnection.utils.update.UpdateManager
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import pub.devrel.easypermissions.EasyPermissions


class MainActivity : BaseActivity(), EasyPermissions.PermissionCallbacks, MainView {
    lateinit var mRoot: View
    var fragmentManager: FragmentManager? = null
    var MainFragment: BaseFragment? = null
    var storeFragment: StoreFragment? = null
    var talkFragment: TalkFragment? = null
    var mineFragment: MineFragment? = null
    var currentFragment: Fragment? = null
    var mPresenter: MainPresenter? = null

    companion object {
        const val MAIN_TAB_HOME = 0
        const val MAIN_TAB_STORE = 1
        const val MAIN_TAB_TALK = 2
        const val MAIN_TAB_MINE = 3
        val REQUESTCODE_PERMISSION = 1001
    }

    override fun getLayout(): Int {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        mRoot = layoutInflater.inflate(R.layout.activity_main, null)
        return R.layout.activity_main
    }

    private var permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private var REQUESTCODE_SETTING = 1002
    override fun initView() {
        mPresenter = MainPresenter(this)
        EventBus.getDefault()
            .register(this)
        requestPermission()
        fragmentManager = supportFragmentManager
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        //        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        //        val appBarConfiguration = AppBarConfiguration(
        //            setOf(
        //                R.id.navigation_home,
        //                R.id.navigation_dashboard,
        //                R.id.navigation_notifications,
        //                R.id.navigation_mine
        //            )
        //        )
        //        navController.saveState()
        //        setupActionBarWithNavController(navController, appBarConfiguration)
        //        navView.setupWithNavController(navController)
        //        vpMain.adapter = UIAdapter(supportFragmentManager)
        storeFragment = StoreFragment()
        talkFragment = TalkFragment()
        mineFragment = MineFragment()
        MainFragment = if (UserManager.getUser()?.isMaster == true) {
            navView.menu[0].setTitle("抢单")
            navView.menu[0].setIcon(R.drawable.ic_main_order_checked)
            BattleFragment()
        } else {
            navView.menu[0].setIcon(R.drawable.ic_main_home_unchecked)
            navView.menu[0].setTitle("首页")
            HomeFragment()
        }
        fragmentManager?.beginTransaction()
            ?.apply {
                add(R.id.fragment_main, MainFragment!!, "home")
                add(R.id.fragment_main, storeFragment!!, "store")
                add(R.id.fragment_main, talkFragment!!, "talk")
                add(R.id.fragment_main, mineFragment!!, "mine")
                hide(MainFragment!!)
                hide(storeFragment!!)
                hide(talkFragment!!)
                hide(mineFragment!!)
                commit()
                currentFragment = MainFragment
            }
        navView.setOnNavigationItemSelectedListener { item ->
            val transaction = fragmentManager?.beginTransaction()
            when (item.itemId) {
                R.id.navigation_home -> {
                    //                    vpMain.currentItem = 0
                    transaction?.show(MainFragment!!)
                    if (currentFragment != null && !(currentFragment is HomeFragment || currentFragment is BattleFragment)) {
                        transaction?.hide(currentFragment!!)
                    }
                    currentFragment = MainFragment

                }
                R.id.navigation_dashboard -> {
                    //                    vpMain.currentItem = 1
                    transaction?.show(storeFragment!!)
                    if (currentFragment != null && currentFragment !is StoreFragment) {
                        transaction?.hide(currentFragment!!)
                    }
                    currentFragment = storeFragment
                }
                R.id.navigation_notifications -> {
                    //                    vpMain.currentItem = 2
                    transaction?.show(talkFragment!!)
                    if (currentFragment != null && currentFragment !is TalkFragment) {
                        transaction?.hide(currentFragment!!)
                    }
                    currentFragment = talkFragment
                }
                R.id.navigation_mine -> {
                    //                    vpMain.currentItem = 3
                    transaction?.show(mineFragment!!)
                    if (currentFragment != null && currentFragment !is MineFragment) {
                        transaction?.hide(currentFragment!!)
                    }
                    currentFragment = mineFragment
                }
            }
            transaction?.commit()
            return@setOnNavigationItemSelectedListener true
        }
        navView.selectedItemId = R.id.navigation_home
        mPresenter?.initData()
        checkVersion()
        //判断开启GPS
        if (!MapUtil.isGPSEnable(this)) {
            TipDialog(this).apply {
                setMode(1)
                getTitleView()?.setText("请开启位置定位权限")
                mSingleBtn?.apply {
                    text = "确认"
                    setOnClickListener {
                        dismiss()
                        MapUtil.openGPS(this@MainActivity)
                    }
                }
            }.show()
        }
        // 检查是否已经同意协议
        checkIsAgreeProtocol()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }

    /**
     * 检测版本
     */
    fun checkVersion() {
        Client.getInstance().checkAppVersion(object : JsonCallback<GetAppVersion>() {
            override fun onSuccess(response: Response<GetAppVersion?>?) {
                if (response?.body().isSuccess()) {
                    var res = response?.body()?.data
                    Constant.PLATFORM_USER_PRICE = res?.user_price ?: 0.00
                    Constant.PLATFROM_WORKER_PRICE = res?.woker_price ?: 0.00
                    UpdateManager.build(this@MainActivity).checkVersion(
                        res?.version_title ?: "",
                        res?.version ?: 0,
                        res?.down_url2 ?: "",
                        res?.app_intro ?: "",
                        "${res?.down_filesize ?: 1}M",
                        false, object : UpdateManager.OnUpdateListener {
                            override fun noNewVersion() {
                            }
                        }
                    )
                }
            }

            override fun onError(response: Response<GetAppVersion?>?) {
            }
        })
    }

    private fun checkIsAgreeProtocol() {
        val isAgree = SharePreferenceUtil.getBoolean("agreeProtocol", false)
        if (!isAgree) {
            showPriPrivacytocol()
        }
    }

    class UIAdapter : FragmentPagerAdapter {
        var mFragments: ArrayList<Fragment>? = null

        constructor(manager: FragmentManager) : super(
            manager,
            FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        ) {
            mFragments = arrayListOf()
            mFragments?.add(HomeFragment())
            mFragments?.add(StoreFragment())
            mFragments?.add(TalkFragment())
            mFragments?.add(MineFragment())
        }

        override fun getItem(position: Int): Fragment {
            return mFragments!![position]
        }

        override fun getCount(): Int {
            return mFragments!!.size
        }
    }

    private fun requestPermission() {
        if (!EasyPermissions.hasPermissions(this, *permissions)) {
            EasyPermissions.requestPermissions(this, "权限申请", REQUESTCODE_PERMISSION, *permissions)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        //同意了权限
        MainFragment?.onActivityResult(REQUESTCODE_PERMISSION, 100, null)
    }

    /**
     * 隐私协议弹窗
     */
    private fun showPriPrivacytocol() {
        TipDialog(this).apply {
            getTitleView()?.text = "服务协议和隐私政策"
            var vs = "请你务必审慎阅读、充分理解《隐私协议》和《服务协议》等各条款，   您可阅读隐私协议和服务协议详细信息，请点击同意开始接受我们的服务"
            var span = SpannableString(vs)
            val start1 = vs.indexOf("《")
            val end1 = vs.indexOf("》") + 1
            val start2 = vs.indexOf("《", end1)
            val end2 = vs.indexOf("》", end1) + 1
            span.setSpan(
                ForegroundColorSpan(Color.parseColor("#FF0ED4D4")),
                start1,
                end1,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            span.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
//                showProtocolDialog()
                    WebViewActivity.openWebView(
                        this@MainActivity,
                        Constant.URL_PRIVACY,
                        "隐私政策"
                    )
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.bgColor = Color.TRANSPARENT
                    ds.isUnderlineText = false
                }
            }, start1, end1, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
            span.setSpan(
                ForegroundColorSpan(Color.parseColor("#FF0ED4D4")),
                start2,
                end2,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            span.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
//                showProtocolDialog()
                    WebViewActivity.openWebView(
                        this@MainActivity,
                        Constant.URL_USERPROTOCOL,
                        "服务协议"
                    )
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.bgColor = Color.TRANSPARENT
                    ds.isUnderlineText = false
                }
            }, start2, end2, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
            getDescView()?.apply {
                text = span
                movementMethod = LinkMovementMethod.getInstance()
            }
            getLeftView()?.apply {
                text = "暂不使用"
                setOnClickListener {
                    dismiss()
                    Process.killProcess(Process.myPid())
                }
            }
            getRightView()?.apply {
                text = "同意"
                setOnClickListener {
                    SharePreferenceUtil.putBoolean("agreeProtocol", true)
                    dismiss()
                }
            }
        }.show()
    }


    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
//        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
//            //用户勾选了“不再询问”，引导用户去设置页面打开权限
//            AppSettingsDialog.Builder(this)
//                    .setTitle("权限申请")
//                    .setRationale("应用程序运行缺少必要的权限，请前往设置页面打开")
//                    .setPositiveButton("去设置")
//                    .setNegativeButton("取消")
//                    .setRequestCode(REQUESTCODE_SETTING)
//                    .build()
//                    .show()
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        MainFragment?.onActivityResult(requestCode, resultCode, data)
    }

    private var firstTime = 0L
    override fun onBackPressed() {
        if (System.currentTimeMillis() - firstTime > 2000) {
            toast("再按一次退出车联益众")
            firstTime = System.currentTimeMillis()
            return
        }
        super.onBackPressed()
    }

    /**
     * 切换tab
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTabChange(event: MessageEvent) {
        if (event.code == MessageEvent.MAIN_SWITCHTAB) {
            var msg = event.message
            if (!msg.isNullOrEmpty()) {
                var index = msg[0]
                when (index) {
                    MAIN_TAB_HOME.toString() -> nav_view.selectedItemId = R.id.navigation_home
                    MAIN_TAB_STORE.toString() -> nav_view.selectedItemId = R.id.navigation_dashboard
                    MAIN_TAB_TALK.toString() -> nav_view.selectedItemId =
                        R.id.navigation_notifications
                    MAIN_TAB_MINE.toString() -> nav_view.selectedItemId = R.id.navigation_mine
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault()
            .unregister(this)
        mPresenter?.onDestory()
        mPresenter = null
    }

    override fun onError(err: String?) {
        toast(err ?: "")
    }

}
