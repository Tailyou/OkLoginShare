package com.hengda.zwf.okloginshare

import android.app.Activity
import android.widget.Toast

import com.hengda.zwf.sharelogin.IShareListener

/**
 * 作者：祝文飞（Tailyou）
 * 邮箱：tailyou@163.com
 * 时间：2017/6/6 15:10
 * 描述：
 */
class ShareListener(private val activity: Activity) : IShareListener {

    override fun onSuccess() {
        val result = "分享成功"
        Toast.makeText(activity, result, Toast.LENGTH_SHORT).show()
    }

    override fun onCancel() {
        val result = "取消分享"
        Toast.makeText(activity, result, Toast.LENGTH_SHORT).show()
    }

    override fun onError(errorMsg: String) {
        Toast.makeText(activity, errorMsg, Toast.LENGTH_SHORT).show()
    }

}
