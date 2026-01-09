package com.mrumbl.backend.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class Response<T> {
//    private String code;
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime transactionTime;

    public static <T> Response<T> ok(T data){
        return Response.<T>builder()
                .transactionTime(LocalDateTime.now())
                .message("요청이 성공적으로 처리되었습니다.")
                .data(data)
                .success(true)
                .build();
    }

//    public static <T> Response<T> error(T data, String code){
//        return Response.<T>builder()
//                .transactionTime(LocalDateTime.now())
//                .message("요청 처리에 실패 하였습니다.")
//                .data(data)
//                .code(code)
//                .build();
//    }
}
