package com.follov.db.vo;

import java.util.*;

public class DayOfWeek {

	public static final String SUNDAY = "sunday";
	public static final String MONDAY = "monday";
	public static final String TUESDAY = "tuesday";
	public static final String WEDNESDAY = "wednesday";
	public static final String THURSDAY = "thursday";
	public static final String FRIDAY = "friday";
	public static final String SATURDAY = "saturday";

	public static String isWhichDayOfWeek(int intDay){
		
		switch(intDay){
			case Calendar.SUNDAY:return SUNDAY;
			case Calendar.MONDAY:return MONDAY;
			case Calendar.TUESDAY:return TUESDAY;
			case Calendar.WEDNESDAY:return WEDNESDAY;
			case Calendar.THURSDAY:return THURSDAY;
			case Calendar.FRIDAY:return FRIDAY;
			case Calendar.SATURDAY:return SATURDAY;
		}
		
		return null;
	}
	
}
