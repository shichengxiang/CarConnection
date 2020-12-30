package com.linruan.carconnection.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.hitomi.tilibrary.transfer.TransferConfig
import com.hitomi.tilibrary.transfer.Transferee
import com.linruan.carconnection.R
import com.linruan.carconnection.Router
import com.linruan.carconnection.UserManager
import com.linruan.carconnection.entities.BBS
import com.linruan.carconnection.ui.talk.TalkDetailActivity
import com.linruan.carconnection.utils.MapUtil
import com.linruan.carconnection.utils.Util
import com.vansz.glideimageloader.GlideImageLoader
import kotlinx.android.synthetic.main.item_rv_talk.view.*
import kotlinx.android.synthetic.main.item_rv_talk.view.ivUserHead
import kotlinx.android.synthetic.main.item_rv_talk.view.tvUserName

class TalkAdapter : RecyclerView.Adapter<TalkAdapter.Holder> {
    companion object {
        val TYPE_RECOMMENT = 0
        val TYPE_NEARBY = 1
        val TYPE_ATTENTION = 2
    }

    private var currentTag = TYPE_RECOMMENT
    private var mData = arrayListOf<BBS>()

    constructor(context: Context?, tag: Int = 0) : super() {
        mCxt = context
        this.currentTag = tag
    }

    fun getData() = mData


    fun setTag(tag: Int) {
        this.currentTag = tag
    }

    public fun setData(list: ArrayList<BBS>) {
        this.mData = list
        notifyDataSetChanged()
    }

    fun addData(list: ArrayList<BBS>) {
        var start = mData.size - 1
        if (start < 0) start = 0
        this.mData.addAll(list)
        notifyItemRangeChanged(start, mData.size - 1)
        notifyDataSetChanged()
    }

    private var mCxt: Context? = null

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var goTalkDetail = itemView.vRoot
        var goTalkDetailHead = itemView.rlGotoDetail
        var tagContainer = itemView.tagContainer
        var tvLeixing1=itemView.tvLeixing1
        var tvLeixing2=itemView.tvLeixing2
        var tvDistance = itemView.tvDistance
        var tvGoAttention = itemView.tvGoAttention
        var rvImages = itemView.rvImages
        var ivUserHead = itemView.ivUserHead
        var tvUserName = itemView.tvUserName
        var tvContent = itemView.tvContent
        var tvTimeRelease = itemView.tvTimeRelease
        var tvHitsNum = itemView.tvHitsNum
        var tvCommentsNum = itemView.tvCommentsNum
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var view = LayoutInflater.from(mCxt)
            .inflate(R.layout.item_rv_talk, null)
        return Holder(view)
    }

    override fun getItemCount() = mData.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        var bean = mData[position]
        holder.goTalkDetail.setOnClickListener {
            Router.openUI(
                mCxt,
                TalkDetailActivity::class.java,
                HashMap<String, String>().apply { put("id", bean.id) })
        }
        holder.goTalkDetailHead.setOnClickListener {
            Router.openUI(
                mCxt,
                TalkDetailActivity::class.java,
                HashMap<String, String>().apply { put("id", bean.id) })
        }
        //关注
        if (bean.user_id == UserManager.getUser()?.id || currentTag == TYPE_ATTENTION) {
            holder.tvGoAttention.visibility = View.INVISIBLE
        } else {
            holder.tvGoAttention.visibility = View.VISIBLE
            holder.tvGoAttention.setOnClickListener {
                mOnViewClickListener?.onClick(
                    holder.tvGoAttention,
                    bean.user_id
                )
            }
        }
        holder.tvGoAttention.text = if (bean.user?.follow == 1) "已关注" else "关注"
