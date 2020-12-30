package cn.suozhi.DiBi.c2c.view.chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.netease.nim.uikit.business.recent.RecentContactsCallback;
import com.netease.nim.uikit.business.recent.RecentContactsFragment;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.fragment.TabFragment;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.impl.customization.DefaultP2PSessionCustomization;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.OnlineClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;
//import com.qiyukf.nimlib.sdk.auth.AuthService;
//import com.qiyukf.nimlib.sdk.auth.LoginInfo;
//import com.qiyukf.unicorn.api.RequestCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.c2c.view.chat.reminder.ReminderManager;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.login.LoginActivity;

/**
 * Created by zhoujianghua on 2015/8/17.
 */
public class SessionListFragment extends TabFragment {

    private static final String TAG = SessionListFragment.class.getSimpleName();
    private static DefaultP2PSessionCustomization commonP2PSessionCustomization;
    private View notifyBar;

    private TextView notifyBarText;

    // 同时在线的其他端的信息
    private List<OnlineClient> onlineClients;


    private RecentContactsFragment fragment;
    private View rootView;
    private StatusCode currentCode;

    public SessionListFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.session_list, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onInit();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onCurrent();
    }

    @Override
    public void onDestroy() {
        registerObservers(false);
        super.onDestroy();
    }

    protected void onInit() {
        findViews();
        initDefaultSessionCustomization();
        registerObservers(true);

        addRecentContactsFragment();
    }

    private void registerObservers(boolean register) {
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(userStatusObserver, register);
    }

    private void findViews() {
        notifyBar = rootView.findViewById(R.id.status_notify_bar);
        notifyBarText = rootView.findViewById(R.id.status_desc_label);
        notifyBar.setVisibility(View.GONE);

        notifyBarText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentCode == StatusCode.NET_BROKEN) {
                    notifyBar.setVisibility(View.VISIBLE);
                    notifyBarText.setText("当前网络不可用");
                } else if (currentCode == StatusCode.UNLOGIN) {
                    notifyBar.setVisibility(View.VISIBLE);
                    notifyBarText.setText("未登录");
                } else if (currentCode == StatusCode.CONNECTING) {
                    notifyBar.setVisibility(View.VISIBLE);
                    notifyBarText.setText("连接中...");
                } else if (currentCode == StatusCode.LOGINING) {
                    notifyBar.setVisibility(View.VISIBLE);
                    notifyBarText.setText("登录中...");
                } else {
                    notifyBar.setVisibility(View.GONE);
                }

            }
        });
    }


