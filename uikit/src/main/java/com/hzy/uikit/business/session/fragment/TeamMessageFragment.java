package com.hzy.uikit.business.session.fragment;

import com.hzy.uikit.R;
import com.hzy.uikit.api.NimUIKit;
import com.hzy.uikit.common.ToastHelper;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.model.Team;

/**
 * Created by zhoujianghua on 2015/9/10.
 */
public class TeamMessageFragment extends MessageFragment {

    private Team team;

    @Override
    public boolean isAllowSendMessage(IMMessage message) {
        if (team == null) {
            team = NimUIKit.getTeamProvider().getTeamById(sessionId);
        }

        if (team == null || !team.isMyTeam()) {
            ToastHelper.showToast(getActivity(), R.string.team_send_message_not_allow);
            return false;
        }

        return super.isAllowSendMessage(message);
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}