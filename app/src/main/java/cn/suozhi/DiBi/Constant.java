package cn.suozhi.DiBi;

/**
 * 常量
 */
public interface Constant {

//    boolean Release = false;
    boolean Release = true;
//
    interface URL {

        // 滑块
        String BaseScroll = Release ? "https://www.dibic.net" : "https://www.dibic.net";
        // 接口地址
        String BaseUrl = Release ? "https://restapi.dibic.net/user" : "http://8.135.100.114:9005/user";
        String BaseGame = Release ? "https://restapi.dibic.net/game" :"http://washtraderest.nnszwl.com/game";
        String BaseC2CUrl = Release ? "https://restapi.dibic.net/c2c" :"http://washtraderest.nnszwl.com/c2c";
        // socket
        String Socket = Release ? "wss://ws.dibic.net/websocket" : "ws://8.135.100.114:8091/websocket";

        // 滑动验证ws://8.135.100.114:8091/websocket
        String ScrollVerify = BaseScroll + "/nc.html";

        //查询版本
        String GetAppVersion = BaseUrl + "/v1/system/versions/getVersion/2";
        //获取Banner
        String GetBanner = BaseUrl + "/v1/index/bannerList";
        //公告
        String GetNotify = BaseUrl + "/v1/announcement/list";
        //公告详情
        String GetNotifyDetail = BaseUrl + "/v1/announcement/info/";

        //获取用户信息
        String GetGameUser = BaseGame + "/api/getGameUserInfo";
        //更新用户游戏昵称
        String UpdateGameName = BaseGame + "/api/updateNikeName";
        //获取游戏登入参数
        String GetGame = BaseGame + "/api/getUrl";
        //获取用户返水余额
        String GetRebate = BaseGame + "/api/getRebateAmount";

        //预测合约交割
        String GetDeliver = BaseUrl + "/v1/future/deliveryList";
        //预测合约资金流水
        String GetFlow = BaseUrl + "/v1/future/fund/flowRecord";
        //预测合约分析
        String GetAnalysis = BaseUrl + "/v1/future/recordSummary";

        //基本信息
        String GetInfo = BaseUrl + "/v1/user/info";
        //修改个人信息
        String UpdateInfo = BaseUrl + "/v1/user/updateInfo";
        //上传图片
        String Upload = BaseUrl + "/v1/file/upload";
        //获取下拉列表
        String GetSelect = BaseUrl + "/v1/sys/dict/selectList";
        //C1认证
        String C1 = BaseUrl + "/v1/user/firstIdentity";
        //快捷登录密码验证
        String Quck = BaseUrl +"/v1/user/verifypassword";
        //C2认证
        String C2 = BaseUrl + "/v1/user/secondIdentity";
        //中国国籍C2认证人脸识别
        String GetFaceToken = BaseUrl + "/v1/user/faceDetect";
        //人脸识别成功回调
        String FaceVerify = BaseUrl + "/v1/user/describeVerifyResult";
        //获取未读数量
        String GetUnread = BaseUrl + "/userMessage/getUnreadCount";
        //登出
        String Logout = BaseUrl + "/v1/user/logout";

        //绑定操作
        String Bind = BaseUrl + "/v1/user/bind";
        //获取谷歌安全绑定Key
        String GetGoogleKey = BaseUrl + "/v1/user/gaSecurityKey";
        //解绑谷歌验证码操作
        String UnbindGoogle = BaseUrl + "/v1/user/unbindGa";
        //重置登录密码
        String ResetPassword = BaseUrl + "/v1/user/resetPwd";
        //邀请好友列表
        String GetInviteList = BaseUrl + "/v1/user/inviteList";
        //返佣记录
        String GetCommissionList = BaseUrl + "/rakeBack/list";
        //返佣总计
        String GetCommission = BaseUrl + "/rakeBack/summary";


