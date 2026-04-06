package com.deerecho.My_Blog.util;

import java.util.Random;

/**
 * 随机验证码工具类（数字 + 大小写字母混合）
 */
public class RandomUtil {

    // 字符池：数字 + 大小写字母
    private static final String STR = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static String randomNumbers(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(STR.length());
            sb.append(STR.charAt(index));
        }
        return sb.toString();
    }
}