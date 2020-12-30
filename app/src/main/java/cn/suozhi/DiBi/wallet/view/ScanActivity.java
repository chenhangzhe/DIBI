package cn.suozhi.DiBi.wallet.view;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.ToolbarUtil;

/**
 * 功能描述：扫一扫界面
 */
public class ScanActivity extends BaseActivity implements QRCodeView.Delegate {

    @BindView(R.id.toolbar_center)
    public Toolbar toolbar;
    @BindView(R.id.zxingview)

    ZXingView mZXingView;
    private int fromType = 1;//从哪个页面跳转
    boolean isOpen = false;//一开始散光灯是关闭的


    @Override
    protected int getViewResId() {
        return R.layout.activity_scanne;
    }


    @Override
    protected void init() {
        super.init();
        ToolbarUtil.initToolbar(toolbar, getString(R.string.str_scan), v -> onBackPressed());
        fromType = getIntent().getIntExtra("fromType", 1);
        mZXingView.setDelegate(this);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {//扫码成功回调 摄像头扫码时只要回调了该方法 result 就一定有值，不会为 null。解析本地图片或 Bitmap 时 result 可能为 null

        if (!TextUtils.isEmpty(result)) {
            //扫描结果不为空时
            Intent intent = new Intent();
            intent.putExtra("result", result);
            setResult(RESULT_OK, intent);//设置resultCode
            finish();
        } else {
            finish();
            ToastUtil.initToast(ScanActivity.this, "这个图片不是正确的二维码图片");
        }

    }

    /**
     * 处理打开相机出错
     */
    @Override
    public void onScanQRCodeOpenCameraError() {


    }


    @Override
    protected void onStart() {
        super.onStart();

        if (mZXingView != null) {

            mZXingView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
//        mZXingView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT); // 打开前置摄像头开始预览，但是并未开始识别
            //  mZXingView.startSpotAndShowRect(); // 显示扫描框，并且延迟0.5秒后开始识别
            mZXingView.startSpotDelay(300); // 显示扫描框，并且延迟300毫秒后开始识别

        }

    }

    @Override
    protected void onStop() {

        if (mZXingView != null) {
            mZXingView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        }
        super.onStop();
    }


    @Override
    protected void onDestroy() {

        if (mZXingView != null) {
            mZXingView.onDestroy(); // 销毁二维码扫描控件
        }
        super.onDestroy();
    }


    @OnClick({R.id.img_light})
    public void OnClick(View view) {

        switch (view.getId()) {

            case R.id.img_light:
                if (!isOpen) {
                    isOpen = true;
                    mZXingView.openFlashlight(); // 打开闪光灯
                } else {
                    isOpen = false;
                    mZXingView.closeFlashlight(); // 关闭闪光灯
                }

                break;


        }

    }

    //    @Override
//    public void onRightClick(View v) {
//        super.onRightClick(v);
//
//        XXPermissions.with(ScanActivity.this)
//                //.constantRequest() //可设置被拒绝后继续申请，直到用户授权或者永久拒绝
//                //.permission(Permission.SYSTEM_ALERT_WINDOW, Permission.REQUEST_INSTALL_PACKAGES) //支持请求6.0悬浮窗权限8.0请求安装权限
//                .permission(Manifest.permission.READ_EXTERNAL_STORAGE) //不指定权限则自动获取清单中的危险权限
//                .request(new OnPermission() {
//
//                    @Override
//                    public void hasPermission(List<String> granted, boolean isAll) {
//                        if (isAll) {
//                            //跳转扫描
//                            /*
//                从相册选取二维码图片，这里为了方便演示，使用的是
//                https://github.com/bingoogolapple/BGAPhotoPicker-Android
//                这个库来从图库中选择二维码图片，这个库不是必须的，你也可以通过自己的方式从图库中选择图片
//                 */
//
//                            PicSelrctorHandler.selePicScan(ScanActivity.this);
//
//
//                        } else {
//
//                            ToastUtils.show(getResources().getString(R.string.quanxian_01));
//                        }
//                    }
//
//                    @Override
//                    public void noPermission(List<String> denied, boolean quick) {
//                        if (quick) {
//                            ToastUtils.show(getResources().getString(R.string.quanxian_02));
//                            //如果是被永久拒绝就跳转到应用权限系统设置页面
//                            XXPermissions.gotoPermissionSettings(ScanActivity.this);
//                        } else {
//                            ToastUtils.show(getResources().getString(R.string.quanxian_03));
//                        }
//                    }
//                });
//
//    }
//
    private List<LocalMedia> selectList = new ArrayList<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
                    for (LocalMedia media : selectList) {
                        Log.e("errow", "图片-----》" + media.getPath());
                        // 本来就用到 QRCodeView 时可直接调 QRCodeView 的方法，走通用的回调
                        mZXingView.decodeQRCode(media.getPath());
                    }

                    break;
            }

        }

    }


}
