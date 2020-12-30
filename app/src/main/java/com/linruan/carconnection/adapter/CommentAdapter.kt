package com.linruan.carconnection.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.linruan.carconnection.R
import com.linruan.carconnection.entities.Comment
import com.linruan.carconnection.logE
import com.linruan.carconnection.utils.Util
import kotlinx.android.synthetic.main.item_rv_comment.view.*

/**
 * 评论区
 */

class CommentAdapter : RecyclerView.Adapter<CommentAdapter.Holder> {
    private var currentBelowId = ""  // 指定评论下
    private var isTop=true//是否是一级列表
    private var mCommentListener: CommentListener? = null
    private var mData = ArrayList<Comment>()

    constructor(context: Context, belowId: String,isTop:Boolean) : super() {
        mCxt = context
        currentBelowId = belowId
        this.isTop=isTop
    }

    public fun putData(list: ArrayList<Comment>) {
        if (list != null)
            mData = list
        else {
            mData.clear()
        }
        notifyDataSetChanged()
    }

    public fun addData(comment: Comment) {
        if (mData == null)
            mData = arrayListOf()
        mData.add(comment)
        notifyDataSetChanged()
    }
    fun addData(index:Int,comment: Comment) {
        if (mData == null)
            mData = arrayListOf()
        mData.add(index,comment)
        notifyDataSetChanged()
    }
    fun addData(comments: ArrayList<Comment>?) {
        if(comments.isNullOrEmpty())
            return
        if (mData == null)
            mData = arrayListOf()
        mData.addAll(comments)
        notifyDataSetChanged()
    }

    fun getData() = mData

    private var mCxt: Context? = null

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var replyLayout = itemView.findViewById<View>(R.id.llReplyLayout)
        var tvReplyPrefix = itemView.tvReplyPrefix
        var tvUserComment = itemView.findViewById<TextView>(R.id.tvUserComment)
        var tvUserName = itemView.findViewById<TextView>(R.id.tvUserName)
        var replyList = itemView.rvReplyList
        var tvReleaseTime = itemView.tvReleaseTime
        var tvCommentCount = itemView.tvCommentCount
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var view = LayoutInflater.from(mCxt)
                .inflate(R.layout.item_rv_comment, null)
        return Holder(view)
    }

    override fun getItemCount() = mData.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        var comment = mData[position]
        var belowId=if(isTop) comment.id else currentBelowId
        holder.tvUserComment.setOnClickListener {
            mCommentListener?.replayClick(comment.id, belowId, comment.user?.name ?: "")
        }
        holder.tvUserName.text = comment.user?.name ?: ""
        if (!comment.reply.isNullOrBlank()) {
            holder.tvReplyPrefix.visibility = View.VISIBLE
            holder.tvUserComment.text = "${comment.reply}: ${comment.content}"
        } else {
            holder.tvReplyPrefix.visibility = View.GONE
            holder.tvUserComment.text = "${comment.content}"
        }
        if (!isTop) {
            holder.replyLayout.visibility = View.GONE
            holder.tvReleaseTime.visibility = View.GONE
            holder.tvCommentCount.visibility = View.GONE
        } else {
            holder.tvCommentCount.text = "${comment.sub?.size ?: 0}"
            holder.tvReleaseTime.text = Util.getDiffTimeToCurrent(comment.create_time*1000)
        }

        // 回复列表
        logE("sub.size=== ${comment.sub?.size ?: 0}")
        if (comment.sub != null && comment.sub!!.size > 0) {
            holder.replyLayout.visibility = View.VISIBLE
            holder.replyList.layoutManager = LinearLayoutManager(mCxt)
            holder.replyList.adapter = CommentAdapter(mCxt!!, comment.id,false).apply {
                putData(comment.sub!!)
                setCommentListener(this@CommentAdapter.mCommentListener)
            }
            holder.replyList.setHasFixedSize(true)
            holder.replyList.isNestedScrollingEnabled = false
        } else {
            holder.replyLayout.visibility = View.GONE
        }
    }

    fun setCommentListener(listener: CommentListener?) {
        this.mCommentListener = listener
    }

    interface CommentListener {
        fun replayClick(replyId: String, belowId: String,replyName:String)
    }
}