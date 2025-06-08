package com.api.validatejwt.v1.util;

import java.lang.reflect.Field;
import java.util.Arrays;

public class ReflectionUtils {
    public static String[] getFieldNames(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        return Arrays.stream(fields)
                .map(Field::getName)
                .toArray(String[]::new);
    }
}