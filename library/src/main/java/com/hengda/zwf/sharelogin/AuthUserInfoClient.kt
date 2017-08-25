package com.hengda.zwf.sharelogin

import android.content.Context

import com.hengda.zwf.sharelogin.type.LoginPlatform
import com.sina.weibo.sdk.exception.WeiboException
import com.sina.weibo.sdk.net.AsyncWeiboRunner
import com.sina.weibo.sdk.net.RequestListener
import com.sina.weibo.sdk.net.WeiboParameters

import org.json.JSONException
import org.json.JSONObject

object AuthUserInfoClient {

    fun getUserInfo(context: Context, @LoginPlatform type: String, accessToken: String, uid: String, listener: UserInfoListener?) {
        when (type) {
            LoginPlatform.QQ -> getQQUserInfo(context, accessToken, uid, listener)
            LoginPlatform.WEIBO -> getWeiBoUserInfo(context, accessToken, uid, listener)
            LoginPlatform.WEIXIN -> getWeiXinUserInfo(context, accessToken, uid, listener)
        }
    }

    /**
     * 得到用户的信息，是一个静态的基础方法
     * @see "http://wiki.open.qq.com/wiki/website/get_simple_userinfo"
     */
    fun getQQUserInfo(context: Context, accessToken: String, userId: String, listener: UserInfoListener?) {
        val runner = AsyncWeiboRunner(context)
        val params = WeiboParameters(null)
        params.put("access_token", accessToken)
        params.put("openid", userId)
        params.put("oauth_consumer_key", ShareLoginConfig.qqAppId)
        params.put("format", "json")
        runner.requestAsync("https://graph.qq.com/user/get_simple_userinfo", params, "GET", object : UserInfoRequestListener(listener) {
            @Throws(JSONException::class)
            override fun onSuccess(jsonObj: JSONObject): AuthUserInfo {
                val screen_name = jsonObj.getString("nickname")
                val gender = jsonObj.getString("gender")
                val avatar_large = jsonObj.getString("figureurl_qq_1")
                return AuthUserInfo(screen_name, gender, avatar_large, userId)
            }
        })
    }

    /**
     * 得到微博用户的信息
     * @see "http://open.weibo.com/wiki/2/users/show"
     */
    fun getWeiBoUserInfo(context: Context, accessToken: String, uid: String, listener: UserInfoListener?) {
        val runner = AsyncWeiboRunner(context)
        val params = WeiboParameters(null)
        params.put("access_token", accessToken)
        params.put("uid", uid)
        runner.requestAsync("https://api.weibo.com/2/users/show.json", params, "GET", object : UserInfoRequestListener(listener) {
            @Throws(JSONException::class)
            override fun onSuccess(jsonObj: JSONObject): AuthUserInfo {
                val screen_name = jsonObj.getString("screen_name")
                val gender = jsonObj.getString("gender")
                val avatar_large = jsonObj.getString("avatar_large")
                val id = jsonObj.getString("id")
                return AuthUserInfo(screen_name, gender, avatar_large, id)
            }
        })
    }

    /**
     * 得到微博用户的信息
     * @see "http://open.weibo.com/wiki/2/users/show"
     */
    fun getWeiXinUserInfo(context: Context, accessToken: String, uid: String, listener: UserInfoListener?) {
        val runner = AsyncWeiboRunner(context)
        val params = WeiboParameters(null)
        params.put("access_token", accessToken)
        params.put("openid", uid)
        runner.requestAsync("https://api.weixin.qq.com/sns/userinfo", params, "GET", object : UserInfoRequestListener(listener) {
            @Throws(JSONException::class)
            override fun onSuccess(jsonObj: JSONObject): AuthUserInfo {
                val screen_name = jsonObj.getString("nickname")
                val gender = jsonObj.getString("sex")
                val avatar_large = jsonObj.getString("headimgurl")
                val id = jsonObj.getString("unionid")
                return AuthUserInfo(screen_name, gender, avatar_large, id)
            }
        })
    }

    private abstract class UserInfoRequestListener internal constructor(private val listener: UserInfoListener?) : RequestListener {
        override fun onComplete(s: String) {
            var userInfo: AuthUserInfo? = null
            try {
                userInfo = onSuccess(JSONObject(s))
            } catch (e: JSONException) {
                e.printStackTrace()
                listener?.onError(e.message!!)
            }
            if (listener != null && userInfo != null) {
                listener.onSuccess(userInfo)
            }
        }

        @Throws(JSONException::class)
        internal abstract fun onSuccess(jsonObj: JSONObject): AuthUserInfo

        override fun onWeiboException(e: WeiboException) {
            e.printStackTrace()
            listener?.onError(e.message!!)
        }
    }

    interface UserInfoListener {
        fun onSuccess(userInfo: AuthUserInfo)

        fun onError(msg: String)
    }

}
