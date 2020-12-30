package com.linruan.carconnection.entities

/**
 * author：shichengxiang on 2020/6/8 11:21
 * email：1328911009@qq.com
 */
class BBS {
    /**
     * id : 9
     * group_id : 1
     * user_id : 1
     * content : 这里是内容啦
     * imgs : [{"id":"1","filepath":"http://chelianyizhong.com/public/uploads/20200604/fc23c2dc79ffa8c377bfd1a21bd6f951.png"},{"id":"2","filepath":"http://chelianyizhong.com/public/uploads/20200604/e5baf0d8797902e53751c1bf17544bb7.png"},{"id":"3","filepath":"http://chelianyizhong.com/public/uploads/20200604/19be7b41b97febdc7ab5e61c3f33f882.png"}]
     * leixing_id : 443
     * items_id : 446
     * recommend : 1
     * lng : null
     * lat : null
     * create_time : 1591066370
     * status : 1
     * user : {"avatar":"","name":"测试会员"}
     */
    var id = ""
    var group_id = ""
    var user_id = ""
    var content: String? = null
    var leixing_id = 0
    var items_id = 0
    var recommend = 0
    var hits=0 //点击
    var comments=0 //评论数量
    var lng: String? = null
    var lat: String? = null
    var create_time = 0L
    var status = 0
    var user: UserBean? = null
    var imgs: ArrayList<ImgsBean>? = null
    var commentslist:ArrayList<Comment>?=null
    var leixing:String?=null
    var items:String?=null

    class UserBean {
        /**
         * avatar :
         * name : 测试会员
         */
        var avatar: String? = null
        var name: String? = null
        var follow=0

    }

    class ImgsBean {
        /**
         * id : 1
         * filepath : http://chelianyizhong.com/public/uploads/20200604/fc23c2dc79ffa8c377bfd1a21bd6f951.png
         */
        var id: String? = null
        var filepath: String? = null
        var width=0f
        var height=0f
    }
}
