package cn.suozhi.DiBi.home.model;

public class IeoDetailsEntity {

    /**
     * code : 0
     * msg : 成功
     * data : {"ieoId":4,"logo":"https://www.dibic.net/img//images/common/2020-04-03/158589962864131680.png","projectName":"Dic申购","baseCurrencyCode":"DIC","quoteCurrencyCode":"USDT","totalAmount":1000000,"surplusAmount":1000000,"ieoPrice":0.142,"ieoNum":2000000,"startDate":"2020-04-03 16:40:03","endDate":"2020-04-04 00:00:00","status":0,"ieoIntro":"ginx的配置文件的整体结构 Nginx是通过配置文件来做到各个功能的实现的。Nginx的配置文件的格式非常合乎逻辑,学习这种格式以及如何使用这种每个部分是基础,这将帮助我们有可能手工创建一个配置文件","giveIntro":"ginx的配置文件的整体结构 Nginx是通过配置文件来做到各个功能的实现的。Nginx的配置文件的格式非常合乎逻辑,学习这种格式以及如何使用这种每个部分是基础,这将帮助我们有可能手工创建一个配置文件","riskIntro":"ginx的配置文件的整体结构 Nginx是通过配置文件来做到各个功能的实现的。Nginx的配置文件的格式非常合乎逻辑,学习这种格式以及如何使用这种每个部分是基础,这将帮助我们有可能手工创建一个配置文件","peopleNum":0,"availableAmount":0,"exchangeAmount":0,"minAmount":10,"maxAmount":10000}
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
         * ieoIntro : ginx的配置文件的整体结构 Nginx是通过配置文件来做到各个功能的实现的。Nginx的配置文件的格式非常合乎逻辑,学习这种格式以及如何使用这种每个部分是基础,这将帮助我们有可能手工创建一个配置文件
         * giveIntro : ginx的配置文件的整体结构 Nginx是通过配置文件来做到各个功能的实现的。Nginx的配置文件的格式非常合乎逻辑,学习这种格式以及如何使用这种每个部分是基础,这将帮助我们有可能手工创建一个配置文件
         * riskIntro : ginx的配置文件的整体结构 Nginx是通过配置文件来做到各个功能的实现的。Nginx的配置文件的格式非常合乎逻辑,学习这种格式以及如何使用这种每个部分是基础,这将帮助我们有可能手工创建一个配置文件
         * peopleNum : 0
         * availableAmount : 0
         * exchangeAmount : 0
         * minAmount : 10
         * maxAmount : 10000
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
        private String startDate;
        private String endDate;
        private int status;
        private String ieoIntro;
        private String giveIntro;
        private String riskIntro;
        private int peopleNum;
        private double availableAmount;
        private double exchangeAmount;
        private double minAmount;
        private double maxAmount;

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

        public String getIeoIntro() {
            return ieoIntro;
        }

        public void setIeoIntro(String ieoIntro) {
            this.ieoIntro = ieoIntro;
        }

        public String getGiveIntro() {
            return giveIntro;
        }

        public void setGiveIntro(String giveIntro) {
            this.giveIntro = giveIntro;
        }

        public String getRiskIntro() {
            return riskIntro;
        }

        public void setRiskIntro(String riskIntro) {
            this.riskIntro = riskIntro;
        }

        public int getPeopleNum() {
            return peopleNum;
        }

        public void setPeopleNum(int peopleNum) {
            this.peopleNum = peopleNum;
        }

        public double getAvailableAmount() {
            return availableAmount;
        }

        public void setAvailableAmount(int availableAmount) {
            this.availableAmount = availableAmount;
        }

        public double getExchangeAmount() {
            return exchangeAmount;
        }

        public void setExchangeAmount(int exchangeAmount) {
            this.exchangeAmount = exchangeAmount;
        }

        public double getMinAmount() {
            return minAmount;
        }

        public void setMinAmount(int minAmount) {
            this.minAmount = minAmount;
        }

        public double getMaxAmount() {
            return maxAmount;
        }

        public void setMaxAmount(int maxAmount) {
            this.maxAmount = maxAmount;
        }
    }
}
