package cn.suozhi.DiBi.market.view;

import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.BindView;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.util.JsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.market.model.CoinIntroEntity;

public class CoinIntroActivity extends BaseActivity implements OkHttpUtil.OnDataListener, View.OnClickListener {

    private String currentCode;
    private String currentShowCode;

    @BindView(R.id.tv_coin_name)
    public TextView tvCoinName;

    @BindView(R.id.tv_icoDate)
    public TextView tvIcoDate;

    @BindView(R.id.tv_icoQuantity)
    public TextView tvIcoQuantity;

    @BindView(R.id.tv_circulateQuantity)
    public TextView tvCirculateQuantity;

    @BindView(R.id.tv_icoPrice)
    public TextView tvIcoPrice;

    @BindView(R.id.tv_whitePaper)
    public TextView tvWhitePaper;

    @BindView(R.id.tv_website)
    public TextView tvWebsite;

    @BindView(R.id.tv_blockchainExplorer)
    public TextView tvBlockchainExplorer;

    @BindView(R.id.tv_coinIntro)
    public TextView tvCoinIntro;

    @BindView(R.id.iv_left_back)
    public ImageView ivLeftBack;

    private String lag;

    @Override
    protected int getViewResId() {
        return R.layout.item_coin_intro;
    }

    @Override
    protected void init() {
        currentCode = getIntent().getStringExtra("coinCode");
        currentShowCode = getIntent().getStringExtra("coinShowCode");
        String lague = SharedUtil.getLanguage(this);
        if(lague == "chs" || lague.equals("chs")){
             lag = "zh_CN";
        } else if (lague == "cht" || lague.equals("cht")){
            lag = "zh_TW";
        } else if (lague == "en" || lague.equals("en")){
            lag = "en_US";
        }
        getCoinIntro(lag);
        ivLeftBack.setOnClickListener(this);
    }

    @Override
    protected void loadData() {}

    private void getCoinIntro(String str){
        OkHttpUtil.getJsonHeader2(Constant.URL.coinIntro, "", str , CoinIntroActivity.this,"currency",currentCode);
    }

    @Override
    public void onResponse(String url, String json, String session) {
        switch (url) {
            case Constant.URL.coinIntro:
                CoinIntroEntity cie = JsonUtil.fromJsonO(json, CoinIntroEntity.class);
                if (cie == null) {
                    break;
                }
                if (Constant.Int.SUC == cie.getCode()) {
                    if(!TextUtils.isEmpty(currentShowCode)){
                        tvCoinName.setText(currentShowCode);
                    } else {
                        tvCoinName.setText(cie.getData().getCoinName());
                    }
                    tvIcoDate.setText(cie.getData().getIcoDate());
                    tvIcoQuantity.setText(cie.getData().getIcoQuantity());
                    tvCirculateQuantity.setText(cie.getData().getCirculateQuantity());
                    tvIcoPrice.setText(cie.getData().getIcoPrice());
                    tvWhitePaper.setText(Html.fromHtml(cie.getData().getWhitePaper()));
                    tvWhitePaper.setMovementMethod(LinkMovementMethod.getInstance());
                    tvWebsite.setText(Html.fromHtml(cie.getData().getWebsite()));
                    tvWebsite.setMovementMethod(LinkMovementMethod.getInstance());
                    tvBlockchainExplorer.setText(Html.fromHtml(cie.getData().getBlockchainExplorer()));
                    tvBlockchainExplorer.setMovementMethod(LinkMovementMethod.getInstance());
                    tvCoinIntro.setText(cie.getData().getIntro() + "\n\n\n\n\n\n\n");
                }
                break;
        }
    }

    @Override
    public void onFailure(String url, String error) {
        //
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left_back:
                finish();
                break;
        }
    }


}