        //手机区号
        String distrctCode = BaseUrl + "/v1/sys/dict/findAreaCode";
        //注册
        String register = BaseUrl + "/v1/user/register";
        //获取一次性token
        String onceToken = BaseUrl + "/v1/captcha/createOnceToken";
        //邮箱验证码 - 需要token
        String SendEmail = BaseUrl + "/v1/captcha/sendEmail";
        //手机验证码 - 需要token
        String SendPhone = BaseUrl + "/v1/captcha/sendPhone";
        //发送登录(L)、注册(S)、忘记密码(R)类邮件验证码
        String SendEmailCode = BaseUrl + "/v1/captcha/sendEmailCode";
        //发送登录(L)、注册(S)、忘记密码(R)类短信验证码
        String SendPhoneCode = BaseUrl + "/v1/captcha/sendPhoneCode";
        //发送验证码 - 预登录后
        String SendLoginEmail = BaseUrl + "/v1/captcha/sendLoginEmailCode";
        //发送验证码 - 预登录后
        String SendLoginPhone = BaseUrl + "/v1/captcha/sendLoginPhoneCode";
        //重置登录密码
        String forgetPsw = BaseUrl + "/v1/user/forgetPwd";
        //预登陆
        String preLogin = BaseUrl + "/v1/user/preLogin";
        //登录
        String login = BaseUrl + "/v1/user/login";
        //钱包首页币种列表
        String walletCoin = BaseUrl + "/v1/account/list";
        //钱包首页  所有资产
        String coinAssert = BaseUrl + "/v1/account/assetDistribution";
        //提币地址列表
        String coinAddress = BaseUrl + "/v1/account/addressTagList";
        //添加币种地址
        String addCoinAddress = BaseUrl + "/v1/account/createOrEditAddressTag";
        //删除币种地址
        String deleteCoinAddress = BaseUrl + "/v1/account/deleteAddressTag/";
        //币种列表
        String coinList = BaseUrl + "/v1/currency/list";
        //充币
        String chargeCoin = BaseUrl + "/v1/account/deposit";
        //提币
        String getCoin = BaseUrl + "/v1/account/withdraw";
        //充提记录
        String accountOrder = BaseUrl + "/v1/account/transactionList";
        //获取用户收款信息
        String collectMoneyType = BaseUrl + "/v1/payment/payList";
        //添加收款方式
        String addCollectMoneyType = BaseUrl + "/v1/payment/createOrUpdate";
        //用户单个币种信息
        String singleCoinInfo = BaseUrl + "/v1/account/findByCurrencyId";
        String singleCoinInfoU = BaseUrl + "/v1/account/findByCurrencyId";
        String singleCoinInfoE = BaseUrl + "/v1/account/findByCurrencyId";
        String singleCoinInfoByName = BaseUrl + "/v1/account/findByCurrencyCode";
        //登录日志
        String loginLogcat = BaseUrl + "/v1/user/signInLog";
        //用户最近充提币种
        String chargeGetCoin = BaseUrl + "/v1/account/findLatestCurrency";
        //获取申诉订单详情
        String complaintDetail = BaseC2CUrl + "/v1/appeal/getAppeal";
        //订单申诉
        String complaintOrder = BaseC2CUrl + "/v1/appeal/saveAppeal";
        //发布广告
        String publishAdvance = BaseC2CUrl + "/v1/advert/create";
        //获取可用的支付方式
        String getPayType = BaseC2CUrl + "/v1/payment/userPayMode";
        //我的广告
        String myAdvance = BaseC2CUrl + "/v1/advert/myAd";
        //我的订单
        String myOrder = BaseC2CUrl + "/v1/order/list";
        //买卖列表
        String sellAndBuyList = BaseC2CUrl + "/v1/advert/list";
        //下单界面购买信息
        String adbussinessInfo = BaseC2CUrl + "/v1/advert/info";
        //确定下单
        String comfirmOrder = BaseC2CUrl + "/v1/order/create";
        //订单详情
        String orderDetail = BaseC2CUrl + "/v1/order/detail";
        //取消订单
        String cancelOrder = BaseC2CUrl + "/v1/order/cancel";
        //确认支付
        String comfirmPay = BaseC2CUrl + "/v1/order/pay";
        //确认收款
        String comfirmCollect = BaseC2CUrl + "/v1/order/receive";
        //法币发布广告币种列表
        String coinListC2c = BaseUrl + "/v1/sys/dict/selectList";
        //获取参考价格
        String getCkPrice = BaseC2CUrl + "/v1/comm/proposedPrice";
        // 币种资料
        String coinIntro = BaseUrl + "/v1/currency/coinIntro";
        // 法币币种
        String currencies = BaseC2CUrl + "/v1/comm/currencies";
        // 法币转账
        String otcTransfer = BaseUrl + "/v1/account/otcTransfer";
        // 法币转账记录
        String otcTransferRecord = BaseUrl + "/v1/account/otcTransferRecord";
        // 确认收款
        String orderReceive = BaseC2CUrl + "/v1/order/receive";

        // 用户是否存在
        String checkUserExist = BaseUrl + "/v1/user/checkUserExist";
        // 用户资金流水
        String flowList = BaseUrl + "/v1/account/userFundList";

