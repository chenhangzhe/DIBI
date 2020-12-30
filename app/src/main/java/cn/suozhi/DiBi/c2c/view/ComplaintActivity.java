package cn.suozhi.DiBi.c2c.view;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.c2c.adapter.UpLoadImagAdapter;
import cn.suozhi.DiBi.c2c.model.OrderDetailEnity;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.base.DecoRecycler;
import cn.suozhi.DiBi.common.dialog.ChooseSingleDialog;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.util.Base64Util;
import cn.suozhi.DiBi.common.util.BitMapUtil;
import cn.suozhi.DiBi.common.util.CodeStrUtil;
import cn.suozhi.DiBi.common.util.FileUtil;
import cn.suozhi.DiBi.common.util.GlideUtil;
import cn.suozhi.DiBi.common.util.GsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.ToolbarUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.adapter.SelePicAdapter;
import cn.suozhi.DiBi.home.model.StringEntity;
import cn.suozhi.DiBi.login.model.OnceTokenEnity;

/**
 * 发起申诉
 */
public class ComplaintActivity extends BaseActivity implements BaseDialog.OnItemClickListener, AbsRecyclerAdapter.OnItemClickListener, OkHttpUtil.OnDataListener {
    @BindView(R.id.rv_photo)
    RecyclerView rvPhoto;
    @BindView(R.id.toolbar_center)
    public Toolbar toolbar;

    @BindView(R.id.et_title)
    EditText etTitle;

    @BindView(R.id.et_content)
    EditText etContent;

    @BindView(R.id.tv_order_number)
    TextView tvOrderNo;

    @BindView(R.id.tv_money)
    TextView tvMoney;

    @BindView(R.id.tv_pay_type)
    TextView tvPayType;

    @BindView(R.id.tv_order_time)
    TextView tvOrderTime;


    private String path;
    private SelePicAdapter selePicAdapter;
    private String title;
    private String content;
    private String oderNo;

    //当前是那一张图片再上传
    private int currentPic = 0;
    private LoadingDialog loadingDialog;
    //是否正在提交资料中
    private boolean isCommitting;
    private String materials = "";
    private OrderDetailEnity.DataBean dataBean;

    @BindView(R.id.tv_hint_one)
    TextView tvHintOne;

    @BindView(R.id.tv_hint_two)
    TextView tvHintTwo;

    @BindView(R.id.tv_hint_three)
    TextView tvHintThree;
    private boolean icClickCommit;

    private String onceToken;

    private List<String> tempPathList = new ArrayList<>();

    private List<String> seleImgList;
    private String uploadUrl;
    private List<String> submitUrlList = new ArrayList<>();
    List<String> imgList;
    private String submitImageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getViewResId() {
        return R.layout.activity_complaint;
    }

