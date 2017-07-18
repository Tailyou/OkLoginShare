package com.hengda.zwf.sharelogin.content

import android.os.Parcelable

import com.hengda.zwf.sharelogin.type.ContentType

interface ShareContent : Parcelable {

    /**
     * @return 分享的方式
     */
    @get:ContentType
    val type: Int

    /**
     * 标题
     */
    val title: String?

    /**
     * 文本(摘要)
     */
    val text: String?

    /**
     * 获取跳转的链接
     */
    val url: String?

    /**
     * 分享的缩略图片
     */
    val thumbBmpBytes: ByteArray?

    /**
     * 分享大图路径
     */
    val largeBmpPath: String?

}
