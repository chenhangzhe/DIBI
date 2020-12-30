package cn.suozhi.DiBi.home.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import cn.suozhi.DiBi.MainActivity;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.login.LoginActivity;
import cn.suozhi.DiBi.quick.uitls.FingerprintCallback;
import cn.suozhi.DiBi.quick.uitls.FingerprintVerifyManager;

public class Thefingerprint extends AppCompatActivity implements View.OnClickListener {
    private long useTime;
    private SharedPreferences time;
    private TextView paw;
    private TextView shou;
    private LinearLayout zhiwen;
    private View tv_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thefingerprint);
        paw = findViewById(R.id.tv_login_paw);
        shou = findViewById(R.id.tv_login_shou);
        zhiwen = findViewById(R.id.lin_zhiwen);
        tv_view = findViewById(R.id.tv_View);

        paw.setOnClickListener(this);
        shou.setOnClickListener(this);
        zhiwen.setOnClickListener(this);

        FingerprintVerifyManager.Builder builder = new FingerprintVerifyManager.Builder(Thefingerprint.this);
        builder.callback(fingerprintCallback)
                .fingerprintColor(ContextCompat.getColor(Thefingerprint.this, R.color.biometricprompt_color_primary1))
                .build();
        Noshoushi();
    }

    private void Noshoushi() {
        SharedPreferences password = getSharedPreferences("password", Context.MODE_PRIVATE);
        String shoushi = password.getString("shoushi", "");
        if (shoushi.equals("shoushi")){
            shou.setVisibility(View.VISIBLE);
            tv_view.setVisibility(View.VISIBLE);
        }else {
           shou.setVisibility(View.GONE);
           tv_view.setVisibility(View.GONE);
        }
    }


    private FingerprintCallback fingerprintCallback = new FingerprintCallback() {
        @Override
        public void onSucceeded() {
            Toast.makeText(Thefingerprint.this, getString(R.string.biometricprompt_verify_success), Toast.LENGTH_SHORT).show();
              MainActivity.start(Thefingerprint.this);
              finish();
        }

        @Override
        public void onFailed() {
            Toast.makeText(Thefingerprint.this, getString(R.string.biometricprompt_verify_failed), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onUsepwd() {
            Toast.makeText(Thefingerprint.this, getString(R.string.fingerprint_usepwd), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(Thefingerprint.this, getString(R.string.fingerprint_cancel), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onHwUnavailable() {
            //zhiwen.setVerticalGravity(View.GONE);
            zhiwen.setVisibility(View.GONE);
            Toast.makeText(Thefingerprint.this, getString(R.string.biometricprompt_finger_hw_unavailable), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNoneEnrolled() {
            //弹出提示框，跳转指纹添加页面
            AlertDialog.Builder builder = new AlertDialog.Builder(Thefingerprint.this);
            builder.setTitle(getString(R.string.biometricprompt_tip))
                    .setMessage(getString(R.string.biometricprompt_finger_add))
                    .setCancelable(false)
                    .setNegativeButton(getString(R.string.biometricprompt_finger_add_confirm), (new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_FINGERPRINT_ENROLL);
                            startActivity(intent);
                        }
                    }
                    ))
                    .setPositiveButton(getString(R.string.biometricprompt_cancel), (new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }
                    ))
                    .create().show();
        }

    };
    public static void start(Context context){
        Intent intent = new Intent(context, Thefingerprint.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_login_paw:
                Util.checkLoginSocket(Thefingerprint.this,"2010018");
                Intent intent = new Intent(Thefingerprint.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
            case R.id.tv_login_shou:

                Yestures.start(this);
                finish();
                break;
            case R.id.lin_zhiwen:
                FingerprintVerifyManager.Builder builder = new FingerprintVerifyManager.Builder(Thefingerprint.this);
                builder.callback(fingerprintCallback)
                        .fingerprintColor(ContextCompat.getColor(Thefingerprint.this, R.color.biometricprompt_color_primary1))
                        .build();
                break;
        }
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK ) {
            //do something.
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }
    public void finsh(){
        finish();
    }
}