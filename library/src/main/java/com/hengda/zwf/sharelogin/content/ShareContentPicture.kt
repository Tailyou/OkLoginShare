package com.hengda.zwf.sharelogin.content

import android.os.Parcel
import android.os.Parcelable
import com.hengda.zwf.sharelogin.type.ContentType

/**
 * 分享类型-图片
 * @time 2017/6/6 16:29
 */
class ShareContentPicture(override val largeBmpPath: String?) : ShareContent {

    override val type: Int
        get() = ContentType.PIC

    override val title: String?
        get() = null

    override val text: String?
        get() = null

    override val url: String?
        get() = null

    override val thumbBmpBytes: ByteArray?
        get() = null

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<ShareContentPicture> = object : Parcelable.Creator<ShareContentPicture> {
            override fun createFromParcel(source: Parcel): ShareContentPicture = ShareContentPicture(source)
            override fun newArray(size: Int): Array<ShareContentPicture?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(largeBmpPath)
    }

}