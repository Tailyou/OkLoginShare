package com.hengda.zwf.sharelogin.sina

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import com.hengda.zwf.sharelogin.ILoginListener
import com.hengda.zwf.sharelogin.ShareLoginClient
import com.hengda.zwf.sharelogin.ShareLoginConfig
import com.hengda.zwf.sharelogin.content.ShareContent
import com.hengda.zwf.sharelogin.content.ShareContentImage
import com.hengda.zwf.sharelogin.content.ShareContentPage
import com.hengda.zwf.sharelogin.content.ShareContentText
import com.hengda.zwf.sharelogin.type.ContentType
import com.sina.weibo.sdk.WbSdk
import com.sina.weibo.sdk.api.ImageObject
import com.sina.weibo.sdk.api.TextObject
import com.sina.weibo.sdk.api.WebpageObject
import com.sina.weibo.sdk.api.WeiboMultiMessage
import com.sina.weibo.sdk.auth.AuthInfo
import com.sina.weibo.sdk.auth.Oauth2AccessToken
import com.sina.weibo.sdk.auth.WbAuthListener
import com.sina.weibo.sdk.auth.WbConnectErrorMessage
import com.sina.weibo.sdk.auth.sso.SsoHandler
import com.sina.weibo.sdk.share.WbShareCallback
import com.sina.weibo.sdk.share.WbShareHandler
import com.sina.weibo.sdk.utils.Utility

class SinaHandlerActivity : Activity(), WbShareCallback {

    lateinit var mSsoHandler: SsoHandler
    lateinit var shareHandler: WbShareHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WbSdk.install(this, AuthInfo(this, ShareLoginConfig.weiBoAppId, ShareLoginConfig.weiBoRedirectUrl, ShareLoginConfig.weiBoScope))
        when (intent.action) {
            ShareLoginClient.ACTION_LOGIN -> doLogin(ShareLoginClient.sLoginListener)
            ShareLoginClient.ACTION_SHARE -> {
                val shareContent = intent.extras.get(ShareLoginClient.SHARE_CONTENT) as ShareContent
                doShare(shareContent)
            }
        }
    }

    /**
     * 登录
     * @param loginListener
     * @time 2017/6/6 13:46
     */
    private fun doLogin(loginListener: ILoginListener?) {
        mSsoHandler = SsoHandler(this)
        mSsoHandler.authorizeClientSso(object : WbAuthListener {
            override fun onSuccess(oauth2AccessToken: Oauth2AccessToken) {
                if (oauth2AccessToken.isSessionValid) {
                    loginListener?.onSuccess(oauth2AccessToken.token, oauth2AccessToken.uid, oauth2AccessToken.expiresTime / 1000000)
                } else {
                    loginListener?.onError("登录失败")
                }
            }

            override fun onFailure(p0: WbConnectErrorMessage?) {
                loginListener?.onError(p0?.errorMessage)
            }

            override fun cancel() {
                loginListener?.onCancel()
            }
        })
    }

    /**
     * 分享
     * @time 2017/6/6 14:57
     */
    private fun doShare(shareContent: ShareContent) {
        shareHandler = WbShareHandler(this)
        shareHandler.registerApp()
        val weiboMessage = WeiboMultiMessage()
        when (shareContent.type) {
            ContentType.PIC -> weiboMessage.imageObject = getImageObj(shareContent as ShareContentImage)
            ContentType.TEXT -> weiboMessage.textObject = getTextObj(shareContent as ShareContentText)
            ContentType.WEBPAGE -> weiboMessage.mediaObject = getWebpageObj(shareContent as ShareContentPage)
        }
        shareHandler.shareMessage(weiboMessage, false)
    }

    /**
     * 创建文本消息对象
     * @time 2017/6/6 15:01
     */
    private fun getTextObj(shareContent: ShareContentText): TextObject {
        val textObject = TextObject()
        textObject.text = shareContent.text
        return textObject
    }

    /**
     * 创建图片消息对象
     * @time 2017/6/6 15:26
     */
    private fun getImageObj(shareContent: ShareContentImage): ImageObject {
        val imageObject = ImageObject()
        imageObject.imagePath = shareContent.largeImgPath
        return imageObject
    }

    /**
     * 创建多媒体（网页）消息对象。
     * @time 2017/6/6 15:30
     */
    private fun getWebpageObj(shareContent: ShareContentPage): WebpageObject {
        val mediaObject = WebpageObject()
        mediaObject.identify = Utility.generateGUID()
        mediaObject.title = shareContent.title//标题
        mediaObject.description = shareContent.text//摘要
        mediaObject.actionUrl = shareContent.url//地址
        val thumbBmpBytes = shareContent.thumbImgBytes
        val bitmap = BitmapFactory.decodeByteArray(thumbBmpBytes, 0, thumbBmpBytes.size)
        mediaObject.setThumbImage(bitmap)//缩略图
        return mediaObject
    }

    override fun onWbShareSuccess() {
        ShareLoginClient.sShareListener?.onSuccess()
        finish()
    }

    override fun onWbShareCancel() {
        ShareLoginClient.sShareListener?.onCancel()
        finish()
    }

    override fun onWbShareFail() {
        ShareLoginClient.sShareListener?.onError("分享失败")
        finish()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        shareHandler.doResultIntent(intent, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
        mSsoHandler.authorizeCallBack(requestCode, resultCode, data)
        finish()
    }

}