//    public void doLogin(String userCode, String imToken) {
//        LoginInfo info = new LoginInfo(userCode, imToken); // config...
//        RequestCallback<LoginInfo> callback =
//                new RequestCallback<LoginInfo>() {
//                    @Override
//                    public void onSuccess(LoginInfo param) {
//                        // 可以在此保存LoginInfo到本地，下次启动APP做自动登录用
//                        Log.d("wangli", "云信登录成功");
//
//                        SharedUtil.saveUserAccount(userCode);
//                        SharedUtil.saveUserToken(imToken);
//
//                        Log.d("wangli", "保存账号密码成功");
//                    }
//
//                    @Override
//                    public void onFailed(int code) {
//
//                    }
//
//                    @Override
//                    public void onException(Throwable exception) {
//
//                    }
//
//                };
//      NIMClient.getService(AuthService.class).login(info)
//                .setCallback(callback);
//    }

    /**
     * 用户状态变化
     */
    Observer<StatusCode> userStatusObserver = new Observer<StatusCode>() {
        @Override
        public void onEvent(StatusCode code) {
            currentCode = code;
            if (code.wontAutoLogin()) {
                kickOut(code);
            } else {
                if (code == StatusCode.NET_BROKEN) {
                    notifyBar.setVisibility(View.VISIBLE);
                    notifyBarText.setText("当前网络不可用");
                } else if (code == StatusCode.UNLOGIN) {
                    notifyBar.setVisibility(View.VISIBLE);
                    notifyBarText.setText("未登录");
                } else if (code == StatusCode.CONNECTING) {
                    notifyBar.setVisibility(View.VISIBLE);
                    notifyBarText.setText("连接中...");
                } else if (code == StatusCode.LOGINING) {
                    notifyBar.setVisibility(View.VISIBLE);
                    notifyBarText.setText("登录中...");
                } else {
                    notifyBar.setVisibility(View.GONE);
                }
            }
        }
    };


    // 初始化会话定制，P2P、Team、ChatRoom
    private static void initDefaultSessionCustomization() {
        if (commonP2PSessionCustomization == null) {
            commonP2PSessionCustomization = new DefaultP2PSessionCustomization();
        }

    }

    private void kickOut(StatusCode code) {
//        SharedUtil.saveUserToken("");

        if (code == StatusCode.PWD_ERROR) {
            LogUtil.e("Auth", "user password error");
            ToastHelper.showToast(getActivity(), "登陆失败");
        } else {
            LogUtil.i("Auth", "Kicked!");
        }
//        onLogout();
    }

    // 注销
    private void onLogout() {
        // 清理缓存&注销监听&清除状态
//        NIMClient.getService(AuthService.class).logout();

        SharedUtil.clearData(getContext());
        getContext().startActivity(new Intent(getContext(), LoginActivity.class)
                .putExtra("goBack", false));
        getActivity().finish();
    }

    // 将最近联系人列表fragment动态集成进来。 开发者也可以使用在xml中配置的方式静态集成。
    private void addRecentContactsFragment() {
        fragment = new RecentContactsFragment();
        fragment.setContainerId(R.id.messages_fragment);

        final UI activity = (UI) getActivity();

        // 如果是activity从堆栈恢复，FM中已经存在恢复而来的fragment，此时会使用恢复来的，而new出来这个会被丢弃掉
        fragment = (RecentContactsFragment) activity.addFragment(fragment);

        fragment.setCallback(new RecentContactsCallback() {
            @Override
            public void onRecentContactsLoaded() {
                // 最近联系人列表加载完毕
            }

            @Override
            public void onUnreadCountChange(int unreadCount) {
                ReminderManager.getInstance().updateSessionUnreadNum(unreadCount);
            }

            @Override
            public void onItemClick(RecentContact recent) {
                // 回调函数，以供打开会话窗口时传入定制化参数，或者做其他动作
                P2PChatMessageActivity.start(getContext(), recent.getContactId(),recent.getFromNick(), commonP2PSessionCustomization, null);
            }

            @Override
            public String getDigestOfAttachment(RecentContact recentContact, MsgAttachment attachment) {
                // 设置自定义消息的摘要消息，展示在最近联系人列表的消息缩略栏上
                // 当然，你也可以自定义一些内建消息的缩略语，例如图片，语音，音视频会话等，自定义的缩略语会被优先使用。
//                if (attachment instanceof GuessAttachment) {
//                    GuessAttachment guess = (GuessAttachment) attachment;
//                    return guess.getValue().getDesc();
//                } else if (attachment instanceof RTSAttachment) {
//                    return "[白板]";
//                } else if (attachment instanceof StickerAttachment) {
//                    return "[贴图]";
//                } else if (attachment instanceof SnapChatAttachment) {
//                    return "[阅后即焚]";
//                } else if (attachment instanceof RedPacketAttachment) {
//                    return "[红包]";
//                } else if (attachment instanceof RedPacketOpenedAttachment) {
//                    return ((RedPacketOpenedAttachment) attachment).getDesc(recentContact.getSessionType(), recentContact.getContactId());
//                }

                return null;
            }

            @Override
            public String getDigestOfTipMsg(RecentContact recent) {
                String msgId = recent.getRecentMessageId();
                List<String> uuids = new ArrayList<>(1);
                uuids.add(msgId);
                List<IMMessage> msgs = NIMClient.getService(MsgService.class).queryMessageListByUuidBlock(uuids);
                if (msgs != null && !msgs.isEmpty()) {
                    IMMessage msg = msgs.get(0);
                    Map<String, Object> content = msg.getRemoteExtension();
                    if (content != null && !content.isEmpty()) {
                        return (String) content.get("content");
                    }
                }

                return null;
            }
        });
    }
}