//        holder.tagContainer.removeAllViews()
//        holder.tagContainer.addView(
//            LayoutInflater.from(mCxt)
//                .inflate(R.layout.item_tag_electromobile, null)
//        )
//        holder.tagContainer.addView(
//            LayoutInflater.from(mCxt)
//                .inflate(R.layout.item_tag_motorbike, null)
//        )
        holder.tvLeixing1.text=bean.leixing
        holder.tvLeixing2.text=bean.items
        var layoutManager =
            if (bean.imgs?.size ?: 0 < 1) LinearLayoutManager(mCxt, RecyclerView.VERTICAL, false)
            else GridLayoutManager(mCxt, 2)
        holder.rvImages.layoutManager = layoutManager
        holder.rvImages.adapter = ImageAdapter(mCxt!!, mData[position].imgs,bean.id)
        //头像
        Glide.with(mCxt!!)
            .load(bean.user?.avatar)
            .error(R.mipmap.img_default_head)
            .dontAnimate()
            .into(holder.ivUserHead)
        //昵称
        holder.tvUserName.text = bean.user?.name ?: " "
        //内容
        holder.tvContent.text = bean.content ?: " "
        //时间
        holder.tvTimeRelease.text = Util.getDiffTimeToCurrent(bean.create_time * 1000)
        if (currentTag == TYPE_NEARBY) {
            holder.tvDistance.visibility = View.VISIBLE
            holder.tvDistance.text = "${Util.decimalFormat1(
                MapUtil.calculateDistance(
                    UserManager.currentLocation[0],
                    UserManager.currentLocation[1],
                    Util.stringToDouble(bean.lat),
                    Util.stringToDouble(bean.lng)
                )
                    .toDouble() / 1000
            )}km"
        } else {
            holder.tvDistance.visibility = View.GONE
        }
        //数量
        holder.tvHitsNum.text = Util.numToK(bean.hits)
        holder.tvCommentsNum.text = Util.numToK(bean.comments)
    }

    private var mOnViewClickListener: OnViewClickListener? = null
    fun setOnClickListener(listener: OnViewClickListener) {
        mOnViewClickListener = listener
    }

    class ImageAdapter : RecyclerView.Adapter<ImageAdapter.ViewHolder> {
        private var context: Context? = null
        private var mImgs = arrayListOf<BBS.ImgsBean>()
        private var mId:String?=null

        constructor(context: Context?, imgs: ArrayList<BBS.ImgsBean>?,id:String?) {
            this.context = context
            mId=id
            if (imgs.isNullOrEmpty()) {
                mImgs.clear()
            } else {
                this.mImgs = imgs
            }
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var ivImage = itemView.findViewById<ImageView>(R.id.ivImage)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            var v = View.inflate(context, R.layout.item_show_image_wrap, null)
            return ViewHolder(v)
        }

        override fun getItemCount() = mImgs.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val param = holder.ivImage.layoutParams
            var bean=mImgs[position]
            holder.ivImage.setOnClickListener {
                Router.openUI(
                    context,
                    TalkDetailActivity::class.java,
                    HashMap<String, String>().apply { put("id", mId?:"") })
            }
            if (mImgs.size > 1) {
                param.width=ViewGroup.LayoutParams.MATCH_PARENT
                param.height=ViewGroup.LayoutParams.WRAP_CONTENT
                holder.ivImage.layoutParams=param
                Glide.with(context!!)
                    //                                        .asBitmap()
                    .load(bean.filepath ?: "")
                    .error(R.mipmap.img_talk_small_default)
                    .placeholder(R.mipmap.img_talk_small_default)
                    .override(500, 500)
                    .centerCrop()
                    .dontAnimate()
                    .into(holder.ivImage)
            } else {
                param.width=Util.getScreenWidth()/2
                param.height=Util.sizeImageHeight(param.width.toFloat(),bean.width,bean.height).toInt()
                holder.ivImage.layoutParams=param
                Glide.with(context!!)
                    .load(mImgs[position].filepath ?: "")
                    .placeholder(R.mipmap.img_talk_small_default)
                    .error(R.mipmap.img_talk_small_default)
                    .fitCenter()
                    .dontAnimate()
                    .into(holder.ivImage)
            }
//            holder.ivImage.setOnClickListener { showBigImage(holder.ivImage, imgPath) }
            //                                .into(object :CustomTarget<Bitmap>(){
            //                                    override fun onLoadCleared(placeholder: Drawable?) {
            //                                    }
            //
            //                                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            //                                        var resWidth=resource.width
            //                                        var resHeight=resource.height
            //                                        var scale=/
            //                                    }
            //                                })
        }

        fun showBigImage(iv: ImageView, source: String?) {
            //            TransferConfig.build()
            //                    .bindImageView(iv,source)
            Transferee.getDefault(context)
                .apply(
                    TransferConfig.build()
                        .setImageLoader(GlideImageLoader.with(context))
                        .bindImageView(iv, source)
                )
                .show()
        }
    }
}
