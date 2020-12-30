package cn.suozhi.DiBi.home.model;

import java.util.List;

/**
 * 功能描述：工单详情页面
 */
public class WorkOrderDetailEntity {



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

    public static class DataBean {
        /**
         * content : string
         * createBy : string
         * createTime : 2019-07-29T08:54:41.616Z
         * orderNumber : string
         * replyContent : string
         * replyUser : string
         * status : 0
         * sysWorkSheetPicture : [{"createTime":"2019-07-29T08:54:41.617Z","objectId":0,"objectModule":"string","picId":0,"picName":"string","picPath":"string","picType":"string","sort":0,"updateTime":"2019-07-29T08:54:41.617Z"}]
         * sysWorkSheetsReplys : [{"createBy":"string","createTime":"2019-07-29T08:54:41.617Z","replyContent":"string","replyId":0,"updateBy":"string","updateTime":"2019-07-29T08:54:41.617Z","userId":0,"userName":"string","workSheetId":0}]
         * title : string
         * type : 0
         * updateBy : string
         * updateTime : 2019-07-29T08:54:41.617Z
         * userCode : string
         * userId : 0
         * userName : string
         * workSheetId : 0
         */

        private String content;
        private String createBy;
        private String createTime;
        private String orderNumber;
        private String replyContent;
        private String replyUser;
        private int status;
        private String title;
        private int type;
        private String updateBy;
        private String updateTime;
        private String userCode;
        private int userId;
        private String userName;
        private int workSheetId;
        private List<SysWorkSheetPictureBean> sysWorkSheetPicture;
        private List<SysWorkSheetsReplysBean> sysWorkSheetsReplys;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getCreateBy() {
            return createBy;
        }

        public void setCreateBy(String createBy) {
            this.createBy = createBy;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getOrderNumber() {
            return orderNumber;
        }

        public void setOrderNumber(String orderNumber) {
            this.orderNumber = orderNumber;
        }

        public String getReplyContent() {
            return replyContent;
        }

        public void setReplyContent(String replyContent) {
            this.replyContent = replyContent;
        }

        public String getReplyUser() {
            return replyUser;
        }

        public void setReplyUser(String replyUser) {
            this.replyUser = replyUser;
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

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getUpdateBy() {
            return updateBy;
        }

        public void setUpdateBy(String updateBy) {
            this.updateBy = updateBy;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getUserCode() {
            return userCode;
        }

        public void setUserCode(String userCode) {
            this.userCode = userCode;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public int getWorkSheetId() {
            return workSheetId;
        }

        public void setWorkSheetId(int workSheetId) {
            this.workSheetId = workSheetId;
        }

        public List<SysWorkSheetPictureBean> getSysWorkSheetPicture() {
            return sysWorkSheetPicture;
        }

        public void setSysWorkSheetPicture(List<SysWorkSheetPictureBean> sysWorkSheetPicture) {
            this.sysWorkSheetPicture = sysWorkSheetPicture;
        }

        public List<SysWorkSheetsReplysBean> getSysWorkSheetsReplys() {
            return sysWorkSheetsReplys;
        }

        public void setSysWorkSheetsReplys(List<SysWorkSheetsReplysBean> sysWorkSheetsReplys) {
            this.sysWorkSheetsReplys = sysWorkSheetsReplys;
        }

        public static class SysWorkSheetPictureBean {
            /**
             * createTime : 2019-07-29T08:54:41.617Z
             * objectId : 0
             * objectModule : string
             * picId : 0
             * picName : string
             * picPath : string
             * picType : string
             * sort : 0
             * updateTime : 2019-07-29T08:54:41.617Z
             */

            private String createTime;
            private int objectId;
            private String objectModule;
            private int picId;
            private String picName;
            private String picPath;
            private String picType;
            private int sort;
            private String updateTime;

            public String getCreateTime() {
                return createTime;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }

            public int getObjectId() {
                return objectId;
            }

            public void setObjectId(int objectId) {
                this.objectId = objectId;
            }

            public String getObjectModule() {
                return objectModule;
            }

            public void setObjectModule(String objectModule) {
                this.objectModule = objectModule;
            }

            public int getPicId() {
                return picId;
            }

            public void setPicId(int picId) {
                this.picId = picId;
            }

            public String getPicName() {
                return picName;
            }

            public void setPicName(String picName) {
                this.picName = picName;
            }

            public String getPicPath() {
                return picPath;
            }

            public void setPicPath(String picPath) {
                this.picPath = picPath;
            }

            public String getPicType() {
                return picType;
            }

            public void setPicType(String picType) {
                this.picType = picType;
            }

            public int getSort() {
                return sort;
            }

            public void setSort(int sort) {
                this.sort = sort;
            }

            public String getUpdateTime() {
                return updateTime;
            }

            public void setUpdateTime(String updateTime) {
                this.updateTime = updateTime;
            }
        }

        public static class SysWorkSheetsReplysBean {
            /**
             * createBy : string
             * createTime : 2019-07-29T08:54:41.617Z
             * replyContent : string
             * replyId : 0
             * updateBy : string
             * updateTime : 2019-07-29T08:54:41.617Z
             * userId : 0
             * userName : string
             * workSheetId : 0
             */

            private String createBy;
            private String createTime;
            private String replyContent;
            private int replyId;
            private String updateBy;
            private String updateTime;
            private int userId;
            private String userName;
            private int workSheetId;

            public String getCreateBy() {
                return createBy;
            }

            public void setCreateBy(String createBy) {
                this.createBy = createBy;
            }

            public String getCreateTime() {
                return createTime;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }

            public String getReplyContent() {
                return replyContent;
            }

            public void setReplyContent(String replyContent) {
                this.replyContent = replyContent;
            }

            public int getReplyId() {
                return replyId;
            }

            public void setReplyId(int replyId) {
                this.replyId = replyId;
            }

            public String getUpdateBy() {
                return updateBy;
            }

            public void setUpdateBy(String updateBy) {
                this.updateBy = updateBy;
            }

            public String getUpdateTime() {
                return updateTime;
            }

            public void setUpdateTime(String updateTime) {
                this.updateTime = updateTime;
            }

            public int getUserId() {
                return userId;
            }

            public void setUserId(int userId) {
                this.userId = userId;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public int getWorkSheetId() {
                return workSheetId;
            }

            public void setWorkSheetId(int workSheetId) {
                this.workSheetId = workSheetId;
            }
        }
    }
}
