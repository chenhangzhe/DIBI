package cn.suozhi.DiBi.c2c.model;

import java.util.List;

/**
 * Created by issuser on 2019/7/10.
 */

public class C2cSliderEnity {

    private String sort;

    private List<C2cSelectedEnity> list;

    public C2cSliderEnity(String sort, List<C2cSelectedEnity> list) {
        this.sort = sort;
        this.list = list;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public List<C2cSelectedEnity> getList() {
        return list;
    }

    public void setList(List<C2cSelectedEnity> list) {
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
