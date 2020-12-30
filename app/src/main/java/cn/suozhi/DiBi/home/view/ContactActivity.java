package cn.suozhi.DiBi.home.view;

import android.content.Context;
import android.content.Intent;
import android.view.View;

//import com.qiyukf.unicorn.api.ConsultSource;
//import com.qiyukf.unicorn.api.ProductDetail;
//import com.qiyukf.unicorn.api.Unicorn;

import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.util.ToolbarUtil;

/**
 * 联系我们
 */
public class ContactActivity extends BaseActivity {

    @BindView(R.id.toolbar_text)
    public Toolbar toolbar;

    @Override
    protected int getViewResId() {
        return R.layout.activity_contact;
    }

    @Override
    protected void init() {
        ToolbarUtil.initToolbar(toolbar, getString(R.string.contactUs), v -> onBackPressed());
    }

    @OnClick({R.id.tv_contactWork, R.id.tv_contactSubmit})
    public void contact(View v) {
        switch (v.getId()) {
            case R.id.tv_contactWork://我的工单
                startActivity(new Intent(this, MyWorkOrderActivity.class));
                break;
            case R.id.tv_contactSubmit://提交工单
                startActivity(new Intent(this, SubmitOrderWorkActivity.class));
                break;
        }
    }


//    public static void consultService(final Context context, String uri, String title, ProductDetail productDetail) {
//        // 启动聊天界面
//        ConsultSource source = new ConsultSource(uri, title, null);
//
//        source.productDetail = productDetail;
//        Unicorn.openServiceActivity(context, staffName(), source);
//    }

    private static String staffName() {
        return "智能客服小V";
    }
}
