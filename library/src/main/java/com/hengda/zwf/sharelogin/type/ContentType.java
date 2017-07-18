package com.hengda.zwf.sharelogin.type;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 分享内容类型（1：文本，2：图片，3：网页，4：音频）
 *
 * @author 祝文飞（Tailyou）
 * @time 2017/6/6 18:32
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({ContentType.TEXT, ContentType.PIC, ContentType.WEBPAGE, ContentType.MUSIC})
public @interface ContentType {
    int TEXT = 1, PIC = 2, WEBPAGE = 3, MUSIC = 4;
}