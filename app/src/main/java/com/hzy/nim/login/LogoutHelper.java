package com.hzy.nim.login;


import com.hzy.nim.DemoCache;
import com.hzy.nim.session.redpacket.NIMRedPacketClient;
import com.hzy.uikit.api.NimUIKit;

/**
 * 注销帮助类
 * Created by huangjun on 2015/10/8.
 */
public class LogoutHelper {
    public static void logout() {
        // 清理缓存&注销监听&清除状态
        NimUIKit.logout();
        DemoCache.clear();
        NIMRedPacketClient.clear();
    }
}
