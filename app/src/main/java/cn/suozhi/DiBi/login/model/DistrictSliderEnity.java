package cn.suozhi.DiBi.login.model;

import java.util.List;

/**
 * Created by issuser on 2019/7/10.
 */

public class DistrictSliderEnity {

    private String sort;

    private List<PhoneCodeEntity.DataBean> list;

    public DistrictSliderEnity(String sort, List<PhoneCodeEntity.DataBean> list) {
        this.sort = sort;
        this.list = list;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public List<PhoneCodeEntity.DataBean> getList() {
        return list;
    }

    public void setList(List<PhoneCodeEntity.DataBean> list) {
        this.list = list;
    }
}
