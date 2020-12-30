package cn.suozhi.DiBi.home.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * 币种交易对
 */
@Table(name = "Search")
public class QuoteEntity extends Model {

    @Column
    private String symbol;

    private String p;
    private String t;
    private int pp;//p小数位数
    private int tp;//t小数位数
    private double price;
    private double rate;
    private boolean favor;
    private double volume;
    private double cny;
    private long time;
    private String showSymbol;
    private double avgPrice;
    private double maxRise;
    private double maxFall;
    private String currencyPairRegion;

    private int type;

    public QuoteEntity() {
        super();
    }

    public QuoteEntity(int type) {
        this.type = type;
    }

    public QuoteEntity(String symbol, int type) {
        this.symbol = symbol;
        this.type = type;
    }

    public QuoteEntity(String symbol, String p, String t, int tp, double price, double rate) {
        this.symbol = symbol;
        this.p = p;
        this.t = t;
        this.tp = tp;
        this.price = price;
        this.rate = rate;
    }

    public QuoteEntity(String symbol, String p, String t, int pp, int tp, double price, double rate,
                       double volume, double cny) {
        this.symbol = symbol;
        this.p = p;
        this.t = t;
        this.pp = pp;
        this.tp = tp;
        this.price = price;
        this.rate = rate;
        this.volume = volume;
        this.cny = cny;
    }

    public QuoteEntity(String symbol, String p, String t, int pp, int tp, double price, double rate,
                       double volume, double cny, String showSymbol) {
        this.symbol = symbol;
        this.p = p;
        this.t = t;
        this.pp = pp;
        this.tp = tp;
        this.price = price;
        this.rate = rate;
        this.volume = volume;
        this.cny = cny;
        this.showSymbol = showSymbol;
    }

    public QuoteEntity(String symbol, String p, String t, int pp, int tp, double price, double rate,
                       double volume, double cny, String showSymbol, double avgPrice) {
        this.symbol = symbol;
        this.p = p;
        this.t = t;
        this.pp = pp;
        this.tp = tp;
        this.price = price;
        this.rate = rate;
        this.volume = volume;
        this.cny = cny;
        this.showSymbol = showSymbol;
        this.avgPrice = avgPrice;
    }

    public QuoteEntity(String symbol, String p, String t, int pp, int tp, double price, double rate,
                       double volume, double cny, String showSymbol, double avgPrice, double maxRise, double maxFall) {
        this.symbol = symbol;
        this.p = p;
        this.t = t;
        this.pp = pp;
        this.tp = tp;
        this.price = price;
        this.rate = rate;
        this.volume = volume;
        this.cny = cny;
        this.showSymbol = showSymbol;
        this.avgPrice = avgPrice;
        this.maxRise = maxRise;
        this.maxFall = maxFall;
    }

    public QuoteEntity(String symbol, String p, String t, int pp, int tp, double price, double rate,
                       double volume, double cny, String showSymbol, double avgPrice, double maxRise, double maxFall, String currencyPairRegion) {
        this.symbol = symbol;
        this.p = p;
        this.t = t;
        this.pp = pp;
        this.tp = tp;
        this.price = price;
        this.rate = rate;
        this.volume = volume;
        this.cny = cny;
        this.showSymbol = showSymbol;
        this.avgPrice = avgPrice;
        this.maxRise = maxRise;
        this.maxFall = maxFall;
        this.currencyPairRegion = currencyPairRegion;
    }

    public QuoteEntity(double price, double volume) {
        this.price = price;
        this.volume = volume;
    }

    public QuoteEntity(int pp, int tp, double price, double volume, boolean favor, long time) {
        this.pp = pp;
        this.tp = tp;
        this.price = price;
        this.volume = volume;
        this.favor = favor;
        this.time = time;
    }

    public void setValue(double price, double rate, double volume, double cny) {
        this.price = price;
        this.rate = rate;
        this.volume = volume;
        this.cny = cny;
    }

    public String getSymbol() {
        return symbol;
    }

    public QuoteEntity setSymbol(String symbol) {
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public boolean isFavor() {
        return favor;
    }

    public void setFavor(boolean favor) {
        this.favor = favor;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getCny() {
        return cny;
    }

    public void setCny(double cny) {
        this.cny = cny;
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

    public double getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(double avgPrice) {
        this.avgPrice = avgPrice;
    }

    public String getCurrencyPairRegion() {
        return currencyPairRegion;
    }

    public void setCurrencyPairRegion(String currencyPairRegion) {
        this.currencyPairRegion = currencyPairRegion;
    }
}
