package com.liu.util.context.beans;

import com.liu.util.converter.ConversionUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.Date;

public class DateBean {

	public Date parse(String source) {
		return ConversionUtils.convert(source, Date.class);
	}

	public Date getToday() {
		LocalDate dt = new LocalDate();
		return dt.toDate();
	}

	public Date getDatetime() {
		return new Date();
	}

	public Date getNow() {
		return getDatetime();
	}

	public Date getTomorrow() {
		LocalDate dt = new LocalDate();
		return dt.plusDays(1).toDate();
	}

	public Date getDatetimeOfNextDay() {
		DateTime dt = new DateTime();
		return dt.plusDays(1).toDate();
	}

	public Date getDateOfNextMonth() {
		LocalDate dt = new LocalDate();
		return dt.plusMonths(1).toDate();
	}

	public Date getDatetimeOfNextMonth() {
		DateTime dt = new DateTime();
		return dt.plusMonths(1).toDate();
	}

	public Date getDateOfNextYear() {
		LocalDate dt = new LocalDate();
		return dt.plusYears(1).toDate();
	}

	public Date getDatetimeOfNextYear() {
		DateTime dt = new DateTime();
		return dt.plusYears(1).toDate();
	}

	public Date getYesterday() {
		LocalDate dt = new LocalDate();
		return dt.minusDays(1).toDate();
	}

	public Date getDatetimeOfLastDay() {
		DateTime dt = new DateTime();
		return dt.minusDays(1).toDate();
	}

	public Date getDateOfLastMonth() {
		LocalDate dt = new LocalDate();
		return dt.minusMonths(1).toDate();
	}

	public Date getDatetimeOfLastMonth() {
		DateTime dt = new DateTime();
		return dt.minusMonths(1).toDate();
	}

	public Date getDateOfLastYear() {
		LocalDate dt = new LocalDate();
		return dt.minusYears(1).toDate();
	}

	public Date getDatetimeOfLastYear() {
		DateTime dt = new DateTime();
		return dt.minusYears(1).toDate();
	}

	public Date getDateOfLastWeek() {
		LocalDate dt = new LocalDate();
		return dt.minusWeeks(1).toDate();
	}

	public Date getDatetimeOfLastWeek() {
		DateTime dt = new DateTime();
		return dt.minusWeeks(1).toDate();
	}

	public int getYear() {
		LocalDate dt = new LocalDate();
		return dt.getYear();
	}

	public int getMonth() {
		LocalDate dt = new LocalDate();
		return dt.getMonthOfYear();
	}

	public int getDay() {
		LocalDate dt = new LocalDate();
		return dt.getDayOfMonth();
	}

	public int getWeekDay() {
		LocalDate dt = new LocalDate();
		return dt.getDayOfWeek();
	}

}
