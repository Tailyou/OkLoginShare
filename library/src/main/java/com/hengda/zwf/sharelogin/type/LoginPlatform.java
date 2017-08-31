package com.hengda.zwf.sharelogin.type;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 登录平台（QQ、微博、微信）
 * @time 2017/6/6 18:34
 */
@Retention(RetentionPolicy.SOURCE)
@StringDef({LoginPlatform.WEIXIN, LoginPlatform.WEIBO, LoginPlatform.QQ})
public @interface LoginPlatform {
    String QQ = "QQ", WEIBO = "WEIBO", WEIXIN = "WEIXIN";
}
