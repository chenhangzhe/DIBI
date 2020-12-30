package cn.suozhi.DiBi.home.model;

import java.util.List;

public class IeoEntity {

    /**
     * code : 0
     * msg : 成功
     * data : {"records":[{"ieoId":4,"logo":"https://www.dibic.net/img//images/common/2020-04-03/158589962864131680.png","projectName":"Dic申购","baseCurrencyCode":"DIC","quoteCurrencyCode":"USDT","totalAmount":1000000,"surplusAmount":1000000,"ieoPrice":0.142,"ieoNum":2000000,"startDate":"2020-04-03 16:40:03","endDate":"2020-04-04 00:00:00","status":0},{"ieoId":3,"logo":"https://www.dibic.net/img//images/common/2020-04-02/158579837609968220.png","projectName":"项目名称& #40;多语种& #41;","baseCurrencyCode":"ZMM","quoteCurrencyCode":"USDT","totalAmount":1.0E7,"surplusAmount":9990000,"ieoPrice":1,"ieoNum":1.0E7,"startDate":"2020-04-02 11:30:05","endDate":"2020-05-09 00:00:00","status":2}],"total":2,"size":15,"current":1,"searchCount":true,"pages":1}
     */

    private int code;
    private String msg;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * records : [{"ieoId":4,"logo":"https://www.dibic.net/img//images/common/2020-04-03/158589962864131680.png","projectName":"Dic申购","baseCurrencyCode":"DIC","quoteCurrencyCode":"USDT","totalAmount":1000000,"surplusAmount":1000000,"ieoPrice":0.142,"ieoNum":2000000,"startDate":"2020-04-03 16:40:03","endDate":"2020-04-04 00:00:00","status":0},{"ieoId":3,"logo":"https://www.dibic.net/img//images/common/2020-04-02/158579837609968220.png","projectName":"项目名称& #40;多语种& #41;","baseCurrencyCode":"ZMM","quoteCurrencyCode":"USDT","totalAmount":1.0E7,"surplusAmount":9990000,"ieoPrice":1,"ieoNum":1.0E7,"startDate":"2020-04-02 11:30:05","endDate":"2020-05-09 00:00:00","status":2}]
         * total : 2
         * size : 15
         * current : 1
         * searchCount : true
         * pages : 1
         */

        private int total;
        private int size;
        private int current;
        private boolean searchCount;
        private int pages;
        private List<RecordsBean> records;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getCurrent() {
            return current;
        }

        public void setCurrent(int current) {
            this.current = current;
        }

        public boolean isSearchCount() {
            return searchCount;
        }

        public void setSearchCount(boolean searchCount) {
            this.searchCount = searchCount;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public List<RecordsBean> getRecords() {
            return records;
        }

        public void setRecords(List<RecordsBean> records) {
            this.records = records;
        }

        public static class RecordsBean {
            /**
             * ieoId : 4
             * logo : https://www.dibic.net/img//images/common/2020-04-03/158589962864131680.png
             * projectName : Dic申购
             * baseCurrencyCode : DIC
             * quoteCurrencyCode : USDT
             * totalAmount : 1000000
             * surplusAmount : 1000000
             * ieoPrice : 0.142
             * ieoNum : 2000000
             * startDate : 2020-04-03 16:40:03
             * endDate : 2020-04-04 00:00:00
             * status : 0
             */

            private int ieoId;
            private String logo;
            private String projectName;
            private String baseCurrencyCode;
            private String quoteCurrencyCode;
            private double totalAmount;
            private double surplusAmount;
            private double ieoPrice;
            private double ieoNum;
            private String startDate; // 开始时间
            private String endDate; // 结束时间
            private int status;

            // ---------------------------------------------------------
            // 定义当前系统时间
            private String nowTime;
            // 计算出的活动结束时间和当前系统时间差 单位:ms 毫秒
            private long endCountTime;
            // 计算出的活动开始时间和当前系统时间差 单位:ms 毫秒
            private long startCountTime;
            // 储存每个子项的倒计时的时间格式 并用TV显示
            private String startShowTime;
            private String endShowTime;
            // ---------------------------------------------------------

            private int loadType;

            public RecordsBean(int loadType) {
                this.loadType = loadType;
            }

            public int getIeoId() {
                return ieoId;
            }

            public void setIeoId(int ieoId) {
                this.ieoId = ieoId;
            }

            public String getLogo() {
                return logo;
            }

            public void setLogo(String logo) {
                this.logo = logo;
            }

            public String getProjectName() {
                return projectName;
            }

            public void setProjectName(String projectName) {
                this.projectName = projectName;
            }

            public String getBaseCurrencyCode() {
                return baseCurrencyCode;
            }

            public void setBaseCurrencyCode(String baseCurrencyCode) {
                this.baseCurrencyCode = baseCurrencyCode;
            }

            public String getQuoteCurrencyCode() {
                return quoteCurrencyCode;
            }

            public void setQuoteCurrencyCode(String quoteCurrencyCode) {
                this.quoteCurrencyCode = quoteCurrencyCode;
            }

            public double getTotalAmount() {
                return totalAmount;
            }

            public void setTotalAmount(int totalAmount) {
                this.totalAmount = totalAmount;
            }

            public double getSurplusAmount() {
                return surplusAmount;
            }

            public void setSurplusAmount(int surplusAmount) {
                this.surplusAmount = surplusAmount;
            }

            public double getIeoPrice() {
                return ieoPrice;
            }

            public void setIeoPrice(double ieoPrice) {
                this.ieoPrice = ieoPrice;
            }

            public double getIeoNum() {
                return ieoNum;
            }

            public void setIeoNum(int ieoNum) {
                this.ieoNum = ieoNum;
            }

            public String getStartDate() {
                return startDate;
            }

            public void setStartDate(String startDate) {
                this.startDate = startDate;
            }

            public String getEndDate() {
                return endDate;
            }

            public void setEndDate(String endDate) {
                this.endDate = endDate;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public int getLoadType() {
                return loadType;
            }

            public void setLoadType(int loadType) {
                this.loadType = loadType;
            }

            public String getNowTime() {
                return nowTime;
            }

            public void setNowTime(String nowTime) {
                this.nowTime = nowTime;
            }

            public long getEndCountTime() {
                return endCountTime;
            }

            public void setEndCountTime(long endCountTime) {
                this.endCountTime = endCountTime;
            }

            public long getStartCountTime() {
                return startCountTime;
            }

            public void setStartCountTime(long startCountTime) {
                this.startCountTime = startCountTime;
            }

            public String getStartShowTime() {
                return startShowTime;
            }

            public void setStartShowTime(String startShowTime) {
                this.startShowTime = startShowTime;
            }

            public String getEndShowTime() {
                return endShowTime;
            }

            public void setEndShowTime(String endShowTime) {
                this.endShowTime = endShowTime;
            }
        }
    }
}
