package com.api.validatejwt.v1.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReflectionUtils {
    public static String[] getFieldNames(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        return Arrays.stream(fields)
                .map(field -> {
                    JsonProperty annotation = field.getAnnotation(JsonProperty.class);
                    if (annotation != null && !annotation.value().isEmpty()) {
                        return annotation.value();
                    } else {
                        return field.getName();
                    }
                })
                .toArray(String[]::new);
    }
}
