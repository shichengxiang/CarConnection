package com.linruan.carconnection.entities

/**
 * author：shichengxiang on 2020/5/30 16:47
 * email：1328911009@qq.com
 */
class Comment {
    var id=""
    var user_id=""
    var content_id=""
    var title=""
    var content=""
    var star=""
    var status=0
    var create_time=0L
    var reply_id=""
    var reply=""
    var user:UserBean?=null
    var sub:ArrayList<Comment>?=null

    fun addReply(comment: Comment){
        if(sub==null){
            sub= ArrayList()
        }
        sub?.add(comment)
    }
}