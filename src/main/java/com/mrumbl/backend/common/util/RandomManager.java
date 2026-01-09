package com.mrumbl.backend.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class RandomManager {

    public String createNumericCode(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for(int i=0; i<length; i++){
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
