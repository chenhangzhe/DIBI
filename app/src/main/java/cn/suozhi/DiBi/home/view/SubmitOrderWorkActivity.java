package cn.suozhi.DiBi.home.view;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.dialog.ChooseSingleDialog;
import cn.suozhi.DiBi.common.dialog.LoadingDialog;
import cn.suozhi.DiBi.common.eventbus.EventBusHelper;
import cn.suozhi.DiBi.common.util.Base64Util;
import cn.suozhi.DiBi.common.util.BitMapUtil;
import cn.suozhi.DiBi.common.util.FileUtil;
import cn.suozhi.DiBi.common.util.GlideUtil;
import cn.suozhi.DiBi.common.util.GsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.adapter.SelePicAdapter;
import cn.suozhi.DiBi.home.model.QEntity;
import cn.suozhi.DiBi.home.model.StringEntity;

/**
 * 创建时间：2019-07-26 11:11
 * 作者：Lich_Cool
 * 邮箱：licheng@ld.chainsdir.com
 * 功能描述：提交工单
 */
public class SubmitOrderWorkActivity extends BaseActivity
        implements BaseDialog.OnItemClickListener,
        AbsRecyclerAdapter.OnItemClickListener, OkHttpUtil.OnDataListener {


    @BindView(R.id.iv_mineBack)
    ImageView ivMineBack;
    @BindView(R.id.tv_msg_title)
    TextView tvMsgTitle;
    @BindView(R.id.tv_q_type)
    TextView tvQType;
    @BindView(R.id.img_type_arrow)
    ImageView imgTypeArrow;
    @BindView(R.id.edt_commit_order_title)
    EditText edtCommitOrderTitle;
    @BindView(R.id.edt_commit_order_content)
    EditText edtCommitOrderContent;
    @BindView(R.id.rv_img)
    RecyclerView rvImg;
    @BindView(R.id.item_normal)
    LinearLayout itemNormal;
    @BindView(R.id.img_sfz)
    ImageView imgSfz;
    @BindView(R.id.item_sfz)
    ConstraintLayout itemSfz;
    @BindView(R.id.tv_c1Submit)
    TextView tvC1Submit;
    @BindView(R.id.iv_Delete)
    ImageView ivDelete;
    private int selePos = -1;
    private ArrayList<String> list;

    private SelePicAdapter selePicAdapter;
    List<String> imgList;
    private Gson gson = new GsonBuilder().create();
    private List<QEntity.DataBean> dataBeans;
    private String path;
    private int seleType = -1;//标记类型
    private List<String> seleImgList;
    private String uploadUrl;
    private LoadingDialog loadingDialog;
    private List<String> submitUrlList = new ArrayList<>();
    private List<String> tempPathList = new ArrayList<>();
    private String sfzUrl;

    @Override
    protected int getViewResId() {
        return R.layout.activity_commit_order;
    }

    @Override
    protected void init() {
        lang = SharedUtil.getLanguage4Url(this);
        OkHttpUtil.getJson(Constant.URL.GetSelect, this, "category", "workSheetsType", "language", lang);
        //先初始化选择对话框
        list = new ArrayList<>();
        selePicAdapter = new SelePicAdapter(this);
        imgList = new ArrayList<>();
        imgSfz.setImageResource(R.mipmap.icon_img_sfz);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvImg.setLayoutManager(layoutManager);
        rvImg.setAdapter(selePicAdapter);
        imgList.add("");
        selePicAdapter.setData(imgList);
        selePicAdapter.setOnItemClickListener(this);

        tempPathList = new ArrayList<>();
    }

    @OnClick({R.id.iv_mineBack, R.id.img_type_arrow, R.id.img_sfz, R.id.item_sfz, R.id.tv_c1Submit, R.id.iv_Delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_mineBack:
                finish();
                break;
            case R.id.img_type_arrow:

                ChooseSingleDialog.newInstance(getString(R.string.workOrderType), list, selePos, true)
                        .setOnItemClickListener(this)
                        .show(this);
                break;

            case R.id.img_sfz:
            case R.id.item_sfz:
                seleAlbum();
                break;
            case R.id.tv_c1Submit://提交工单

                if (checkForm()) {

                    String title = edtCommitOrderTitle.getText().toString();
                    String content = edtCommitOrderContent.getText().toString();
                    String picUrls = "";
                    if (seleType == 6) {

                        picUrls = sfzUrl;
                    } else {
                        int len = submitUrlList.size();
                        Log.e("TAG", "大小：" + submitUrlList.size());
                        for (int i = 0; i < len; i++) {
                            picUrls =picUrls+submitUrlList.get(i) + ",";
                        }
                        if (len > 0) {

                            picUrls = picUrls.substring(0, picUrls.length() - 1);
                        }
                    }


                    Log.e("TAG", "上传的图片路径：" + picUrls);

                    //提交工单
                    OkHttpUtil.postJsonToken(Constant.URL.SubmitWorkOrder, SharedUtil.getToken(this), this,
                            "title", title, "content", content, "picUrls", picUrls, "type", seleType + "");
                }


                break;

            case R.id.iv_Delete:

                ivDelete.setVisibility(View.GONE);
                sfzUrl = "";
                imgSfz.setImageResource(R.mipmap.icon_img_sfz);
                break;
        }
    }

    private boolean checkForm() {

        if (seleType == -1) {
            ToastUtil.initToast(this, R.string.vvbtc_sele_order_type);
            return false;
        }

        if (TextUtils.isEmpty(edtCommitOrderTitle.getText())) {

            ToastUtil.initToast(this, R.string.vvbtc_tost_input_title);
            return false;
        }
        if (!Util.hasText(edtCommitOrderTitle.getText().toString())) {
            ToastUtil.initToast(this, R.string.vvbtc_tost_input_title);

            return false;
        }

        if (TextUtils.isEmpty(edtCommitOrderContent.getText())) {

            ToastUtil.initToast(this, R.string.vvbtc_tost_input_title);
            return false;
        }
        if (!Util.hasText(edtCommitOrderContent.getText().toString())) {
            ToastUtil.initToast(this, R.string.vvbtc_tost_input_title);

            return false;
        }

        if (seleType == 6) {

            if (!Util.hasText(sfzUrl)) {
                ToastUtil.initToast(this, R.string.vvbtc_commit_sfz);

                return false;
            }
        }

        return true;
    }

    @Override
    public void onItemClick(View v) {

        selePos = (int) v.getTag();
        tvQType.setText(list.get(selePos));
        seleType = dataBeans.get(selePos).getValue();
        if (seleType == 6) {//更换邮箱手机类型
            itemNormal.setVisibility(View.GONE);
            itemSfz.setVisibility(View.VISIBLE);
        } else {
            itemNormal.setVisibility(View.VISIBLE);
            itemSfz.setVisibility(View.GONE);
        }

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
        Log.e("TAG", json);
        switch (url) {
            case Constant.URL.GetSelect://交易问题列表
                QEntity qEntity = gson.fromJson(json, QEntity.class);
                if (qEntity != null) {
                    dataBeans = qEntity.getData();
                    if (dataBeans != null && dataBeans.size() > 0) {

                        for (int i = 0; i < dataBeans.size(); i++) {
                            list.add(dataBeans.get(i).getName());
                        }
                    }
                }
                break;
            case Constant.URL.SubmitWorkOrder://工单提交成功
                StringEntity entity = gson.fromJson(json, StringEntity.class);
                if (entity != null) {
                    if (entity.getCode() == Constant.Int.SUC) {
                        ToastUtil.initToast(this, R.string.vvbtc_work_order_commit_sucess);
                        EventBusHelper.post(Constant.Strings.REFRESH_WORK_REDER);
                        finish();
                    } else {
                        ToastUtil.initToast(this, entity.getMsg());
                    }
                }
                break;
        }
    }

    @Override
    public void onFailure(String url, String error) {
        Util.saveLog(this, url, error);
        ToastUtil.initToast(this, error);
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
                        if (Util.permCheckReq(SubmitOrderWorkActivity.this, Constant.Code.CameraCode,
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


    private void openCamera() {
        FileUtil.delFile(path);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        path = Util.getDCIMPath() + "/DiBi" + System.currentTimeMillis() + ".jpg";

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            showLoading();
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

    private void upLoadImg(String base64) {
        OkHttpUtil.postJsonToken(Constant.URL.Upload, SharedUtil.getToken(this),
                new OkHttpUtil.OnDataListener() {
                    @Override
                    public void onResponse(String url, String json, String session) {
                        dismissLoading();
                        StringEntity upload = GsonUtil.fromJson(json, StringEntity.class);
                        if (Constant.Int.SUC == upload.getCode()) {
                            uploadUrl = upload.getData();
                            submitUrlList.add(uploadUrl);
                            //判断类型
                            if (seleType != 6) {//如果类型不是6，就处理适配器
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
                            } else {//如果是6，就处理身份证上传
                                //隐藏提示身份证item,隐藏recyclervie,显示图片
                                itemNormal.setVisibility(View.GONE);
                                itemSfz.setVisibility(View.VISIBLE);
                                ivDelete.setVisibility(View.VISIBLE);
                                sfzUrl = uploadUrl;
                                GlideUtil.glide(SubmitOrderWorkActivity.this, imgSfz, path);
                            }

                        } else {
                            ToastUtil.initToast(SubmitOrderWorkActivity.this, Util.getCodeText(SubmitOrderWorkActivity.this,
                                    upload.getCode(), upload.getMsg()));
                        }
                    }

                    @Override
                    public void onFailure(String url, String error) {
                        dismissLoading();
                    }
                }, "file", base64);
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
