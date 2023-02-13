package com.yzlc.common.util;

import org.apache.commons.lang3.StringUtils;

public class StringUtil {

    public static final String EMPTY = "";

    public static final String NULL = "null";

    private StringUtil() {
    }

    /**
     * <p>检查字符串是否为null、""、"null"</p>
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty("null")    = true
     * StringUtils.isEmpty(" ")       = true
     * StringUtils.isEmpty("bob")     = true
     * StringUtils.isEmpty("  bob  ") = true
     * </pre>
     *
     * @param cs
     * @return null、""、"null"返回{@code true}
     */
    public static boolean isEmpty(CharSequence cs) {
        return StringUtils.isEmpty(cs) || NULL.contentEquals(cs);
    }

    /**
     * <p>检查字符串是否不为null、""、"null"</p>
     * <pre>
     * StringUtils.isNotEmpty(null)      = false
     * StringUtils.isNotEmpty("")        = false
     * StringUtils.isNotEmpty("null")    = false
     * StringUtils.isNotEmpty(" ")       = true
     * StringUtils.isNotEmpty("bob")     = true
     * StringUtils.isNotEmpty("  bob  ") = true
     * </pre>
     *
     * @param cs
     * @return 不为null、""、"null"返回{@code true}
     */
    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

    public static void main(String[] args) {
        System.out.println(isEmpty(NULL));
    }
}