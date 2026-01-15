package com.mrumbl.backend.common.aop;

import com.mrumbl.backend.common.Sensitive;

import java.lang.reflect.Field;
import java.util.*;

public class MaskingUtil {

    public static Object mask(Object input){
        return mask(input, new IdentityHashMap<>());
    }

    private static Object mask(Object input, IdentityHashMap<Object, Object> visited){
        if(input == null) return null;

        // 순환 참조 방지: 이미 방문한 객체는 마스킹된 결과 반환
        if(visited.containsKey(input)) {
            return visited.get(input);
        }

        // 기본형 / String / Number / Enum 등은 그대로 반환 (순환 참조 없음)
        if (isPrimitive(input)) {
            return input;
        }

        // 배열 처리
        if(input.getClass().isArray()){
            int length = java.lang.reflect.Array.getLength(input);
            List<Object> maskedList = new ArrayList<>();
            // 배열 자체를 visited에 추가하지 않음 (배열 요소만 처리)
            for (int i=0; i<length; i++){
                Object element = java.lang.reflect.Array.get(input, i);
                maskedList.add(mask(element, visited));
            }
            return maskedList;
        }

        // Collection 처리
        if (input instanceof Collection) {
            Collection<?> col = (Collection<?>) input;
            List<Object> maskedList = new ArrayList<>();
            // Collection 자체를 visited에 추가하지 않음 (요소만 처리)
            for (Object element : col) {
                maskedList.add(mask(element, visited));
            }
            return maskedList;
        }

        // Map 처리
        if (input instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) input;
            Map<Object, Object> maskedMap = new HashMap<>();
            // Map 자체를 visited에 추가하지 않음 (값만 처리)
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                maskedMap.put(entry.getKey(), mask(entry.getValue(), visited));
            }
            return maskedMap;
        }

        // 객체 처리: 순환 참조 방지를 위해 visited에 먼저 placeholder 추가
        // (maskObjectFields 내부에서 재귀 호출 시 순환 참조 방지)
        Map<String, Object> placeholder = new HashMap<>();
        visited.put(input, placeholder);
        
        Object maskedResult = maskObjectFields(input, visited);
        // placeholder를 실제 결과로 교체
        visited.put(input, maskedResult);
        return maskedResult;
    }

    private static Object maskObjectFields(Object object, IdentityHashMap<Object, Object> visited) {
        try {
            Field[] fields = object.getClass().getDeclaredFields();
            Map<String, Object> result = new HashMap<>();

            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(object);

                // @Sensitive 마스킹
                if (field.isAnnotationPresent(Sensitive.class)) {
                    result.put(field.getName(), doMasking(value));
                } else {
                    // 순환 참조 방지를 위해 visited 전달
                    result.put(field.getName(), mask(value, visited));
                }
            }

            return result;

        } catch (Exception e) {
            // Reflection 실패 시 fallback
            return object;
        }
    }

    private static boolean isPrimitive(Object obj) {
        return obj instanceof String ||
                obj instanceof Number ||
                obj instanceof Boolean ||
                obj instanceof Character ||
                obj instanceof Enum<?>;
    }

    private static String doMasking(Object value) {
        return value == null ? null : " ";
    }
}
