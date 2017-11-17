package com.youyou.uucar.Utils;

import java.util.Calendar;

/**
 * Created by taurusxi on 14/10/23.
 */
public class TimeUtils {

    public static final String formatTime(final Calendar calendar) {

        StringBuilder dateBuilder = new StringBuilder();
        dateBuilder.append(calendar.MONTH);
        dateBuilder.append("月");
        dateBuilder.append(calendar.DAY_OF_MONTH);
        dateBuilder.append("日(周");
        switch (calendar.DAY_OF_WEEK) {
            case Calendar.SUNDAY:
                dateBuilder.append("日");
                break;
            case Calendar.MONDAY:
                dateBuilder.append("一");
                break;
            case Calendar.TUESDAY:
                dateBuilder.append("二");
                break;
            case Calendar.WEDNESDAY:
                dateBuilder.append("三");
                break;
            case Calendar.THURSDAY:
                dateBuilder.append("四");
                break;
            case Calendar.FRIDAY:
                dateBuilder.append("五");
                break;
            case Calendar.SATURDAY:
                dateBuilder.append("六");
                break;
        }
        dateBuilder.append(")");
        return dateBuilder.toString();
    }


    public static final String formatTimeWithHHMM(final Calendar calendar) {

        StringBuilder dateBuilder = new StringBuilder();
        dateBuilder.append(calendar.get(Calendar.YEAR));
        dateBuilder.append("年");
        int month = calendar.get(Calendar.MONTH) + 1;
        dateBuilder.append(month < 10 ? "0" + month : month);
        dateBuilder.append("月");
        dateBuilder.append(calendar.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + calendar.get(Calendar.DAY_OF_MONTH) : calendar.get(Calendar.DAY_OF_MONTH));
        dateBuilder.append("日 ");
        dateBuilder.append(calendar.get(Calendar.HOUR_OF_DAY) < 10 ? "0" + calendar.get(Calendar.HOUR_OF_DAY) : calendar.get(Calendar.HOUR_OF_DAY));
        dateBuilder.append(":");
        dateBuilder.append(calendar.get(Calendar.MINUTE) < 10 ? "0" + calendar.get(Calendar.MINUTE) : calendar.get(Calendar.MINUTE));
        dateBuilder.append(" (周");
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SUNDAY:
                dateBuilder.append("日");
                break;
            case Calendar.MONDAY:
                dateBuilder.append("一");
                break;
            case Calendar.TUESDAY:
                dateBuilder.append("二");
                break;
            case Calendar.WEDNESDAY:
                dateBuilder.append("三");
                break;
            case Calendar.THURSDAY:
                dateBuilder.append("四");
                break;
            case Calendar.FRIDAY:
                dateBuilder.append("五");
                break;
            case Calendar.SATURDAY:
                dateBuilder.append("六");
                break;
        }
        dateBuilder.append(")");
        return dateBuilder.toString();
    }


}
