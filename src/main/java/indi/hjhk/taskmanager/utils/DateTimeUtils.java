package indi.hjhk.taskmanager.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateTimeUtils {
    /**
     * 获取下个指定星期几的时间
     * @param current 当前时间
     * @param dayOfWeek 星期几(1~7)
     * @param timeOfDay 指定时间
     * @return 下个指定时间
     */
    public static LocalDateTime nextWeekdayTime(LocalDateTime current, int dayOfWeek, LocalTime timeOfDay){
        if (dayOfWeek < 1 || dayOfWeek > 7) return null;
        if (current.getDayOfWeek().equals(DayOfWeek.of(dayOfWeek))){
            // same day of week, look into time
            if (current.toLocalTime().isBefore(timeOfDay))
                return LocalDateTime.of(current.toLocalDate(), timeOfDay);
            else
                return LocalDateTime.of(current.toLocalDate().plusDays(7), timeOfDay);
        }else{
            int currentDayOfWeek = current.getDayOfWeek().getValue();
            int daysInterval = (dayOfWeek - currentDayOfWeek + 7) % 7;
            return LocalDateTime.of(current.toLocalDate().plusDays(daysInterval), timeOfDay);
        }
    }

    /**
     * 获取下个指定日数的时间
     * @param current 当前时间
     * @param dayOfMonth 日数(1~31)
     * @param timeOfDay 指定时间
     * @return 下个指定时间
     */
    public static LocalDateTime nextDayTime(LocalDateTime current, int dayOfMonth, LocalTime timeOfDay){
        if (dayOfMonth < 1 || dayOfMonth > 31) return null;
        int currentDayOfMonth = Math.min(limitDayOfMonth(current.getYear(), current.getMonthValue()), dayOfMonth);
        if (current.getDayOfMonth()<currentDayOfMonth
                || current.getDayOfMonth()==currentDayOfMonth && current.toLocalTime().isBefore(timeOfDay))
            // use current month
            return LocalDateTime.of(
                    current.toLocalDate().withDayOfMonth(currentDayOfMonth), timeOfDay);
        // use next month
        LocalDate nextMonth = current.toLocalDate().plusMonths(1);
        return LocalDateTime.of(
                nextMonth.withDayOfMonth(
                        Math.min(limitDayOfMonth(nextMonth.getYear(), nextMonth.getMonthValue()), dayOfMonth))
                , timeOfDay);
    }

    public static boolean isLeapYear(int year){
        return ((year & 3) == 0 && year % 100 != 0 || year % 400 == 0);
    }

    public static int limitDayOfMonth(int year, int month){
        int limit = 31;
        switch (month){
            case 2:
                limit = isLeapYear(year) ? 29: 28;
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                limit = 30;
        }
        return limit;
    }
}
