package cn.suozhi.DiBi.home.model;

/**
 * 邀请好友
 */
public class InviteEntity {

    private String pic;
    private String userCode;
    private String date;

    private String coin;
    private double amount;
    private String category;

    private int type;

    public InviteEntity(String pic, String userCode, String date, int type) {
        this.pic = pic;
        this.userCode = userCode;
        this.date = date;
        this.type = type;
    }

    public InviteEntity(String coin, double amount, String category, String date, int type) {
        this.coin = coin;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.type = type;
    }

    public InviteEntity(int type) {
        this.type = type;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
