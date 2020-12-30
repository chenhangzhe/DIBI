package cn.suozhi.DiBi.home.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.dialog.AuthDialog;
import cn.suozhi.DiBi.common.util.GsonUtil;
import cn.suozhi.DiBi.common.util.MD5Util;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.ResUtils;
import cn.suozhi.DiBi.home.model.ObjectEntity;

public class QuickDiaLog extends BaseDialog implements View.OnClickListener, OkHttpUtil.OnDataListener {
    private static final int REQUEST_CODE = 1;
    private String  token ;
    private String bottom;
    private EditText content;
    private String paw;
    private SharedPreferences preferences;
    private SharedPreferences spref;

    public static QuickDiaLog newInstance(String token, String bottom) {
        QuickDiaLog dialog = new QuickDiaLog();
        Bundle bundle = new Bundle();
        bundle.putString("token", token);
        bundle.putString("bottom",bottom);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);//取消标题显示
        Window window = getDialog().getWindow();
        View v = inflater.inflate(R.layout.dialog_is_code, window.findViewById(android.R.id.content), false);

        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//必须设置，否则不能全屏
        window.setLayout(ResUtils.getDimensionPixelSize(R.dimen.auth_dialog_width), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        window.setWindowAnimations(R.style.animTranslateBottom);//进入退出动画
        initView(v);
        initData();

        return v;

    }

    private void initView(View v) {
         content = v.findViewById(R.id.et_content);
        TextView cancel = v.findViewById(R.id.quck_cancel);
        TextView confirm = v.findViewById(R.id.quck_confirm);

        cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);
        preferences =getActivity().getSharedPreferences("password", Context.MODE_PRIVATE);
        spref = getActivity().getSharedPreferences("spref", Context.MODE_PRIVATE);
    }

    private void initData() {
        Bundle arg = getArguments();
        token = arg.getString("token");
        bottom = arg.getString("bottom");


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.quck_cancel:
                onItemClickListener.onItemClick(v);
                dismiss();
                break;
            case R.id.quck_confirm:
               // dismiss();
                onItemClickListener.onItemClick(v);
                paw = content.getText().toString().trim();
                sendCode();

                break;
        }
    }
    //d41d8cd98f00b204e9800998ecf8427e
    //dc483e80a7a0bd9ef71d8cf973673924
    private void sendCode() {
        if (!TextUtils.isEmpty(paw)){
            try {
                OkHttpUtil.postJsonToken(Constant.URL.Quck,token,this,"password", MD5Util.encodeByMD5(paw));
                Log.i("TAG", "sendCode: "+paw);
                Log.i("TAG", "sendCode: "+MD5Util.encodeByMD5(paw)+paw);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(getActivity(), R.string.str_edit_pas, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onResponse(String url, String json, String session) {
        Log.i("TAG", "onResponse: "+json);
        ObjectEntity o = GsonUtil.fromJson(json, ObjectEntity.class);
        if (Constant.Int.SUC == o.getCode()){
            if (bottom.equals("shoushi")){
                // Yestures.start(getActivity());
                preferences.edit().putString("shoushi","shoushi").commit();
                Intent intent = new Intent(getActivity(), Yestures.class);
                startActivityForResult(intent,REQUEST_CODE);
                dismiss();

            }else if (bottom.equals("zhiwen")){
                getActivity().finish();
                Toast.makeText(getContext(), R.string.thefingerprint, Toast.LENGTH_SHORT).show();
                preferences.edit().putString("zhiwen","zhiwen").commit();
                Intent intent = new Intent(getActivity(), SecurityActivity.class);
                startActivity(intent);
                //getActivity().setResult(1);
               dismiss();
            }else if (bottom.equals("shoushi1")){

                preferences.edit().putString("shoushi","").commit();
                spref.edit().putString("password", "").commit();
                spref.edit().putString("tmp_password", "").commit();
                spref.edit().putInt("error_count", 5).commit();
                Intent intent = new Intent(getActivity(), SecurityActivity.class);
                startActivity(intent);
                getActivity().finish();
                dismiss();
            }else if (bottom.equals("zhiwen1")){
                preferences.edit().putString("zhiwen","").commit();
                Intent intent = new Intent(getActivity(), SecurityActivity.class);
                startActivity(intent);
                getActivity().finish();
                dismiss();
            }


        }else {
            Toast.makeText(getActivity(), o.getMsg(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onFailure(String url, String error) {

    }
}
