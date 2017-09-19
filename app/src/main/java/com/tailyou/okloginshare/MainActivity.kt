package com.tailyou.okloginshare

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.hengda.zwf.commonutil.SDCardUtil
import com.hengda.zwf.okloginshare.R
import com.hengda.zwf.sharelogin.*
import com.hengda.zwf.sharelogin.content.ShareContent
import com.hengda.zwf.sharelogin.content.ShareContentImage
import com.hengda.zwf.sharelogin.content.ShareContentPage
import com.hengda.zwf.sharelogin.content.ShareContentText
import com.hengda.zwf.sharelogin.type.LoginPlatform
import com.hengda.zwf.sharelogin.type.SharePlatform
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity(), AnkoLogger {

    lateinit var mBitmap: Bitmap
    lateinit var mLargeBmpPath: String
    lateinit var mThumbBmpBytes: ByteArray
    lateinit var mShareContent: ShareContent
    lateinit var mActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mActivity = this@MainActivity

        object : Thread() {
            override fun run() {
                super.run()
                mBitmap = (ContextCompat.getDrawable(mActivity, R.drawable.large) as BitmapDrawable).bitmap
                mLargeBmpPath = BitmapUtil.saveBitmap(mBitmap, SDCardUtil.getSDCardPath(), "share_pic")
                mThumbBmpBytes = BitmapUtil.bitmap2ByteArray(mBitmap)!!
            }
        }.start()

        mShareContent = ShareContentText("我感觉这是个神奇的问题，昨天项目还一切OK")
        rgShareContentType.setOnCheckedChangeListener({ _, checkedId ->
            when (checkedId) {
                R.id.rbText -> mShareContent = ShareContentText("我感觉这是个神奇的问题，昨天项目还一切OK")
                R.id.rbPicture -> mShareContent = ShareContentImage(mLargeBmpPath)
                R.id.rbWebPage -> mShareContent = ShareContentPage("这是标题，好像不够长",
                        "我感觉这是个神奇的问题，昨天项目还一切OK",
                        "https://segmentfault.com/q/1010000009688458",
                        mLargeBmpPath, mThumbBmpBytes)
            }
        })
    }

    fun doShareLogin(view: View) {
        when (view.id) {
            R.id.btnQQLogin -> doLogin(LoginPlatform.QQ)
            R.id.btnSinaLogin -> doLogin(LoginPlatform.WEIBO)
            R.id.btnWeChatLogin -> doLogin(LoginPlatform.WEIXIN)
            R.id.btnSina -> doShare(SharePlatform.WEIBO_TIME_LINE)
            R.id.btnQQZone -> doShare(SharePlatform.QQ_ZONE)
            R.id.btnQQFriend -> doShare(SharePlatform.QQ_FRIEND)
            R.id.btnWxFriend -> doShare(SharePlatform.WEIXIN_FRIEND)
            R.id.btnWxFriendZone -> doShare(SharePlatform.WEIXIN_FRIEND_ZONE)
            R.id.btnWxFavorite -> doShare(SharePlatform.WEIXIN_FAVORITE)
        }
    }

    //分享
    private fun doShare(platform: String) {
        ShareLoginClient.share(mActivity, platform, mShareContent, object : IShareListener {
            override fun onSuccess() {
                toast("share success")
            }

            override fun onCancel() {
                toast("share cancel")
            }

            override fun onError(msg: String?) {
                toast("share error")
            }
        })
    }

    //登录
    private fun doLogin(platform: String) {
        ShareLoginClient.login(mActivity, platform, object : ILoginListener {
            override fun onSuccess(accessToken: String, uId: String, expiresIn: Long) {
                loadUserInfo(platform, accessToken, uId)
                toast("$accessToken,$uId")
            }

            override fun onError(errorMsg: String?) {
                toast("login error:$errorMsg")
            }

            override fun onCancel() {
                toast("login cancel")
            }
        })
    }

    //获取三方账号信息
    private fun loadUserInfo(platform: String, accessToken: String, uId: String) {
        AuthUserInfoClient.getUserInfo(mActivity, platform, accessToken, uId, object : AuthUserInfoClient.UserInfoListener {
            override fun onSuccess(userInfo: AuthUserInfo) {
                toast(userInfo.toString())
            }

            override fun onError(msg: String) {
                toast(msg)
            }
        })
    }

}
