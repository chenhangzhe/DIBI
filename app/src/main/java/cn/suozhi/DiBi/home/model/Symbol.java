package cn.suozhi.DiBi.home.model;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * 交易对
 */
public class Symbol implements Serializable {

    private String name;
    private String pCoin;
    private String tCoin;
    private int pPoint;
    private int tPoint;
    private int pId;
    private int tId;
    private double rate;//预期收益比率
    private double maxRise;
    private double maxFall;
    private String currencyPairRegion;
    private String showSymbol;

    public Symbol(String name, String pCoin, String tCoin, int pPoint, int tPoint, int pId, int tId) {
        this.name = name;
        this.pCoin = pCoin;
        this.tCoin = tCoin;
        this.pPoint = pPoint;
        this.tPoint = tPoint;
        this.pId = pId;
        this.tId = tId;
    }

    public Symbol(String name, String pCoin, String tCoin, int pPoint, int tPoint, int pId, int tId, double rate) {
        this.name = name;
        this.pCoin = pCoin;
        this.tCoin = tCoin;
        this.pPoint = pPoint;
        this.tPoint = tPoint;
        this.pId = pId;
        this.tId = tId;
        this.rate = rate;
    }

    public Symbol(String name, String pCoin, String tCoin, int pPoint, int tPoint, int pId, int tId, double maxRise, double maxFall, String currencyPairRegion) {
        this.name = name;
        this.pCoin = pCoin;
        this.tCoin = tCoin;
        this.pPoint = pPoint;
        this.tPoint = tPoint;
        this.pId = pId;
        this.tId = tId;
        this.maxRise = maxRise;
        this.maxFall = maxFall;
        this.currencyPairRegion = currencyPairRegion;
    }

    public Symbol(String name, String pCoin, String tCoin, int pPoint, int tPoint, int pId, int tId, double maxRise, double maxFall, String currencyPairRegion, String showSymbol) {
        this.name = name;
        this.pCoin = pCoin;
        this.tCoin = tCoin;
        this.pPoint = pPoint;
        this.tPoint = tPoint;
        this.pId = pId;
        this.tId = tId;
        this.maxRise = maxRise;
        this.maxFall = maxFall;
        this.currencyPairRegion = currencyPairRegion;
        this.showSymbol = showSymbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPCoin() {
        return TextUtils.isEmpty(pCoin) ? name : pCoin;
    }

    public void setPCoin(String pCoin) {
        this.pCoin = pCoin;
    }

    public String getTCoin() {
        return TextUtils.isEmpty(pCoin) ? "" : tCoin;
    }

    public void setTCoin(String tCoin) {
        this.tCoin = tCoin;
    }

    public int getPPoint() {
        return pPoint;
    }

    public void setPPoint(int pPoint) {
        this.pPoint = pPoint;
    }

    public int getTPoint() {
        return tPoint;
    }

    public void setTPoint(int tPoint) {
        this.tPoint = tPoint;
    }

    public int getPId() {
        return pId;
    }

    public void setPId(int pId) {
        this.pId = pId;
    }

    public int getTId() {
        return tId;
    }

    public void setTId(int tId) {
        this.tId = tId;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getpCoin() {
        return pCoin;
    }

    public void setpCoin(String pCoin) {
        this.pCoin = pCoin;
    }

    public double getMaxRise() {
        return maxRise;
    }

    public void setMaxRise(double maxRise) {
        this.maxRise = maxRise;
    }

    public double getMaxFall() {
        return maxFall;
    }

    public void setMaxFall(double maxFall) {
        this.maxFall = maxFall;
    }

    public String getCurrencyPairRegion() {
        return currencyPairRegion;
    }

    public void setCurrencyPairRegion(String currencyPairRegion) {
        this.currencyPairRegion = currencyPairRegion;
    }

    public String getShowSymbol() {
        return showSymbol;
    }

    public void setShowSymbol(String showSymbol) {
        this.showSymbol = showSymbol;
    }
}
