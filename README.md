## 一、概述

OkLoginShare是一个封装了QQ、微信、微博登录和分享功能的库，分享支持纯文字、
纯图片、网页（图文）三种格式，可分享到微博、QQ好友、QQ空间、微信好友、朋友圈、微信收藏。

1. 分享、登录时会自动检查是否安装并给出提示；
2. 由于QQ分享不支持分享纯文本给好友，这一部分采用直接调用QQ的方式实现。

## 二、版本

已上传JitPack，最新版本0.0.1，直接在gradle中添加依赖即可。  
compile 'com.github.Tailyou:OkLoginShare:1.3'

## 三、使用

封装此库的目的是为了在开发第三方登录和分享功能时，尽可能的少一些拷贝和配置。  

### 3.1 配置

配置非常简单，不需要拷贝jar文件，不需要修改AndroidManifest.xml.  

在 app build.gradle中添加对ShareLogin的依赖。

```groovy
   compile 'com.github.Tailyou:OkLoginShare:0.0.1'
```

在工作空间的build.gradle中配置 maven { url 'https://jitpack.io' }
                             maven { url "https://dl.bintray.com/thelasterstar/maven/" }

```groovy
    repositories {
        google()
        jcenter()
        maven { url 'https://jitpack.io' }
        maven { url "https://dl.bintray.com/thelasterstar/maven/" }
    }
```

如果用到QQ平台，需要在app的defaultConfig中配置tencentAuthId，如下所示
```groovy
    defaultConfig {
        applicationId "com.umeng.soexample"
        minSdkVersion 15
        targetSdkVersion 22
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
        manifestPlaceholders = [tencentAuthId: "tencent100424468"]
    }
```

### 3.2 使用

#### 3.2.1 初始化`ShareLoginClient`，配置相应平台的AppId，AppSecret等。

```kotlin
        private fun initShareLoginClient() {
            val slc = ShareLoginConfig.Builder()
                    .debug(true)
                    .appName(getString(R.string.app_name))
                    .qq(Constants.QQ_APP_ID, Constants.QQ_SCOPE)
                    .weiXin(Constants.WECHAT_APP_ID, Constants.WECHAT_APP_SECRET)
                    .weiBo(Constants.SINA_APP_KEY, Constants.SINA_REDIRECT_URL, Constants.SINA_SCOPE)
                    .build()
            ShareLoginClient.init(slc)
        }
```

#### 3.2.2 登录，传入登录平台和回调

微博
```kotlin
    ShareLoginClient.login(mActivity!!, LoginPlatform.WEIBO, LoginListener(mActivity!!, LoginPlatform.WEIBO))
```
微信
```kotlin
    ShareLoginClient.login(mActivity!!, LoginPlatform.WEIXIN, LoginListener(mActivity!!, LoginPlatform.WEIXIN))
```
QQ
```kotlin
    ShareLoginClient.login(mActivity!!, LoginPlatform.QQ, LoginListener(mActivity!!, LoginPlatform.QQ))
```

#### 3.2.3 分享，根据分享类型创建分享内容。  

1）纯文本
```kotlin
    mShareContent = ShareContentText("我感觉这是个神奇的问题，昨天项目还一切OK")
```
2）纯图片
```kotlin
    mShareContent = ShareContentPicture(mLargeBmpPath)
```
3）图文（网页）
```kotlin
    mShareContent = ShareContentPage("这是标题，好像不够长",
                            "我感觉这是个神奇的问题，昨天项目还一切OK",
                            "https://segmentfault.com/q/1010000009688458",
                            mLargeBmpPath, mThumbBmpBytes)
```
            
#### 3.2.4 分享，传入分享平台、分享内容、回调

以微博为例，其他分享平台类似。
```kotlin
    ShareLoginClient.share(mActivity!!, SharePlatform.WEIBO_TIME_LINE, mShareContent!!, ShareListener(mActivity!!))
```
