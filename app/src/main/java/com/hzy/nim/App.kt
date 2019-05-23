package com.hzy.nim

import android.app.Application
import android.text.TextUtils
import com.hzy.nim.chatroom.ChatRoomSessionHelper
import com.hzy.nim.config.preference.Preferences
import com.hzy.nim.config.preference.UserPreferences
import com.hzy.nim.contact.ContactHelper
import com.hzy.nim.event.DemoOnlineStateContentProvider
import com.hzy.nim.mixpush.DemoMixPushMessageHandler
import com.hzy.nim.mixpush.DemoPushContentProvider
import com.hzy.nim.session.NimDemoLocationProvider
import com.hzy.uikit.api.NimUIKit
import com.hzy.uikit.api.UIKitOptions
import com.hzy.uikit.business.contact.core.query.PinYin
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.auth.LoginInfo
import com.netease.nimlib.sdk.mixpush.NIMPushClient
import com.netease.nimlib.sdk.util.NIMUtil

/**
 *
 * @author: ziye_huang
 * @date: 2019/5/22
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        DemoCache.setContext(this)
        // 4.6.0 开始，第三方推送配置入口改为 SDKOption#mixPushConfig，旧版配置方式依旧支持。
        NIMClient.init(this, getLoginInfo(), NimSDKOptionConfig.getSDKOptions(this))
        if (NIMUtil.isMainProcess(this)) {
            // 注册自定义推送消息处理，这个是可选项
            NIMPushClient.registerMixPushMessageHandler(DemoMixPushMessageHandler())

            // 初始化红包模块，在初始化UIKit模块之前执行
//            NIMRedPacketClient.init(this)
            // init pinyin
            PinYin.init(this)
            PinYin.validate()
            // 初始化UIKit模块
            initUIKit()
            // 初始化消息提醒
            NIMClient.toggleNotification(UserPreferences.getNotificationToggle())
            //关闭撤回消息提醒
//            NIMClient.toggleRevokeMessageNotification(false);
            // 云信sdk相关业务初始化
            NIMInitManager.getInstance().init(true)
            // 初始化音视频模块
//            initAVChatKit()
            // 初始化rts模块
//            initRTSKit()
        }
    }
    private fun getLoginInfo(): LoginInfo? {
        val account = Preferences.getUserAccount()
        val token = Preferences.getUserToken()

        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            DemoCache.setAccount(account.toLowerCase())
            return LoginInfo(account, token)
        } else {
            return null
        }
    }

    private fun initUIKit() {
        // 初始化
        NimUIKit.init(this, buildUIKitOptions())

        // 设置地理位置提供者。如果需要发送地理位置消息，该参数必须提供。如果不需要，可以忽略。
        NimUIKit.setLocationProvider(NimDemoLocationProvider())

        // IM 会话窗口的定制初始化。
        SessionHelper.init()

        // 聊天室聊天窗口的定制初始化。
        ChatRoomSessionHelper.init()

        // 通讯录列表定制初始化
        ContactHelper.init()

        // 添加自定义推送文案以及选项，请开发者在各端（Android、IOS、PC、Web）消息发送时保持一致，以免出现通知不一致的情况
        NimUIKit.setCustomPushContentProvider(DemoPushContentProvider())

        NimUIKit.setOnlineStateContentProvider(DemoOnlineStateContentProvider())
    }

    private fun buildUIKitOptions(): UIKitOptions {
        val options = UIKitOptions()
        // 设置app图片/音频/日志等缓存目录
        options.appCacheDir = NimSDKOptionConfig.getAppCacheDir(this) + "/app"
        return options
    }
}