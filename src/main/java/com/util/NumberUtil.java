package com.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * 数字工具类
 */
public class NumberUtil {

    /**
     * 四舍五入保留指定位小数
     */
    public static double halfUp(double value, int numberOfDigits) {
        return BigDecimal.valueOf(value).setScale(numberOfDigits, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 除法并四舍五入保留指定位小数
     */
    public static double divide(double v1, double v2, int numberOfDigits) {
        return BigDecimal.valueOf(v1).divide(BigDecimal.valueOf(v2), numberOfDigits, ROUND_HALF_UP).doubleValue();
    }

    /**
     * 乘法并四舍五入保留指定位小数
     */
    public static double multiply(double v1, double v2, int numberOfDigits) {
        return BigDecimal.valueOf(v1).multiply(BigDecimal.valueOf(v2)).setScale(numberOfDigits, ROUND_HALF_UP)
                .doubleValue();
    }

}
