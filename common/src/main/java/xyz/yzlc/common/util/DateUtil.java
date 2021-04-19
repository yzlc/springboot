package xyz.yzlc.common.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    /**
     * 判断日期范围
     * <p>
     * Example：
     * {@code compare(new Date(),Calendar.MONTH,-6,Calendar.DAY_OF_MONTH,-3);//6月前-3天前}
     * </p>
     *
     * @param date        日期
     * @param beginField  begin calendar field.
     * @param beginAmount the amount of date or time to be added to the field.
     * @param endField    end calendar field.
     * @param endAmount   the amount of date or time to be added to the field.
     * @return true - 在范围内
     */
    public static boolean compare(Date date, int beginField, int beginAmount, int endField, int endAmount) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        Calendar begin = Calendar.getInstance();
        begin.add(beginField, beginAmount);

        Calendar end = Calendar.getInstance();
        end.add(endField, endAmount);
        return c.after(begin) && c.before(end);
    }
}
