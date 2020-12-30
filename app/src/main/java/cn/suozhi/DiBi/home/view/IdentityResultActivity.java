package cn.suozhi.DiBi.home.view;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.security.realidentity.RPEventListener;
import com.alibaba.security.realidentity.RPResult;
import com.alibaba.security.realidentity.RPVerify;
import com.alibaba.security.rp.RPSDK;

import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.util.AppUtil;
import cn.suozhi.DiBi.common.util.JsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.ToolbarUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.FaceTokenEntity;
import cn.suozhi.DiBi.home.model.ObjectEntity;
import cn.suozhi.DiBi.home.model.UserEntity;

/**
 * 认证结果
 */
public class IdentityResultActivity extends BaseActivity implements OkHttpUtil.OnDataListener, RPSDK.RPCompletedListener {

    @BindView(R.id.toolbar_text)
    public Toolbar toolbar;

    @BindView(R.id.iv_irIcon)
    public ImageView ivIcon;
    @BindView(R.id.tv_irTitle)
    public TextView tvTitle;
    @BindView(R.id.tv_irTips)
    public TextView tvTips;
    @BindView(R.id.tv_irNext)
    public TextView tvNext;

    private int level, status, type;
    private String bizId;
    private LoadingDialog loadingDialog;

    @Override
    protected int getViewResId() {
        return R.layout.activity_identity_result;
    }

    @Override
    protected void init() {
        RPVerify.init(mContext);
        Intent intent = getIntent();
        level = intent.getIntExtra("level", 0);
        status = intent.getIntExtra("status", 0);
        type = intent.getIntExtra("type", 1);
        String reason = intent.getStringExtra("reason");

        String title;
        int ls = AppUtil.getIdentify(level, status);
        switch (ls) {
            case 2:
                if (type == 1) {//国内
                    title = getString(R.string.juniorIdentify);
                    initUI(R.mipmap.identify_suc, R.string.juniorTitle, R.string.juniorTips, R.string.juniorNext);
                } else {
                    title = getString(R.string.idLevel1);
                    initUI(R.mipmap.identify_suc, R.string.c1Title, R.string.c1Tips, R.string.c1Next);
                }
                break;
            case 3:
                if (type == 1) {//国内
                    title = getString(R.string.juniorIdentify);
                    initUI(R.mipmap.identify_suc, R.string.juniorTitle, R.string.juniorTips, R.string.juniorNext);
                } else {
                    title = getString(R.string.idLevel2);
                    initUI(R.mipmap.identify_loading, R.string.c2lTitle, R.string.c2lTips, 0);
                }
                break;
            case 4:
                if (type == 1) {//国内
                    title = getString(R.string.juniorIdentify);
                    initUI(R.mipmap.identify_suc, R.string.juniorTitle, R.string.juniorTips, R.string.juniorNext);
                } else {
                    title = getString(R.string.idLevel2);
                    String tips = TextUtils.isEmpty(reason) ? getString(R.string.c2fTips) : reason;
                    initUI(R.mipmap.identify_fail, R.string.c2fTitle, tips, R.string.c2fNext);
                }
                break;
            case 5:
                if (type == 1) {//国内
                    title = getString(R.string.juniorIdentify);
                    initUI(R.mipmap.identify_suc, R.string.juniorTitle, R.string.juniorTips, R.string.juniorNext);
                } else {
                    title = getString(R.string.idLevel2);
                    initUI(R.mipmap.identify_suc, R.string.c2sTitle, R.string.c2sTips, R.string.c2sNext);
                }
                break;
            case 6:
                if (type == 1) {//国内
                    title = getString(R.string.seniorIdentify);
                    initUI(R.mipmap.identify_suc, R.string.seniorTitle, 0, 0);
                } else {
                    title = getString(R.string.idLevel3);
                    initUI(R.mipmap.identify_suc, R.string.c3Title, 0, 0);
                }
                break;
            default:
                title = "";
                break;
        }
        ToolbarUtil.initToolbar(toolbar, title, v -> onBackPressed());
    }

    private void initUI(int icon, int title, int tips, int next) {
        initUI(icon, title, tips > 0 ? getString(tips) : null, next);
    }

    private void initUI(int icon, int title, String tips, int next) {
        ivIcon.setImageResource(icon);
        if (title > 0) {
            tvTitle.setText(title);
        }
        tvTips.setText(tips);
        if (next > 0) {
            tvNext.setText(next);
            tvNext.setVisibility(View.VISIBLE);
        } else {
            tvNext.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.tv_irNext)
    public void next(View v) {
        int ls = AppUtil.getIdentify(level, status);
        switch (ls) {
            case 2:
            case 4:
                if (type == 1) {
                    showLoading();
                    getInfo();
                } else {
                    //Toast.makeText(mContext, "111111111111111", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, C2Activity.class)
                            .putExtra("type", type));
                    finish();
                }
                break;
            case 3:
                if (type == 1) {
                    showLoading();
                    getInfo();
                }
                break;
            case 5:
                if (type == 1) {
                    showLoading();
                    getInfo();
                } else {
                    startActivity(new Intent(this, C3Activity.class));
                }
                break;
        }
    }

