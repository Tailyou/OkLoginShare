package com.hengda.zwf.sharelogin.type;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 分享平台（QQ空间、QQ好友、微博、微信好友、微信朋友圈、微信收藏）
 * @time 2017/6/6 18:35
 */
@Retention(RetentionPolicy.SOURCE)
@StringDef({SharePlatform.WEIXIN_FRIEND, SharePlatform.WEIXIN_FRIEND_ZONE, SharePlatform.WEIXIN_FAVORITE,
        SharePlatform.QQ_ZONE, SharePlatform.QQ_FRIEND, SharePlatform.WEIBO_TIME_LINE})
public @interface SharePlatform {
    String QQ_ZONE = "QQ_ZONE",
            QQ_FRIEND = "QQ_FRIEND",
            WEIBO_TIME_LINE = "WEIBO_TIME_LINE",
            WEIXIN_FRIEND = "WEIXIN_FRIEND",
            WEIXIN_FRIEND_ZONE = "WEIXIN_FRIEND_ZONE",
            WEIXIN_FAVORITE = "WEIXIN_FAVORITE";
}