package cn.linyi.music.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by linyi on 2016/4/17.
 */
public class MD5Util {
    public final static String getMD5String(String s) {
        char hexDigits[] = { '0', '1', '2', '3', '4',
                '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F' };
        byte[] btinput = s.getBytes();
        try {
            //获取MD5加密算法的MessageDigest对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            //使用指定的字节更新摘要
            mdInst.update(btinput);
            //获得密文
            byte[] md = mdInst.digest();
            //将密文转化成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k=0;
            for (int i = 0; i < j; i++){
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 &0xf];
            }
            return new String(str);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
