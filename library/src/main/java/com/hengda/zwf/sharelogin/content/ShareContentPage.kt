package com.hengda.zwf.sharelogin.content

import android.os.Parcel
import android.os.Parcelable
import com.hengda.zwf.sharelogin.type.ContentType

/**
 * 分享类型-网页
 * @time 2017/6/6 16:29
 */
class ShareContentPage(override val title: String, override val text: String, override val url: String, override val largeBmpPath: String?, override val thumbBmpBytes: ByteArray?) : ShareContent {

    override val type: Int
        get() = ContentType.WEBPAGE

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<ShareContentPage> = object : Parcelable.Creator<ShareContentPage> {
            override fun createFromParcel(source: Parcel): ShareContentPage = ShareContentPage(source)
            override fun newArray(size: Int): Array<ShareContentPage?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.createByteArray()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(title)
        dest.writeString(text)
        dest.writeString(url)
        dest.writeString(largeBmpPath)
        dest.writeByteArray(thumbBmpBytes)
    }

}