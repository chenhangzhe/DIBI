package cn.suozhi.DiBi.common.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.suozhi.DiBi.Constant;
import cn.suozhi.DiBi.Messages;
import cn.suozhi.DiBi.R;
import cn.suozhi.DiBi.home.model.QuoteEntity;
import cn.suozhi.DiBi.home.model.Symbol;
import cn.suozhi.DiBi.home.model.UserEntity;
import cn.suozhi.DiBi.market.model.OrderEntity;
import cn.suozhi.DiBi.wallet.model.CoinEnity;

/**
 * 项目相关的公用方法
 */
public class AppUtil {

    /**
     * 重组交易对信息
     */
    public static QuoteEntity getQuote(Messages.DmQuote d, Map<String, Symbol> map) {
        if (d == null) {
            return new QuoteEntity();
        }
        String p, t, r;
        int pp, tp;
        double mr,mf;
        String symbol = d.getSymbol();
        Symbol sym = getSymbolInMap(map, symbol);
        if (sym != null) {
            p = sym.getPCoin();
            t = "/" + sym.getTCoin();
            pp = sym.getPPoint();
            tp = sym.getTPoint();
            mr = sym.getMaxRise();
            mf = sym.getMaxFall();
            r = sym.getCurrencyPairRegion();
        } else {
            p = symbol;
            t = "";
            pp = -1;
            tp = -1;
            mr = 0;
            mf = 0;
            r = "";
        }
        return new QuoteEntity(symbol, p, t, pp, tp, d.getPrice(), getQuoteRate(d), d.getVolume(), d.getCloseCny(),d.getShowSymbol(), d.getAvgPrice(), mr, mf, r);
    }

    /**
     * 获取交易对涨跌幅
     */
    public static double getQuoteRate(Messages.DmQuote d) {
        return d.getOpen() == 0 ? 0 : (d.getPrice() - d.getOpen()) / d.getOpen();
    }

    /**
     * 查找map中的Symbol
     */
    public static Symbol getSymbolInMap(Map<String, Symbol> map, String sym) {
        if (map != null && !TextUtils.isEmpty(sym) && map.containsKey(sym)) {
            return map.get(sym);
        } else {
            return null;
        }
    }

    /**
     * 是否加自选
     */
    public static boolean isFavor(Set<String> set, String symbol) {
        if (set == null || TextUtils.isEmpty(symbol)) {
            return false;
        }
        return set.contains(symbol);
    }

    public static String floorRemoveZero(double d, int point) {
        if (d < 0) {
            return null;
        }
        if (point < 0) {
            point = 0;
        }
        String en = Util.formatFloor(d, point);
        return Util.removePointZero(en, false);
    }

    public static String roundRemoveZero(double d, int point) {
        if (d < 0) {
            return null;
        }
        if (point < 0) {
            point = 0;
        }
        String en = Util.formatDecimal(d, point);
        return Util.removePointZero(en, false);
    }

    /**
     * 获取币币显示位数  point - 接口返回精度
     */
    public static int getTradePoint(boolean isPrice, String t, int point) {
        if (point < 0) {
            return 0;
        }
        return point;
        /*if ("BTC".equals(t) || "ETH".equals(t)) {
            return Math.min(isPrice ? 8 : 2, point);
        }
        return Math.min(4, point);*/
    }

    /**
     * 计算进度值
     */
    public static int calculateProgress(double progress, double max) {
        if (max <= 0 || progress < 0) {
            return 0;
        }
        long round = Math.round(progress * 100 / max);
        if (round < 0) {
            return 0;
        }
        if (round > 100) {
            return 100;
        }
        return (int) round;
    }

    public static void addOrSub(EditText et, boolean isAdd) {
        addOrSub(et, isAdd, -1);
    }

    /**
     * EditText加减运算  p - 控制每次加减的位数 为负则不控制
     */
    public static void addOrSub(EditText et, boolean isAdd, int p) {
        if (et == null) {
            return;
        }
        String dou;
        if (et.length() == 0) {
            dou = "0";
        } else {
            dou = et.getText().toString();
        }
        if (dou.startsWith(".")) {
            dou = "0" + dou;
        }
        if (dou.endsWith(".")) {
            dou = dou + "0";
        }
        int point;//用于加减的小数位数
        if (p < 0) {
            if (dou.contains(".")) {
                point = dou.length() - dou.indexOf(".") - 1;
            } else {
                point = 0;
            }
        } else {
            point = p;
        }
        int pc;//格式化小数位数
        if (dou.contains(".")) {
            pc = dou.length() - dou.indexOf(".") - 1;
        } else {
            pc = 0;
        }
        double d = Util.parseDouble(dou);
        double os = Math.pow(10, -point);//每次加减数值
        int aOb = isAdd ? 1 : -1;//加还是减
        double result = d + aOb * os;
        if (result < 0) {
            return;
        }
        et.setText(Util.formatDecimal(result, pc));
    }

