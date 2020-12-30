package cn.suozhi.DiBi.common.util;


import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import cn.suozhi.DiBi.R;

public class CodeStrUtil {


    public static void showToast(FragmentActivity context, String code) {
        showToastHint(context, context.getString(getServerCodeMsg(code)));
    }

    public static void showToastFail(FragmentActivity context, String code) {
        showToastHintFail(context, context.getString(getServerCodeMsg(code)));
    }

    /**
     * 显示提示语
     *
     * @param hint
     */
    public static void showToastHint(FragmentActivity context, String hint) {
        View toast = ToastUtil.toastView(context, R.layout.toast_icon_text,
                R.mipmap.tick_white_circle, hint);
        ToastUtil.initToast(context, toast, Toast.LENGTH_SHORT);
    }

    /**
     * 显示提示语
     *
     * @param hint
     */
    public static void showToastHintFail(FragmentActivity context, String hint) {
        View toast = ToastUtil.toastView(context, R.layout.toast_icon_text,
                R.mipmap.toast_icon_warning, hint);
        ToastUtil.initToast(context, toast, Toast.LENGTH_SHORT);
    }

    public static int getCodeHint(String code) {
        int hintId = 0;
        switch (code) {
            case "601":
                hintId = R.string.str_code_hint_601;
                break;
            case "602":
                hintId = R.string.str_code_hint_602;
                break;
            case "603":
                hintId = R.string.str_code_hint_603;
                break;
            case "604":
                hintId = R.string.str_code_hint_604;
                break;
            case "605":
                hintId = R.string.str_code_hint_605;
                break;
            case "606":
                hintId = R.string.str_code_hint_606;
                break;
            case "607":
                hintId = R.string.str_code_hint_607;
                break;
            case "608":
                hintId = R.string.str_code_hint_608;
                break;
            case "609":
                hintId = R.string.str_code_hint_609;
                break;
            case "610":
                hintId = R.string.str_code_hint_610;
                break;
            case "611":
                hintId = R.string.str_code_hint_611;
                break;
            case "612":
                hintId = R.string.str_code_hint_612;
                break;
            case "613":
                hintId = R.string.str_code_hint_613;
                break;
            case "614":
                hintId = R.string.str_code_hint_614;
                break;
            case "615":
                hintId = R.string.str_code_hint_615;
                break;
            case "616":
                hintId = R.string.str_code_hint_616;
                break;
        }
        return hintId;
    }

    /**
     * 获取服务器返回码
     *
     * @param code
     * @return
     */
    public static int getServerCodeMsg(String code) {
        int hintId = 0;
        switch (code) {
            case "12001":
                hintId = R.string.code12001;
                break;
            case "1010001":
                hintId = R.string.code1010001;
                break;
            case "12002":
                hintId = R.string.code12002;
                break;
            case "1010002":
                hintId = R.string.code1010002;
                break;
            case "1010003":
                hintId = R.string.code1010003;
                break;
            case "1010004":
                hintId = R.string.code1010004;
                break;
            case "1010011":
                hintId = R.string.code1010011;
                break;
            case "1010010":
                hintId = R.string.code1010010;
                break;
            case "1020001":
                hintId = R.string.code1020001;
                break;
            case "1020002":
                hintId = R.string.code1020002;
                break;
            case "1020003":
                hintId = R.string.code1020003;
                break;
            case "1020004":
                hintId = R.string.code1020004;
                break;
            case "1020005":
                hintId = R.string.code1020005;
                break;
            case "1020006":
                hintId = R.string.code1020006;
                break;
            case "1030005":
                hintId = R.string.code1030005;
                break;
            case "12000":
                hintId = R.string.code12000;
                break;
        }

        return hintId;
    }
}
