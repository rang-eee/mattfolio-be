package com.colon.mattfolio.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtil {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
    public static final DateTimeFormatter DATE_TIME_MINUTE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    public static final DateTimeFormatter DATE_TIME_FORMATTER_DB = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    public static final DateTimeFormatter STRING_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter STRING_DATE_TIME_FORMATTER_MINUTE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Parsing methods

    /**
     * 문자열을 LocalDate로 변환합니다.
     *
     * @param date 변환할 문자열 (형식: yyyyMMdd)
     * @return 변환된 LocalDate 객체
     * @throws IllegalArgumentException 잘못된 형식의 문자열일 경우 예외 발생
     */
    public static LocalDate parseDate(String date) {
        if (date == null)
            return null;

        try {
            return LocalDate.parse(date, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format, expected yyyyMMdd", e);
        }
    }

    /**
     * 문자열을 LocalDateTime으로 변환하고 해당 날짜의 시작 시간을 반환합니다.
     *
     * @param date 변환할 문자열 (형식: yyyyMMdd)
     * @return 변환된 LocalDateTime 객체 (해당 날짜의 00:00:00.000)
     * @throws IllegalArgumentException 잘못된 형식의 문자열일 경우 예외 발생
     */
    public static LocalDateTime parseDateToStartOfDay(String date) {
        if (date == null) {
            return null;
        }

        try {
            LocalDate localDate = LocalDate.parse(date, DATE_FORMATTER);
            return localDate.atStartOfDay();
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format, expected yyyyMMdd", e);
        }
    }

    /**
     * 문자열을 LocalDateTime으로 변환합니다.
     *
     * @param yyyymmddhhmm 변환할 문자열 (형식: yyyyMMddHHmm)
     * @return 변환된 LocalDateTime 객체
     * @throws IllegalArgumentException 잘못된 형식의 문자열일 경우 예외 발생
     */
    public static LocalDateTime parseDateTime(String yyyymmddhhmm) {
        try {
            return LocalDateTime.parse(yyyymmddhhmm, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: yyyymmddhhmm");
        }
    }

    /**
     * 문자열을 LocalDateTime으로 변환합니다.
     *
     * @param yyyymmddhh 변환할 문자열 (형식: yyyyMMddHH)
     * @return 변환된 LocalDateTime 객체
     * @throws IllegalArgumentException 잘못된 형식의 문자열일 경우 예외 발생
     */
    public static LocalDateTime parseDateTimeHour(String yyyymmddhh) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHH");
        try {
            return LocalDateTime.parse(yyyymmddhh, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + yyyymmddhh);
        }
    }

    /**
     * 문자열을 LocalDateTime으로 변환합니다.
     *
     * @param yyyymmddhhmm 변환할 문자열 (형식: yyyyMMddHHmm)
     * @return 변환된 LocalDateTime 객체
     * @throws IllegalArgumentException 잘못된 형식의 문자열일 경우 예외 발생
     */
    public static LocalDateTime parseDateTimeMinute(String yyyymmddhhmm) {
        try {
            return LocalDateTime.parse(yyyymmddhhmm, DATE_TIME_MINUTE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + yyyymmddhhmm);
        }
    }

    // Formatting methods

    /**
     * LocalDate 객체를 문자열로 변환합니다.
     *
     * @param date 변환할 LocalDate 객체
     * @return 변환된 문자열 (형식: yyyyMMdd)
     */
    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    /**
     * LocalDateTime 객체를 주어진 DateTimeFormatter로 변환합니다.
     *
     * @param dateTime 변환할 LocalDateTime 객체
     * @param formatter 사용할 DateTimeFormatter 객체
     * @return 변환된 문자열
     */
    public static String formatDateWithFormatter(LocalDate date, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        if (date == null) {
            throw new IllegalArgumentException("The dateTime parameter cannot be null");
        }
        if (formatter == null) {
            throw new IllegalArgumentException("The formatter parameter cannot be null");
        }
        return date.format(formatter);
    }

    /**
     * LocalDateTime 객체를 문자열로 변환합니다.
     *
     * @param date 변환할 LocalDateTime 객체
     * @return 변환된 문자열 (형식: yyyyMMddHHmm)
     */
    public static String formatDateTime(LocalDateTime date) {
        return date.format(DATE_TIME_FORMATTER);
    }

    /**
     * LocalDateTime 객체를 데이터베이스에 저장할 형식의 문자열로 변환합니다.
     *
     * @param date 변환할 LocalDateTime 객체
     * @return 변환된 문자열 (형식: yyyy-MM-dd HH:mm:ss.SSS)
     */
    public static String formatDateTimeForDb(LocalDateTime date) {
        return date.format(DATE_TIME_FORMATTER_DB);
    }

    /**
     * LocalDateTime 객체를 yyyyMMdd 형식의 문자열로 반환합니다.
     *
     * @param dateTime 변환할 LocalDateTime 객체
     * @return 변환된 문자열 (형식: yyyyMMdd)
     */
    public static String formatDateTimeToDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        LocalDate date = dateTime.toLocalDate();
        return formatDate(date);
    }

    /**
     * LocalDateTime 객체를 yyyy-MM-dd HH:mm 형식의 문자열로 변환합니다.
     *
     * @param dateTime 변환할 LocalDateTime 객체
     * @return 변환된 문자열 (형식: yyyy-MM-dd HH:mm)
     */
    public static String formatDateTimeWithUnit(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("The dateTime parameter cannot be null");
        }
        return dateTime.format(STRING_DATE_TIME_FORMATTER);
    }

    /**
     * LocalDateTime 객체를 yyyy-MM-dd HH:mm 형식의 문자열로 변환합니다.
     *
     * @param dateTime 변환할 LocalDateTime 객체
     * @return 변환된 문자열 (형식: yyyy-MM-dd HH:mm)
     */
    public static String formatDateTimeWithUnitToMinute(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("The dateTime parameter cannot be null");
        }
        return dateTime.format(STRING_DATE_TIME_FORMATTER_MINUTE);
    }

    /**
     * LocalDateTime 객체를 주어진 DateTimeFormatter로 변환합니다.
     *
     * @param dateTime 변환할 LocalDateTime 객체
     * @param formatter 사용할 DateTimeFormatter 객체
     * @return 변환된 문자열
     */
    public static String formatDateTimeWithFormatter(LocalDateTime dateTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        if (dateTime == null) {
            throw new IllegalArgumentException("The dateTime parameter cannot be null");
        }
        if (formatter == null) {
            throw new IllegalArgumentException("The formatter parameter cannot be null");
        }
        return dateTime.format(formatter);
    }

    /**
     * LocalDateTime 객체를 주어진 DateTimeFormatter로 변환합니다.
     *
     * @param dateTime 변환할 LocalDateTime 객체
     * @param formatter 사용할 DateTimeFormatter 객체
     * @return 변환된 문자열
     */
    public static String formatDateTimeWithFormatter(LocalDateTime dateTime, DateTimeFormatter formatter) {
        if (dateTime == null) {
            throw new IllegalArgumentException("The dateTime parameter cannot be null");
        }
        if (formatter == null) {
            throw new IllegalArgumentException("The formatter parameter cannot be null");
        }
        return dateTime.format(formatter);
    }

    // Start and End of Day methods

    /**
     * LocalDate 객체의 시작 시간을 LocalDateTime으로 반환합니다.
     *
     * @param date 변환할 LocalDate 객체
     * @return 변환된 LocalDateTime 객체 (해당 날짜의 00:00:00.000)
     */
    public static LocalDateTime getStartOfDay(LocalDate date) {
        if (date == null)
            return null;

        return date.atStartOfDay();
    }

    /**
     * LocalDate 객체의 종료 시간을 LocalDateTime으로 반환합니다.
     *
     * @param date 변환할 LocalDate 객체
     * @return 변환된 LocalDateTime 객체 (해당 날짜의 23:59:59.999)
     */
    public static LocalDateTime getEndOfDay(LocalDate date) {
        if (date == null)
            return null;

        return date.atTime(LocalTime.MAX);
    }

    /**
     * 문자열을 LocalDateTime으로 변환하고 해당 날짜의 시작 시간을 반환합니다.
     *
     * @param date 변환할 문자열 (형식: yyyyMMdd)
     * @return 변환된 LocalDateTime 객체 (해당 날짜의 00:00:00.000)
     */
    public static LocalDateTime getStartOfDay(String date) {
        LocalDate localDate = parseDate(date);
        return getStartOfDay(localDate);
    }

    /**
     * 문자열을 LocalDateTime으로 변환하고 해당 날짜의 종료 시간을 반환합니다.
     *
     * @param date 변환할 문자열 (형식: yyyyMMdd)
     * @return 변환된 LocalDateTime 객체 (해당 날짜의 23:59:59.999)
     */
    public static LocalDateTime getEndOfDay(String date) {
        LocalDate localDate = parseDate(date);
        return getEndOfDay(localDate);
    }

    // Today methods

    /**
     * 오늘 날짜를 yyyyMMdd 형식의 문자열로 반환합니다.
     *
     * @return 오늘 날짜 문자열 (형식: yyyyMMdd)
     */
    public static String getTodayString() {
        LocalDate today = LocalDate.now();
        return formatDate(today);
    }

    /**
     * 오늘 날짜를 LocalDate 객체로 반환합니다.
     *
     * @return 오늘 날짜의 LocalDate 객체
     */
    public static LocalDate getTodayLocalDate() {
        return LocalDate.now();
    }

    /**
     * 오늘 날짜의 시작 시간을 LocalDateTime 객체로 반환합니다.
     *
     * @return 오늘 날짜의 시작 시간 (LocalDateTime)
     */
    public static LocalDateTime getTodayStartOfDay() {
        return getStartOfDay(LocalDate.now());
    }

    /**
     * 오늘 날짜의 종료 시간을 LocalDateTime 객체로 반환합니다.
     *
     * @return 오늘 날짜의 종료 시간 (LocalDateTime)
     */
    public static LocalDateTime getTodayEndOfDay() {
        return getEndOfDay(LocalDate.now());
    }

    /**
     * 오늘 날짜를 yyyyMMdd 형식으로 반환합니다.
     *
     * @return 오늘 날짜 문자열 (형식: yyyyMMdd)
     */
    public static String getTodayDate() {
        LocalDate today = LocalDate.now();
        return formatDate(today);
    }

    // Current DateTime methods

    /**
     * 현재 시간을 yyyyMMddHHmm 형식의 문자열로 반환합니다.
     *
     * @return 현재 시간 문자열 (형식: yyyyMMddHHmm)
     */
    public static String getCurrentDateTimeAsMinuteString() {
        LocalDateTime now = LocalDateTime.now();
        return now.format(DATE_TIME_MINUTE_FORMATTER);
    }

    /**
     * 특정 일자를 yyyyMMddHHmm 형식의 문자열로 반환합니다.
     *
     * @param dateTime 변환할 LocalDateTime 객체
     * @return 변환된 문자열 (형식: yyyyMMddHHmm)
     * @throws IllegalArgumentException dateTime 파라미터가 null인 경우 예외 발생
     */
    public static String formatDateTimeAsMinuteString(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("The dateTime parameter cannot be null");
        }
        return dateTime.format(DATE_TIME_MINUTE_FORMATTER);

    }
}
