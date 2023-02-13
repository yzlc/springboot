package com.yzlc.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yzlc
 */
public class RegexUtils {
    /**
     * 至少包含n个连续数字
     *
     * @param str str
     * @param n   n
     * @return match - true
     */
    public static boolean containsNumeric(String str, int n) {
        Pattern pattern = Pattern.compile("^.*\\d{" + n + ",}.*$");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * 判断是否包含html字符
     *
     * @param str str
     * @return 包含 - true
     */
    public static boolean containsHtml(String str) {
        return str.contains("<") ||
                str.contains(">") ||
                str.contains(";") ||
                str.contains("\"");
    }
}