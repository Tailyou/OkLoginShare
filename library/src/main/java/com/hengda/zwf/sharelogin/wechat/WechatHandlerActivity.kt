package com.hengda.zwf.sharelogin.wechat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.hengda.zwf.sharelogin.ILoginListener
import com.hengda.zwf.sharelogin.IShareListener
import com.hengda.zwf.sharelogin.ShareLoginClient
import com.hengda.zwf.sharelogin.ShareLoginConfig
import com.hengda.zwf.sharelogin.content.ShareContent
import com.hengda.zwf.sharelogin.content.ShareContentPage
import com.hengda.zwf.sharelogin.content.ShareContentImage
import com.hengda.zwf.sharelogin.content.ShareContentText
import com.hengda.zwf.sharelogin.type.ContentType
import com.hengda.zwf.sharelogin.type.SharePlatform
import com.sina.weibo.sdk.exception.WeiboException
import com.sina.weibo.sdk.net.AsyncWeiboRunner
import com.sina.weibo.sdk.net.RequestListener
import com.sina.weibo.sdk.net.WeiboParameters
import com.sina.weibo.sdk.utils.LogUtil
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.*
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import org.json.JSONException
import org.json.JSONObject

class WechatHandlerActivity : Activity(), IWXAPIEventHandler {

    lateinit var api: IWXAPI
    val TAG = "WechatHandlerActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        api = WXAPIFactory.createWXAPI(this, ShareLoginConfig.weiXinAppId, true)
        api.handleIntent(intent, this)
        finish()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        api.handleIntent(getIntent(), this)
        finish()
    }

    override fun onReq(baseReq: BaseReq) {
        finish()
    }

    override fun onResp(baseResp: BaseResp) {
        if (baseResp is SendAuth.Resp && baseResp.type == 1) {
            parseLoginResp(this, baseResp, ShareLoginClient.sLoginListener)
        } else {
            parseShareResp(baseResp, ShareLoginClient.sShareListener)
        }
        finish()
    }

    /**
     * 分享
     * @time 2017/6/6 14:57
     */
    fun doShare(context: Context, shareContent: ShareContent, @SharePlatform shareType: String) {
        val weChatAppId = ShareLoginConfig.weiXinAppId
        val IWXAPI = WXAPIFactory.createWXAPI(context, weChatAppId, true)
        IWXAPI.registerApp(weChatAppId)
        IWXAPI.sendReq(setupShareRequest(shareContent, shareType))
    }

    /**
     * 解析分享响应
     * @time 2017/6/7 14:55
     */
    private fun parseShareResp(resp: BaseResp, listener: IShareListener) {
        when (resp.errCode) {
            BaseResp.ErrCode.ERR_OK -> listener.onSuccess()
            BaseResp.ErrCode.ERR_USER_CANCEL -> listener.onCancel()
            BaseResp.ErrCode.ERR_AUTH_DENIED -> listener.onError("用户拒绝授权")
            BaseResp.ErrCode.ERR_SENT_FAILED -> listener.onError("发送失败")
            BaseResp.ErrCode.ERR_COMM -> listener.onError("一般错误")
            else -> listener.onError("未知错误")
        }
    }

    /**
     * 组装分享请求
     * @time 2017/6/7 15:23
     */
    private fun setupShareRequest(shareContent: ShareContent, @SharePlatform shareType: String): SendMessageToWX.Req {
        val req = SendMessageToWX.Req()
        req.transaction = System.currentTimeMillis().toString()
        req.message = setupMessage(shareContent)
        when (shareType) {
            SharePlatform.WEIXIN_FRIEND -> req.scene = SendMessageToWX.Req.WXSceneSession
            SharePlatform.WEIXIN_FRIEND_ZONE -> req.scene = SendMessageToWX.Req.WXSceneTimeline
            SharePlatform.WEIXIN_FAVORITE -> req.scene = SendMessageToWX.Req.WXSceneFavorite
        }
        return req
    }

    /**
     * 组装分享内容
     * @time 2017/6/7 15:23
     */
    private fun setupMessage(shareContent: ShareContent): WXMediaMessage {
        val msg = WXMediaMessage()
        msg.title = shareContent.title
        msg.description = shareContent.text
        when (shareContent.type) {
            ContentType.TEXT ->
                // 纯文字
                msg.mediaObject = getTextObj(shareContent as ShareContentText)
            ContentType.PIC ->
                // 纯图片
                msg.mediaObject = getImageObj(shareContent as ShareContentImage)
            ContentType.WEBPAGE ->
                // 网页（图文）
                msg.mediaObject = getWebPageObj(shareContent as ShareContentPage)
            else -> throw UnsupportedOperationException("不支持的分享内容")
        }
        if (!msg.mediaObject.checkArgs()) {
            throw IllegalArgumentException("分享信息的参数类型不正确")
        }
        return msg
    }

    private fun getTextObj(shareContent: ShareContentText): WXMediaMessage.IMediaObject {
        val text = WXTextObject()
        text.text = shareContent.text
        return text
    }

    private fun getImageObj(shareContent: ShareContentImage): WXMediaMessage.IMediaObject {
        val image = WXImageObject()
        image.imagePath = shareContent.largeImgPath
        return image
    }

    private fun getWebPageObj(shareContent: ShareContentPage): WXMediaMessage.IMediaObject {
        val webPage = WXWebpageObject()
        webPage.webpageUrl = shareContent.url
        return webPage
    }

    companion object {
        /**
         * 登录
         * @time 2017/6/6 13:46
         */
        fun doLogin(context: Context) {
            val appId = ShareLoginConfig.weiXinAppId
            val api = WXAPIFactory.createWXAPI(context.applicationContext, appId, true)
            api.registerApp(appId)
            val req = SendAuth.Req()
            req.scope = "snsapi_userinfo"
            api.sendReq(req)
        }
    }

    /**
     * 解析登录响应
     * @time 2017/6/7 14:51
     */
    private fun parseLoginResp(activity: Activity, resp: SendAuth.Resp, listener: ILoginListener) {
        when (resp.errCode) {
            BaseResp.ErrCode.ERR_OK -> getAccessTokenByCode(activity, resp.code, listener)
            BaseResp.ErrCode.ERR_USER_CANCEL -> {
                listener.onCancel()
                LogUtil.d(TAG, "取消登录")
            }
            BaseResp.ErrCode.ERR_AUTH_DENIED -> {
                listener.onError("用户拒绝授权")
                LogUtil.d(TAG, "用户拒绝授权")
            }
            else -> listener.onError("未知错误")
        }
    }

    /**
     * 根据登录成功后的code获取token
     * @time 2017/6/7 14:50
     */
    private fun getAccessTokenByCode(context: Context, code: String, listener: ILoginListener) {
        val params = WeiboParameters(null)
        params.put("appid", ShareLoginConfig.weiXinAppId)
        params.put("secret", ShareLoginConfig.weiXinSecret)
        params.put("grant_type", "authorization_code")
        params.put("code", code)
        AsyncWeiboRunner(context).requestAsync("https://api.weixin.qq.com/sns/oauth2/access_token", params, "GET",
                object : RequestListener {
                    override fun onComplete(s: String) {
                        try {
                            val jsonObject = JSONObject(s)
                            val token = jsonObject.getString("access_token")
                            val openid = jsonObject.getString("openid")
                            val expires_in = jsonObject.getLong("expires_in")
                            listener.onSuccess(token, openid, expires_in)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onWeiboException(e: WeiboException) {
                        listener.onError(e.message!!)
                    }
                })
    }

}
