package com.netease.nim.uikit.business.session.viewholder.extension;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netease.nim.uikit.business.session.viewholder.extension.CustomAttachment;
import com.netease.nim.uikit.business.session.viewholder.extension.CustomAttachmentType;
import com.netease.nim.uikit.business.session.viewholder.extension.GuessAttachment;
import com.netease.nim.uikit.business.session.viewholder.extension.RedPacketAttachment;
import com.netease.nim.uikit.business.session.viewholder.extension.SnapChatAttachment;
import com.netease.nim.uikit.business.session.viewholder.extension.StickerAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachmentParser;

/**
 * Created by zhoujianghua on 2015/4/9.
 */
public class CustomAttachParser implements MsgAttachmentParser {

    private static final String KEY_TYPE = "type";
    private static final String KEY_DATA = "data";

    @Override
    public MsgAttachment parse(String json) {
        CustomAttachment attachment = null;
        try {
            JSONObject object = JSON.parseObject(json);
            int type = object.getInteger(KEY_TYPE);

            attachment = new MyOrderAttachment();
            switch (type) {
                case CustomAttachmentType.Guess:
                    attachment = new GuessAttachment();
                    break;
                case CustomAttachmentType.SnapChat:
                    return new SnapChatAttachment(object);
                case CustomAttachmentType.Sticker:
                    attachment = new StickerAttachment();
                    break;
                    case CustomAttachmentType.MyOrderCustomMsg:
                    attachment = new MyOrderAttachment ();
                    break;
//                case CustomAttachmentType.RTS:
//                    attachment = new RTSAttachment();
//                    break;
                case CustomAttachmentType.RedPacket:
                    attachment = new RedPacketAttachment();
                    break;
//                case CustomAttachmentType.OpenedRedPacket:
//                    attachment = new RedPacketOpenedAttachment();
//                    break;
                default:
                    attachment = new MyOrderAttachment();
                    break;
            }

            if (attachment != null) {
                attachment.fromJson(object);
            }
        } catch (Exception e) {

        }

        return attachment;
    }

    public static String packData(int type, JSONObject data) {
        JSONObject object = new JSONObject();
        object.put(KEY_TYPE, type);
        if (data != null) {
            object.put(KEY_DATA, data);
        }

        return object.toJSONString();
    }
}
