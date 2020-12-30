package cn.suozhi.DiBi.wallet.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.math.BigDecimal;

import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.common.base.AbsRecyclerAdapter;
import cn.suozhi.DiBi.common.base.OnCallbackListener;
import cn.suozhi.DiBi.common.util.AppUtil;
import cn.suozhi.DiBi.common.util.ResUtils;
import cn.suozhi.DiBi.wallet.model.AccountRecordEnity;
import cn.suozhi.DiBi.wallet.model.FlowEntity;

/**
 * 充提记录的适配器
 */
public class FlowAdapter extends AbsRecyclerAdapter<FlowEntity.DataBean.RecordsBean> {

    private int height;//空页面高度
    private int type;
    private OnCallbackListener callbackListener;

    public FlowAdapter(Context context, int height, int type, OnCallbackListener callbackListener) {
        super(context, R.layout.item_account_flow, R.layout.page_more, R.layout.page_bottom);
        this.height = height;
        this.type = type;
        this.callbackListener = callbackListener;
    }


    @Override
    public int getItemType(FlowEntity.DataBean.RecordsBean d) {
        return d.getLoadType();
    }

    @Override
    public int getEmptyCount() {
        return getEmptyCountOfData();
    }

    @Override
    public void setEmptyLayoutParams() {
        setEmptyHeight(height);
    }

    @Override
    public void onBindHolder(RecyclerHolder holder, FlowEntity.DataBean.RecordsBean data, int position) {

        switch (getItemType(data)) {
            case 0:
                if(data.getIsSpecialCurrency()==1){
                    holder.bindTextView(R.id.tv_flow_coin , getString(R.string.flow_coin_name) + data.getShowCode() + " (" + getString(R.string.mto) + ")");
                } else {
                    holder.bindTextView(R.id.tv_flow_coin , getString(R.string.flow_coin_name) + data.getCode() );
                }
//                holder.bindTextView(R.id.tv_flow_coin , getString(R.string.flow_coin_name) + data.getCode() );
                holder.bindTextView(R.id.tv_change_reason , getString(R.string.flow_reason) + getFlowReason(data.getReasonCode()) );
                holder.bindTextView(R.id.tv_flow_date , getString(R.string.flow_cdate) + data.getCreatedDate() );
                BigDecimal amout = new BigDecimal(data.getAmount());
                holder.bindTextView(R.id.tv_flow_amout , getString(R.string.flow_amout) + amout.setScale(2, BigDecimal.ROUND_HALF_UP) );
                holder.bindTextView(R.id.tv_flow_type , getString(R.string.flow_type) + getFlowType(data.getFundType()) );
                break;
            case 1://滑到底，加载下一页
                callbackListener.onCallback();
                break;
        }
    }

    // 资产变动类型 F : 冻结 ,U : 解冻 , IA : 增资 , DF : 减冻 , DA : 减资
    private String getFlowType(String type) {
        switch (type) {
            case "F":
                return getString(R.string.flow_type_F);
            case "U":
                return getString(R.string.flow_type_U);
            case "IA":
                return getString(R.string.flow_type_IA);
            case "DF":
                return getString(R.string.flow_type_DF);
            case "DA":
                return getString(R.string.flow_type_DA);
        }
        return "";
    }

