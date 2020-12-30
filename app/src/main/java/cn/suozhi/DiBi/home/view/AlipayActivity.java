package cn.suozhi.DiBi.home.view;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.dialog.AuthDialog;
import cn.suozhi.DiBi.common.dialog.ChooseSingleDialog;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.util.Base64Util;
import cn.suozhi.DiBi.common.util.BitMapUtil;
import cn.suozhi.DiBi.common.util.CodeStrUtil;
import cn.suozhi.DiBi.common.util.FileUtil;
import cn.suozhi.DiBi.common.util.GlideUtil;
import cn.suozhi.DiBi.common.util.GsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.ResUtils;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.ToolbarUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.CollectMoneyEnity;
import cn.suozhi.DiBi.home.model.StringEntity;

/**
 * 支付宝收款
 */
public class AlipayActivity extends BaseActivity implements BaseDialog.OnItemClickListener, OkHttpUtil.OnDataListener {
    private static final int REQUEST_CODE = 1;
    @BindView(R.id.iv_wechat)
    ImageView ivIcon;


    @BindView(R.id.iv_upload_pic)
    ImageView ivUpLoadPic;

    @BindView(R.id.rly_photo)
    RelativeLayout rlyPhoto;   //上传图片的显示

    @BindView(R.id.lly_add_photo)
    LinearLayout llyAddPhoto; //添加图片的显示

    @BindView(R.id.tv_pay_type)
    TextView tvPayType;

    @BindView(R.id.tv_commit)
    TextView tvCommit;

    @BindView(R.id.et_name)
    EditText etName;

    @BindView(R.id.et_account)
    EditText etAccount;

    @BindView(R.id.toolbar_text)
    public Toolbar toolbar;
    private int type;//1支付宝  2 微信
    private String title;

    private boolean isUpdating;//头像是否正在上传
    private String path;
    private String name;
    private String account;
    private String img;
    private String uploadUrl;
    private boolean isBind;
    private LoadingDialog loadingDialog;
    //是否提交资料中
    private boolean isCommiting;
    private CollectMoneyEnity.DataBean.RecordsBean data;
    private String qrCode;

    @BindView(R.id.tv_hint_one)
    TextView tvHintOne;

    @BindView(R.id.tv_hint_two)
    TextView tvHintTwo;

    @BindView(R.id.tv_hint_three)
    TextView tvHintThree;
    private boolean icClickCommit;
    private String fullName;

    @Override
    protected int getViewResId() {
        return R.layout.activity_alipay;
    }

    @Override
    protected void init() {
        Intent intent = getIntent();
        fullName = intent.getStringExtra("fullName");
        data = (CollectMoneyEnity.DataBean.RecordsBean) intent.getSerializableExtra("data");
        type = intent.getIntExtra("type", -1);
        isBind = intent.getBooleanExtra("isBind", false);
        if (type == 1) {
            title = getString(R.string.str_alipay_bind);
            ivIcon.setImageResource(R.mipmap.bind_alipay);
            tvPayType.setText(getString(R.string.str_alipay));
        } else {
            title = getString(R.string.str_wechat_bind);
            ivIcon.setImageResource(R.mipmap.bind_wechat);
            tvPayType.setText(getString(R.string.str_wechat));
        }

        ToolbarUtil.initToolbar(toolbar, title, v -> onBackPressed());
        etName.setText(fullName);
        etName.setEnabled(intent.getBooleanExtra("isSeller", false));

        if (isBind) {
            if (data != null) {
                etName.setText(TextUtils.isEmpty(data.getAccountName()) ? "" : data.getAccountName());
                etAccount.setText(data.getAccountNumber());
                rlyPhoto.setVisibility(View.VISIBLE);
                llyAddPhoto.setVisibility(View.GONE);
                GlideUtil.glide(this, ivUpLoadPic, data.getQrCode(), GlideUtil.getOption(0, 0, 0));
                uploadUrl = data.getQrCode();
                tvCommit.setText(getString(R.string.str_update));
            }
            ivUpLoadPic.setEnabled(true);
        } else {
            tvCommit.setText(getString(R.string.submit));
            ivUpLoadPic.setEnabled(false);
        }
        setLisenter();
    }

