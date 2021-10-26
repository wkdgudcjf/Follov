package com.follov.db.vo;

public class Date {

	private int date_code;
	private int year;
	private int month;
	private int day;
	private String day_of_week;
	private String start_time;
	private String end_time;
	private String weather;
	
	public Date() {
		super();
	}

	public Date(int date_code, int year, int month, int day, String day_of_week) {
		super();
		this.date_code = date_code;
		this.year = year;
		this.month = month;
		this.day = day;
		this.day_of_week = day_of_week;
	}

	public Date(int date_code, int year, int month, int day,
			String day_of_week, String start_time) {
		super();
		this.date_code = date_code;
		this.year = year;
		this.month = month;
		this.day = day;
		this.day_of_week = day_of_week;
		this.start_time = start_time;
	}

	public int getDate_code() {
		return date_code;
	}

	public void setDate_code(int date_code) {
		this.date_code = date_code;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public String getDay_of_week() {
		return day_of_week;
	}

	public void setDay_of_week(String day_of_week) {
		this.day_of_week = day_of_week;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}

	@Override
	public String toString() {
		return "Date [date_code=" + date_code + ", year=" + year + ", month="
				+ month + ", day=" + day + ", day_of_week=" + day_of_week
				+ ", start_time=" + start_time + ", end_time=" + end_time
				+ ", weather=" + weather + "]";
	}
	
	
	
	
	
	
	
	
}
