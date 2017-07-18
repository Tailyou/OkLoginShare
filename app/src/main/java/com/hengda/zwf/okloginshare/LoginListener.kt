package com.hengda.zwf.okloginshare

import android.app.Activity
import android.widget.Toast
import com.hengda.zwf.sharelogin.ILoginListener
import com.hengda.zwf.sharelogin.type.LoginPlatform

class LoginListener : ILoginListener {

    private val activity: Activity
    private val type: String

    constructor(activity: Activity, @LoginPlatform type: String) {
        this.activity = activity
        this.type = type
    }

    override fun onSuccess(accessToken: String, userId: String, expiresIn: Long) {
        val result = "登录成功，token:" + accessToken
        Toast.makeText(activity, result, Toast.LENGTH_SHORT).show()
    }

    override fun onError(msg: String) {
        val result = "登录失败,失败信息：" + msg
        Toast.makeText(activity, result, Toast.LENGTH_SHORT).show()
    }

    override fun onCancel() {
        val result = "取消登录"
        Toast.makeText(activity, result, Toast.LENGTH_SHORT).show()
    }

}
