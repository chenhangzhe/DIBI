package cn.suozhi.DiBi.contract.model;

/**
 * 预测合约
 */
public class ContractEntity {

    private long time;
    private String status;
    private double orderMoney;//下单金额
    private double deliveryMoney;//交割金额

    private String money;//下单金额

    private double close;//收盘价
    private double preClose;//前收价
    private String direction;//方向

    private int type;

    public ContractEntity(long time, String status, double orderMoney, double deliveryMoney, int type) {
        this.time = time;
        this.status = status;
        this.orderMoney = orderMoney;
        this.deliveryMoney = deliveryMoney;
        this.type = type;
    }

    public ContractEntity(long time, String status, String money, int type) {
        this.time = time;
        this.status = status;
        this.money = money;
        this.type = type;
    }

    public ContractEntity(long time, String status, double orderMoney, double deliveryMoney,
                          double close, double preClose, String direction, int type) {
        this.time = time;
        this.status = status;
        this.orderMoney = orderMoney;
        this.deliveryMoney = deliveryMoney;
        this.close = close;
        this.preClose = preClose;
        this.direction = direction;
        this.type = type;
    }

    public ContractEntity(String status, int type) {
        this.status = status;
        this.type = type;
    }

    public ContractEntity(int type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getOrderMoney() {
        return orderMoney;
    }

    public void setOrderMoney(double orderMoney) {
        this.orderMoney = orderMoney;
    }

    public double getDeliveryMoney() {
        return deliveryMoney;
    }

    public void setDeliveryMoney(double deliveryMoney) {
        this.deliveryMoney = deliveryMoney;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getPreClose() {
        return preClose;
    }

    public void setPreClose(double preClose) {
        this.preClose = preClose;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