    /**
     * 重组最新成交信息
     */
    public static QuoteEntity getDeal(Messages.DmTrade t, int pp, int tp) {
        if (t == null) {
            return new QuoteEntity();
        }
        return new QuoteEntity(pp, tp, t.getPrice(), t.getSize(), "B".equals(t.getTakerSide()), t.getTime());
    }

    /**
     * 重组委托单信息
     */
    public static OrderEntity getOrder(Messages.DmOrder o, Symbol s) {
        if (o == null) {
            return new OrderEntity();
        }
        String symbol, p, t;
        int pp, tp;

        if (s != null) {
            symbol = s.getName();
            p = s.getPCoin();
            t = s.getTCoin();
            pp = AppUtil.getTradePoint(false, t, s.getPPoint());
            tp = AppUtil.getTradePoint(true, t, s.getTPoint());
        } else {
            symbol = "";
            p = "";
            t = "";
            pp = 4;
            tp = 4;
        }
        return new OrderEntity(symbol, p, t, pp, tp, o.getOrderId(), "B".equals(o.getAction()),
                o.getOrderType(), /*getOrderStatus(o.getOrderStatus())*/o.getOrderStatus(), o.getPrice(), o.getSize(),
                o.getTriggerPrice(), o.getFilledAmount(), o.getFilledSize(),
                getAvgPrice(o.getOrderStatus(), o.getAvgPrice()), o.getCreatedTime(), o.getShowSymbol(), o.getAction());
    }

    private static int getOrderStatus(String os) {
        int status = 0;
        if ("C".equals(os) || "E".equals(os)) {
            status = 1;
        } else if ("S".equals(os) || "P".equals(os)) {
            status = 2;
        } else if ("F".equals(os) || "CP".equals(os) || "EP".equals(os)) {
            status = 3;
        } else if ("D".equals(os)) {
            status = 4;
        } else if ("DP".equals(os)) {
            status = 5;
        }
        return status;
    }

    public static int getStatusColor(String status) {
        if (TextUtils.isEmpty(status)) {
            return R.color.gy8A;
        }
        int colorId;
        switch (status) {
            case "S":
            case "P":
                colorId = R.color.orangeE0;
                break;
            case "F":
            case "CP":
            case "EP":
            case "DP":
                colorId = R.color.green3F;
                break;
            default:
                colorId = R.color.gy8A;
                break;
        }
        return colorId;
    }

    private static double getAvgPrice(String status, double avg) {
        if ("S".equals(status) || "C".equals(status) || "E".equals(status) || "D".equals(status)) {
            return -1;
        }
        return avg;
    }

    /**
     * 重组委托单信息
     */
    public static OrderEntity getExecution(Messages.DmExecution e, Symbol s) {
        if (e == null) {
            return new OrderEntity();
        }
        String symbol, p, t;
        int pp, tp;
        if (s != null) {
            symbol = s.getName();
            p = s.getPCoin();
            t = s.getTCoin();
            pp = AppUtil.getTradePoint(false, t, s.getPPoint());
            tp = AppUtil.getTradePoint(true, t, s.getTPoint());
        } else {
            symbol = e.getSymbol();
            p = symbol;
            t = "";
            pp = 4;
            tp = 4;
        }
        return new OrderEntity(symbol, p, t, pp, tp, "B".equals(e.getAction()), e.getPrice(),
                e.getFilled(), e.getTotalAmout(), e.getCommission(), e.getTime() , e.getShowSymbol());
    }

    /**
     * 计算约值
     */
    public static String calculateCny(double number, double close, double price) {
        if (price <= 0 || number < 0 || close < 0) {
            return approximateCny(0);
        }
        return approximateCny(number * close / price);
    }

    /**
     * 约等于法币
     */
    public static String approximateCny(double number) {
        if (number < 0) {
            number = 0;
        }
        return "≈" + Util.Format4.format(number) + " " + Constant.Strings.RMB_Symbol;
    }

