package com.follov.db.vo;

public class LocationPhoto
{
	private int date_code;
	private int loc_no;
	private int photo_no;
	private String name;
	private String date;
	
	public LocationPhoto() {
		super();
	}
	public LocationPhoto(int date_code, int loc_no, int photo_no, String name,
			String date) {
		super();
		this.date_code = date_code;
		this.loc_no = loc_no;
		this.photo_no = photo_no;
		this.name = name;
		this.date = date;
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
	public int getPhoto_no() {
		return photo_no;
	}
	public void setPhoto_no(int photo_no) {
		this.photo_no = photo_no;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	@Override
	public String toString() {
		return "LocationPhoto [date_code=" + date_code + ", loc_no=" + loc_no
				+ ", photo_no=" + photo_no + ", name=" + name + ", date="
				+ date + "]";
	}
	
}
