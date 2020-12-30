package cn.suozhi.DiBi.home.view;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.dialog.ChooseSingleDialog;
import cn.suozhi.DiBi.common.dialog.ConfirmDialog;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.util.Base64Util;
import cn.suozhi.DiBi.common.util.BitMapUtil;
import cn.suozhi.DiBi.common.util.FileUtil;
import cn.suozhi.DiBi.common.util.GlideUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.ToolbarUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.ObjectEntity;
import cn.suozhi.DiBi.home.model.StringEntity;

/**
 * C2认证
 */
public class C2Activity extends BaseActivity implements OkHttpUtil.OnDataListener {

    @BindView(R.id.toolbar_text)
    public Toolbar toolbar;

    @BindViews({R.id.iv_c20, R.id.iv_c21, R.id.iv_c22})
    public List<ImageView> ivs;//图片
    @BindViews({R.id.iv_c20Ground, R.id.iv_c21Ground, R.id.iv_c22Ground})
    public List<ImageView> ivg;//背景 - 选择图片后隐藏
    @BindViews({R.id.tv_c20Name, R.id.tv_c21Name, R.id.tv_c22Name})
    public List<TextView> tvs;//下方文字
    @BindViews({R.id.iv_c20Tips, R.id.iv_c21Tips, R.id.iv_c22Tips})
    public List<TextView> tips;//上传中提示
    @BindViews({R.id.iv_c20Delete, R.id.iv_c21Delete, R.id.iv_c22Delete})
    public List<ImageView> del;//删除

    private int type;//1 身份证 2 护照
    private String basePath;
    private String[] paths;//拍照路径
    private String[] urls = new String[3];//上传成功链接
    private boolean[] uploading = {false, false, false};

    private LoadingDialog loadingDialog;
    private Gson gson = new GsonBuilder().create();

    @Override
    protected int getViewResId() {
        return R.layout.activity_c2;
    }

    @Override
    protected void init() {
        ToolbarUtil.initToolbar(toolbar, getString(R.string.idLevel2), v -> onBackPressed());

        type = getIntent().getIntExtra("type", 1);
        boolean foreign = type == 2;
        tvs.get(0).setText(foreign ? R.string.c2PassName0 : R.string.c2CardName0);
        tvs.get(1).setText(foreign ? R.string.c2PassName1 : R.string.c2CardName1);
        tvs.get(2).setText(foreign ? R.string.c2PassName2 : R.string.c2CardName2);
        ivg.get(0).setImageResource(foreign ? R.mipmap.passport0 : R.mipmap.id_card0);
        ivg.get(1).setImageResource(foreign ? R.mipmap.passport1 : R.mipmap.id_card1);
        ivg.get(2).setImageResource(foreign ? R.mipmap.passport2 : R.mipmap.id_card2);

        initPath();
    }

    private void initPath() {
        basePath = Util.getDCIMPath() + "/identify";
        paths = new String[]{basePath + "0.jpg", basePath + "1.jpg", basePath + "2.jpg"};
    }

    @OnClick({R.id.iv_c20, R.id.iv_c21, R.id.iv_c22, R.id.iv_c20Delete, R.id.iv_c21Delete,
            R.id.iv_c22Delete, R.id.tv_c2Submit})
    public void c2(View v) {
        switch (v.getId()) {
            case R.id.iv_c20:
                if (!uploading[0]) {
                    choosePhoto(0, Constant.Code.Code_0, Constant.Code.Code_1);
                }
                break;
            case R.id.iv_c21:
                if (!uploading[1]) {
                    choosePhoto(1, Constant.Code.Code_2, Constant.Code.Code_3);
                }
                break;
            case R.id.iv_c22:
                if (!uploading[2]) {
                    choosePhoto(2, Constant.Code.Code_4, Constant.Code.Code_5);
                }
                break;
            case R.id.iv_c20Delete:
                delete(0);
                break;
            case R.id.iv_c21Delete:
                delete(1);
                break;
            case R.id.iv_c22Delete:
                delete(2);
                break;
            case R.id.tv_c2Submit:
                if (TextUtils.isEmpty(urls[0])) {
                    ToastUtil.initToast(this, getString(R.string.pleaseUploadId0));
                } else if (TextUtils.isEmpty(urls[1])) {
                    ToastUtil.initToast(this, getString(R.string.pleaseUploadId1));
                } else if (TextUtils.isEmpty(urls[2])) {
                    ToastUtil.initToast(this, getString(R.string.pleaseUploadId2));
                } else {
                    showLoading();
                    OkHttpUtil.putJsonToken(Constant.URL.C2, SharedUtil.getToken(this),
                            this, "back", urls[1], "front", urls[0], "handler", urls[2]);
                }
                break;
        }
    }

    private void delete(int index) {
        if (paths == null) {
            initPath();
            return;
        }
        if (index < 0 || index >= paths.length) {
            return;
        }
        FileUtil.delFile(paths[index]);
        urls[index] = null;
        ivs.get(index).setImageResource(R.color.trans);
        ivg.get(index).setVisibility(View.VISIBLE);
        del.get(index).setVisibility(View.GONE);
    }

