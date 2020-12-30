package cn.suozhi.DiBi.c2c.view.chat.qiyu;

import android.widget.LinearLayout;

//import com.qiyukf.unicorn.api.ConsultSource;
//import com.qiyukf.unicorn.api.Unicorn;
//import com.qiyukf.unicorn.ui.fragment.ServiceMessageFragment;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.util.ToolbarUtil;

public class QIYuChatActivity extends BaseActivity {

    @BindView(R.id.toolbar_center)
    public Toolbar toolbar;

    @Override
    protected int getViewResId() {
        return R.layout.activity_qiyu_chat;
    }

    @Override
    protected void init() {
        String title = "智能客服小V"; // 标题
        ToolbarUtil.initToolbar(toolbar, title, v -> onBackPressed());

//        ConsultSource source = new ConsultSource("", title, "custom information string"); // 访客来源信息
// 构造一个 ViewGroup，用于放置sdk的评价和人工客服按钮。该控件推荐放在标题栏右边。可以用以下两种方式:
// 1. 将 container 放到 layout 文件中
// LinearLayout sdkIconContainer = (LinearLayout)findViewById(R.id.xxx);
// 2. 动态构建，动态添加
        LinearLayout sdkIconContainer = new LinearLayout(this);
        sdkIconContainer.setOrientation(LinearLayout.HORIZONTAL);
// 构造好后，还需要将 ViewGroup 添加到你的 Activity 中
//        ServiceMessageFragment fragment = Unicorn.newServiceFragment(title, source, sdkIconContainer);
//        if (fragment == null) {
//            return;
//        }
//        FragmentManager fm = getSupportFragmentManager();
//        FragmentTransaction transaction = fm.beginTransaction();
//        transaction.add(R.id.fl_qc, fragment, "qy"); // 将 fragment 放到对应的 containerId 中。containerId 为 ViewGroup 的 resId
//        transaction.commitAllowingStateLoss();
    }
}
