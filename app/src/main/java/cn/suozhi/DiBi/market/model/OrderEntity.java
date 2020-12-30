package cn.suozhi.DiBi.market.model;

/**
 * 币币委托及成交
 */
public class OrderEntity {

    private String symbol;
    private String p;
    private String t;
    private int pp;//p小数位数
    private int tp;//t小数位数
    private long orderId;
    private boolean isBuy;
    private String orderType;//订单类型
    private String status;//订单状态
    private int orderStatus;//订单状态  1 - 撤单、 2 - 挂单中、 3 - 成交、 4 - 无对应挂单、 5 - 部分成交
    private double price;
    private double volume;
    private double priceTarget;//触发价
    private double total;//市价买入总额 / 成交记录时则为成交总额
    private double volumeDeal;//已成交数量
    private double priceAverage;//成交均价
    private double fee;
    private long time;
    private String showSymbol;
    private String currencyPairRegion;

    private int type;

    public OrderEntity() {}

    public OrderEntity(int type) {
        this.type = type;
    }

    public OrderEntity(String symbol, String p, String t, int pp, int tp, long orderId, boolean isBuy,
                       String orderType, String status, double price, double volume, double priceTarget,
                       double total, double volumeDeal, double priceAverage, long time) {
        this.symbol = symbol;
        this.p = p;
        this.t = t;
        this.pp = pp;
        this.tp = tp;
        this.orderId = orderId;
        this.isBuy = isBuy;
        this.orderType = orderType;
        this.status = status;
        this.price = price;
        this.volume = volume;
        this.priceTarget = priceTarget;
        this.total = total;
        this.volumeDeal = volumeDeal;
        this.priceAverage = priceAverage;
        this.time = time;
    }

    public OrderEntity(String symbol, String p, String t, int pp, int tp, long orderId, boolean isBuy,
                       String orderType, String status, double price, double volume, double priceTarget,
                       double total, double volumeDeal, double priceAverage, long time, String showSymbol, String currencyPairRegion) {
        this.symbol = symbol;
        this.p = p;
        this.t = t;
        this.pp = pp;
        this.tp = tp;
        this.orderId = orderId;
        this.isBuy = isBuy;
        this.orderType = orderType;
        this.status = status;
        this.price = price;
        this.volume = volume;
        this.priceTarget = priceTarget;
        this.total = total;
        this.volumeDeal = volumeDeal;
        this.priceAverage = priceAverage;
        this.time = time;
        this.showSymbol = showSymbol;
        this.currencyPairRegion = currencyPairRegion;
    }

    public OrderEntity(String symbol, String p, String t, int pp, int tp, boolean isBuy,
                       double price, double volume, double total, double fee, long time, String showSymbol) {
        this.symbol = symbol;
        this.p = p;
        this.t = t;
        this.pp = pp;
        this.tp = tp;
        this.isBuy = isBuy;
        this.price = price;
        this.volume = volume;
        this.total = total;
        this.fee = fee;
        this.time = time;
        this.showSymbol = showSymbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public OrderEntity setSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public int getPp() {
        return pp;
    }

    public void setPp(int pp) {
        this.pp = pp;
    }

    public int getTp() {
        return tp;
    }

    public void setTp(int tp) {
        this.tp = tp;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public boolean isBuy() {
        return isBuy;
    }

    public void setBuy(boolean buy) {
        isBuy = buy;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getPriceTarget() {
        return priceTarget;
    }

    public void setPriceTarget(double priceTarget) {
        this.priceTarget = priceTarget;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getVolumeDeal() {
        return volumeDeal;
    }

    public void setVolumeDeal(double volumeDeal) {
        this.volumeDeal = volumeDeal;
    }

    public double getPriceAverage() {
        return priceAverage;
    }

    public void setPriceAverage(double priceAverage) {
        this.priceAverage = priceAverage;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getShowSymbol() {
        return showSymbol;
    }

    public void setShowSymbol(String showSymbol) {
        this.showSymbol = showSymbol;
    }

    public String getCurrencyPairRegion() {
        return currencyPairRegion;
    }

    public void setCurrencyPairRegion(String currencyPairRegion) {
        this.currencyPairRegion = currencyPairRegion;
    }
}
