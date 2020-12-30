/**
 * Project Name:exsd-android.
 * Package Name:com.exgj.exsd.common.util.
 * Date:2017/2/8 16:45. Copyright (c) 2017, 深圳易享时代网络科技有限公司.
 */
package cn.suozhi.DiBi.common.util;

import java.security.MessageDigest;

/**
 * @author dell.
 * @ClassName: MD5Util.
 * @Description: md5加密工具类.
 * @Date: 2017/2/8 16:45.
 */
public class MD5Util {

    // 十六进制下数字到字符的映射数组
    private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};

    private static MessageDigest md;
    public static final String DEFAULT_PWD = "123123";
    public static final int MIN_LENGTH = 6;
    public static final int MAX_LENGTH = 18;
    public static final int MD5_LENGTH = 32;

    public static void init() throws Exception {
        try {
            // 创建具有指定算法名称的信息摘要
            md = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        }
    }

    /**
     * 把inputString加密
     */
    public static String generatePassword(String inputString)
            throws Exception {
        return encodeByMD5(inputString);
    }

    /**
     * 验证输入的密码是否正确
     *
     * @param password    加密后的密码
     * @param inputString 输入的字符串
     * @return 验证结果，TRUE:正确 FALSE:错误
     * @throws Exception
     */
    public static boolean validatePassword(String password, String inputString)
            throws Exception {
        return password.equals(encodeByMD5(inputString));
    }

    /**
     * 对字符串进行MD5加密
     *
     * @throws Exception
     */
    public static String encodeByMD5(String originString)
            throws Exception {
        if (originString != null) {
            // 使用指定的字节数组对摘要进行最后更新，然后完成摘要计算
            if (md == null) {
                init();
            }
            byte[] results = md.digest(originString.getBytes());
            // 将得到的字节数组变成字符串返回
            String resultString = byteArrayToHexString(results);
            return resultString.toLowerCase();
        }
        return null;
    }

    /**
     * 转换字节数组为十六进制字符串
     *
     * @param b 字节数组
     * @return 十六进制字符串
     */
    private static String byteArrayToHexString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }

    /**
     * 将一个字节转化成十六进制形式的字符串
     */
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    public static void main(String[] args) {
        try {
            String pwd1 = "123456";
            String pwd2 = "";
            System.out.println("未加密的密码:" + pwd1);
            // 将123加密
            pwd2 = MD5Util.generatePassword(pwd1);
            System.out.println("加密后的密码1:" + pwd2);
            String pwd3 = MD5Util.generatePassword(pwd2 + "0602");
            System.out.println("加密后的密码:" + pwd3);
            System.out.print("验证密码是否下确:");
            if (MD5Util.validatePassword(pwd2, pwd1)) {
                System.out.println("正确");
            } else {
                System.out.println("错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