    // 资产变动原因
    private String getFlowReason(String reason) {
        switch (reason) {
            case "D":
                return getString(R.string.flow_reason_D);

            case "WF":
                return getString(R.string.flow_reason_WF);
            case "WC":
                return getString(R.string.flow_reason_WC);
            case "WS":
                return getString(R.string.flow_reason_WS);
            case "WE":
                return getString(R.string.flow_reason_WE);

            case "BF":
                return getString(R.string.flow_reason_BF);
            case "BS":
                return getString(R.string.flow_reason_BS);
            case "BC":
                return getString(R.string.flow_reason_BC);
            case "BE":
                return getString(R.string.flow_reason_BE);

            case "SF":
                return getString(R.string.flow_reason_SF);
            case "SS":
                return getString(R.string.flow_reason_SS);
            case "SC":
                return getString(R.string.flow_reason_SC);
            case "SE":
                return getString(R.string.flow_reason_SE);

            case "TF":
                return getString(R.string.flow_reason_TF);
            case "TS":
                return getString(R.string.flow_reason_TS);
            case "TC":
                return getString(R.string.flow_reason_TC);
            case "TE":
                return getString(R.string.flow_reason_TE);

            case "TI":
                return getString(R.string.flow_reason_TI);
            case "EP":
                return getString(R.string.flow_reason_EP);
            case "AD":
                return getString(R.string.flow_reason_AD);
            case "LW":
                return getString(R.string.flow_reason_LW);
            case "CR":
                return getString(R.string.flow_reason_CR);

            case "CI":
                return getString(R.string.flow_reason_CI);
            case "CO":
                return getString(R.string.flow_reason_CO);
            case "CF":
                return getString(R.string.flow_reason_CF);
            case "CC":
                return getString(R.string.flow_reason_CC);

            case "FB":
                return getString(R.string.flow_reason_FB);
            case "FR":
                return getString(R.string.flow_reason_FR);
            case "FS":
                return getString(R.string.flow_reason_FS);
            case "FC":
                return getString(R.string.flow_reason_FC);
            case "FU":
                return getString(R.string.flow_reason_FU);
            case "FT":
                return getString(R.string.flow_reason_FT);
            case "FD":
                return getString(R.string.flow_reason_FD);
            case "FM":
                return getString(R.string.flow_reason_FM);

            case "VS":
                return getString(R.string.flow_reason_VS);
            case "VC":
                return getString(R.string.flow_reason_VC);
            case "VE":
                return getString(R.string.flow_reason_VE);
            case "VL":
                return getString(R.string.flow_reason_VL);
            case "VR":
                return getString(R.string.flow_reason_VR);
            case "VB":
                return getString(R.string.flow_reason_VB);

            case "OI":
                return getString(R.string.flow_reason_OI);
            case "OO":
                return getString(R.string.flow_reason_OO);

            case "OT":
                return getString(R.string.flow_reason_OT);
            case "OC":
                return getString(R.string.flow_reason_OC);
            case "OD":
                return getString(R.string.flow_reason_OD);
            case "OF":
                return getString(R.string.flow_reason_OF);

            case "IP":
                return getString(R.string.flow_reason_IP);
            case "IFR":
                return getString(R.string.flow_reason_IFR);
            case "IS":
                return getString(R.string.flow_reason_IS);
            case "IR":
                return getString(R.string.flow_reason_IR);
            case "IF":
                return getString(R.string.flow_reason_IF);

            case "US":
                return getString(R.string.flow_reason_US);
            case "UC":
                return getString(R.string.flow_reason_UC);

            case "AT":
                return getString(R.string.flow_reason_AT);

            case "STR":
                return getString(R.string.flow_reason_STR);
            case "BAD":
                return getString(R.string.flow_reason_BAD);

            case "CAF":
                return getString(R.string.flow_reason_CAF);
            case "CAUF":
                return getString(R.string.flow_reason_CAUF);
            case "CAC":
                return getString(R.string.flow_reason_CAC);
            case "CCA":
                return getString(R.string.flow_reason_CCA);
            case "UTF":
                return getString(R.string.flow_reason_UTF);

            case "STF":
                return getString(R.string.flow_reason_STF);
            case "SFD":
                return getString(R.string.flow_reason_SFD);
            case "SUF":
                return getString(R.string.flow_reason_SUF);

            case "VFD":
                return getString(R.string.flow_reason_VFD);
            case "VUF":
                return getString(R.string.flow_reason_VUF);
            case "VOS":
                return getString(R.string.flow_reason_VOS);

        }
        return "";
    }

}
