package com.linruan.carconnection.ui.talk

import android.graphics.BitmapFactory
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.graphics.scale
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.jiguang.share.android.api.JShareInterface
import cn.jiguang.share.android.api.PlatActionListener
import cn.jiguang.share.android.api.Platform
import cn.jiguang.share.android.api.ShareParams
import cn.jiguang.share.qqmodel.QQ
import cn.jiguang.share.qqmodel.QZone
import cn.jiguang.share.wechat.Wechat
import cn.jiguang.share.wechat.WechatMoments
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.LogUtils
import com.bumptech.glide.Glide
import com.linruan.carconnection.*
import com.linruan.carconnection.adapter.CommentAdapter
import com.linruan.carconnection.adapter.ImageWrapAdapter
import com.linruan.carconnection.adapter.MTextWatcher
import com.linruan.carconnection.decorview.SpaceItemDecoraton
import com.linruan.carconnection.dialog.CommonDialog
import com.linruan.carconnection.entities.BBS
import com.linruan.carconnection.entities.Comment
import com.linruan.carconnection.entities.UserBean
import com.linruan.carconnection.entities.net.GetShareDataResponse
import com.linruan.carconnection.moudle.TalkDetailPresenter
import com.linruan.carconnection.moudle.TalkDetailView
import com.linruan.carconnection.utils.Util
import kotlinx.android.synthetic.main.activity_talkdetail.*
import kotlinx.android.synthetic.main.customer_notitfication_layout.*
import java.util.HashMap

class TalkDetailActivity : BaseActivity(), TalkDetailView {

    private var mCommentAdapter: CommentAdapter? = null
    private var mImageWrapAdapter: ImageWrapAdapter? = null
    private val HINT = "说说你的想法..."
    private var currentReplyId = "" //当前要回复的帖子id
    private var currentReplyName = "" //当前回复人姓名
    private var currentBelowId = "" //在谁的评论下
    private var isFirstEmpty = true
    private var mId = ""
    private var mPresenter: TalkDetailPresenter? = null
    private var mShareParams: ShareParams? = null //分享参数

    override fun getLayout() = R.layout.activity_talkdetail

    override fun initView() {
        if (intent.hasExtra("id"))
            mId = intent.getStringExtra("id")
        mPresenter = TalkDetailPresenter(this)
        toolbar.setTitle("")
        toolbar.getRightImageView()
            .apply {
                visibility = View.VISIBLE
                setImageResource(R.drawable.ic_share)
                setOnClickListener {
                    if (mShareParams != null){
                        Util.sharePlatform(this@TalkDetailActivity, mShareParams!!, object : PlatActionListener {
                            override fun onComplete(
                                p0: Platform?,
                                p1: Int,
                                p2: HashMap<String, Any>?
                            ) {
                                runOnUiThread { toast("分享成功") }
                            }

                            override fun onCancel(p0: Platform?, p1: Int) {
                                runOnUiThread { toast("取消分享") }
                            }

                            override fun onError(p0: Platform?, p1: Int, p2: Int, p3: Throwable?) {
                                LogUtils.e(p3?.message)
                                runOnUiThread { toast("分享错误") }
                            }

                        })
                    }else{
                        toast("分享异常")
                    }

                }
            }
        etInputMsg.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                return@OnEditorActionListener true
            }
            false
        })
        rvImgsList.layoutManager = LinearLayoutManager(this)
        rvImgsList.addItemDecoration(
            SpaceItemDecoraton(
                0,
                Util.px2dp(8),
                0,
                0,
                RecyclerView.VERTICAL
            )
        )
        mImageWrapAdapter = ImageWrapAdapter(this)
        rvImgsList.adapter = mImageWrapAdapter

        //添加标签
