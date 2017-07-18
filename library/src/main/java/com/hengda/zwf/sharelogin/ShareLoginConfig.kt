package com.hengda.zwf.sharelogin

object ShareLoginConfig {

    var weiXinAppId: String = ""
    var weiXinSecret: String = ""
    var weiBoAppId: String = ""
    var weiBoRedirectUrl: String = ""
    var weiBoScope: String = ""
    var qqAppId: String = ""
    var qqScope: String = ""
    var appName: String = ""
    var isDebug: Boolean = false

    class Builder {

        fun appName(appName: String): ShareLoginConfig.Builder {
            ShareLoginConfig.appName = appName
            return this
        }

        fun debug(debug: Boolean): ShareLoginConfig.Builder {
            ShareLoginConfig.isDebug = debug
            return this
        }

        fun qq(qqAppId: String, scope: String): ShareLoginConfig.Builder {
            ShareLoginConfig.qqAppId = qqAppId
            ShareLoginConfig.qqScope = scope
            return this
        }

        fun weiBo(weiBoAppId: String, redirectUrl: String, scope: String): ShareLoginConfig.Builder {
            ShareLoginConfig.weiBoAppId = weiBoAppId
            ShareLoginConfig.weiBoRedirectUrl = redirectUrl
            ShareLoginConfig.weiBoScope = scope
            return this
        }

        fun weiXin(weiXinAppId: String, weiXinSecret: String): ShareLoginConfig.Builder {
            ShareLoginConfig.weiXinAppId = weiXinAppId
            ShareLoginConfig.weiXinSecret = weiXinSecret
            return this
        }

        fun build(): ShareLoginConfig {
            return ShareLoginConfig
        }

    }

}
