package com.yzlc.common.util;

import java.util.Map;
import java.util.TreeMap;

public class SignUtil {
    public static String sign(Object object, String key) {
        if (object == null) {
            return null;
        }

        Map<String, Object> data = BeanUtil.object2Map(object);
        if (data == null || data.isEmpty()) {
            return null;
        }

        return sign(data, key);
    }

    public static String sign(Map<String, Object> data, String key) {
        if (data == null || data.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        TreeMap<String, Object> map = new TreeMap<>(data);

        map.forEach((k, v) ->
                sb.append(k).append("=").append(v).append("&"));
        sb.deleteCharAt(sb.length() - 1).append(key);
        return EncryptUtil.md5(sb.toString());
    }
}