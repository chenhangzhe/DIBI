package cn.suozhi.DiBi.contract.model;

/**
 * 预测合约分析
 */
public class AnalysisEntity {

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
         * drawCount : 0
         * expenditure : 23.11
         * income : 95.19
         * loseCount : 2
         * total : 72.08
         * winCount : 2
         */

        private int drawCount;
        private double expenditure;
        private double income;
        private int loseCount;
        private double total;
        private int winCount;

        public int getDrawCount() {
            return drawCount;
        }

        public void setDrawCount(int drawCount) {
            this.drawCount = drawCount;
        }

        public double getExpenditure() {
            return expenditure;
        }

        public void setExpenditure(double expenditure) {
            this.expenditure = expenditure;
        }

        public double getIncome() {
            return income;
        }

        public void setIncome(double income) {
            this.income = income;
        }

        public int getLoseCount() {
            return loseCount;
        }

        public void setLoseCount(int loseCount) {
            this.loseCount = loseCount;
        }

        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
            this.total = total;
        }

        public int getWinCount() {
            return winCount;
        }

        public void setWinCount(int winCount) {
            this.winCount = winCount;
        }
    }
}
