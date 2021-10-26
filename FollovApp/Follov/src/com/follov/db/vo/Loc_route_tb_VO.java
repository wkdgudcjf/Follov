package com.follov.db.vo;

import java.io.*;

public class Loc_route_tb_VO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int date_code;
	private int loc_no;
	private double latitude;
	private double longitude;
	private String isSpecial;
	private String loc_time;
	
	public Loc_route_tb_VO() {
		super();
	}

	public Loc_route_tb_VO(int date_code, int loc_no, double latitude,
			double longitude, String isSpecial, String loc_time) {
		super();
		this.date_code = date_code;
		this.loc_no = loc_no;
		this.latitude = latitude;
		this.longitude = longitude;
		this.isSpecial = isSpecial;
		this.loc_time = loc_time;
	}

	public int getDate_code() {
		return date_code;
	}

	public void setDate_code(int date_code) {
		this.date_code = date_code;
	}

	public int getLoc_no() {
		return loc_no;
	}

	public void setLoc_no(int loc_no) {
		this.loc_no = loc_no;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getIsSpecial() {
		return isSpecial;
	}

	public void setIsSpecial(String isSpecial) {
		this.isSpecial = isSpecial;
	}

	public String getLoc_time() {
		return loc_time;
	}

	public void setLoc_time(String loc_time) {
		this.loc_time = loc_time;
	}

	public void increaseLoc_no(){
		this.loc_no++;
	}
	@Override
	public String toString() {
		return "Loc_route_tb_VO [date_code=" + date_code + ", loc_no=" + loc_no
				+ ", latitude=" + latitude + ", longitude=" + longitude
				+ ", isSpecial=" + isSpecial + ", loc_time=" + loc_time + "]";
	}
}
