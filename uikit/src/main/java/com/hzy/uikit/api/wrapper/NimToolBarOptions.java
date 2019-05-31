package com.hzy.uikit.api.wrapper;

import android.graphics.Color;
import com.hzy.uikit.R;
import com.hzy.uikit.common.activity.ToolBarOptions;

/**
 * Created by hzxuwen on 2016/6/16.
 */
public class NimToolBarOptions extends ToolBarOptions {

    public NimToolBarOptions() {
        // 设置返回按钮 logoId，如果不需要设置为0
//        logoId = R.drawable.nim_actionbar_nest_dark_logo;
        logoId = 0;
        navigateId = R.drawable.nim_actionbar_white_back_icon;
        isNeedNavigate = true;
        toolbarBgColor = Color.parseColor("#1A1A1A");
    }
}
