package cn.suozhi.DiBi.quick.uitls;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.os.CancellationSignal;

import cn.suozhi.DiBi.AppContext;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.view.Thefingerprint;
import cn.suozhi.DiBi.home.view.Yestures;
import cn.suozhi.DiBi.login.LoginActivity;

import static com.blankj.utilcode.util.ActivityUtils.getMainActivities;
import static com.blankj.utilcode.util.ActivityUtils.startActivity;


/**
 * Android M == 6.0
 * Created by ZuoHailong on 2019/7/9.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintAndrM extends AppContext implements IFingerprint {

    private final String TAG = FingerprintAndrM.class.getName();
    private Context context;

    private static FingerprintAndrM fingerprintAndrM;
    //指纹验证框
    private static FingerprintDialog fingerprintDialog;
    //指向调用者的指纹回调
    private FingerprintCallback fingerprintCallback;

    //用于取消扫描器的扫描动作
    private CancellationSignal cancellationSignal;
    //指纹加密
    private static FingerprintManagerCompat.CryptoObject cryptoObject;
    //Android 6.0 指纹管理
    private FingerprintManagerCompat fingerprintManagerCompat;

    @Override
    public void authenticate(Activity context, VerificationDialogStyleBean bean, FingerprintCallback callback) {

        this.context = context;
        this.fingerprintCallback = callback;
        //Android 6.0 指纹管理 实例化
        fingerprintManagerCompat = FingerprintManagerCompat.from(context);

        //取消扫描，每次取消后需要重新创建新示例
        cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
            @Override
            public void onCancel() {
                fingerprintDialog.dismiss();
            }
        });

        //调起指纹验证
        fingerprintManagerCompat.authenticate(cryptoObject, 0, cancellationSignal, authenticationCallback, null);
        //指纹验证框
        fingerprintDialog = FingerprintDialog.newInstance().setActionListener(dialogActionListener).setDialogStyle(bean);
        fingerprintDialog.show(context.getFragmentManager(), TAG);
    }

    public static FingerprintAndrM newInstance() {
        if (fingerprintAndrM == null) {
            synchronized (FingerprintAndrM.class) {
                if (fingerprintAndrM == null) {
                    fingerprintAndrM = new FingerprintAndrM();
                }
            }
        }
        //指纹加密，提前进行Cipher初始化，防止指纹认证时还没有初始化完成
        try {
            cryptoObject = new FingerprintManagerCompat.CryptoObject(new CipherHelper().createCipher());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fingerprintAndrM;
    }

    /**
     * 指纹验证框按键监听
     */
    private FingerprintDialog.OnDialogActionListener dialogActionListener = new FingerprintDialog.OnDialogActionListener() {
        @Override
        public void onUsepwd() {
            if (fingerprintCallback != null)
               // fingerprintCallback.onUsepwd();
                Yestures.start(context);
        }

        @Override
        public void onCancle() {//取消指纹验证，通知调用者
            if (fingerprintCallback != null)
                fingerprintCallback.onCancel();
        }

        @Override
        public void onDismiss() {//验证框消失，取消指纹验证
            if (cancellationSignal != null && !cancellationSignal.isCanceled())
                cancellationSignal.cancel();
        }
    };

    /**
     * 指纹验证结果回调
     */
    private FingerprintManagerCompat.AuthenticationCallback authenticationCallback = new FingerprintManagerCompat.AuthenticationCallback() {
        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            super.onAuthenticationError(errMsgId, errString);
            //errMsgId==5时，在OnDialogActionListener的onCancle回调中处理；！=5的报错，才需要显示在指纹验证框中。
            if (errMsgId != 5){
                fingerprintDialog.setTip(errString.toString(), R.color.biometricprompt_color_FF5555);
                //Toast.makeText(context, "1111111111111", Toast.LENGTH_SHORT).show();
//                Util.checkLoginSocket(context,"2010018");
//                Intent intent = new Intent(context, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);
//                Intent intent = new Intent(context, LoginActivity.class);
//                context.startActivity(intent);
            }

        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            super.onAuthenticationHelp(helpMsgId, helpString);
            fingerprintDialog.setTip(helpString.toString(), R.color.biometricprompt_color_FF5555);
            Toast.makeText(context, helpString.toString(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            fingerprintDialog.setTip(context.getString(R.string.biometricprompt_verify_success), R.color.biometricprompt_color_82C785);
            fingerprintCallback.onSucceeded();
            fingerprintDialog.dismiss();
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
            fingerprintDialog.setTip(context.getString(R.string.biometricprompt_verify_failed), R.color.biometricprompt_color_FF5555);
            fingerprintCallback.onFailed();
        }
    };

    /*
     * 在 Android Q，Google 提供了 Api BiometricManager.canAuthenticate() 用来检测指纹识别硬件是否可用及是否添加指纹
     * 不过尚未开放，标记为"Stub"(存根)
     * 所以暂时还是需要使用 Andorid 6.0 的 Api 进行判断
     * */
    @Override
    public boolean canAuthenticate(Context context, FingerprintCallback fingerprintCallback) {
        /*
         * 硬件是否支持指纹识别
         * */
        if (!FingerprintManagerCompat.from(context).isHardwareDetected()) {
            fingerprintCallback.onHwUnavailable();
            return false;
        }
        //是否已添加指纹
        if (!FingerprintManagerCompat.from(context).hasEnrolledFingerprints()) {
            fingerprintCallback.onNoneEnrolled();
            return false;
        }
        return true;
    }

}
