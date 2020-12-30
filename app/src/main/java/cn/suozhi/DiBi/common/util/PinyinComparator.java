package cn.suozhi.DiBi.common.util;

import java.util.Comparator;

import cn.suozhi.DiBi.home.model.SelectEntity;

/**
 * PinyinComparator接口用来对ListView中的数据根据A-Z进行排序，
 * 前面两个if判断主要是将不是以汉字开头的数据放在后面
 */
public class PinyinComparator implements Comparator<SelectEntity.DataEntity> {

    @Override
    public int compare(SelectEntity.DataEntity o1, SelectEntity.DataEntity o2) {
        if (o1.getSortLetter().equals("@") || o2.getSortLetter().equals("#")) {
            return -1;
        } else if (o1.getSortLetter().equals("#") || o2.getSortLetter().equals("@")) {
            return 1;
        } else {
            return o1.getSortLetter().compareTo(o2.getSortLetter());
        }
    }
}
