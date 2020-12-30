package cn.suozhi.DiBi.wallet.model;

import java.util.List;

/**
 * Created by issuser on 2019/7/10.
 */

public class CoinSliderEnity {

    private String sort;

    private List<AllCoinEnity.DataBean.CurrencyDtoBean> list;

    public CoinSliderEnity(String sort, List<AllCoinEnity.DataBean.CurrencyDtoBean> list) {
        this.sort = sort;
        this.list = list;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public List<AllCoinEnity.DataBean.CurrencyDtoBean> getList() {
        return list;
    }

    public void setList(List<AllCoinEnity.DataBean.CurrencyDtoBean> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "CoinSliderEnity{" +
                "sort='" + sort + '\'' +
                ", list=" + list +
                '}';
    }
}
