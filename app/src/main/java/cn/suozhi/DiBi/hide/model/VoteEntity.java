package cn.suozhi.DiBi.hide.model;

import java.util.List;

public class VoteEntity {

    /**
     * code : 0
     * data : {"current":0,"pages":0,"records":[{"activityId":0,"beginTime":0,"endTime":0,"isWin":0,"projectList":[{"currencyCode":"string","currencyId":0,"getVotes":0,"intro":"string","logo":"string","projectId":0,"showCode":"string","takePartNum":0}],"round":0,"status":0,"totalVotes":0,"winProject":{"currencyCode":"string","currencyId":0,"getVotes":0,"intro":"string","logo":"string","projectId":0,"showCode":"string","takePartNum":0}}],"searchCount":true,"size":0,"total":0}
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

    public static class DataBean {
        /**
         * current : 0
         * pages : 0
         * records : [{"activityId":0,"beginTime":0,"endTime":0,"isWin":0,"projectList":[{"currencyCode":"string","currencyId":0,"getVotes":0,"intro":"string","logo":"string","projectId":0,"showCode":"string","takePartNum":0}],"round":0,"status":0,"totalVotes":0,"winProject":{"currencyCode":"string","currencyId":0,"getVotes":0,"intro":"string","logo":"string","projectId":0,"showCode":"string","takePartNum":0}}]
         * searchCount : true
         * size : 0
         * total : 0
         */

        private int current;
        private int pages;
        private boolean searchCount;
        private int size;
        private int total;
        private List<RecordsBean> records;

        public int getCurrent() {
            return current;
        }

        public void setCurrent(int current) {
            this.current = current;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public boolean isSearchCount() {
            return searchCount;
        }

        public void setSearchCount(boolean searchCount) {
            this.searchCount = searchCount;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<RecordsBean> getRecords() {
            return records;
        }

        public void setRecords(List<RecordsBean> records) {
            this.records = records;
        }

        public static class RecordsBean {
            /**
             * activityId : 0
             * beginTime : 0
             * endTime : 0
             * isWin : 0
             * has : 0
             * projectList : [{"currencyCode":"string","currencyId":0,"getVotes":0,"intro":"string","logo":"string","projectId":0,"showCode":"string","takePartNum":0}]
             * round : 0
             * status : 0
             * totalVotes : 0
             * winProject : {"currencyCode":"string","currencyId":0,"getVotes":0,"intro":"string","logo":"string","projectId":0,"showCode":"string","takePartNum":0}
             */

            private int activityId;
            private long beginTime;
            private long endTime;
            private int isWin;
            private int has;
            private int round;
            private int status;
            private double totalVotes;
            private WinProjectBean winProject;
            private List<ProjectListBean> projectList;

            private int loadType;

            public RecordsBean(int loadType) {
                this.loadType = loadType;
            }

            public int getActivityId() {
                return activityId;
            }

            public void setActivityId(int activityId) {
                this.activityId = activityId;
            }

            public long getBeginTime() {
                return beginTime;
            }

            public void setBeginTime(long beginTime) {
                this.beginTime = beginTime;
            }

            public long getEndTime() {
                return endTime;
            }

            public void setEndTime(long endTime) {
                this.endTime = endTime;
            }

            public int getIsWin() {
                return isWin;
            }

            public void setIsWin(int isWin) {
                this.isWin = isWin;
            }

            public int getRound() {
                return round;
            }

            public void setRound(int round) {
                this.round = round;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public double getTotalVotes() {
                return totalVotes;
            }

            public void setTotalVotes(double totalVotes) {
                this.totalVotes = totalVotes;
            }

            public WinProjectBean getWinProject() {
                return winProject;
            }

            public void setWinProject(WinProjectBean winProject) {
                this.winProject = winProject;
            }

            public List<ProjectListBean> getProjectList() {
                return projectList;
            }

            public void setProjectList(List<ProjectListBean> projectList) {
                this.projectList = projectList;
            }

            public int getHas() {
                return has;
            }

            public void setHas(int has) {
                this.has = has;
            }

            public int getLoadType() {
                return loadType;
            }

            public void setLoadType(int loadType) {
                this.loadType = loadType;
            }

            public static class WinProjectBean {
                /**
                 * currencyCode : string
                 * currencyId : 0
                 * getVotes : 0
                 * intro : string
                 * logo : string
                 * projectId : 0
                 * showCode : string
                 * takePartNum : 0
                 */

                private String currencyCode;
                private int currencyId;
                private double getVotes;
                private String intro;
                private String logo;
                private int projectId;
                private String showCode;
                private int takePartNum;

                public String getCurrencyCode() {
                    return currencyCode;
                }

                public void setCurrencyCode(String currencyCode) {
                    this.currencyCode = currencyCode;
                }

                public int getCurrencyId() {
                    return currencyId;
                }

                public void setCurrencyId(int currencyId) {
                    this.currencyId = currencyId;
                }

                public double getGetVotes() {
                    return getVotes;
                }

                public void setGetVotes(double getVotes) {
                    this.getVotes = getVotes;
                }

                public String getIntro() {
                    return intro;
                }

                public void setIntro(String intro) {
                    this.intro = intro;
                }

                public String getLogo() {
                    return logo;
                }

                public void setLogo(String logo) {
                    this.logo = logo;
                }

                public int getProjectId() {
                    return projectId;
                }

                public void setProjectId(int projectId) {
                    this.projectId = projectId;
                }

                public String getShowCode() {
                    return showCode;
                }

                public void setShowCode(String showCode) {
                    this.showCode = showCode;
                }

                public int getTakePartNum() {
                    return takePartNum;
                }

                public void setTakePartNum(int takePartNum) {
                    this.takePartNum = takePartNum;
                }
            }

            public static class ProjectListBean {
                /**
                 * currencyCode : string
                 * currencyId : 0
                 * getVotes : 0
                 * intro : string
                 * logo : string
                 * projectId : 0
                 * showCode : string
                 * takePartNum : 0
                 */

                private String currencyCode;
                private int currencyId;
                private double getVotes;
                private String intro;
                private String logo;
                private int projectId;
                private String showCode;
                private int takePartNum;

                public String getCurrencyCode() {
                    return currencyCode;
                }

                public void setCurrencyCode(String currencyCode) {
                    this.currencyCode = currencyCode;
                }

                public int getCurrencyId() {
                    return currencyId;
                }

                public void setCurrencyId(int currencyId) {
                    this.currencyId = currencyId;
                }

                public double getGetVotes() {
                    return getVotes;
                }

                public void setGetVotes(double getVotes) {
                    this.getVotes = getVotes;
                }

                public String getIntro() {
                    return intro;
                }

                public void setIntro(String intro) {
                    this.intro = intro;
                }

                public String getLogo() {
                    return logo;
                }

                public void setLogo(String logo) {
                    this.logo = logo;
                }

                public int getProjectId() {
                    return projectId;
                }

                public void setProjectId(int projectId) {
                    this.projectId = projectId;
                }

                public String getShowCode() {
                    return showCode;
                }

                public void setShowCode(String showCode) {
                    this.showCode = showCode;
                }

                public int getTakePartNum() {
                    return takePartNum;
                }

                public void setTakePartNum(int takePartNum) {
                    this.takePartNum = takePartNum;
                }
            }
        }
    }
}
