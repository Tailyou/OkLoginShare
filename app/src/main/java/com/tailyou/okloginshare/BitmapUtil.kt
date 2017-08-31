package com.tailyou.okloginshare

import android.graphics.Bitmap
import android.media.ThumbnailUtils

import com.hengda.zwf.commonutil.SDCardUtil

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object BitmapUtil {

    /**
     * 保存Bitmap到SD卡
     * @time 2017/6/6 18:25
     */
    fun saveBitmap(bitmap: Bitmap, dirPath: String, imgName: String): String {
        makeDir(dirPath)
        try {
            val f = File(dirPath + imgName + ".png")
            val fOut = FileOutputStream(f)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
            fOut.flush()
            fOut.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return SDCardUtil.getSDCardPath() + imgName + ".png"
    }

    /**
     * 创建目录
     * @time 2017/6/6 18:23
     */
    fun makeDir(dirPath: String) {
        try {
            val file = File(dirPath)
            if (!file.exists()) {
                file.mkdir()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Bitmap 转 byte[]
     * @time 2017/6/6 19:19
     */
    fun bitmap2ByteArray(src: Bitmap): ByteArray? {
        val bitmap: Bitmap
        if (src.width > 250 || src.height > 250) {
            bitmap = ThumbnailUtils.extractThumbnail(src, 250, 250)
        } else {
            bitmap = src
        }
        var thumbData: ByteArray? = null
        var outputStream: ByteArrayOutputStream? = null
        try {
            outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
            thumbData = outputStream.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush()
                    outputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return thumbData
    }

}
