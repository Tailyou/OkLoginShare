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
     * 链接
     */
    val url: String?

    /**
     * 缩略图
     */
    val thumbImgBytes: ByteArray?

    /**
     * 大图
     */
    val largeImgPath: String?

}
