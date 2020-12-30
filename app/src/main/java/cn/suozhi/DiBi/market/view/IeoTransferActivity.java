package cn.suozhi.DiBi.market.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.BaseActivity;
import cn.suozhi.DiBi.common.base.BaseDialog;
import cn.suozhi.DiBi.common.dialog.CodeVerifyDialog;
import cn.suozhi.DiBi.common.dialog.GoogleVerifyDialog;
import cn.suozhi.DiBi.common.util.AppUtil;
import cn.suozhi.DiBi.common.util.CodeStrUtil;
import cn.suozhi.DiBi.common.util.GsonUtil;
import cn.suozhi.DiBi.common.util.OkHttpUtil;
import cn.suozhi.DiBi.common.util.SharedUtil;
import cn.suozhi.DiBi.common.util.ToastUtil;
import cn.suozhi.DiBi.common.util.Util;
import cn.suozhi.DiBi.home.model.ObjectEntity;
import cn.suozhi.DiBi.home.model.UserEntity;
import cn.suozhi.DiBi.login.LoginActivity;
import cn.suozhi.DiBi.login.model.OnceTokenEnity;
import cn.suozhi.DiBi.market.model.IeoTransferEntity;

/**
 * IEO多解一转账界面
 */
public class IeoTransferActivity extends BaseActivity implements BaseDialog.OnItemClickListener, OkHttpUtil.OnDataListener {

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.tv_get_coin_name)
    TextView tvGetCoinNmae;

    @BindView(R.id.tv_t_from)
    public TextView tvF;
    @BindView(R.id.tv_t_to)
    public TextView tvT;

    //可用余额
    @BindView(R.id.tv_avalible_amount)
    TextView tvAvalible;
    @BindView(R.id.tv_frozen_amount)
    TextView tvFrozen;

    @BindView(R.id.tv_ieo_transfer_hint1)
    TextView tvHint1;
    @BindView(R.id.tv_nums)
    TextView tvNums;

    DecimalFormat df = new DecimalFormat("0.00000000");

    //小数位
    private int precis = 3;

    @BindView(R.id.et_amount)
    EditText etAmount;

    private int gs;
    private String area;
    private String account;

    private UserEntity mine;

    private int spinPosition = 0;

    //可用、冻结中余额
    private double availableAmount,frozenAmout;

    //是否正在提交
    private boolean isCommiting = false;
    private String verifyCode;

    @BindView(R.id.sp_choose_coin_type)
    public Spinner fromSP;

    private String fromAmount,fromCurrencyCode,toCurrencyCode;

    private List<IeoTransferEntity.DataBean> dataBeans = new ArrayList<>();

    private List<String> listNormalShowCurrencyCode = new ArrayList<>(); // 转入
    private List<String> listSpecialCurrencyCode = new ArrayList<>(); // 转出

    private int tType = 0; // 转账类型 0-转入 1-转出

    @Override
    protected int getViewResId() {
        return R.layout.activity_ieo_transfer;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void init() {
        setSystemUI();

        String tstr = getString(R.string.enter_transfer_number);
        SpannableString ss = new SpannableString(tstr);
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(10,true);
        ss.setSpan(ass,0,ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (SharedUtil.getLanguage(this).equals("en")) {
            tvTitle.setTextSize(22);
            tvHint1.setTextSize(12);
            tvNums.setTextSize(12);
            etAmount.setHint(ss);
            tvTitle.setText("Launch(DiBi Launch)");
            tvF.setText("Out Launch");
            tvT.setText("Into Launch");
        } else {
            tvTitle.setTextSize(28);
            tvHint1.setTextSize(15);
            tvNums.setTextSize(15);
            etAmount.setHint(tstr);
            tvTitle.setText("Launch(创新交易区)");
            tvF.setText("转出Launch");
            tvT.setText("转入Launch");
        }

        tvT.setBackground(getResources().getDrawable(R.drawable.order_slected_bg));
        tvF.setBackground(getResources().getDrawable(R.drawable.order_unselected_bg));
    }

    @Override
    protected void loadData() {
        getCoinList();
        getUserInfo();
    }

    private String getPointNums(String nums, int flag) {
        return Util.formatFloor(Double.parseDouble(nums), flag);
    }

    @OnClick({R.id.iv_back, R.id.tv_commit, R.id.tv_all, R.id.tv_t_to, R.id.tv_t_from})
    public void user(View v) {
        switch (v.getId()) {
            case R.id.tv_commit:
                if (!isCommiting) {
                    isCommiting = true;
                    if (checkInfo()) {
                        if (gs == 0) {
                            GoogleVerifyDialog.newInstance()
                                    .setOnItemClickListener(this)
                                    .setOnDismissListener(() -> isCommiting = false)
                                    .show(this);
                        } else {
                            CodeVerifyDialog.newInstance(gs == 2, SharedUtil.getToken(this),
                                    area, account, "W")
                                    .setOnItemClickListener(this)
                                    .setOnDismissListener(() -> isCommiting = false)
                                    .show(this);
                        }
                    } else {
                        isCommiting = false;
                    }
                }
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_all:
                etAmount.setText(df.format(availableAmount));
                etAmount.setSelection(etAmount.getText().length());
                break;
            case R.id.tv_t_to:
                tType = 0;
                setThisUi(tType);
                break;
            case R.id.tv_t_from:
                tType = 1;
                setThisUi(tType);
                break;
        }
    }

    private boolean checkInfo() {
        fromAmount = etAmount.getText().toString().trim();

        if (TextUtils.isEmpty(fromAmount)) {
            CodeStrUtil.showToastHintFail(this, getString(R.string.enter_transfer_number));
            return false;
        }

        if (Double.parseDouble(fromAmount) > availableAmount) {
            CodeStrUtil.showToastHintFail(this, getString(R.string.str_not_enough_money));
            return false;
        }

        return true;
    }

    @Override
    public void onItemClick(View v) {
        if (v.getId() == R.id.et_dgCvCode || v.getId() == R.id.et_dgGvCode) {
            showLoading();
            verifyCode = (String) v.getTag();
            OkHttpUtil.getJson(Constant.URL.onceToken, this, "moduleId", Constant.Strings.oncetoken);
        }
    }

    @Override
    public void onResponse(String url, String json, String session) {
        if (url.equals(Constant.URL.onceToken)) {
            OnceTokenEnity onceTokenEnity = GsonUtil.fromJson(json, OnceTokenEnity.class);
            if (onceTokenEnity.getCode() == Constant.Int.SUC) {
                String onceToken = onceTokenEnity.getData().getResultToken();
                OkHttpUtil.postJsonToken(Constant.URL.ieoTransfer, SharedUtil.getToken(mContext), this,
                        "amount",getPointNums(fromAmount,precis), // 转出金额
                        "fromCurrencyCode",fromCurrencyCode, // 转出币种CODE
                        "toCurrencyCode", toCurrencyCode,  // 转入币种CODE
                        "onceToken", onceToken, //一次性token
                        "verifyCode", verifyCode//验证码
                );
            } else {
                isCommiting = false;
            }
        }
        else if (url.equals(Constant.URL.ieoTransfer)) {
            isCommiting = false;
            ObjectEntity objectEntity = GsonUtil.fromJson(json, ObjectEntity.class);
            if (objectEntity.getCode() == Constant.Int.SUC) {
                View toast = ToastUtil.toastView(this, R.layout.toast_icon_text,
                        R.mipmap.tick_white_circle, getString(R.string.transfer_success));
                ToastUtil.initToast(this, toast, Toast.LENGTH_SHORT);
                finish();
            } else {
                    ToastUtil.initToast(this, Util.getCodeText(this,
                            objectEntity.getCode(), objectEntity.getMsg()));
                    Util.checkLogin(this, objectEntity.getCode());
            }
        }
        else if (url.equals(Constant.URL.ieoSpecialCurrency)){
            IeoTransferEntity dd = GsonUtil.fromJson(json, IeoTransferEntity.class);
            if (Constant.Int.SUC == dd.getCode()) {
                if(dataBeans == null){
                    dataBeans = new ArrayList<>();
                } else {
                    dataBeans.clear();
                }
                dataBeans.addAll(dd.getData());
                listNormalShowCurrencyCode.clear();
                listSpecialCurrencyCode.clear();
                // 选择转账币种  绑定下拉
                for (int i = 0 ; i < dataBeans.size() ; i++){
                    listNormalShowCurrencyCode.add(dataBeans.get(i).getNormalShowCurrencyCode()); // 转入code
                    listSpecialCurrencyCode.add(dataBeans.get(i).getSpecialShowCurrencyCode()); // 转出code
                }
                if(tType==0){ // 转入
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_display_style,R.id.spinner_item_text,listNormalShowCurrencyCode);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_style);
                    fromSP.setAdapter(adapter);
                } else { // 转出
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_display_style,R.id.spinner_item_text,listSpecialCurrencyCode);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_style);
                    fromSP.setAdapter(adapter);
                }
                fromSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        spinPosition = position;
                        tvGetCoinNmae.setText(parent.getItemAtPosition(position).toString());
                        etAmount.setText(null);
                        if(tType==0){ // 转入
                            fromCurrencyCode = dataBeans.get(position).getNormalCurrencyCode();
                            toCurrencyCode = dataBeans.get(position).getSpecialCurrencyCode();
                            availableAmount = Double.parseDouble(dataBeans.get(position).getNormalAvailableAmount());
                            frozenAmout = Double.parseDouble(dataBeans.get(position).getNormalFreezeAmount());
                            precis = dataBeans.get(position).getPrecis();
                        } else { // 转出
                            fromCurrencyCode = dataBeans.get(position).getSpecialCurrencyCode();
                            toCurrencyCode = dataBeans.get(position).getNormalCurrencyCode();
                            availableAmount = Double.parseDouble(dataBeans.get(position).getSpecialAvailableAmount());
                            frozenAmout = Double.parseDouble(dataBeans.get(position).getSpecialFreezeAmount());
                            precis = dataBeans.get(position).getPrecis();
                        }
                        setLisenter();
                        tvFrozen.setText(getString(R.string.ieo_t_f) + df.format(frozenAmout));
                        tvAvalible.setText(getString(R.string.str_avlible_amount) + df.format(availableAmount));
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        //
                    }
                });
            }
            else {
                ToastUtil.initToast(IeoTransferActivity.this,getString(R.string.ieo_transfer_get_failed));
            }
        }
        else if (url.equals(Constant.URL.GetInfo)){
            mine = GsonUtil.fromJson(json, UserEntity.class);
            if (Constant.Int.SUC == mine.getCode()) {
                UserEntity.DataEntity.InfoEntity data = mine.getData().getInfo();
                if (data == null) {
                    return;
                }
                int temp_gs = AppUtil.getBindState(data.getGaEnabled(), data.getPhoneEnabled(), data.getEmailEnabled());
                switch (temp_gs) {
                    case 1:
                        area = data.getPhoneArea();
                        account = data.getCellPhone();
                        break;
                    case 2:
                        area = "";
                        account = data.getEmail();
                        break;
                    default:
                        area = "";
                        account = "";
                        break;
                }
                gs = temp_gs;
            } else {
                ToastUtil.initToast(IeoTransferActivity.this,getString(R.string.code2010018));
                finish();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void setThisUi(Integer type){
        etAmount.setText(null);
        if(type==0){
            tvT.setBackground(getResources().getDrawable(R.drawable.order_slected_bg));
            tvF.setBackground(getResources().getDrawable(R.drawable.order_unselected_bg));
            fromCurrencyCode = dataBeans.get(spinPosition).getNormalCurrencyCode();
            toCurrencyCode = dataBeans.get(spinPosition).getSpecialCurrencyCode();
            availableAmount = Double.parseDouble(dataBeans.get(spinPosition).getNormalAvailableAmount());
            frozenAmout = Double.parseDouble(dataBeans.get(spinPosition).getNormalFreezeAmount());
            precis = dataBeans.get(spinPosition).getPrecis();
        } else {
            tvT.setBackground(getResources().getDrawable(R.drawable.order_unselected_bg));
            tvF.setBackground(getResources().getDrawable(R.drawable.order_slected_bg));
            fromCurrencyCode = dataBeans.get(spinPosition).getSpecialCurrencyCode();
            toCurrencyCode = dataBeans.get(spinPosition).getNormalCurrencyCode();
            availableAmount = Double.parseDouble(dataBeans.get(spinPosition).getSpecialAvailableAmount());
            frozenAmout = Double.parseDouble(dataBeans.get(spinPosition).getSpecialFreezeAmount());
            precis = dataBeans.get(spinPosition).getPrecis();
        }
        setLisenter();
        tvFrozen.setText(getString(R.string.ieo_t_f) + df.format(frozenAmout));
        tvAvalible.setText(getString(R.string.str_avlible_amount) + df.format(availableAmount));
    }

    @Override
    public void onFailure(String url, String error) {
        isCommiting = false;
    }

    // 获取多解一币种列表
    private void getCoinList(){
        OkHttpUtil.getJsonToken(Constant.URL.ieoSpecialCurrency, SharedUtil.getToken(this) ,this);
    }

    // 获取用户信息
    private void getUserInfo(){
        OkHttpUtil.getJsonToken(Constant.URL.GetInfo, SharedUtil.getToken(this), this);
    }

    private void setLisenter() {
        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //删除“.”后面超过8位后的数据
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > precis) {
                        s = s.toString().subSequence(0, s.toString().indexOf(".") + precis + 1);
                        etAmount.setText(s);
                        etAmount.setSelection(s.length()); //光标移到最后
                    }
                }
                //如果"."在起始位置,则起始位置自动补0
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    etAmount.setText(s);
                    etAmount.setSelection(2);
                }

                //如果起始位置为0,且第二位跟的不是".",则无法后续输入
                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        etAmount.setText(s.subSequence(0, 1));
                        etAmount.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

}