    private void setLisenter() {
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (icClickCommit) {
                    if (TextUtils.isEmpty(s.toString().trim())) {
                        tvHintOne.setVisibility(View.VISIBLE);
                    } else {
                        tvHintOne.setVisibility(View.GONE);
                    }
                }
            }
        });

        etAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (icClickCommit) {
                    if (TextUtils.isEmpty(s.toString().trim())) {
                        tvHintTwo.setVisibility(View.VISIBLE);
                    } else {
                        tvHintTwo.setVisibility(View.GONE);
                    }
                }
            }
        });
    }


    @OnClick({R.id.tv_commit, R.id.iv_delete, R.id.lly_add_photo, R.id.iv_upload_pic})
    public void alipayClick(View v) {
        switch (v.getId()) {
            case R.id.tv_commit:
                if (CheckCommitInfo()) {
                    if (!isCommiting) {
                        isCommiting = true;
                        icClickCommit = true;
                        showLoading();
                        if (isBind) {
                            if (TextUtils.isEmpty(img)) {
                                commit(name, account, type == 1 ? "1" : "2", uploadUrl);
                            } else {
                                upLoadImg();
                            }
                        } else {
                            upLoadImg();
                        }
                    }
                }

                break;
            case R.id.lly_add_photo:
                if (!isUpdating) {
                    ArrayList<String> list = new ArrayList<>();
                    list.add(getString(R.string.takePicture));
                    list.add(getString(R.string.chooseFromAlbum));
                    ChooseSingleDialog.newInstance(list)
                            .setOnItemClickListener(this)
                            .show(this);
                }
                break;
            case R.id.iv_delete:
                rlyPhoto.setVisibility(View.GONE);
                llyAddPhoto.setVisibility(View.VISIBLE);
                if (isBind) {
                    ivUpLoadPic.setEnabled(false);
                }
                break;
            case R.id.iv_upload_pic:

                startActivityForResult(new Intent(mContext, AlipayDetailActivity.class)
                        .putExtra("url", data.getQrCode())
                        .putExtra("title", type == 1 ? getString(R.string.str_alipay_qrcode) : getString(R.string.str_wechat_qrcode)), REQUEST_CODE);

                break;
        }
    }


    /**
     * 检查提交信息
     */
    private boolean CheckCommitInfo() {
        name = etName.getText().toString().trim();
        account = etAccount.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            tvHintOne.setVisibility(View.VISIBLE);
            return false;
        }

        if (TextUtils.isEmpty(account)) {
            tvHintTwo.setVisibility(View.VISIBLE);
            return false;
        }

        if (!isBind && TextUtils.isEmpty(img)) {
            tvHintThree.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }

    @Override
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.tv_stItem:
                int position = (int) v.getTag();
                if (position == 0) {
                    if (Util.permCheckReq(this, Constant.Code.PermCode,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {
                        openCamera();
                    }
                } else {
                    startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"),
                            Constant.Code.AlbumCode);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!Util.permCheck(grantResults)) {
            ToastUtil.initToast(this, R.string.permDeny);
        }
    }

    private void openCamera() {
        FileUtil.delFile(path);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        path = Util.getDCIMPath() + "/gather_" + System.currentTimeMillis() + ".jpg";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(path)));
        } else {//7.0
            ContentValues cv = new ContentValues(1);
            cv.put(MediaStore.Images.Media.DATA, path);
            Uri uri = getApplication().getContentResolver()
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        startActivityForResult(intent, Constant.Code.CameraCode);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constant.Code.CameraCode:
                    setAndUpload(BitMapUtil.decodeBitmap(path, BitMapUtil.K512));
                    break;
                case Constant.Code.AlbumCode:
                    if (data == null) {
                        break;
                    }
                    setAndUpload(BitMapUtil.Uri2Bitmap(this, data.getData(), BitMapUtil.K512));
                    break;
                case REQUEST_CODE:
                    rlyPhoto.setVisibility(View.GONE);
                    llyAddPhoto.setVisibility(View.VISIBLE);
                    ivUpLoadPic.setEnabled(false);
                    break;
            }
        }
    }


    private void setAndUpload(Bitmap bm) {
        if (bm == null) {
            return;
        }
        tvHintThree.setVisibility(View.GONE);
        rlyPhoto.setVisibility(View.VISIBLE);
        llyAddPhoto.setVisibility(View.GONE);
        GlideUtil.glide(this, ivUpLoadPic, bm);

        img = Base64Util.encodeByte(BitMapUtil.Bitmap2Bytes(bm));
//        OkHttpUtil.postJsonToken(Constant.URL.Upload, SharedUtil.getToken(this),
//                this, "file", img);
    }


    private void showAuthDialog() {
        AuthDialog.newInstance(ResUtils.getString(mContext, R.string.str_enable_edit_hint),
                ResUtils.getString(mContext, R.string.str_go_handle),
                ResUtils.getString(mContext, R.string.cancel))
                .show(this);
    }


    /**
     * 提交收款信息
     */
    private void commit(String accountName, String accountNumber, String accountType, String qrCode) {
        OkHttpUtil.postJsonToken(Constant.URL.addCollectMoneyType, SharedUtil.getToken(mContext), this
                , "accountName", accountName,//账户名
                "accountNumber", accountNumber,//收款账号
                "accountType", accountType, //账号类型 1支付宝 2微信 3银行卡
                "bank", "", //开户银行
                "branchBank", "", //开户支行
                "qrCode", qrCode);//二維碼
    }

    /**
     * 上传图片
     */
    private void upLoadImg() {
        OkHttpUtil.postJsonToken(Constant.URL.Upload, SharedUtil.getToken(this),
                new OkHttpUtil.OnDataListener() {
                    @Override
                    public void onResponse(String url, String json, String session) {
                        StringEntity upload = GsonUtil.fromJson(json, StringEntity.class);
                        if (Constant.Int.SUC == upload.getCode()) {
                            uploadUrl = upload.getData();
                            commit(name, account, type == 1 ? "1" : "2", uploadUrl);
                        } else {
                            ToastUtil.initToast(AlipayActivity.this, Util.getCodeText(AlipayActivity.this,
                                    upload.getCode(), upload.getMsg()));
                        }
                    }

                    @Override
                    public void onFailure(String url, String error) {
                        dismissLoading();
                        isCommiting = false;
                    }
                }, "file", img);
    }

    @Override
    public void onResponse(String url, String json, String session) {
        dismissLoading();
        StringEntity upload = GsonUtil.fromJson(json, StringEntity.class);
        isCommiting = false;
        if (Constant.Int.SUC == upload.getCode()) {
            if (isBind) {
                CodeStrUtil.showToastHint(this, getString(R.string.str_update_suc));
            } else {
                if (type == 1) {
                    CodeStrUtil.showToastHint(this, getString(R.string.str_alipay_bind_suc));
                } else {
                    CodeStrUtil.showToastHint(this, getString(R.string.str_wechat_bind_suc));
                }
            }
            finish();
        } else {
            ToastUtil.initToast(AlipayActivity.this, Util.getCodeText(AlipayActivity.this,
                    upload.getCode(), upload.getMsg()));
            Util.checkLogin(mContext, upload.getCode());
        }
    }

    @Override
    public void onFailure(String url, String error) {
        isCommiting = false;
        dismissLoading();
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
}