        // 多解一币种列表
        String ieoSpecialCurrency = BaseUrl + "/v1/currency/getSpecialCurrency";
        // 多解一转账
        String ieoTransfer = BaseUrl + "/v1/account/ieoTransfer";

        // IEO共识兑换列表
        String ieoList = BaseUrl + "/v1/ieo/list";
        // IEO共识兑换-共识详情
        String ieoDetails = BaseUrl + "/v1/ieo/info/";
        // IEO 进行共识兑换
        String ieoExchange = BaseUrl + "/v1/ieo/exchange";

        // 投票活动列表
        String voteList = BaseUrl + "/v1/vote/activityList";
        // 投票
        String doVote = BaseUrl + "/v1/vote/voteSubmit";

        //站内信
        String MsgList = BaseUrl + "/userMessage/list";
        //修改为已读
        String UpdateRead = BaseUrl + "/userMessage/updateRead";
        //提交工单
        String SubmitWorkOrder = BaseUrl + "/v1/job/create";
        //工单列表
        String WorkOrderList = BaseUrl + "/v1/job/list";
        //工单详情
        String Work_Order_Detail = BaseUrl + "/v1/job/info/";
        //帮助分类
        String GetTypeList = BaseUrl + "/v1/announcement/articleTypeList";
        //帮助搜索
        String SearchHelp = BaseUrl + "/v1/announcement/search";
        //获取帮助数量
        String Like = BaseUrl + "/v1/announcement/like/";

        //广告上架
        String advanceUp = BaseC2CUrl + "/v1/advert/enable";
        //广告下架
        String advanceDown = BaseC2CUrl + "/v1/advert/disable";
        //获取广告详情
        String advanceDetail = BaseC2CUrl + "/v1/advert/detail";
        //编辑广告
        String editAdvance = BaseC2CUrl + "/v1/advert/edit";
        //取消订单数量
        String CancelLimit = BaseC2CUrl + "/v1/order/cancelLimit";
    }

    interface Strings {
        //手机号正则
        String RegexMobile = "^1(3[0-9]|4[5,7]|5[0-9]|66|7[0-9]|8[0-9]|9[8,9])\\d{8}$";
        //邮箱正则
        String RegexEmail = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        //身份证正则
        String RegexIdNum = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$";
        //最多2位小数正则
        String RegexNum = "^\\d{1,4}|\\d{1,4}.\\d{0,2}$";
        //密码正则
        String RegexPassword = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,21}$";


        String Yuan = "¥";
        String Dollar = "$";
        String Approximate = "≈";
        String Copyright = "©";
        String China_Phone = "+86";
        String China_Nation = "cn";
        String Log = "Server_log";
        String Cache_Dir = "/Android/data/cn.suozhi.DiBi";
        String Img_Head = "data:image/png;base64,";
        String Google_Auth_Package = "com.google.android.apps.authenticator2";
        String Google_Auth_Link = "https://mobile.baidu.com/item?docid=3826380&source=mobres&from=1010680m";
        String Default_Symbol = "BTCUSDT";
        String RMB_Symbol = "CNY";
        String Banner_Notify = "newsdetail#";
        String Code_Split = "#";
        String Chain_Type = "USDT";

        //Intent传递参数
        String Intent_Id = "ID";
        String Intent_Key = "Key";
        String Intent_Choose_Coin = "Choose";
        String Intent_Choose_Recharge = "ChooseRecharge";
        String Intent_Choose_Withdraw = "ChooseWithdraw";
        String Intent_Back_Recharge = "BackRecharge";
        String Intent_Back_Withdraw = "BackWithdraw";
        String Intent_Choose_Transfer = "ChooseTransfer";
        String Intent_Back_Transfer = "BackTransfer";


