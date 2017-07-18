package com.hengda.zwf.sharelogin.content

import android.os.Parcel
import android.os.Parcelable
import com.hengda.zwf.sharelogin.type.ContentType

/**
 * 分享类型-文本

 * @author 祝文飞（Tailyou）
 * *
 * @time 2017/6/6 16:29
 */
class ShareContentText(override val text: String) : ShareContent {

    override val type: Int
        get() = ContentType.TEXT

    override val title: String?
        get() = null

    override val url: String?
        get() = null

    override val thumbBmpBytes: ByteArray?
        get() = null

    override val largeBmpPath: String?
        get() = null

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<ShareContentText> = object : Parcelable.Creator<ShareContentText> {
            override fun createFromParcel(source: Parcel): ShareContentText = ShareContentText(source)
            override fun newArray(size: Int): Array<ShareContentText?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(text)
    }

}