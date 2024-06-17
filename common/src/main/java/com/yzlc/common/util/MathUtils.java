package com.yzlc.common.util;

import org.springframework.cglib.core.ReflectUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * @author yzlc2
 */
public class MathUtils {
    private static final String NONE = "-";
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    /**
     * 环比
     *
     * @param decimal 当期
     * @param before  上期
     * @return (decimal - before) / before
     */
    public static String ring(BigDecimal decimal, BigDecimal before) {
        return percent(subtract(decimal, before), before);
    }

    /**
     * 除
     *
     * @param dividend dividend
     * @param divisor  divisor
     * @return dividend / divisor
     */
    public static BigDecimal divide(BigDecimal dividend,BigDecimal divisor) {
        if (Objects.isNull(dividend) || BigDecimal.ZERO.equals(dividend))
            return dividend;

        assert divisor != null;
        assert !BigDecimal.ZERO.equals(divisor);

        return dividend.divide(divisor, SCALE, ROUNDING_MODE);
    }

    /**
     * 百分比
     *
     * @param dividend dividend
     * @param divisor  divisor
     * @return dividend / divisor
     */
    public static String percent(BigDecimal dividend, BigDecimal divisor) {
        if (Objects.isNull(divisor) || BigDecimal.ZERO.equals(divisor))
            return NONE;

        BigDecimal divide = divide(dividend, divisor.abs());
        if (Objects.isNull(divide))
            return NONE;

        NumberFormat PERCENT = NumberFormat.getPercentInstance();
        return PERCENT.format(divide);
    }

    /**
     * 乘
     *
     * @param multiplier   multiplier
     * @param multiplicand multiplicand
     * @return multiplier * multiplicand
     */
    public static BigDecimal multiply(BigDecimal multiplier, BigDecimal... multiplicand) {
        if (Objects.isNull(multiplier) || BigDecimal.ZERO.equals(multiplier))
            return multiplier;

        for (BigDecimal b : multiplicand) {
            if (Objects.isNull(b) || BigDecimal.ZERO.equals(b))
                return BigDecimal.ZERO;
            multiplier = multiplier.multiply(b);
        }
        return multiplier;
    }

    /**
     * 减
     * <p><b>Examples:</b>
     * <pre>
     * subtract("2.32", "1.01", "", "0.2")      "1.11"
     * </pre>
     *
     * @param minuend    minuend
     * @param subtrahend subtrahend
     * @return minuend - subtrahend
     */
    public static BigDecimal subtract(BigDecimal minuend, BigDecimal... subtrahend) {
        if (Objects.isNull(minuend))
            minuend = BigDecimal.ZERO;

        for (BigDecimal b : subtrahend) {
            if (!Objects.isNull(b))
                minuend = minuend.subtract(b);
        }
        return minuend;
    }

    /**
     * 加
     *
     * @param augend augend
     * @return addend + augend
     */
    public static BigDecimal add(BigDecimal... augend) {
        BigDecimal addend = BigDecimal.ZERO;
        for (BigDecimal b : augend) {
            if (!Objects.isNull(b))
                addend = addend.add(b);
        }
        return addend;
    }

    /**
     * public static void main(String[] args) {
     *         // 创建示例对象列表
     *         List<ExampleObject> objects = List.of(
     *             new ExampleObject(BigDecimal.TEN, new BigDecimal("20.5")),
     *             new ExampleObject(new BigDecimal("15.3"), new BigDecimal("25")),
     *             new ExampleObject(new BigDecimal("30"), new BigDecimal("35.8"))
     *         );
     *
     *         // 添加合计
     *         addSumToLast(objects, "value1", "value2");
     *
     *         // 打印包含合计的列表
     *         objects.forEach(obj -> System.out.println("Value1: " + obj.getValue1() + ", Value2: " + obj.getValue2()));
     *
     *         // 注意：合计值并没有在示例对象的字段中显示，而是通过调用 setter 方法设置的
     *     }
     * @param list
     * @param fields
     */
    public static <T> void addSumToLast(List<T> list, String... fields) throws InstantiationException, IllegalAccessException {
        if (list.isEmpty()) return;
        T lastItem = list.get(list.size() - 1);
        T sumItem = (T) lastItem.getClass().newInstance();

        BigDecimal[] sums = IntStream.range(0, fields.length).mapToObj(i -> BigDecimal.ZERO).toArray(BigDecimal[]::new);
        list.forEach(item -> IntStream.range(0, fields.length).forEach(i -> {
            Object value = ReflectionUtils.getField(ReflectionUtils.findField(item.getClass(), fields[i]),fields[i]);
            if (Objects.nonNull(value)) sums[i] = sums[i].add((BigDecimal) value);
        }));

        Field firstField = sumItem.getClass().getDeclaredFields()[0];
        firstField.setAccessible(true);
        firstField.set(sumItem, "合计");

        IntStream.range(0, fields.length).forEach(i -> ReflectionUtils.setField(ReflectionUtils.findField(sumItem.getClass(), fields[i]), fields[i], sums[i]));
        list.add(sumItem);
    }
}