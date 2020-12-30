package cn.suozhi.DiBi.c2c.view.chat;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.util.ToolbarUtil;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import com.netease.nim.uikit.common.activity.UI;

public class ChatListActivity extends UI {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        Toolbar toolbar = findView(R.id.toolbar_text);
        ToolbarUtil.initToolbar(toolbar, getString(R.string.my_message), v -> onBackPressed());
        init();
    }


    protected void init() {
        getSupportFragmentManager()    //
                .beginTransaction()
                .add(R.id.fragment_container, new SessionListFragment())   // 此处的R.id.fragment_container是要盛放fragment的父容器
                .commit();
    }


    protected void loadData() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
