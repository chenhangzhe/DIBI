package cn.suozhi.DiBi.c2c.model;

import java.io.Serializable;

/**
 * 申诉详情的界面
 */
public class CompalintDetailEnity {
    /**
     * code : 0
     * data : {"descr":"string","initiate":0,"materials":"string","status":0,"title":"string"}
     * msg : string
     */

    private int code;
    private DataBean data;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean implements Serializable {
        /**
         * descr : string
         * initiate : 0
         * materials : string
         * status : 0
         * title : string
         */
        //描述
        private String descr;
        //发起方[1买方|2卖方]
        private int initiate;
        //申诉材料，多张图片用逗号分割
        private String materials;
        //状态[1处理中|2买家胜诉|3卖家胜诉]
        private int status;
        //标题
        private String title;

        public String getDescr() {
            return descr;
        }

        public void setDescr(String descr) {
            this.descr = descr;
        }

        public int getInitiate() {
            return initiate;
        }

        public void setInitiate(int initiate) {
            this.initiate = initiate;
        }

        public String getMaterials() {
            return materials;
        }

        public void setMaterials(String materials) {
            this.materials = materials;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
