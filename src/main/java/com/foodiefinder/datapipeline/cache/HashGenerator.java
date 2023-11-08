package com.foodiefinder.datapipeline.cache;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HashGenerator {
    /**
     * 문자열을 해시값 문자열로 변환
     * @param str 문자열
     * @return SHA-256 해시값
     */
    public static String calculateSHA256(String str) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(digest.digest(str.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
