package cn.suozhi.DiBi.home.model;

import java.util.List;

/**
 * 邀请好友列表
 */
public class FriendEntity {

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
         * inviteCode : 9997600
         * inviteUrl : http://47.244.45.1
         * inviteUserList : {}
         */

        private String inviteCode;
        private String inviteUrl;
        private String h5InviteUrl;
        private UserEntity inviteUserList;

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

        public UserEntity getInviteUserList() {
            return inviteUserList;
        }

        public void setInviteUserList(UserEntity inviteUserList) {
            this.inviteUserList = inviteUserList;
        }

        public static class UserEntity {
            /**
             * records : []
             * total : 1
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
            private List<RecordsEntity> records;

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

            public List<RecordsEntity> getRecords() {
                return records;
            }

            public void setRecords(List<RecordsEntity> records) {
                this.records = records;
            }

            public static class RecordsEntity {
                /**
                 * userCode : 939079
                 * userType : 0
                 * pic : null
                 * country : 中国
                 * createdDate : 2019-07-12 11:27:36
                 */

                private String userCode;
                private int userType;
                private String pic;
                private String country;
                private String createdDate;

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

                public String getPic() {
                    return pic;
                }

                public void setPic(String pic) {
                    this.pic = pic;
                }

                public String getCountry() {
                    return country;
                }

                public void setCountry(String country) {
                    this.country = country;
                }

                public String getCreatedDate() {
                    return createdDate;
                }

                public void setCreatedDate(String createdDate) {
                    this.createdDate = createdDate;
                }
            }
        }
    }
}
