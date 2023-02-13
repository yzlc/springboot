package com.yzlc.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yzlc
 */
public class RegexUtil {
    private static final String REGEX_IP = "((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}";

    public static List<String> getIp(CharSequence input) {
        return get(input, REGEX_IP);
    }

    private static List<String> get(CharSequence input, String regex) {
        List<String> list = new ArrayList<>();
        Matcher m = Pattern.compile(regex).matcher(input);
        while (m.find())
            list.add(m.group());
        return list;
    }
}
