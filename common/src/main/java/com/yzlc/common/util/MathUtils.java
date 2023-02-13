package com.yzlc.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Objects;

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
}