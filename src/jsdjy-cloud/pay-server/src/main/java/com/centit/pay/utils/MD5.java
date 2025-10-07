package com.centit.pay.utils;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.MessageDigest;

/**
 * MD5加密
 *
 * @author
 */
public class MD5 implements PasswordEncoder {

    private static final String SALT = "wanDeFour19864369";

    public static String md5(String password) {
        return md5Salt(password, "");
    }

    public static String md5Salt(String password) {
        return md5Salt(password, SALT);
    }

    public static String md5Salt(String password, String salt) {
        password = password + salt;
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        char[] charArray = password.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    @Override
    public String encode(CharSequence charSequence) {
        return md5(charSequence.toString());
    }

    @Override
    public boolean matches(CharSequence charSequence, String s) {
        return md5(charSequence.toString()).equals(s);
    }

}
