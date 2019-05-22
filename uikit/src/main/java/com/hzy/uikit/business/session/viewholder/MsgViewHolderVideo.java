package com.hzy.uikit.business.session.viewholder;


import com.hzy.uikit.R;
import com.hzy.uikit.business.session.activity.WatchVideoActivity;
import com.hzy.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.hzy.uikit.common.util.media.BitmapDecoder;
import com.netease.nimlib.sdk.msg.attachment.VideoAttachment;

/**
 * Created by zhoujianghua on 2015/8/5.
 */
public class MsgViewHolderVideo extends MsgViewHolderThumbBase {

    public MsgViewHolderVideo(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.nim_message_item_video;
    }

    @Override
    protected void onItemClick() {
        WatchVideoActivity.start(context, message);
    }

    @Override
    protected String thumbFromSourceFile(String path) {
        VideoAttachment attachment = (VideoAttachment) message.getAttachment();
        String thumb = attachment.getThumbPathForSave();
        return BitmapDecoder.extractThumbnail(path, thumb) ? thumb : null;
    }
}
