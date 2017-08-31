package com.tailyou.okloginshare

import android.support.multidex.MultiDexApplication
import com.hengda.zwf.okloginshare.R
import com.hengda.zwf.sharelogin.ShareLoginClient
import com.hengda.zwf.sharelogin.ShareLoginConfig

class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        initShareLoginClient()
    }

    /**
     * 初始化 ShareLoginClient
     * @time 2017/6/6 13:40
     */
    private fun initShareLoginClient() {
        val slc = ShareLoginConfig.Builder()
                .debug(true)
                .appName(getString(R.string.app_name))
                .qq(Constants.QQ_APP_ID, Constants.QQ_SCOPE)
                .weiXin(Constants.WECHAT_APP_ID, Constants.WECHAT_APP_SECRET)
                .weiBo(Constants.SINA_APP_KEY, Constants.SINA_REDIRECT_URL, Constants.SINA_SCOPE)
                .build()
        ShareLoginClient.init(slc)
    }

}
