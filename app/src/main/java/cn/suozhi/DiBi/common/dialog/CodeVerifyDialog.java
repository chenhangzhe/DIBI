package cn.suozhi.DiBi.common.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.ObjectEntity;

/**
 * 安全校验 - 短信 、 邮箱
 * 点击确认按钮或键盘上按钮，回调时View为etCode
 */
public class CodeVerifyDialog extends BaseDialog implements View.OnClickListener,
        TextView.OnEditorActionListener, OkHttpUtil.OnDataListener {

    private EditText etCode;
    private TextView tvSend, tveCode;

    private boolean isEmail;
    private String token, area, account, type;

    private final int retryLimit = Constant.Int.RetryTime;//重试秒数上限
    private int timeNum = retryLimit;//获取验证码倒计时

    private Handler handler = new Handler();
    private Gson gson = new GsonBuilder().create();

    public static CodeVerifyDialog newInstance(boolean isEmail, String token, String area, String account, String type) {
        CodeVerifyDialog dialog = new CodeVerifyDialog();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isEmail", isEmail);
        bundle.putString("token", token);
        bundle.putString("area", area);
        bundle.putString("account", account);
        bundle.putString("type", type);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);//取消标题显示
        getDialog().setCanceledOnTouchOutside(false);
        Window window = getDialog().getWindow();
        View v = inflater.inflate(R.layout.dialog_code_verify, window.findViewById(android.R.id.content), false);

        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//必须设置，否则不能全屏
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.animTranslateBottom);//进入退出动画

        v.findViewById(R.id.iv_dgCvClose).setOnClickListener(this);
        TextView tvType = v.findViewById(R.id.tv_dgCvType);
        TextView tvAccount = v.findViewById(R.id.tv_dgCvAccount);
        etCode = v.findViewById(R.id.et_dgCvCode);
        tvSend = v.findViewById(R.id.tv_dgCvSend);
        tveCode = v.findViewById(R.id.et_dgCvCodeError);
        v.findViewById(R.id.tv_dgCvConfirm).setOnClickListener(this);
        Util.editListenerError(etCode, tveCode);
        etCode.setOnEditorActionListener(this);
        tvSend.setOnClickListener(this);
        etCode.requestFocus();
        Util.showKeyboard(etCode);

        Bundle arg = getArguments();
        isEmail = arg.getBoolean("isEmail", true);
        token = arg.getString("token");
        area = arg.getString("area");
        account = arg.getString("account");
        type = arg.getString("type");

        tvType.setText(isEmail ? R.string.emailCode : R.string.smsCode);
        tvAccount.setText(Util.addStarInMiddle(account));
        tvSend.setSelected(!TextUtils.isEmpty(account));

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_dgCvClose:
                dismiss();
                break;
            case R.id.tv_dgCvSend:
                sendCode();
                break;
            case R.id.tv_dgCvConfirm:
                if (etCode.length() < 6) {
                    tveCode.setText(R.string.inputNum6);
                    etCode.requestFocus();
                    etCode.setSelection(etCode.length());
                } else {
                    if (onItemClickListener != null) {
                        etCode.setTag(etCode.getText().toString());
                        onItemClickListener.onItemClick(etCode);
                    }
                    dismiss();
                }
                break;
        }
    }

    private void sendCode() {
        if (tvSend.isSelected()) {
            tvSend.setSelected(false);
            if (isEmail) {
                OkHttpUtil.postJsonToken(Constant.URL.SendEmail, token, this, "email", account,
                        "type", type);
            } else {
                OkHttpUtil.postJsonToken(Constant.URL.SendPhone, token, this, "areaCode", area,
                        "phone", account, "type", type);
            }
            //设置XX秒后重试
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (handler != null && tvSend != null) {
                        tvSend.setText(timeNum + " S");
                        if (timeNum > 0) {
                            handler.postDelayed(this, 1000);
                            timeNum--;
                        } else {
                            tvSend.setText(R.string.resend);
                            timeNum = retryLimit;
                            tvSend.setSelected(true);
                        }
                    }
                }
            });
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_GO ||
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            if (etCode.length() >= 6) {
                if (onItemClickListener != null) {
                    v.setTag(etCode.getText().toString());
                    onItemClickListener.onItemClick(v);
                }
                dismiss();
            } else {
                Util.hideKeyboard(etCode);
            }
            return true;
        }
        return false;
    }

    @Override
    public void dismiss() {
        handler = null;
        Util.hideKeyboard(etCode);
        super.dismiss();
    }

    @Override
    public void onResponse(String url, String json, String session) {
        ObjectEntity code = gson.fromJson(json, ObjectEntity.class);
        ToastUtil.initToast(getActivity(), Util.getCodeText(getActivity(),
                code.getCode(), code.getMsg()));
        if (Constant.Int.SUC != code.getCode()) {
            upTimeNum();
        }
    }

    @Override
    public void onFailure(String url, String error) {
        upTimeNum();
    }

    private void upTimeNum() {
        if (timeNum < retryLimit) {//倒计时中
            timeNum = 0;
        }
    }
}
