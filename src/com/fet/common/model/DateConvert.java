package com.fet.common.model;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateConvert {
	public static final String DateSeparator = "/";
	public static final String LongDatePattern = "yyyy" + DateSeparator + "MM"
			+ DateSeparator + "dd HH:mm:ss";
	
	public static String DateToStr(Date datetime, String pattern, Locale locale) {
		String strdate = "";
		if (datetime == null) {
			return "";
		}
		if (locale == null)
			locale = Locale.getDefault();
		SimpleDateFormat simformat = new SimpleDateFormat(pattern, locale);
		strdate = simformat.format(datetime);
		return strdate;
	}
	
	public static String DateToStr(Date datetime, String pattern) {
		return DateToStr(datetime, pattern, null);
	}
}
