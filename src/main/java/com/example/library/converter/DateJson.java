package com.example.library.converter;

import java.util.Calendar;
import java.util.Date;

public class DateJson {

    private Date date;
    private int year;

    public DateJson(){}

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    public void setYear(int year) {
        this.year = year;
    }
}
