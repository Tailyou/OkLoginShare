package com.hengda.zwf.sharelogin.qq

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.hengda.zwf.sharelogin.ILoginListener
import com.hengda.zwf.sharelogin.IShareListener
import com.hengda.zwf.sharelogin.ShareLoginClient
import com.hengda.zwf.sharelogin.ShareLoginConfig
import com.hengda.zwf.sharelogin.content.ShareContent
import com.hengda.zwf.sharelogin.content.ShareContentImage
import com.hengda.zwf.sharelogin.content.ShareContentPage
import com.hengda.zwf.sharelogin.content.ShareContentText
import com.hengda.zwf.sharelogin.type.ContentType
import com.hengda.zwf.sharelogin.type.SharePlatform
import com.hengda.zwf.sharelogin.wechat.WechatHandlerActivity.Companion.doLogin
import com.tencent.connect.common.Constants
import com.tencent.connect.share.QQShare
import com.tencent.connect.share.QzonePublish
import com.tencent.connect.share.QzoneShare
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import org.json.JSONObject

class QQHandlerActivity : Activity() {

    private lateinit var mUIListener: IUiListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent != null && !intent.action.isNullOrBlank()) {
            when (intent.action) {
                ShareLoginClient.ACTION_LOGIN -> doLogin(ShareLoginClient.sLoginListener!!)
                ShareLoginClient.ACTION_SHARE -> {
                    if (intent.hasExtra(ShareLoginClient.SHARE_CONTENT) &&
                            intent.hasExtra(ShareLoginClient.SHARE_PLATFORM)) {
                        val shareContent = intent.extras.get(ShareLoginClient.SHARE_CONTENT) as ShareContent
                        val sharePlatform = intent.extras.getString(ShareLoginClient.SHARE_PLATFORM)
                        doShare(sharePlatform, shareContent, ShareLoginClient.sShareListener!!)
                    }
                }
                else -> {
                    //ignore
                }
            }
        }
    }

    /**
     * 登录
     * @param loginListener
     * @time 2017/6/6 13:46
     */
    private fun doLogin(loginListener: ILoginListener) {
        mUIListener = object : IUiListener {
            override fun onComplete(p0: Any?) {
                try {
                    val jsonObject = p0 as JSONObject
                    val token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN)
                    val openId = jsonObject.getString(Constants.PARAM_OPEN_ID)
                    val expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN)
                    loginListener.onSuccess(token, openId, java.lang.Long.valueOf(expires)!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onCancel() {
                loginListener.onCancel()
            }

            override fun onError(p0: UiError?) {
                loginListener.onError(p0?.errorMessage)
            }
        }
        val tencent = Tencent.createInstance(ShareLoginConfig.qqAppId, this.applicationContext)
        if (!tencent.isSessionValid) {
            tencent.login(this, ShareLoginConfig.qqScope, mUIListener)
        } else {
            tencent.logout(this)
        }
    }

    /**
     * 分享
     * @time 2017/6/6 14:57
     */
    private fun doShare(sharePlatform: String, shareContent: ShareContent, shareListener: IShareListener) {
        mUIListener = object : IUiListener {
            override fun onComplete(p0: Any?) {
                shareListener.onSuccess()
            }

            override fun onCancel() {
                shareListener.onCancel()
            }

            override fun onError(p0: UiError?) {
                shareListener.onError(p0?.errorMessage)
            }
        }
        val tencent = Tencent.createInstance(ShareLoginConfig.qqAppId, applicationContext)
        if (sharePlatform == SharePlatform.QQ_FRIEND) {
            when (shareContent.type) {
                ContentType.TEXT //文本
                -> shareQQText(shareContent as ShareContentText)
                ContentType.PIC //图片
                -> tencent.shareToQQ(this, setupQQImageBundle(shareContent as ShareContentImage), mUIListener)
                ContentType.WEBPAGE //网页（图文）
                -> tencent.shareToQQ(this, setupQQPageBundle(shareContent as ShareContentPage), mUIListener)
            }
        } else {
            when (shareContent.type) {
                ContentType.TEXT //文本
                -> tencent.publishToQzone(this, setupQzoneTextBundle(shareContent as ShareContentText), mUIListener)
                ContentType.PIC //图片
                -> tencent.publishToQzone(this, setupQzoneImageBundle(shareContent as ShareContentImage), mUIListener)
                ContentType.WEBPAGE //网页（图文）
                -> tencent.shareToQzone(this, setupQzonePageBundle(shareContent as ShareContentPage), mUIListener)
            }
        }
    }

    /**
     * QQ好友-文本
     * @time 2017/6/7 17:06
     */
    private fun shareQQText(shareContent: ShareContentText) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareContent.text)
        sendIntent.type = "text/plain"
        sendIntent.setClassName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity")
        startActivity(sendIntent)
        finish()
    }

    /**
     * QQ好友-图片
     * @time 2017/6/6 15:26
     */
    private fun setupQQImageBundle(shareContent: ShareContentImage): Bundle {
        var shareContentPicture = shareContent
        val params = Bundle()
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE)
        val imgPath = shareContentPicture.largeImgPath
        if (imgPath.startsWith("http")) {
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imgPath)
        } else {
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imgPath)
        }
        return params
    }

    /**
     * QQ好友-网页（图文）
     * @time 2017/6/6 15:30
     */
    private fun setupQQPageBundle(shareContent: ShareContentPage): Bundle {
        var shareContentPage = shareContent
        val params = Bundle()
        params.putString(QQShare.SHARE_TO_QQ_TITLE, shareContentPage.title)
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareContentPage.text)
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareContentPage.url)
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareContentPage.largeImgPath)
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT)
        return params
    }

    /**
     * QQ空间-说说-文本
     * @time 2017/6/8 8:21
     */
    private fun setupQzoneTextBundle(shareContent: ShareContentText): Bundle {
        var shareContentText = shareContent
        val params = Bundle()
        params.putInt(QzonePublish.PUBLISH_TO_QZONE_KEY_TYPE, QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHMOOD)
        params.putString(QzonePublish.PUBLISH_TO_QZONE_SUMMARY, shareContentText.text)
        return params
    }

    /**
     * QQ空间-说说-图片
     * @time 2017/6/8 8:21
     */
    private fun setupQzoneImageBundle(shareContent: ShareContentImage): Bundle {
        var shareContentPicture = shareContent
        val params = Bundle()
        params.putInt(QzonePublish.PUBLISH_TO_QZONE_KEY_TYPE, QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHMOOD)
        params.putStringArrayList(QzonePublish.PUBLISH_TO_QZONE_IMAGE_URL, arrayListOf(shareContentPicture.largeImgPath))
        return params
    }

    /**
     * QQ空间分享-网页（图文）
     * @time 2017/6/7 12:12
     */
    private fun setupQzonePageBundle(shareContent: ShareContentPage): Bundle {
        var shareContentPage = shareContent
        val params = Bundle()
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT)
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareContentPage.title)
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, shareContentPage.text)
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareContentPage.url)
        params.putString(QzoneShare.SHARE_TO_QQ_IMAGE_URL, shareContentPage.largeImgPath)
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, arrayListOf(shareContentPage.largeImgPath))
        return params
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (mUIListener != null) {
            Tencent.handleResultData(data, mUIListener)
        }
        finish()
    }

}
