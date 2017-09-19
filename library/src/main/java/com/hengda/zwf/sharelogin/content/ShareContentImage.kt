package com.hengda.zwf.sharelogin.content

import android.os.Parcel
import android.os.Parcelable
import com.hengda.zwf.sharelogin.type.ContentType

/**
 * 分享类型-图片
 * @time 2017/6/6 16:29
 */
class ShareContentImage(override val largeImgPath: String) : ShareContent {

    override val type: Int
        get() = ContentType.PIC

    override val title: String?
        get() = null

    override val text: String?
        get() = null

    override val url: String?
        get() = null

    override val thumbImgBytes: ByteArray?
        get() = null

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<ShareContentImage> = object : Parcelable.Creator<ShareContentImage> {
            override fun createFromParcel(source: Parcel): ShareContentImage = ShareContentImage(source)
            override fun newArray(size: Int): Array<ShareContentImage?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(largeImgPath)
    }

}