    private void setLisenter() {
        etTitle.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
        etTitle.addTextChangedListener(new TextWatcher() {
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

        etContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
        etContent.addTextChangedListener(new TextWatcher() {
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

    public void editRemoveEmoji(EditText editText) {
        editText.setFilters(new InputFilter[]{inputFilter});
    }


    InputFilter inputFilter = new InputFilter() {

        Pattern pattern = Pattern.compile("[^a-zA-Z0-9\\u4E00-\\u9FA5_]");

        @Override
        public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
            Matcher matcher = pattern.matcher(charSequence);
            if (!matcher.find()) {
                return null;
            } else {
                return "";
            }

        }
    };

    @Override
    protected void init() {
        super.init();
        ToolbarUtil.initToolbar(toolbar, getString(R.string.str_apply_complaint), v -> onBackPressed());

        dataBean = (OrderDetailEnity.DataBean) getIntent().getSerializableExtra("oderEnity");
        oderNo = dataBean.getOrderNo();

        imgList = new ArrayList<>();

        selePicAdapter = new SelePicAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvPhoto.setLayoutManager(layoutManager);
        rvPhoto.setAdapter(selePicAdapter);
        imgList.add("");
        selePicAdapter.setData(imgList);
        selePicAdapter.setOnItemClickListener(this);

        editRemoveEmoji(etTitle);
        editRemoveEmoji(etContent);

        tempPathList = new ArrayList<>();

        if (dataBean != null) {
            tvOrderNo.setText(dataBean.getOrderNo());
            tvMoney.setText(Util.formatTinyDecimal(dataBean.getTotalPrice(), false) + "  " + dataBean.getLegalCurrencyCode());
            tvOrderTime.setText(dataBean.getTime());


            switch (dataBean.getPayMode()) {
                case 1:
                    tvPayType.setText(getString(R.string.str_alipay));
                    tvPayType.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.alipay, 0, 0, 0);
                    break;
                case 2:
                    tvPayType.setText(getString(R.string.str_wechat));
                    tvPayType.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.wechat, 0, 0, 0);
                    break;
                case 3:
                    tvPayType.setText(getString(R.string.str_bank));
                    tvPayType.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.bank, 0, 0, 0);
                    break;
            }
        }

        setLisenter();

    }

    @Override
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.tv_stItem:
                seleAlbum();
                break;
        }
    }

    private void openCamera() {
        FileUtil.delFile(path);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        path = Util.getDCIMPath() + "/avatar" + System.currentTimeMillis() + ".jpg";

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

    private void seleAlbum() {
        //选择相册
        ArrayList<String> list = new ArrayList<>();
        list.add(getString(R.string.takePicture));
        list.add(getString(R.string.chooseFromAlbum));
        ChooseSingleDialog.newInstance(list)
                .setOnItemClickListener(new BaseDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v) {
                        if (Util.permCheckReq(ComplaintActivity.this, Constant.Code.CameraCode,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {
                            int position = (int) v.getTag();
                            if (position == 0) {
                                openCamera();
                            } else {
                                startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"),
                                        Constant.Code.AlbumCode);
                            }
                        }
                    }
                })
                .show(this);
    }

    @Override
    protected void loadData() {
        super.loadData();
    }

    @OnClick({R.id.tv_commit})
    public void complaintClick(View v) {
        switch (v.getId()) {
            case R.id.tv_commit:
                icClickCommit = true;
                submit();
                break;
        }
    }

    /**
     * 提交申诉资料
     */
    private void submit() {
        if (checkParamInfo()) {
            showLoading();
            if (!isCommitting) {
                isCommitting = true;
                getOnceToken();
            }
        }
    }

    private boolean checkParamInfo() {
        title = etTitle.getText().toString().trim();
        content = etContent.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            tvHintOne.setVisibility(View.VISIBLE);
            return false;
        }

        if (TextUtils.isEmpty(content)) {
            tvHintTwo.setVisibility(View.VISIBLE);
            return false;
        }

        String picUrls = "";
        int len = submitUrlList.size();
//        Log.e("TAG", "大小：" + submitUrlList.size());
        for (int i = 0; i < len; i++) {
            picUrls = picUrls + submitUrlList.get(i) + ",";
        }
        if (len > 0) {
            picUrls = picUrls.substring(0, picUrls.length() - 1);
        }
