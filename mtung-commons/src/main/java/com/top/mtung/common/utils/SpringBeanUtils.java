package com.top.mtung.common.utils;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.top.mtung.common.exception.BaseException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author zhenguo.yao
 */
public class SpringBeanUtils extends BeanUtils {

    /**
     * 获取对象中参数值为空的参数
     *
     * @param source
     * @return
     */
    public static String[] getNullPropertyNames(Object source) {
        BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<>();
        PropertyDescriptor[] var4 = pds;
        int var5 = pds.length;
        for (int var6 = 0; var6 < var5; ++var6) {
            PropertyDescriptor pd = var4[var6];
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    /**
     * 忽略空值复制
     *
     * @param source
     * @param target
     */
    public static void copyPropertiesIgnoreNull(Object source, Object target) {
        Class<?> actualEditable = target.getClass();
        PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
        PropertyDescriptor[] var4 = targetPds;
        int var5 = targetPds.length;

        for (int var6 = 0; var6 < var5; ++var6) {
            PropertyDescriptor targetPd = var4[var6];
            if (targetPd.getWriteMethod() != null) {
                PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null && sourcePd.getReadMethod() != null) {
                    try {
                        Method readMethod = sourcePd.getReadMethod();
                        Method writeMethod = targetPd.getWriteMethod();
                        if (readMethod != null && ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
                            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                readMethod.setAccessible(true);
                            }
                            Object value = readMethod.invoke(source);
                            if (value != null) {
                                if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                    writeMethod.setAccessible(true);
                                }
                                writeMethod.invoke(target, value);
                            }
                        }
                    } catch (Exception var12) {
                        throw new FatalBeanException("Could not copy properties from source to target", var12);
                    }
                }
            }
        }

    }

    /**
     * 谨慎使用,未对json对象中的类型做判断,使用前需要确认是否适用
     *
     * @param source
     * @param target
     * @param ignoreProperties
     */
    public static void copyPropertiesExcludeIgnore(Map<String, Object> source, Object target, String... ignoreProperties) {
        if (null == source || source.isEmpty()) {
            return;
        }
        List<String> ignorePropertiesList = Collections.emptyList();
        if (ignoreProperties.length > 0) {
            ignorePropertiesList = Arrays.asList(ignoreProperties);
        }
        Class<?> actualEditable = target.getClass();
        PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
        PropertyDescriptor[] var4 = targetPds;
        int var5 = targetPds.length;
        for (int var6 = 0; var6 < var5; ++var6) {
            PropertyDescriptor targetPd = var4[var6];
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null) {
                String targetPdName = targetPd.getName();
                if (source.containsKey(targetPdName) && !ignorePropertiesList.contains(targetPdName)) {
                    // 只要有这个key就允许value为null
                    Object targetPdValue = source.get(targetPdName);
                    if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                        writeMethod.setAccessible(true);
                    }
                    try {
                        if (null == targetPdValue) {
                            writeMethod.invoke(target, new Object[]{null});
                        } else {
                            // 目标对象的数据类型
                            Class<?> parameterType = writeMethod.getParameterTypes()[0];
                            if (parameterType.isEnum()) {
                                // 目标属性是枚举类型
                                writeMethod.invoke(target, Enum.valueOf((Class) parameterType, (String) targetPdValue));
                            } else {
                                switch (parameterType.getTypeName()) {
                                    case "java.util.Set":
                                        writeMethod.invoke(target, new HashSet<>((List<String>) targetPdValue));
                                        break;
                                    case "java.lang.Double":
                                        if (StringUtils.isNotBlank(String.valueOf(targetPdValue))) {
                                            writeMethod.invoke(target, Double.valueOf((String.valueOf(targetPdValue))));
                                        }
                                        break;
                                    case "java.lang.Float":
                                        if (StringUtils.isNotBlank(String.valueOf(targetPdValue))) {
                                            writeMethod.invoke(target, Float.valueOf((String.valueOf(targetPdValue))));
                                        }
                                        break;
                                    case "java.lang.Integer":
                                        if (StringUtils.isNotBlank(String.valueOf(targetPdValue))) {
                                            writeMethod.invoke(target, Integer.valueOf((String.valueOf(targetPdValue))));
                                        }
                                        break;
                                    case "java.util.Date":
                                        writeMethod.invoke(target, getDate(actualEditable.getDeclaredField(targetPdName), String.valueOf(targetPdValue)));
                                        break;
                                    case "java.lang.Boolean":
                                        writeMethod.invoke(target, Boolean.valueOf(String.valueOf(targetPdValue)));
                                        break;
                                    default:
                                        writeMethod.invoke(target, targetPdValue);
                                        break;
                                }
                            }
                        }
                    } catch (Exception var12) {
                        throw new FatalBeanException("Could not copy properties from source to target targetPdName : " + targetPdName + " ,targetPdValue : " + targetPdValue, var12);
                    }
                }
            }
        }
    }

    private static Date getDate(Field field, String date) {
        JsonFormat jsonFormat = field.getAnnotation(JsonFormat.class);
        if (jsonFormat != null) {
            return DateUtil.parseByPatterns(date, jsonFormat.pattern()).getTime();
        }
        throw new BaseException(" unknow date pattern date :" + date);
    }

}
