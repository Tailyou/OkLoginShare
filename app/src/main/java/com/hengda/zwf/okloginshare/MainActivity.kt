package com.hengda.zwf.okloginshare

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import com.hengda.zwf.commonutil.SDCardUtil
import com.hengda.zwf.sharelogin.ShareLoginClient
import com.hengda.zwf.sharelogin.content.ShareContent
import com.hengda.zwf.sharelogin.content.ShareContentPage
import com.hengda.zwf.sharelogin.content.ShareContentPicture
import com.hengda.zwf.sharelogin.content.ShareContentText
import com.hengda.zwf.sharelogin.type.LoginPlatform
import com.hengda.zwf.sharelogin.type.SharePlatform

class MainActivity : AppCompatActivity() {

    private var rgShareContentType: RadioGroup? = null
    private var rbText: RadioButton? = null
    private var mShareContent: ShareContent? = null
    private var mBitmap: Bitmap? = null
    private var mLargeBmpPath: String? = null
    private var mThumbBmpBytes: ByteArray? = null
    private var mActivity: MainActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mActivity = this@MainActivity
        rgShareContentType = findViewById(R.id.rgShareContentType)
        rbText = findViewById(R.id.rbText)

        object : Thread() {
            override fun run() {
                super.run()
                mBitmap = (ContextCompat.getDrawable(mActivity!!, R.drawable.large) as BitmapDrawable).bitmap
                mLargeBmpPath = BitmapUtil.saveBitmap(mBitmap!!, SDCardUtil.getSDCardPath(), "share_pic")
                mThumbBmpBytes = BitmapUtil.bitmap2ByteArray(mBitmap!!)
            }
        }.start()

        mShareContent = ShareContentText("我感觉这是个神奇的问题，昨天项目还一切OK")
        rgShareContentType!!.setOnCheckedChangeListener({ _, checkedId ->
            when (checkedId) {
                R.id.rbText -> mShareContent = ShareContentText("我感觉这是个神奇的问题，昨天项目还一切OK")
                R.id.rbPicture -> mShareContent = ShareContentPicture(mLargeBmpPath)
                R.id.rbWebPage -> mShareContent = ShareContentPage("这是标题，好像不够长",
                        "我感觉这是个神奇的问题，昨天项目还一切OK",
                        "https://segmentfault.com/q/1010000009688458",
                        mLargeBmpPath, mThumbBmpBytes)
            }
        })
    }

    fun doShareLogin(view: View) {
        when (view.id) {
            R.id.btnSinaLogin//微博登录
            -> ShareLoginClient.login(mActivity!!, LoginPlatform.WEIBO, LoginListener(mActivity!!, LoginPlatform.WEIBO))
            R.id.btnQQLogin//QQ登录
            -> ShareLoginClient.login(mActivity!!, LoginPlatform.QQ, LoginListener(mActivity!!, LoginPlatform.QQ))
            R.id.btnWeChatLogin//微信登录
            -> ShareLoginClient.login(mActivity!!, LoginPlatform.WEIXIN, LoginListener(mActivity!!, LoginPlatform.WEIXIN))
            R.id.btnSina//分享-微博
            -> ShareLoginClient.share(mActivity!!, SharePlatform.WEIBO_TIME_LINE, mShareContent!!, ShareListener(mActivity!!))
            R.id.btnQQZone//分享-QQ空间
            -> ShareLoginClient.share(mActivity!!, SharePlatform.QQ_ZONE, mShareContent!!, ShareListener(mActivity!!))
            R.id.btnQQFriend//分享-QQ好友
            -> ShareLoginClient.share(mActivity!!, SharePlatform.QQ_FRIEND, mShareContent!!, ShareListener(mActivity!!))
            R.id.btnWxFriend//分享-微信好友
            -> ShareLoginClient.share(mActivity!!, SharePlatform.WEIXIN_FRIEND, mShareContent!!, ShareListener(mActivity!!))
            R.id.btnWxFriendZone//分享-微信朋友圈
            -> ShareLoginClient.share(mActivity!!, SharePlatform.WEIXIN_FRIEND_ZONE, mShareContent!!, ShareListener(mActivity!!))
            R.id.btnWxFavorite//分享-微信收藏
            -> ShareLoginClient.share(mActivity!!, SharePlatform.WEIXIN_FAVORITE, mShareContent!!, ShareListener(mActivity!!))
        }
    }

}