//        Log.e("TAG", "上传的图片路径：" + picUrls);
        submitImageUrl = picUrls;

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constant.Code.CameraCode:
                    Bitmap bmCamera = getBitmapCamera(path);
                    upLoadImg(Base64Util.encodeByte(BitMapUtil.Bitmap2Bytes(bmCamera)));
                    break;
                case Constant.Code.AlbumCode:
                    if (data == null) {
                        break;
                    }
                    Bitmap bm = getBitmapAlbum(data);
                    upLoadImg(Base64Util.encodeByte(BitMapUtil.Bitmap2Bytes(bm)));
                    break;
            }
        }
    }

    private Bitmap getBitmapCamera(String path) {
        tempPathList.add(path);
        return BitMapUtil.decodeBitmap(path, BitMapUtil.K512);
    }

    private Bitmap getBitmapAlbum(Intent data) {
        tempPathList.add(String.valueOf(data.getData()));
        return BitMapUtil.Uri2Bitmap(this, data.getData(), BitMapUtil.K512);
    }

    @Override
    public void onItemClick(View v, int position) {
        switch (v.getId()) {
            case R.id.item_cl_pic://图片
                seleImgList = selePicAdapter.getData();
                if (!Util.hasText(seleImgList.get(position))) {
                    seleAlbum();
                }
                break;
            case R.id.img_close://关闭
                seleImgList = selePicAdapter.getData();
                submitUrlList.remove(position);
                if (!Util.hasText(seleImgList.get(seleImgList.size() - 1))) {
                    seleImgList.remove(position);
                    tempPathList.remove(position);
                } else {
                    seleImgList.remove(position);
                    seleImgList.add("");
                    tempPathList.remove(position);
                }
                selePicAdapter.setData(seleImgList);
                break;
        }
    }

    @Override
    public void onResponse(String url, String json, String session) {
        if (url.equals(Constant.URL.onceToken)) {
            OnceTokenEnity onceTokenEnity = GsonUtil.fromJson(json, OnceTokenEnity.class);
            if (onceTokenEnity.getCode() == Constant.Int.SUC) {
                onceToken = onceTokenEnity.getData().getResultToken();
                applyCompalint();
            }
        }
        else if (url.equals(Constant.URL.complaintOrder)) {
            dismissLoading();
            isCommitting = false;
            StringEntity complaint = GsonUtil.fromJson(json, StringEntity.class);
            if (Constant.Int.SUC == complaint.getCode()) {
                //跳转到申诉详情界面
                Intent intent = new Intent(mContext, ComplaintOrderActivity.class);
                intent.putExtra("oderNo", oderNo);
                intent.putExtra("oderEnity", dataBean);
                startActivity(intent);
                CodeStrUtil.showToastHint(ComplaintActivity.this, getString(R.string.str_complaint_suc));
                finish();
            } else {
                ToastUtil.initToast(ComplaintActivity.this, Util.getCodeText(ComplaintActivity.this,
                        complaint.getCode(), complaint.getMsg()));
                Util.checkLogin(mContext, complaint.getCode());
            }
        }
    }

    private void upLoadImg(String base64) {
        OkHttpUtil.postJsonToken(Constant.URL.Upload, SharedUtil.getToken(this),
                new OkHttpUtil.OnDataListener() {
                    @Override
                    public void onResponse(String url, String json, String session) {
                        dismissLoading();
                        StringEntity upload = GsonUtil.fromJson(json, StringEntity.class);
                        if (Constant.Int.SUC == upload.getCode()) {
                            uploadUrl = upload.getData(); // 返回的地址
                            submitUrlList.add(uploadUrl); // 添加到最终提交的数组
                            int size = imgList.size();
                            if (size < 3) {
                                imgList.remove(size - 1);
                                imgList.add(tempPathList.get(size-1));
                                imgList.add("");
                                selePicAdapter.setData(imgList);
                            } else if (size == 3) {
                                imgList.remove(size - 1);
                                imgList.add(tempPathList.get(size-1));
                                selePicAdapter.setData(imgList);
                            }
                        } else {
                            ToastUtil.initToast(ComplaintActivity.this, Util.getCodeText(ComplaintActivity.this,
                                    upload.getCode(), upload.getMsg()));
                        }
                    }

                    @Override
                    public void onFailure(String url, String error) {
                        dismissLoading();
                    }
                }, "file", base64);
    }

    private void getOnceToken(){
        OkHttpUtil.getJson(Constant.URL.onceToken, this, "moduleId", Constant.Strings.getComfirmPayOncetoken);
    }

    private void applyCompalint() {
        OkHttpUtil.postJsonToken(Constant.URL.complaintOrder, SharedUtil.getToken(mContext), this,
                "descr", content, //申诉描述
                "materials", submitImageUrl,//图片路径,多图片用英文逗号隔开
                "orderId", oderNo,//申诉订单id
                "title", title,
                "onceToken",onceToken); //申诉标题
    }

    @Override
    public void onFailure(String url, String error) {
        dismissLoading();
        isCommitting = false;
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
