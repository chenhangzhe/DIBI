package cn.suozhi.DiBi.c2c.model;


/**
 * 法币筛选
 */
public class C2cCoinEnity {


    private String  name ;
    private  boolean isSelected;

    public C2cCoinEnity(String name, boolean isSelected) {
        this.name = name;
        this.isSelected = isSelected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
