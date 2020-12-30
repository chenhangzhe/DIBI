package cn.suozhi.DiBi.home.view;

import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.dialog.ConfirmDialog;
import cn.suozhi.DiBi.common.util.GlideUtil;
import cn.suozhi.DiBi.common.util.ResUtils;
import cn.suozhi.DiBi.common.util.ToolbarUtil;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class AlipayDetailActivity extends BaseActivity implements BaseDialog.OnItemClickListener {
    @BindView(R.id.toolbar_center)
    public Toolbar toolbar;

    @BindView(R.id.toolbar_right)
    public ImageView ivDelete;

    @BindView(R.id.iv_qrcode)
    public ImageView ivQrcode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getViewResId() {
        return R.layout.activity_alipay_detail;
    }

    @Override
    protected void init() {
        super.init();
        String url = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");
        ToolbarUtil.initToolbar(toolbar, title, v -> onBackPressed(),R.mipmap.delete_two);
        GlideUtil.glide(this,ivQrcode,url);
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog();
            }
        });

    }

    @Override
    protected void loadData() {
        super.loadData();
    }


    private void showDeleteDialog() {
        ConfirmDialog.newInstance(ResUtils.getString(mContext, R.string.str_delete_this_picture),
                ResUtils.getString(mContext, R.string.str_comfirm),
                ResUtils.getString(mContext, R.string.cancel)).setOnItemClickListener(this)
                .show(this);
    }


    @Override
    public void onItemClick(View v) {
        if (v.getId() == R.id.tv_dgcConfirm){
            setResult(RESULT_OK);//设置resultCode
            finish();
        }
    }
}
