package com.hengda.zwf.sharelogin

/**
 * 时间：2017/6/6 13:21
 * 描述：第三方登录回调
 */
interface ILoginListener {

    fun onSuccess(accessToken: String, uId: String, expiresIn: Long)

    fun onError(errorMsg: String?)

    fun onCancel()

}