    /**
     * 获取个人信息中的昵称 -未设置则检测手机 -未绑定则检测邮箱
     */
    public static String getAlias(UserEntity.DataEntity.InfoEntity d, Context context) {
        if (d == null) {
            return null;
        }
        if (!TextUtils.isEmpty(d.getUserName())) {
            return addDotBeyond(d.getUserName(), 14);
        }
        int anInt = SharedUtil.getInt(context, "Login", "LoginType", 1);
        if (anInt == 1){
            if (d.getEmailEnabled() == 1) {
                return addDotBeyond(Util.addStarInMiddle(d.getEmail()), 14);
            }
        }else {
            if (d.getPhoneEnabled() == 1) {
                return addDotBeyond(Util.addStarInMiddle(d.getCellPhone()), 14);
            }
        }


        return null;
    }

    public static String addDotBeyond(String s, int length) {
        if (TextUtils.isEmpty(s) || length <= 0) {
            return s;
        }
        if (s.length() > length) {
            return s.substring(0, length) + "...";
        }
        return s;
    }

    /**
     * 获取认证状态 1 - 未开始 、 2 - C1成功 、 3 - C2审核中 、 4 - C2失败 、 5 - C2成功 、 6 - C3成功
     */
    public static int getIdentify(int level, int status) {
        int ls = 0;
        if (level <= 0 || level > 3) {
            ls = 1;
        } else if (level == 1 && status == 0) {
            ls = 2;
        } else if (level == 1 && status == 1) {
            ls = 3;
        } else if (level == 1 && status == 2) {
            ls = 4;
        } else if (level == 2 && status == 3) {
            ls = 5;
        } else if (level == 3) {
            ls = 6;
        }
        return ls;
    }

    /**
     * 获取认证文字
     */
    public static int getIdentifyState(int level, int status, int type) {
        int ls = getIdentify(level, status);
        int id;
        switch (ls) {
            case 1:
                id = R.string.goIdentify;
                break;
            case 2:
                id = type == 1 ? R.string.junior: R.string.idLevel1;
                break;
            case 3:
                id = type == 1 ? R.string.junior: R.string.idLevel2Ing;
                break;
            case 4:
                id = type == 1 ? R.string.junior: R.string.idLevel2Fail;
                break;
            case 5:
                id = type == 1 ? R.string.junior: R.string.idLevel2;
                break;
            case 6:
                id = type == 1 ? R.string.senior: R.string.idLevel3;
                break;
            default:
                id = 0;
                break;
        }
        return id;
    }

    /**
     * 依次检测绑定状态
     */
    public static int getBindState(int... enable) {
        if (enable.length == 0) {
            return -1;
        }
        int state = -1;
        for (int i = 0; i < enable.length; i++) {
            if (enable[i] == 1) {
                state = i;
                break;
            }
        }
        return state;
    }

    /**
     * 截取日期的年月日
     */
    public static String formatDate2y2d(String date) {
        if (TextUtils.isEmpty(date) || !date.contains(" ")) {
            return date;
        }
        int index = date.indexOf(" ");
        return date.substring(0, index);
    }

    /**
     * 截取日期的年月日
     */
    public static String formatDate2M2d(String date) {
        String y2d = formatDate2y2d(date);
        if (TextUtils.isEmpty(y2d) || !y2d.contains("-")) {
            return y2d;
        }
        int index = date.indexOf("-");
        return y2d.substring(index + 1);
    }

    /**
     * 格式化Y轴标签 使两个图表Y标签宽度一致
     */
    public static String getYLabel(float value, float max) {
        float m = Math.abs(max);
        if (m >= 10) {
            return Util.Format0.format(value);
        }
        if (m >= 1) {
            return Util.Format2.format(value);
        }
        return Util.Format4.format(value);
    }

    public static String getSpace(int count) {
        if (count <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    /**
     * ImageView加载Bitmap
     */
    public static void loadBitmap(ImageView iv, Bitmap bm) {
        try {
            new Handler().post(() -> iv.setImageBitmap(bm));
        } catch (Exception e) {
            Log.e("loge", "loadBitmap: "+e.getMessage());
        }
    }

    public static boolean hasForbidReason(List<CoinEnity.DataBean.ForbiddenDataBean> list) {
        if (list == null || list.size() == 0) {
            return false;
        }
        return !TextUtils.isEmpty(list.get(0).getContent());
    }

    public static long getNotifyId(String link, String split) {
        if (TextUtils.isEmpty(link) || TextUtils.isEmpty(split)) {
            return Util.parseLong(link);
        }
        int index = link.lastIndexOf(split);
        if (index >= 0 && index < link.length()) {
            return Util.parseLong(link.substring(index + 1));
        }
        return 0;
    }
}