        String oncetoken = "1";
        String getCoinOncetoken = "22";
        String getCancelOrderOncetoken = "33";
        String getComfirmPayOncetoken = "44";
        String getComfirmColletOncetoken = "55";
        String getAdvanceOncetoken = "";
        String Android = "2";
        String SP_DEVICES_ID = "SP_DEVICES_ID";
        String WORK_ORDER_TYPE = "work_order_type";
        String REFRESH_WORK_REDER = "refresh_work_order";
    }

    interface Int {
        //成功
        long SUC = 0;
        String SUC_S = "0";
        //普通商家
        int USER = 1;
        //商家
        int BUSSINESS = 2;

        //请重新登录
        long Please_Login = 401;
        //token无效
        long Token_Invalid = 1000001;

        //用户未登录
        String Socket_No_Login = "2010018";
        //请重新登录
        String Socket_Please_Login = "401";
        //token不存在
        String Socket_Token_No_Exist = "2010020";


        //短信验证码重试秒数
        int RetryTime = 60;
    }

    interface Code {
        int PermCode = 1;
        int PermStorageCode = 2;
        int PermCameraCode = 3;

        int JumpCode = 11;
        int EditCode = 12;
        int PayCode = 13;
        int SelectCode = 14;

        //拍照请求码
        int CameraCode = 21;
        //打开相册请求码
        int AlbumCode = 22;
        int KlineCode = 23;

        int Code_0 = 31;
        int Code_1 = 32;
        int Code_2 = 33;
        int Code_3 = 34;
        int Code_4 = 35;
        int Code_5 = 36;

        int Type_Favor = 0;
        int Type_Favor_All = 1;
        int Type_Favor_Add = 2;
        int Type_Favor_Del = 3;
        int Type_Home_Top = 4;
        int Type_Home_New = 5;
        int Type_Home_Rise = 6;
        int Type_Home_Fall = 7;

        int Type_BTC = 11;
        int Type_ETH = 12;
        int Type_USDT = 13;
        int Type_DIC = 14;
        int Type_USDTE = 15;

        int Type_Trade = 31;
        int Type_Login = 32;
        int Type_Quote = 33;
        int Type_Depth = 34;
        int Type_New_Deal = 35;

        int Type_Buy_Limit = 41;
        int Type_Buy_Market = 42;
        int Type_Buy_Target = 43;
        int Type_Sell_Limit = 44;
        int Type_Sell_Market = 45;
        int Type_Sell_Target = 46;

        int Type_Current_Entrust = 51;
        int Type_History_Entrust = 52;
        int Type_History_Deal = 53;
        int Type_Cancel_Current = 54;
        int Type_Cancel_History = 55;

        int Type_Replied = 2;//已回复
        int Type_Waited_Reply = 1;//待回复


        //币种及交易对信息
        long Master = 1000;
        //登录
        long Login = 1001;
        //登录并获取资产
        long Login_Account = 1002;
        //自选
        long Watch = 1003;
        //自选
        long Watch_All = 1004;
        //自选编辑
        long Watch_Edit = 1005;
        //搜索
        long Search = 1006;
        //自选Popup
        long Watch_Pop = 1007;
        //取消订阅
        long Stop = 1008;

        //首页获取币种
        long Home_Top = 1011;
        long Home_New = 1012;
        long Home_Rise = 1013;
        long Home_Fall = 1014;

        //市场获取币种 - 须设置连续数字
        long Market_BTC = 1021;
        long Market_ETH = 1022;
        long Market_USDT = 1023;
        long Market_DIC = 1024;
        long Market_USDTE = 1025;

        //Popup - 须设置连续数字
        long Pop_BTC = 1031;
        long Pop_ETH = 1032;
        long Pop_USDT = 1033;
        long Pop_DIC = 1034;
        long Pop_USDTE = 1035;

        //买入 - 限价
        long Buy_Limit = 1041;
        //买入 - 市价
        long Buy_Market = 1042;
        //买入 - 止盈止损
        long Buy_Target = 1043;
        //卖出 - 限价
        long Sell_Limit = 1044;
        //卖出 - 市价
        long Sell_Market = 1045;
        //卖出 - 止盈止损
        long Sell_Target = 1046;

        //交易 行情
        long Trade_Quote = 1051;
        //交易 深度
        long Trade_Depth = 1052;
        //交易 最新成交
        long Trade_New_Deal = 1053;
        //交易 当前委托
        long Trade_Current_Entrust = 1054;
        //交易 历史委托
        long Trade_History_Entrust = 1055;
        //交易 历史成交
        long Trade_History_Deal = 1056;
        //交易 取消委托 - 当前
        long Trade_Cancel_Current = 1057;
        //交易 取消委托 - 历史
        long Trade_Cancel_History = 1058;

        //K线 行情
        long Kline_Quote = 1061;
        //K线数据
        long Kline_Bar = 1062;
        //K线 深度
        long Kline_Depth = 1063;
        //交易 最新成交
        long Kline_New_Deal = 1064;

        //预测合约 行情
        long Contract_Quote = 1071;
        //预测合约
        long Contract_Bar = 1072;
        //下单
        long Contract_Place = 1073;
        //委托
        long Contract_Order = 1074;
    }
}