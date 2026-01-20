package com.mrumbl.backend.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class RandomManager {
    // 13자리 epoch 기준: 9999999999999 (2286년까지 커버)
    private static final long MAX_EPOCH = 9999999999999L;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final char[] BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    public String createNumericCode(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public static String createRandomBase62(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(BASE62[RANDOM.nextInt(BASE62.length)]);
        }
        return sb.toString();
    }

    public static String createCartKey(){
        long now = System.currentTimeMillis();        // 13자리 epoch ms
        long rev = MAX_EPOCH - now;                   // 데이터 정렬용 숫자
        String random = createRandomBase62(6);       // 충돌 방지 + 분산성
        return rev + "-" + random;
    }



}