    private void choosePhoto(int index, int code0, int code1) {
        ArrayList<String> list = new ArrayList<>();
        list.add(getString(R.string.takePicture));
        list.add(getString(R.string.chooseFromAlbum));
        ChooseSingleDialog.newInstance(list)
                .setOnItemClickListener(v -> {
                    int position = (int) v.getTag();
                    if (position == 0) {
                        if (Util.permCheckReq(this, Constant.Code.PermCode,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {
                            openCamera(index, code0);
                        }
                    } else {
                        startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"),
                                code1);
                    }
                })
                .show(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!Util.permCheck(grantResults)) {
            ToastUtil.initToast(this, R.string.permDeny);
        }
    }

    private void openCamera(int index, int code) {
        if (paths == null) {
            initPath();
        }
        FileUtil.delFile(paths[index]);
        paths[index] = basePath + index + "_" + System.currentTimeMillis() + ".jpg";
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String path = paths[index];
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(path)));
        } else {//7.0
            ContentValues cv = new ContentValues(1);
            cv.put(MediaStore.Images.Media.DATA, path);
            Uri uri = getApplication().getContentResolver()
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        startActivityForResult(intent, code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constant.Code.Code_0:
                    Bitmap bm0 = getBitmapCamera(paths[0]);
                    setAndUpload(bm0, 0);
                    break;
                case Constant.Code.Code_1:
                    if (data == null) {
                        break;
                    }
                    setAndUpload(getBitmapAlbum(data), 0);
                    break;
                case Constant.Code.Code_2:
                    Bitmap bm2 = getBitmapCamera(paths[1]);
                    setAndUpload(bm2, 1);
                    break;
                case Constant.Code.Code_3:
                    if (data == null) {
                        break;
                    }
                    setAndUpload(getBitmapAlbum(data), 1);
                    break;
                case Constant.Code.Code_4:
                    Bitmap bm4 = getBitmapCamera(paths[2]);
                    setAndUpload(bm4, 2);
                    break;
                case Constant.Code.Code_5:
                    if (data == null) {
                        break;
                    }
                    setAndUpload(getBitmapAlbum(data), 2);
                    break;
            }
        }
    }

    private Bitmap getBitmapCamera(String path) {
        return BitMapUtil.decodeBitmap(path, BitMapUtil.K512);
    }

    private Bitmap getBitmapAlbum(Intent data) {
        return BitMapUtil.Uri2Bitmap(this, data.getData(), BitMapUtil.K512);
    }

    private void setAndUpload(Bitmap bm, int index) {
        if (bm == null) {
            return;
        }
        ivg.get(index).setVisibility(View.GONE);
        GlideUtil.glide(this, ivs.get(index), bm);

        urls[index] = null;
        uploading[index] = true;
        tips.get(index).setText(R.string.uploading);
        tips.get(index).setVisibility(View.VISIBLE);
        String img = Base64Util.encodeByte(BitMapUtil.Bitmap2Bytes(bm));
        OkHttpUtil.postJsonToken(Constant.URL.Upload, SharedUtil.getToken(this),
                new OkHttpUtil.OnDataListener() {
            @Override
            public void onResponse(String url, String json, String session) {
                StringEntity upload = gson.fromJson(json, StringEntity.class);
                uploading[index] = false;
                if (Constant.Int.SUC == upload.getCode()) {
                    urls[index] = upload.getData();
                    tips.get(index).setVisibility(View.GONE);
                    del.get(index).setVisibility(View.VISIBLE);
                } else {
                    tips.get(index).setText(R.string.uploadFail);
                    ToastUtil.initToast(C2Activity.this, Util.getCodeText(C2Activity.this,
                            upload.getCode(), upload.getMsg()));
                }
            }

            @Override
            public void onFailure(String url, String error) {
                uploading[index] = false;
                tips.get(index).setText(R.string.uploadFail);
            }
        }, "file", img);
    }

    @Override
    public void onResponse(String url, String json, String session) {
//        Log.e("loge", "C2: " + json);
        ObjectEntity c2 = gson.fromJson(json, ObjectEntity.class);
        dismissLoading();
        ToastUtil.initToast(this, Util.getCodeText(this,
                c2.getCode(), c2.getMsg()));
        if (Constant.Int.SUC == c2.getCode()) {
            startActivity(new Intent(this, IdentityResultActivity.class)
                    .putExtra("level", 1)
                    .putExtra("status", 1)
                    .putExtra("type", type));
            finish();
        } else {
            Util.checkLogin(this, c2.getCode());
        }
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
    public void onBackPressed() {
        if (!TextUtils.isEmpty(urls[0]) || !TextUtils.isEmpty(urls[1]) || !TextUtils.isEmpty(urls[2])) {
            ConfirmDialog.newInstance(getString(R.string.goBackTips), getString(R.string.cancel),
                    getString(R.string.confirm))
                    .setOnItemClickListener(v -> {
                        if (v.getId() == R.id.tv_dgcConfirm) {
                            C2Activity.super.onBackPressed();
                        }
                    })
                    .show(this);
        } else {
            super.onBackPressed();
        }
    }
}
