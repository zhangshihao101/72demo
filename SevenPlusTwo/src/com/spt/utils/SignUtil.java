package com.spt.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.umeng.socialize.utils.Log;

public class SignUtil {

//    public static void main(String[] args) {
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("client_id", "localhost");
//        params.put("redirect_uri", "https://localhost:8443/");
//        params.put("userLoginId", "1024");
//        String secretKey = "1024";
//        String sign = genSign(params, secretKey);
//        System.out.println(sign);
//
//    }

    public static String genSign(HashMap<String, String> params, String secretKey) {
        // 将参数以参数名的字典升序排序
        Map<String, String> sortParams = new TreeMap<String, String>(params);
        Set<Entry<String, String>> entrys = sortParams.entrySet();

        // 遍历排序的字�?,并拼�?"key=value"格式
        StringBuilder baseString = new StringBuilder();
        for (Entry<String, String> entry : entrys) {
            baseString.append(entry.getKey()).append("=").append(entry.getValue());
        }
        baseString.append(secretKey);

        // 使用MD5对待签名串求�?
        byte[] bytes = null;
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            try {
                bytes = md5.digest(baseString.toString().getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // 将MD5输出的二进制结果转换为小写的十六进制
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex);
        }
        Log.e(sign.toString());
        return sign.toString();
    }
}
