package cn.suozhi.DiBi.c2c.model;

public class CoinIntroEnity {

    /**
     * code : 0
     * msg : 成功
     * data : {"currencyId":2,"coinName":"Ethereum","icoDate":"2014-07-24","icoQuantity":"96311500.00000000","circulateQuantity":"96311500.00000000","icoPrice":"0.31000000","whitePaper":"https://github.com/ethereum/wiki/wiki/%5BEnglish%5D-White-Paper","website":"https://www.ethereum.org/","blockchainExplorer":"https://etherscan.io/","intro":"以太坊（Ethereum）是下一代密码学账本，可以支持众多的高级功能，包括用户发行货币，智能协议，去中心化的交易和设立去中心化自治组织& #40;DAOs& #41;或去中心化自治公司（DACs）。以太坊并不是把每一单个类型的功能作为特性来特别支持，相反，以太坊包括一个内置的图灵完备的脚本语言，允许通过被称为\u201c合同\u201d的机制来为自己想实现的特性写代码。一个合同就像一个自动的代理，每当接收到一笔交易，合同就会运行特定的一段代码，这段代码能修改合同内部的数据存储或者发送交易。\n"}
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
         * currencyId : 2
         * coinName : Ethereum
         * icoDate : 2014-07-24
         * icoQuantity : 96311500.00000000
         * circulateQuantity : 96311500.00000000
         * icoPrice : 0.31000000
         * whitePaper : https://github.com/ethereum/wiki/wiki/%5BEnglish%5D-White-Paper
         * website : https://www.ethereum.org/
         * blockchainExplorer : https://etherscan.io/
         * intro : 以太坊（Ethereum）是下一代密码学账本，可以支持众多的高级功能，包括用户发行货币，智能协议，去中心化的交易和设立去中心化自治组织& #40;DAOs& #41;或去中心化自治公司（DACs）。以太坊并不是把每一单个类型的功能作为特性来特别支持，相反，以太坊包括一个内置的图灵完备的脚本语言，允许通过被称为“合同”的机制来为自己想实现的特性写代码。一个合同就像一个自动的代理，每当接收到一笔交易，合同就会运行特定的一段代码，这段代码能修改合同内部的数据存储或者发送交易。

         */

        private int currencyId;
        private String coinName;
        private String icoDate;
        private String icoQuantity;
        private String circulateQuantity;
        private String icoPrice;
        private String whitePaper;
        private String website;
        private String blockchainExplorer;
        private String intro;

        public int getCurrencyId() {
            return currencyId;
        }

        public void setCurrencyId(int currencyId) {
            this.currencyId = currencyId;
        }

        public String getCoinName() {
            return coinName;
        }

        public void setCoinName(String coinName) {
            this.coinName = coinName;
        }

        public String getIcoDate() {
            return icoDate;
        }

        public void setIcoDate(String icoDate) {
            this.icoDate = icoDate;
        }

        public String getIcoQuantity() {
            return icoQuantity;
        }

        public void setIcoQuantity(String icoQuantity) {
            this.icoQuantity = icoQuantity;
        }

        public String getCirculateQuantity() {
            return circulateQuantity;
        }

        public void setCirculateQuantity(String circulateQuantity) {
            this.circulateQuantity = circulateQuantity;
        }

        public String getIcoPrice() {
            return icoPrice;
        }

        public void setIcoPrice(String icoPrice) {
            this.icoPrice = icoPrice;
        }

        public String getWhitePaper() {
            return whitePaper;
        }

        public void setWhitePaper(String whitePaper) {
            this.whitePaper = whitePaper;
        }

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        public String getBlockchainExplorer() {
            return blockchainExplorer;
        }

        public void setBlockchainExplorer(String blockchainExplorer) {
            this.blockchainExplorer = blockchainExplorer;
        }

        public String getIntro() {
            return intro;
        }

        public void setIntro(String intro) {
            this.intro = intro;
        }
    }
}
