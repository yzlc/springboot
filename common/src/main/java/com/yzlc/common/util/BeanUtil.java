package com.yzlc.common.util;

import com.google.common.base.CaseFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.*;

/**
 * <dependency>
 *     <groupId>com.google.guava</groupId>
 *     <artifactId>guava</artifactId>
 *     <version>26.0-jre</version>
 *     <scope>compile</scope>
 * </dependency>
 */

public class BeanUtil {

    public static  Map<String, Object> object2Map(Object object) {
        Map<String, Object> data = new TreeMap<>(); //对key进行排序
        try {
            BeanInfo info = Introspector.getBeanInfo(object.getClass(), Introspector.IGNORE_ALL_BEANINFO);
            PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
            for (PropertyDescriptor pd : descriptors) {
                // 将 驼峰式写法转成下划线写法
                String name = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, pd.getName());
                Object value = pd.getReadMethod().invoke(object);
                if ("class".equals(name) || value == null)
                    continue;
                data.put(name, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static <T>  T map2Object(Class<T> clazz, Map<String, Object> data) {
        try {
            T object = clazz.newInstance();
            BeanInfo info = Introspector.getBeanInfo(object.getClass(), Introspector.IGNORE_ALL_BEANINFO);
            PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
            for (PropertyDescriptor pd : descriptors) {
                String name = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, pd.getName());

                Object value = data.get(name);
                if (value == null)
                    continue;
                pd.getWriteMethod().invoke(object, value);

            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void copyProperties(Object source, Object target) throws BeansException {
        if (Objects.isNull(source)) return;
        BeanUtils.copyProperties(source, target,getNullPropertyNames(source));
    }

    public static String[] getNullPropertyNames (Object source) {
//        org.springframework.beans.BeanWrapper
        final BeanWrapper src = new BeanWrapperImpl(source);
//        java.beans.PropertyDescriptor
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for(PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}
