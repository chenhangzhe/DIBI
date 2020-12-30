package com.netease.nim.uikit.business.session.viewholder.extension;

import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderText;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;


/**
 * Created by zhoujianghua on 2015/8/4.
 */
public class MsgViewHolderDefCustom extends MsgViewHolderText {

    public MsgViewHolderDefCustom(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected String getDisplayText() {
        DefaultCustomAttachment attachment = (DefaultCustomAttachment) message.getAttachment();
        if (attachment == null){
            return "附件内容为空";
        }

        return "type: " + attachment.getType() == null ? "附件类型为空" : attachment.getType() + ", data: " + attachment.getContent() == null ? "附件文本为空" : attachment.getContent();
    }
}
