package cn.suozhi.DiBi.home.model;

import java.util.List;

/**
 * 创建时间：2019-07-24 18:26
 * 作者：Lich_Cool
 * 邮箱：licheng@ld.chainsdir.com
 * 功能描述：站内信实体类
 */
public class MsgListEntity {

    private long code;
    private String msg;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * records : []
         * total : 11
         * size : 10
         * current : 1
         * searchCount : true
         * pages : 2
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
             * id : 6
             * userId : 1
             * source : 2
             * title : C2认证审核通过
             * content : 恭喜您通过C2认证申请，当前实名认证等级为C2。
             * createTime : 2019-07-02 22:20:59
             * createUser : 1
             * status : 1
             */

            private long id;
            private long userId;
            private long source;
            private String title;
            private String content;
            private String createTime;
            private Object createUser;
            private int status;
            private boolean isExpanded;

            private int type;

            public RecordsBean(int type) {
                this.type = type;
            }

            public int getType() {
                return type;
            }
            public void setType(int type) {
                this.type = type;
            }
            public boolean isExpanded() {
                return isExpanded;
            }

            public void setExpanded(boolean expanded) {
                isExpanded = expanded;
            }

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public long getUserId() {
                return userId;
            }

            public void setUserId(long userId) {
                this.userId = userId;
            }

            public long getSource() {
                return source;
            }

            public void setSource(long source) {
                this.source = source;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getCreateTime() {
                return createTime;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }

            public Object getCreateUser() {
                return createUser;
            }

            public void setCreateUser(Object createUser) {
                this.createUser = createUser;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }
        }
    }
}
