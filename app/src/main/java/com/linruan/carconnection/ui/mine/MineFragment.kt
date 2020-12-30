package com.linruan.carconnection.ui.mine

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.jaeger.library.StatusBarUtil
import com.linruan.carconnection.*
import com.linruan.carconnection.dialog.TipDialog
import com.linruan.carconnection.entities.ImageLoadUp
import com.linruan.carconnection.entities.net.GetUserInfo
import com.linruan.carconnection.net.BaseResponse
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.linruan.carconnection.ui.WebViewActivity
import com.linruan.carconnection.ui.master.BattleNewsActivity
import com.linruan.carconnection.ui.master.MasterAuthActivity
import com.linruan.carconnection.ui.user.LoginActivity
import com.linruan.carconnection.ui.wallet.MyWalletActivity
import com.linruan.carconnection.utils.ImageUtil
import com.linruan.carconnection.utils.Util
import com.lzy.okgo.model.Response
import com.yanzhenjie.album.Album
import kotlinx.android.synthetic.main.fragment_mine.*
import java.math.BigDecimal

class MineFragment : BaseFragment() {
    override fun getLayout() = R.layout.fragment_mine
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        lifecycle.addObserver(object : LifecycleObserver {
        //            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        //            fun onResume() {
        //            }
        //        })
    }

    override fun initView(view: View, savedInstanceState: Bundle?) {
        tvGotoWallet.setOnClickListener {
            if (!UserManager.isLoginAndSkip(activity)) {
                return@setOnClickListener
            }
            Router.openUI(activity, MyWalletActivity::class.java)
        }
        rlGotoMsgUI.setOnClickListener {
            if (!UserManager.isLoginAndSkip(activity)) {
                return@setOnClickListener
            }
            if (UserManager.getUser()?.isMaster == true) {
                Router.openUI(activity, BattleNewsActivity::class.java)
            } else {
                Router.openUI(activity, MessageActivity::class.java)
            }
        }
        rlGotoAuthMaster.setOnClickListener {
            if (!UserManager.isLoginAndSkip(activity)) {
                return@setOnClickListener
            }
            WebViewActivity.openWebView(activity, Constant.URL_WORKERAUTH_STATEMENT, "修车师傅认证")
        }
        rlGotoRuleUI.setOnClickListener { Router.openUI(activity, RuleIntroActivity::class.java) }
        rlGotoAboutUsUI.setOnClickListener { Router.openUI(activity, AboutUsActivity::class.java) }
        rlCustomService.setOnClickListener {
            activity?.let { it1 ->
                var dialog = TipDialog(it1)
                dialog.getTitleView()?.text = "联系客服"
                dialog.getDescView()?.text = "如果遇到商品质量或订单相关问题，建议您直接使用自助服务进行处理，或者直接联系在线客服解决。"
                dialog.getLeftView()
                    ?.apply {
                        text = "以后再说"
                        setOnClickListener { dialog.dismiss() }
                    }
                dialog.getRightView()
                    ?.apply {
                        text = "电话联系"
                        setOnClickListener {
                            dialog.dismiss()
                            Util.callPhone(activity, Constant.CUSTOMERSERVICE_MOBILE)
                        }
                    }
                dialog.setCancelable(true)
                dialog.show()
            }
        }
        ivSetting.setOnClickListener {
            if (!UserManager.isLoginAndSkip(activity)) {
                return@setOnClickListener
            }
            Router.openUI(activity, SettingActivity::class.java)
        }
        // 修车订单
        tvMoreRepairOrder.setOnClickListener {
            if (!UserManager.isLoginAndSkip(activity)) {
                return@setOnClickListener
            }
            Router.openUI(
                activity,
                RePairCarOrderActivity::class.java,
                HashMap<String, Int>().apply { put(RePairCarOrderActivity.ORDERTAG, 0) })
        }
        llCarAll.setOnClickListener {
            if (!UserManager.isLoginAndSkip(activity)) {
                return@setOnClickListener
            }
            Router.openUI(
                activity,
                RePairCarOrderActivity::class.java,
                HashMap<String, Int>().apply { put(RePairCarOrderActivity.ORDERTAG, 1) })
        }
        llOrderWaitPay.setOnClickListener {
            if (!UserManager.isLoginAndSkip(activity)) {
                return@setOnClickListener
            }
            Router.openUI(
                activity,
                RePairCarOrderActivity::class.java,
                HashMap<String, Int>().apply { put(RePairCarOrderActivity.ORDERTAG, 2) })
        }
        llCarOrderFinished.setOnClickListener {
            if (!UserManager.isLoginAndSkip(activity)) {
                return@setOnClickListener
            }
            Router.openUI(
                activity,
                RePairCarOrderActivity::class.java,
                HashMap<String, Int>().apply { put(RePairCarOrderActivity.ORDERTAG, 3) })
        }
        llCarOrderCancel.setOnClickListener {
            if (!UserManager.isLoginAndSkip(activity)) {
                return@setOnClickListener
            }
            Router.openUI(
                activity,
                RePairCarOrderActivity::class.java,
                HashMap<String, Int>().apply { put(RePairCarOrderActivity.ORDERTAG, 4) })
        }
        //商城订单
        tvMoreStoreOrder.setOnClickListener {
            if (!UserManager.isLoginAndSkip(activity)) {
                return@setOnClickListener
            }
            Router.openUI(
                activity,
                StoreOrderActivity::class.java,
                HashMap<String, Int>().apply { put(StoreOrderActivity.ORDERTAG, 0) })
        }
        llStoreOrderWaitPay.setOnClickListener {
            if (!UserManager.isLoginAndSkip(activity)) {
                return@setOnClickListener
            }
            Router.openUI(
                activity,
                StoreOrderActivity::class.java,
                HashMap<String, Int>().apply { put(StoreOrderActivity.ORDERTAG, 1) })
        }
        llStoreOrderWaitSend.setOnClickListener {
            if (!UserManager.isLoginAndSkip(activity)) {
                return@setOnClickListener
            }
            Router.openUI(
                activity,
                StoreOrderActivity::class.java,
                HashMap<String, Int>().apply { put(StoreOrderActivity.ORDERTAG, 2) })
        }
        llStoreOrderWaitReceived.setOnClickListener {
            if (!UserManager.isLoginAndSkip(activity)) {
                return@setOnClickListener
            }
            Router.openUI(
                activity,
                StoreOrderActivity::class.java,
                HashMap<String, Int>().apply { put(StoreOrderActivity.ORDERTAG, 3) })
        }
        llStoreOrderAfterSaled.setOnClickListener {
            if (!UserManager.isLoginAndSkip(activity)) {
                return@setOnClickListener
            }
            Router.openUI(
                activity,
                StoreOrderActivity::class.java,
                HashMap<String, Int>().apply { put(StoreOrderActivity.ORDERTAG, 4) })
        }
        //个人信息
        rlUserInfo.setOnClickListener {
            if (!UserManager.isLoginAndSkip(activity)) {
                return@setOnClickListener
            }
            if (!UserManager.isLogin()) {
                Router.openUI(activity, LoginActivity::class.java)
            }
        }
        iv_head.setOnClickListener {
            if (UserManager.isLogin()) {
                updateAvatar()
            }
        }
        initLayout()
    }

    override fun getData() {
    }

    override fun onResume() {
        super.onResume()
        if (userVisibleHint) {
            changeLoginStatus(UserManager.isLogin())
            requestUserInfo()
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            StatusBarUtil.setDarkMode(activity)
            changeLoginStatus(UserManager.isLogin())
            requestUserInfo()
        }
    }

    private fun initLayout() {
        if (UserManager.getUser()?.isMaster == true) {
            rlRepairOrderLayout.visibility = View.GONE
            rlIntroOfMaster.visibility = View.VISIBLE
            rlGotoAuthMaster.visibility = View.GONE
        } else {
            rlRepairOrderLayout.visibility = View.VISIBLE
            rlIntroOfMaster.visibility = View.GONE
            rlGotoAuthMaster.visibility = View.VISIBLE
        }
    }

    /**
     * 获取用户信息
     */
    fun requestUserInfo() {
        Client.getInstance()
            .getUserInfo(object : JsonCallback<GetUserInfo>() {
                override fun onSuccess(response: Response<GetUserInfo?>?) {
                    var body = response?.body()
                    if (body?.isSuccess() == true) {
                        var nick = body.data?.name
                        var balance = body.data?.balance //余额
                        var avatar = body.data?.avatar
                        UserManager.setNick(nick ?: "")
                        tvUserName.text = nick ?: ""
                        UserManager.getUser()?.balance =
                            BigDecimal(balance ?: "0").setScale(2, BigDecimal.ROUND_DOWN).toString()
                        tvAmount.text = UserManager.getUser()?.balance ?: ""
                        activity?.let {
                            Glide.with(it)
                                .load(avatar ?: "")
                                .error(R.mipmap.img_default_head)
                                .dontAnimate()
                                .into(iv_head)
                        }
                        //订单数量
                        if (body.data?.repair_obligation ?: 0 > 0) {//修车待付款
                            tvRepairWaitCount.visibility = View.VISIBLE
                            tvRepairWaitCount.text = "${body.data?.repair_obligation}"
                        } else {
                            tvRepairWaitCount.visibility = View.GONE
                        }
                        if (body.data?.repair_finish ?: 0 > 0) { //修车已完成
                            tvRepairFinishedCount.visibility = View.VISIBLE
                            tvRepairFinishedCount.text = "${body.data?.repair_finish}"
                        } else {
                            tvRepairFinishedCount.visibility = View.GONE
                        }
                        if (body.data?.repair_cancel ?: 0 > 0) {//修车已取消
                            tvRepairCancelCount.visibility = View.VISIBLE
                            tvRepairCancelCount.text = "${body.data?.repair_cancel}"
                        } else {
                            tvRepairCancelCount.visibility = View.GONE
                        }
                        if (body.data?.order_obligation ?: 0 > 0) { //商城代付款
                            tvGoodsWaitCount.visibility = View.VISIBLE
                            tvGoodsWaitCount.text = "${body.data?.order_obligation}"
                        } else {
                            tvGoodsWaitCount.visibility = View.GONE
                        }
                        if (body.data?.order_wait_sending ?: 0 > 0) {//商城待发货
                            tvGoodsWaitSendCount.visibility = View.VISIBLE
                            tvGoodsWaitSendCount.text = "${body.data?.order_wait_sending}"
                        } else {
                            tvGoodsWaitSendCount.visibility = View.GONE
                        }
                        if (body.data?.order_wait_pickup ?: 0 > 0) {//商城待收货
                            tvGoodsWaitReceivedCount.visibility = View.VISIBLE
                            tvGoodsWaitReceivedCount.text = "${body.data?.order_wait_pickup}"
                        } else {
                            tvGoodsWaitReceivedCount.visibility = View.GONE
                        }
                        if (body.data?.order_refund ?: 0 > 0) {//商城售后
                            tvGoodsAfterCount.visibility = View.VISIBLE
                            tvGoodsAfterCount.text = "${body.data?.order_refund}"
                        } else {
                            tvGoodsAfterCount.visibility = View.GONE
                        }
                    } else {
                        activity?.toast(body?.msg ?: "拉取用户信息失败")
                    }
                }
            })
    }

    /**
     * 更新用户头像
     */
    fun updateAvatar() {
        Album.image(activity)
            .singleChoice()
            .camera(true)
            .columnCount(3)
            .onResult {
                activity?.let { it1 ->
                    Glide.with(it1).load(it[0].path).error(R.mipmap.img_default_head).dontAnimate()
                        .into(iv_head)
                }
                var img = ImageLoadUp().apply {
                    filepath = it[0].path
                }
                ImageUtil.build(activity!!)
                    .withImagelist(arrayListOf(img))
                    .onListener(object : ImageUtil.onUpLoadListener {
                        override fun onLoadupSuccess(id: String, index: Int) {
                            //头像上传成功 开始更新数据库
                            Client.getInstance()
                                .updateUserInfo(id, "", "", object : JsonCallback<BaseResponse>() {
                                    override fun onSuccess(response: Response<BaseResponse?>?) {
                                        if (response?.body()
                                                ?.isSuccess() == true
                                        ) {
                                            activity?.toast("头像上传成功")
                                        } else {
                                            activity?.toast(response?.body()?.msg ?: "头像上传失败")
                                        }
                                    }
                                })
                        }

                        override fun onFinishAll() {
                        }

                        override fun onUpLoadError(index: Int) {
                        }

                        override fun onProgress(progres: Int, index: Int) {
                        }
                    })
                    .start()
            }
            .start()
    }

    private fun changeLoginStatus(isLogin: Boolean) {
        if (isLogin) {
            tvUserName.text = UserManager.getUser()?.name ?: ""
            tvUserPhone.text = UserManager.getUser()?.phone ?: ""
        } else {
            tvUserName.text = resources.getString(R.string.registorlogin)
            tvUserPhone.text = resources.getString(R.string.phone_safe)
        }
    }
}
