package com.follov.jdbc.dto.location;

/**
 * @author JHC
 *
 */
public class Date_tb_VO {

	private int date_code;

	private int year;
	private int month;
	private int day;
	private String day_of_week = "";
	private String start_time = "";
	private String end_time = "";
	private String last_sync_email = "";
	private String is_modified = "";
	private String is_merged = "";
	private String weather = "";
	
	public Date_tb_VO() {
		super();
	}
	

	public Date_tb_VO(int date_code, int year, int month,
			int day, String day_of_week, String start_time, String last_sync_email, String is_modified, String is_merged) {
		super();
		this.date_code = date_code;
		this.year = year;
		this.month = month;
		this.day = day;
		this.day_of_week = day_of_week;
		this.start_time = start_time;
		this.last_sync_email = last_sync_email;
		this.is_modified = is_modified;
		this.is_merged = is_merged;
	}


	public Date_tb_VO(int date_code, int year, int month, int day, String day_of_week,
			String start_time, String end_time, String weather) {
		this.date_code = date_code;
		this.year = year;
		this.month = month;
		this.day = day;
		this.day_of_week = day_of_week;
		this.start_time = start_time;
		this.end_time = end_time;
		this.weather = weather;
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

	public String getLast_sync_email()
	{
		return last_sync_email;
	}


	public void setLast_sync_email(String last_sync_email)
	{
		this.last_sync_email = last_sync_email;
	}

	

	public String getIs_modified()
	{
		return is_modified;
	}


	public void setIs_modified(String is_modified)
	{
		this.is_modified = is_modified;
	}


	public String getIs_merged()
	{
		return is_merged;
	}


	public void setIs_merged(String is_merged)
	{
		this.is_merged = is_merged;
	}


	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}
	
	
	
}
