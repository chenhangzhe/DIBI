package cn.suozhi.DiBi.login.view;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.util.GsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.home.model.NotifyEntity;

public class ProtocolActivity extends BaseActivity implements OkHttpUtil.OnDataListener {

    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    private String title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getViewResId() {
        return R.layout.activity_protocol;
    }

    @Override
    protected void init() {
        super.init();
        lang = SharedUtil.getLanguage4Url(mContext);
        title = getIntent().getStringExtra("title");
        tvTitle.setText(title);
    }

    @Override
    protected void loadData() {
        super.loadData();
        if (title.equals(getString(R.string.str_use_protocol_two))) {
            getKown("termsOfService");
        } else if (title.equals(getString(R.string.str_secret_protocol_two))) {
            getKown("privacyPolicy");
        }

    }

    private void getKown(String type) {
        OkHttpUtil.getJson(Constant.URL.GetNotify, this, "lang", lang, "typeCode", type);
    }


    @OnClick({R.id.rly_back})
    public void user(View v) {
        switch (v.getId()) {
            case R.id.rly_back:
                finish();
                break;
        }
    }


    @Override
    public void onResponse(String url, String json, String session) {
        if (url.equals(Constant.URL.GetNotify)) {
            NotifyEntity notify = GsonUtil.fromJson(json, NotifyEntity.class);
            if (Constant.Int.SUC == notify.getCode()) {
                String tradeKnow = notify.getData().getRecords().get(0).getContent();
                if (!TextUtils.isEmpty(tradeKnow)) {
                    tvContent.setText(Html.fromHtml(tradeKnow));
                }

            }
        }
    }

    @Override
    public void onFailure(String url, String error) {

    }
}
