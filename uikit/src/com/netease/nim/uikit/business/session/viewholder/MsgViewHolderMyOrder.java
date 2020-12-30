package com.netease.nim.uikit.business.session.viewholder;

import android.graphics.Color;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.session.emoji.MoonUtil;
import com.netease.nim.uikit.business.session.viewholder.extension.MyOrderAttachment;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.impl.NimUIKitImpl;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;

public class MsgViewHolderMyOrder extends MsgViewHolderBase {

    private MyOrderAttachment attachment;
    private TextView tv_order_title;
    private TextView tv_order_content;
    private LinearLayout ll_msg_order;
    private TextView tvDetail;

    public MsgViewHolderMyOrder(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        //自己定义的布局
        return R.layout.im_msg_order;
    }

    @Override
    protected void inflateContentView() {
        ll_msg_order = findViewById(R.id.ll_msg_order);
        tv_order_title = findViewById(R.id.tv_order_title);
        tv_order_content = findViewById(R.id.tv_order_content);
        tvDetail = findViewById(R.id.tv_detail);
    }

    @Override
    protected void bindContentView() {
        layoutDirection();
        attachment = (MyOrderAttachment) message.getAttachment();
        String orderNo =  attachment.getContent();

        MoonUtil.identifyFaceExpression(NimUIKit.getContext(), ll_msg_order, orderNo, ImageSpan.ALIGN_BOTTOM);
//        MoonUtil.identifyFaceExpression(NimUIKit.getContext(), tv_order_title, orderNo, ImageSpan.ALIGN_BOTTOM);
//        MoonUtil.identifyFaceExpression(NimUIKit.getContext(), tv_order_content, attachment.getTitle(), ImageSpan.ALIGN_BOTTOM);
//        MoonUtil.identifyFaceExpression(NimUIKit.getContext(), tvDetail, "查看订单详情＞", ImageSpan.ALIGN_BOTTOM);

        tv_order_title.setMovementMethod(LinkMovementMethod.getInstance());
        tv_order_content.setMovementMethod(LinkMovementMethod.getInstance());
        tvDetail.setMovementMethod(LinkMovementMethod.getInstance());

        if (TextUtils.isEmpty(attachment.getContent())) {
            tv_order_content.setVisibility(View.GONE);
        } else {
            tv_order_content.setVisibility(View.VISIBLE);
            if (lisenter != null){
                lisenter.onOrderClick(tv_order_content,attachment.getTitle(),tv_order_title,orderNo,tvDetail,message.getDirect());
            }
//            tv_order_content.setText(context.getString(CodeStrUtil.getCodeHint(attachment.getTitle())));
        }

        ll_msg_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击事件


            }
        });


    }


    public interface OnOrderNoClikLisenter {
        void onOrderClick(TextView tv_order_content, String title, TextView tv_order_title, String orderNo, TextView tvView, MsgDirectionEnum msgDirectionEnum);
    }

    private OnOrderNoClikLisenter lisenter;


    public void setOnShowOrderInfoLisenter(OnOrderNoClikLisenter lisenter) {
        this.lisenter = lisenter;
    }



    private void layoutDirection() {
        if (isReceivedMessage()) {
            ll_msg_order.setBackgroundResource(NimUIKitImpl.getOptions().messageLeftBackground);
            tv_order_title.setTextColor(Color.BLACK);
            tv_order_content.setTextColor(Color.BLACK);
            tvDetail.setTextColor(Color.BLUE);
            ll_msg_order.setPadding(ScreenUtil.dip2px(15), ScreenUtil.dip2px(8), ScreenUtil.dip2px(10), ScreenUtil.dip2px(8));
        } else {
            ll_msg_order.setBackgroundResource(NimUIKitImpl.getOptions().messageRightBackground);
            tv_order_title.setTextColor(Color.WHITE);
            tv_order_content.setTextColor(Color.WHITE);
            tvDetail.setTextColor(Color.BLACK);
            ll_msg_order.setPadding(ScreenUtil.dip2px(10), ScreenUtil.dip2px(8), ScreenUtil.dip2px(15), ScreenUtil.dip2px(8));
        }
    }


    //若是要自己修改气泡背景
    // 当是发送出去的消息时，内容区域背景的drawable id
    // @Override
    @Override
    protected int leftBackground() {
        return 0;
    }

    @Override
    protected int rightBackground() {
        return 0;
    }

    protected String getDisplayText() {
        return message.getContent();
    }
}

