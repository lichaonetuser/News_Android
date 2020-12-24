package com.box.app.news.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具
 *
 * @author 北京亦水方舟 张健
 */
public class MD5Utils {
    private static final int STREAM_BUFFER_LENGTH = 1024;

    public static MessageDigest getDigest() throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("MD5");
    }

    public static String md5ToString(String text) {
        String md5String;
        MessageDigest messageDigest = null;
        try {
            messageDigest = getDigest();
            messageDigest.reset();
            messageDigest.update(text.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (messageDigest != null) {
            byte[] byteArray = messageDigest.digest();
            StringBuilder md5Builder = new StringBuilder();
            for (byte b : byteArray) {
                if (Integer.toHexString(0xFF & b).length() == 1) {
                    md5Builder.append("0").append(
                            Integer.toHexString(0xFF & b));
                } else {
                    md5Builder.append(Integer.toHexString(0xFF & b));
                }
            }
            md5String = md5Builder.toString();
        } else {
            return text;
        }
        return md5String;
    }

    public static String md5ToString(byte[] bytes) {
        if (bytes != null) {
            StringBuilder md5Builder = new StringBuilder();
            for (byte b : bytes) {
                if (Integer.toHexString(0xFF & b).length() == 1) {
                    md5Builder.append("0").append(
                            Integer.toHexString(0xFF & b));
                } else {
                    md5Builder.append(Integer.toHexString(0xFF & b));
                }
            }
            return md5Builder.toString();
        }
        return null;
    }

    public static String md5ToString(String text, int start, int end) {
        return md5ToString(text).substring(start, end);
    }

    public static byte[] md5(String txt) {
        return md5(txt.getBytes());
    }

    public static byte[] md5(byte[] bytes) {
        try {
            MessageDigest digest = getDigest();
            digest.update(bytes);
            return digest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] md5(InputStream is) throws NoSuchAlgorithmException, IOException {
        MessageDigest messageDigest = getDigest();
        final byte[] buffer = new byte[STREAM_BUFFER_LENGTH];
        int read = is.read(buffer, 0, STREAM_BUFFER_LENGTH);
        while (read > -1) {
            messageDigest.update(buffer, 0, read);
            read = is.read(buffer, 0, STREAM_BUFFER_LENGTH);
        }

        return messageDigest.digest();
    }

    /**
     * 获文件的MD5值
     *
     * @param file
     * @return
     */
    public static String getFileMD5(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return null;
        }
        try {
            FileInputStream in = new FileInputStream(file);
            String md5String = md5ToString(md5(in));
            in.close();
            return md5String;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    final protected static char[] hexArray = "0123456789abcdef".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
