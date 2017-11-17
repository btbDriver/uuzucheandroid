package com.youyou.uucar.Utils.Support;

/**
 * Created by taurusxi on 14/10/21.
 */
public class NumbUtils {


    public static String formatValue(String format, float value) {
        String valueStr = String.valueOf(value);
        if (format.equals("%.2f")) {
            return String.format("%.2f", value);

        } else {
            if (valueStr.endsWith(".0")) {
                Float valueFl = new Float(value);
                return String.valueOf(valueFl.intValue());
            }
            return String.format("%.2f", value);
        }
    }

}