//        flowTagsContainer.addView(layoutInflater.inflate(R.layout.item_tag_electromobile, null))
//        flowTagsContainer.addView(layoutInflater.inflate(R.layout.item_tag_motorbike, null))
        //消息列表
        rvNews.layoutManager = LinearLayoutManager(this)
        mCommentAdapter = CommentAdapter(this, "", true)
        rvNews.adapter = mCommentAdapter
        mCommentAdapter?.setCommentListener(object : CommentAdapter.CommentListener {
            override fun replayClick(replyId: String, belowId: String, replyName: String) {
                etInputMsg.hint = "正在回复：$replyName"
                currentReplyId = replyId
                currentBelowId = belowId
                currentReplyName = replyName
                KeyboardUtils.showSoftInput(etInputMsg)
            }
        })
        rvImgsList.setHasFixedSize(true)
        rvImgsList.isNestedScrollingEnabled = false
        rvNews.setHasFixedSize(true)
        rvNews.isNestedScrollingEnabled = false
        // 评论
        handleBottom()
        mPresenter?.getBBSDetail(mId)
        mPresenter?.getShareData(mId)
        Glide.with(this)
            .load(UserManager.getUser()?.avatar ?: "")
            .dontAnimate()
            .into(ivHead)
        mRefreshLayout.setOnLoadMoreListener {
            mPresenter?.getBBSDetail(mId, true, mRefreshLayout)
        }
    }

    /**
     * 底部输入框逻辑
     */
    fun handleBottom() {
        etInputMsg.addTextChangedListener(object : MTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tvSend.isEnabled = !s.isNullOrBlank()
                if (s?.length ?: 0 > 0) {
                    isFirstEmpty = true
                }
            }
        })
        etInputMsg.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode === KeyEvent.KEYCODE_DEL) {
                    if (etInputMsg.text.toString()
                            .isEmpty() && !isFirstEmpty
                    ) {
                        etInputMsg.hint = HINT
                        currentReplyId = ""
                        isFirstEmpty = true
                    } else if (etInputMsg.text.toString()
                            .isEmpty()
                    ) {
                        isFirstEmpty = false
                    }
                }
                return false
            }
        })
        tvSend.setOnClickListener {
            KeyboardUtils.hideSoftInput(it)
            var newMsg = etInputMsg.text.toString()
            etInputMsg.setText("")
            if (!currentReplyId.isNullOrBlank()) {
                mCommentAdapter?.getData()
                    ?.forEach {
                        if (it.id == currentBelowId) {
                            it.addReply(Comment().apply {
                                user = UserBean().apply {
                                    name = UserManager.getUser()?.name ?: "我"
                                }
                                content = newMsg
                                reply_id = currentReplyId
                                reply = currentReplyName
                                create_time = System.currentTimeMillis()
                            })
                            mCommentAdapter?.notifyDataSetChanged()
                            return@forEach
                        }
                    }
            } else {
                tvNoComment.visibility = View.GONE
                mCommentAdapter?.addData(0, Comment().apply {
                    user = UserBean().apply {
                        name = UserManager.getUser()?.name ?: "我"
                    }
                    content = newMsg
                })
            }
            mPresenter?.sendComments(mId, newMsg, currentReplyId, currentBelowId)
            currentReplyId = ""
        }
        KeyboardUtils.registerSoftInputChangedListener(
            this,
            KeyboardUtils.OnSoftInputChangedListener {
                if (it < 200) {
                    //软键盘收起
                    etInputMsg.hint = HINT
                    currentReplyId = ""
                }
            })

    }

    private var mActionListener = object : PlatActionListener {
        override fun onComplete(platform: Platform?, p1: Int, data: HashMap<String, Any>?) {
            runOnUiThread {
                toast("分享成功")
            }
        }

        override fun onCancel(p0: Platform?, action: Int) {
            runOnUiThread {
                toast("分享取消")
            }
        }

        override fun onError(p0: Platform?, action: Int, errorCode: Int, e: Throwable?) {
            runOnUiThread {
                toast(e?.message ?: "")
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        KeyboardUtils.unregisterSoftInputChangedListener(window)
        mPresenter?.onDestory()
    }

    override fun onGetShareDataSuccess(data: GetShareDataResponse.DataBean?) {
        if (data != null)
            mShareParams = ShareParams().apply {
                shareType = Platform.SHARE_WEBPAGE
                title = data.title
                text = data.content
                if (data.image.isNullOrBlank()) {
                    imageData = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
                } else {
                    imageUrl = data.image
                }
                url = data.url
            }
    }

    override fun onGetDetailSuccess(detail: BBS, isMore: Boolean) {
        if (isMore) {
            mCommentAdapter?.addData(detail.commentslist)
            return
        }
        tvLeixing1.text = detail.leixing
        tvLeixing2.text = detail.items
        //头像
        Glide.with(this)
            .load(detail.user?.avatar ?: "")
            .error(R.mipmap.img_default_head)
            .dontAnimate()
            .into(ivUserHead)
        tvUserName.text = detail.user?.name ?: "未知"
        tvIntervalTime.text = Util.getDiffTimeToCurrent(detail.create_time * 1000)
        tvUserContent.text = detail.content ?: ""
        var imgs = arrayListOf<String>()
        detail.imgs?.forEach {
            imgs.add(it.filepath ?: "")
        }
        mImageWrapAdapter?.setData(imgs)
        tvHitsNum.text = "${detail.hits}"
        tvCommentsNum.text = "${detail.comments}"
        //评论
        val commentslist = detail.commentslist
        if (commentslist.isNullOrEmpty()) {
            tvNoComment.visibility = View.VISIBLE
            return
        }
        tvNoComment.visibility = View.GONE
        mCommentAdapter?.putData(commentslist)
    }

    override fun onError(err: String?) {
        toast(err ?: "")
    }
}
