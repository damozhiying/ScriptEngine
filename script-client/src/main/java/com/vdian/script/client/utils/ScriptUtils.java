package com.vdian.script.client.utils;


import com.google.common.base.Strings;
import com.vdian.script.client.exception.ScriptEngineException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * @author jifang
 * @since 2016/11/19 上午9:55.
 */
public class ScriptUtils {

    private static String ENCRYPT_CODE = "Ω≈≈ç√˜≤≥Ωæ…";

    public static void checkSaltSecurity(String salt) {
        if (Strings.isNullOrEmpty(salt) || salt.length() < 7 || salt.length() % 2 == 0) {
            throw new ScriptEngineException("salt length need great the 7 and need a odd number");
        }
    }

    public static byte[] encodeMd5(String string, String salt) {
        String[] salts = splitSalt(salt);

        try {
            StringBuilder sb = new StringBuilder(salts[0]);
            sb.append(ENCRYPT_CODE.charAt(salt.length() % ENCRYPT_CODE.length()));
            sb.append(string);
            sb.append(ENCRYPT_CODE.charAt(salt.length() * 2 % ENCRYPT_CODE.length()));
            byte[] bytes = sb.append(salts[1]).toString().getBytes("UTF-8");

            return MessageDigest.getInstance("MD5").digest(bytes);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            throw new ScriptEngineException(e);
        }
    }

    public static boolean isEqual(String string, String salt, byte[] md5) {
        return !Strings.isNullOrEmpty(string)
                && md5 != null
                && md5.length != 0
                && Arrays.equals(encodeMd5(string, salt), md5);
    }

    private static String[] splitSalt(String salt) {
        int middle = salt.length() / 2 + 1;
        String prefix = salt.substring(0, middle);
        String suffix = salt.substring(middle + 1);

        return new String[]{prefix, suffix};
    }

    public static Pair<String, Integer> getServiceAddr() {
        ServerSocket socket = null;
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            socket = new ServerSocket(0);
            int port = socket.getLocalPort();

            return new Pair<>(ip, port);

        } catch (IOException e) {
            throw new ScriptEngineException(e);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
