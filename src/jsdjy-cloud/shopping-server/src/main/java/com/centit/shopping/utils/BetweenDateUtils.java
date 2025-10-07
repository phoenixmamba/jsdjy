package com.centit.shopping.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BetweenDateUtils {

    private List<Date> getBetweenDates(Date start, Date end) {
        List<Date> result = new ArrayList<Date>();
        Calendar tempStart = Calendar.getInstance();
        tempStart.setTime(start);
        tempStart.add(Calendar.DAY_OF_YEAR, 1);

        Calendar tempEnd = Calendar.getInstance();
        tempEnd.setTime(end);
        while (tempStart.before(tempEnd)) {
            result.add(tempStart.getTime());
            tempStart.add(Calendar.DAY_OF_YEAR, 1);
        }
        return result;
    }

    public static List<Date> getDays(String startTime, String endTime, String dateType) {

        // 返回的日期集合
        List<Date> days = new ArrayList<Date>();
        String format;
        if ("year".equals(dateType)) {
            format = "yyyy";
        } else if ("month".equals(dateType)) {
            format = "yyyy-MM";
        } else {
            format = "yyyy-MM-dd";
        }
        DateFormat dateFormat = new SimpleDateFormat(format);
        try {
            Date start = dateFormat.parse(startTime);
            Date end = dateFormat.parse(endTime);

            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(start);

            Calendar tempEnd = Calendar.getInstance();
            tempEnd.setTime(end);
            tempEnd.add(Calendar.DATE, +1);// 日期加1(包含结束)
            while (tempStart.before(tempEnd)) {
                days.add(tempStart.getTime());
                tempStart.add(Calendar.DAY_OF_YEAR, 1);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return days;
    }

    public static List<String> getDaysStrings(String startTime, String endTime, String dateType) {

        // 返回的日期集合
        List<String> days = new ArrayList<String>();
        String format;
        if ("year".equals(dateType)) {
            format = "yyyy";
        } else if ("month".equals(dateType)) {
            format = "yyyy-MM";
        } else {
            format = "yyyy-MM-dd";
        }
        DateFormat dateFormat = new SimpleDateFormat(format);
        try {
            Date start = dateFormat.parse(startTime);
            Date end = dateFormat.parse(endTime);

            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(start);

            Calendar tempEnd = Calendar.getInstance();
            tempEnd.setTime(end);
            tempEnd.add(Calendar.DATE, +1);// 日期加1(包含结束)
            while (tempStart.before(tempEnd)) {
                Date sdate = tempStart.getTime();
                if(!days.contains(dateFormat.format(sdate))){
                    days.add(dateFormat.format(sdate));
                }


//                days.add(tempStart.getTime());
                tempStart.add(Calendar.DAY_OF_YEAR, 1);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return days;
    }


    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        for(int i=0;i<25;i++){
            list.add(i);
        }
        System.out.println(list.subList(20,25));

//        System.out.println(getDaysStrings("2021-02", "2021-07", "month"));
    }
}
