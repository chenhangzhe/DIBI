package cn.suozhi.DiBi.home.model;

/**
 * 返佣总计
 */
public class CommissionEntity {

    /**
     * code : 0
     * msg : 成功
     * data : {}
     */

    private long code;
    private String msg;
    private DataEntity data;

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataEntity getData() {
        return data;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public static class DataEntity {
        /**
         * todayRakeBack : 0
         * totalRakeBack : 0
         */

        private double todayRakeBack;
        private double totalRakeBack;

        public double getTodayRakeBack() {
            return todayRakeBack;
        }

        public void setTodayRakeBack(double todayRakeBack) {
            this.todayRakeBack = todayRakeBack;
        }

        public double getTotalRakeBack() {
            return totalRakeBack;
        }

        public void setTotalRakeBack(double totalRakeBack) {
            this.totalRakeBack = totalRakeBack;
        }
    }
}