    private void getInfo() {
        OkHttpUtil.getJsonHeader2(Constant.URL.GetInfo, SharedUtil.getToken(this),
                SharedUtil.getLanguage4Url(this), this);
    }

    @Override
    public void onResponse(String url, String json, String session) {
        Log.e("loge", "Face: " + json);
        switch (url) {
            case Constant.URL.GetInfo:
                UserEntity mine = JsonUtil.fromJsonO(json, UserEntity.class);
                if (mine == null) {
                    dismissLoading();
                    break;
                }
                if (Constant.Int.SUC == mine.getCode()) {
                    UserEntity.DataEntity.InfoEntity d = mine.getData().getInfo();
                    OkHttpUtil.getJsonHeader2(Constant.URL.GetFaceToken, SharedUtil.getToken(this),
                            SharedUtil.getLanguage4Url(this), this, "bizId", d.getUserId() + d.getIdNumber());
                } else {
                    dismissLoading();
                    ToastUtil.initToast(this, Util.getCodeText(this,
                            mine.getCode(), mine.getMsg()));
                    Util.checkLogin(this, mine.getCode());
                }
                break;
            case Constant.URL.GetFaceToken:
                FaceTokenEntity face = JsonUtil.fromJsonO(json, FaceTokenEntity.class);
                dismissLoading();
                if (face == null) {
                    break;
                }
                if (Constant.Int.SUC == face.getCode()) {
                    bizId = face.getData().getBizId();
                   // Toast.makeText(mContext, "11111111111111", Toast.LENGTH_SHORT).show();
                    RPVerify.start(IdentityResultActivity.this, face.getData().getVerifyToken(), new RPEventListener() {
                        @Override
                        public void onFinish(RPResult rpResult, String s, String s1) {
                            if (rpResult == RPResult.AUDIT_PASS) {
                                Log.e(TAG, "onAuditResult: 通过");
                                OkHttpUtil.getJsonHeader2(Constant.URL.FaceVerify, SharedUtil.getToken(IdentityResultActivity.this),
                                        SharedUtil.getLanguage4Url(IdentityResultActivity.this), IdentityResultActivity.this, "bizId", bizId);
                            } else if (rpResult == RPResult.AUDIT_FAIL) {
                                // 认证不通过。建议接入方调用实人认证服务端接口DescribeVerifyResult来获取最终的认证状态，并以此为准进行业务上的判断和处理。
                                // do something
                                Log.e(TAG, "onAuditResult: 失败");
                                ToastUtil.initToast(IdentityResultActivity.this, R.string.code_1);
                            } else if (rpResult == RPResult.AUDIT_NOT) {
                                // 未认证，具体原因可通过code来区分（code取值参见错误码说明），通常是用户主动退出或者姓名身份证号实名校验不匹配等原因，导致未完成认证流程。
                                // do something
                                somehting(s);
                            }
                        }
                    });
                } else {
                    ToastUtil.initToast(this, Util.getCodeText(this,
                            face.getCode(), face.getMsg()));
                    Util.checkLogin(this, face.getCode());
                }
                break;
            case Constant.URL.FaceVerify:
                ObjectEntity verify = JsonUtil.fromJson(json, ObjectEntity.class);
                dismissLoading();
                if (verify == null) {
                    break;
                }
                ToastUtil.initToast(this, Util.getCodeText(this,
                        verify.getCode(), verify.getMsg()));
                if (Constant.Int.SUC == verify.getCode()) {
                    startActivity(new Intent(this, IdentityResultActivity.class)
                            .putExtra("level", 3)
                            .putExtra("type", type));
                    finish();
                } else {
                    Util.checkLogin(this, verify.getCode());
                }
                break;
        }
    }

    private void somehting(String s) {
        Log.e(TAG, "onAuditResult: 未认证");
        ToastUtil.initToast(this, Util.getStringInResId(IdentityResultActivity.this,
                "notIdentify" + s.replace("-", "_"), getString(R.string.notIdentify)));
    }

    @Override
    public void onFailure(String url, String error) {
        dismissLoading();
        Util.saveLog(this, url, error);
    }

    @Override
    protected void showLoading() {
        loadingDialog = LoadingDialog.newInstance();
        loadingDialog.show(this);
    }

    @Override
    protected void dismissLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onAuditResult(RPSDK.AUDIT audit, String code) {
//        if (audit == RPSDK.AUDIT.AUDIT_PASS) {
//            Log.e(TAG, "onAuditResult: 通过");
//            OkHttpUtil.getJsonHeader2(Constant.URL.FaceVerify, SharedUtil.getToken(this),
//                    SharedUtil.getLanguage4Url(this), this, "bizId", bizId);
//        } else if (audit == RPSDK.AUDIT.AUDIT_FAIL) {
//            Log.e(TAG, "onAuditResult: 失败");
//            ToastUtil.initToast(this, R.string.code_1);
//        } else if (audit == RPSDK.AUDIT.AUDIT_NOT) {
//            Log.e(TAG, "onAuditResult: 未认证");
//            ToastUtil.initToast(this, Util.getStringInResId(this,
//                    "notIdentify" + code.replace("-", "_"), getString(R.string.notIdentify)));
//        }
    }
}
