package com.company.security.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class DateUtils {
    /**
     * "yyyy-MM-dd'T'HH:mm:ss"
     */
    public static final DateFormat FORMAT_Long = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * "MMyy"
     */
    public static final DateFormat FORMAT_MMYY = new SimpleDateFormat("MMyy");

    /**
     * "yyMM"
     */
    public static final DateFormat FORMAT_YYMM = new SimpleDateFormat("yyMM");

    /**
     * "dd.MM.yyyy"
     */
    public static final DateFormat FORMAT_DD_MM_YYYY = new SimpleDateFormat("dd.MM.yyyy");

    /**
     * "ddMMyyyy"
     */
    public static final DateFormat FORMAT_DDMMYYYY = new SimpleDateFormat("ddMMyyyy");

    /**
     * "yyyy-MM-dd"
     */
    public static final DateFormat FORMAT_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * "yyyy-MM-dd HH:mm:ss"
     */
    public static final DateFormat FORMAT_STANDARD = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * "yyMMddHHmmss"
     */
    public static final DateFormat FORMAT_YYMMDDHHMMSS = new SimpleDateFormat("yyMMddHHmmss");

    /**
     * "yyyy-MM"
     */
    public static final DateFormat FORMAT_VISA_DIRECT_CARD_EXP = new SimpleDateFormat("yyyy-MM");

    /**
     * "yyyyMMdd"
     */
    public static final DateFormat FORMAT_YYYYMMDD = new SimpleDateFormat("yyyyMMdd");

    /**
     * "HHmmss"
     */
    public static final DateFormat FORMAT_HHMMSS = new SimpleDateFormat("HHmmss");

    /**
     * "HH:mm:ss"
     */
    public static final DateFormat FORMAT_HH_MM_SS = new SimpleDateFormat("HH:mm:ss");

    /**
     * "dd.MM.yyyy HH:mm:ss"
     */
    public static final DateFormat FORMAT_DD_MM_YYYY_HH_MM_SS = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    /**
     * "yyyyMM"
     */
    public static final DateFormat FORMAT_YYYYMM = new SimpleDateFormat("yyyyMM");

    public static Date getStartOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getStartOfDay());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    public static Date getEndOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getEndOfDay());
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    public static Date getStartOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getEndOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND,calendar.getActualMaximum(Calendar.MILLISECOND));
        return calendar.getTime();
    }


}
