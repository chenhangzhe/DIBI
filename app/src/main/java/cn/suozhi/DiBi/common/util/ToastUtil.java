package cn.suozhi.DiBi.common.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import androidx.fragment.app.FragmentActivity;
import cn.suozhi.DiBi.R;

/**
 * Toast的工具类  - 无权限时最多弹窗3次提醒跳转设置页面
 */
public class ToastUtil {

    private static Toast toast;
    private static int countTime;
    private static Handler handler;

    public static void initToast(FragmentActivity activity, String string) {
        initToast(activity, string, Toast.LENGTH_SHORT);
    }

    public static void initToast(FragmentActivity activity, int stringId) {
        initToast(activity, stringId, Toast.LENGTH_SHORT);
    }

    public static void initToast(FragmentActivity activity, int stringId, int duration) {
        initToast(activity, activity.getString(stringId), duration);
    }

    @SuppressLint("ShowToast")
    public static void initToast(FragmentActivity activity, String string, int duration) {
        if (Util.permCheckNotify(activity.getApplicationContext())) {
            if (toast == null) {
                toast = Toast.makeText(activity.getApplicationContext(), string, duration);
            } else {
                toast.setText(string);
                toast.setDuration(duration);
            }
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            postClear(duration);
        } else {
            View view = activity.getWindow().findViewById(android.R.id.content);
            if (view != null) {
                showSnack(view, string, duration == Toast.LENGTH_SHORT ? Snackbar.LENGTH_SHORT : Snackbar.LENGTH_LONG);
            }
        }
    }

    /*private static boolean checkPerm(FragmentActivity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return true;
        }
        final Context context = activity.getApplicationContext();
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
        boolean enable = NotificationManagerCompat.from(context).areNotificationsEnabled();
        int count = SharedUtil.getInt(context, "Constant", "toast",
                0);
        if (enable && count != 0) {
            SharedUtil.putInt(context, "Constant", "toast", 0);
        }
        if (!enable && count < 3) {
            SharedUtil.putInt(context, "Constant", "toast", count + 1);

            ConfirmDialog.newInstance(activity.getString(R.string.toastPermTips),
                        activity.getString(R.string.cancel), activity.getString(R.string.set))
                    .setOnItemClickListener(v -> {
                        if (v.getId() == R.id.tv_dgcConfirm) {
                            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName())
                                    .putExtra(Settings.EXTRA_CHANNEL_ID, context.getApplicationInfo().uid);
                            context.startActivity(intent);
                        }
                    })
                    .show(activity);
        }
        return enable;
    }*/

    public static void showSnack(View v, String string, int duration) {
        Snackbar snack = Snackbar.make(v, string, duration);
        initSnack(snack.getView());
        snack.show();
    }

    private static void initSnack(View view) {
        //设置居中
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        view.setLayoutParams(lp);

        view.setBackgroundResource(R.drawable.sp2_solid_bk_0a);
        TextView tv = view.findViewById(R.id.snackbar_text);
        tv.setGravity(Gravity.CENTER);
    }

    /**
     * Toasts弹文字和弹View不能混用
     */
    public static void initToast(FragmentActivity activity, View v, int duration) {
        if (Util.permCheckNotify(activity.getApplicationContext())) {
            Toast toast = new Toast(activity.getApplicationContext());
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(duration);
            toast.setView(v);
            toast.show();
        } else {
            if (v instanceof ViewGroup) {
                String text = null;
                for (int i = 0; i < ((ViewGroup) v).getChildCount(); i++) {
                    View child = ((LinearLayout) v).getChildAt(i);
                    if (child instanceof TextView) {
                        text = ((TextView) child).getText().toString();
                        break;
                    }
                }
                if (!TextUtils.isEmpty(text)) {
                    View view = activity.getWindow().findViewById(android.R.id.content);
                    if (view != null) {
                        showSnack(view, text, duration == Toast.LENGTH_SHORT ? Snackbar.LENGTH_SHORT : Snackbar.LENGTH_LONG);
                    }
                }
            }
        }
    }

    @SuppressLint("ShowToast")
    public static void initToastNormal(FragmentActivity activity, String string) {
        if (Util.permCheckNotify(activity.getApplicationContext())) {
            if (toast == null) {
                toast = Toast.makeText(activity.getApplicationContext(), string, Toast.LENGTH_SHORT);
            } else {
                toast.setText(string);
                toast.setDuration(Toast.LENGTH_SHORT);
            }
            toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0,
                    Util.dp2px(activity.getApplicationContext(), 64));
            toast.show();
            postClear(Toast.LENGTH_SHORT);
        }
    }

    private static void postClear(int duration) {
        if (handler == null) {
            handler = new Handler();
        } else {
            handler.removeCallbacks(r);
        }
        countTime = duration == Toast.LENGTH_SHORT ? 3 : 5;
        handler.post(r);
    }

    static Runnable r = new Runnable() {
        @Override
        public void run() {
            if (handler != null) {
                if (countTime <= 0) {
                    toast = null;
                } else {
                    countTime--;
                    handler.postDelayed(this, 1000);
                }
            }
        }
    };

    public static View toastView(Context context, int viewId, int iconId, String text) {
        View v = LayoutInflater.from(context).inflate(viewId, null, false);
        if (v instanceof LinearLayout) {
            for (int i = 0; i < ((LinearLayout) v).getChildCount(); i++) {
                View child = ((LinearLayout) v).getChildAt(i);
                if (child instanceof ImageView) {
                    ((ImageView) child).setImageResource(iconId);
                } else if (child instanceof TextView) {
                    ((TextView) child).setText(text);
                }
            }
        }
        return v;
    }
}
