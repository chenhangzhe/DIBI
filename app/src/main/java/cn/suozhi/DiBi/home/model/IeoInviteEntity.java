package cn.suozhi.DiBi.home.model;

import java.util.List;

public class IeoInviteEntity {

    /**
     * code : 0
     * msg : 成功
     * data : {"inviteCode":"2014195","inviteUrl":"https://www.dibic.net/#/register?invite=2014195","h5InviteUrl":"https://www.dibic.net/#/h5/register?invite=2014195","inviteUserList":{"records":[{"userCode":"18070906858","userType":1,"pic":null,"country":null,"createdDate":"2020-04-03 20:01:44","ieoExchangeAmountList":[{"currencyCode":"USDT","amount":8603},{"currencyCode":"DIC","amount":1000}],"ieoExchangeAmountStr":"\n1000.00000000 DIC"},{"userCode":"18776141844","userType":2,"pic":null,"country":"中国","createdDate":"2020-03-24 11:05:16","ieoExchangeAmountList":[{"currencyCode":"USDT","amount":443.64}],"ieoExchangeAmountStr":"443.64000000 USDT"}],"total":2,"size":15,"current":1,"searchCount":true,"pages":1}}
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
         * inviteCode : 2014195
         * inviteUrl : https://www.dibic.net/#/register?invite=2014195
         * h5InviteUrl : https://www.dibic.net/#/h5/register?invite=2014195
         * inviteUserList : {"records":[{"userCode":"18070906858","userType":1,"pic":null,"country":null,"createdDate":"2020-04-03 20:01:44","ieoExchangeAmountList":[{"currencyCode":"USDT","amount":8603},{"currencyCode":"DIC","amount":1000}],"ieoExchangeAmountStr":"\n1000.00000000 DIC"},{"userCode":"18776141844","userType":2,"pic":null,"country":"中国","createdDate":"2020-03-24 11:05:16","ieoExchangeAmountList":[{"currencyCode":"USDT","amount":443.64}],"ieoExchangeAmountStr":"443.64000000 USDT"}],"total":2,"size":15,"current":1,"searchCount":true,"pages":1}
         */

        private String inviteCode;
        private String inviteUrl;
        private String h5InviteUrl;
        private InviteUserListBean inviteUserList;

        public String getInviteCode() {
            return inviteCode;
        }

        public void setInviteCode(String inviteCode) {
            this.inviteCode = inviteCode;
        }

        public String getInviteUrl() {
            return inviteUrl;
        }

        public void setInviteUrl(String inviteUrl) {
            this.inviteUrl = inviteUrl;
        }

        public String getH5InviteUrl() {
            return h5InviteUrl;
        }

        public void setH5InviteUrl(String h5InviteUrl) {
            this.h5InviteUrl = h5InviteUrl;
        }

        public InviteUserListBean getInviteUserList() {
            return inviteUserList;
        }

        public void setInviteUserList(InviteUserListBean inviteUserList) {
            this.inviteUserList = inviteUserList;
        }

        public static class InviteUserListBean {
            /**
             * records : [{"userCode":"18070906858","userType":1,"pic":null,"country":null,"createdDate":"2020-04-03 20:01:44","ieoExchangeAmountList":[{"currencyCode":"USDT","amount":8603},{"currencyCode":"DIC","amount":1000}],"ieoExchangeAmountStr":"\n1000.00000000 DIC"},{"userCode":"18776141844","userType":2,"pic":null,"country":"中国","createdDate":"2020-03-24 11:05:16","ieoExchangeAmountList":[{"currencyCode":"USDT","amount":443.64}],"ieoExchangeAmountStr":"443.64000000 USDT"}]
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
                 * userCode : 18070906858
                 * userType : 1
                 * pic : null
                 * country : null
                 * createdDate : 2020-04-03 20:01:44
                 * ieoExchangeAmountList : [{"currencyCode":"USDT","amount":8603},{"currencyCode":"DIC","amount":1000}]
                 * ieoExchangeAmountStr :
                 1000.00000000 DIC
                 */

                private String userCode;
                private int userType;
                private Object pic;
                private Object country;
                private String createdDate;
                private String ieoExchangeAmountStr;
                private List<IeoExchangeAmountListBean> ieoExchangeAmountList;

                public String getUserCode() {
                    return userCode;
                }

                public void setUserCode(String userCode) {
                    this.userCode = userCode;
                }

                public int getUserType() {
                    return userType;
                }

                public void setUserType(int userType) {
                    this.userType = userType;
                }

                public Object getPic() {
                    return pic;
                }

                public void setPic(Object pic) {
                    this.pic = pic;
                }

                public Object getCountry() {
                    return country;
                }

                public void setCountry(Object country) {
                    this.country = country;
                }

                public String getCreatedDate() {
                    return createdDate;
                }

                public void setCreatedDate(String createdDate) {
                    this.createdDate = createdDate;
                }

                public String getIeoExchangeAmountStr() {
                    return ieoExchangeAmountStr;
                }

                public void setIeoExchangeAmountStr(String ieoExchangeAmountStr) {
                    this.ieoExchangeAmountStr = ieoExchangeAmountStr;
                }

                public List<IeoExchangeAmountListBean> getIeoExchangeAmountList() {
                    return ieoExchangeAmountList;
                }

                public void setIeoExchangeAmountList(List<IeoExchangeAmountListBean> ieoExchangeAmountList) {
                    this.ieoExchangeAmountList = ieoExchangeAmountList;
                }

                public static class IeoExchangeAmountListBean {
                    /**
                     * currencyCode : USDT
                     * amount : 8603
                     */

                    private String currencyCode;
                    private double amount;

                    public String getCurrencyCode() {
                        return currencyCode;
                    }

                    public void setCurrencyCode(String currencyCode) {
                        this.currencyCode = currencyCode;
                    }

                    public double getAmount() {
                        return amount;
                    }

                    public void setAmount(int amount) {
                        this.amount = amount;
                    }
                }
            }
        }
    }
}
