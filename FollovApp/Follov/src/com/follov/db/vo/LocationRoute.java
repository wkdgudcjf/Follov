package com.follov.db.vo;

import com.google.android.gms.maps.model.LatLng;

public class LocationRoute {

	public static final String IS_SPECIAL = "y";
	public static final String IS_NOT_SPECIAL = "";
	
	private int date_code;
	private int loc_no;
	private LatLng latLng;
	private String isSpecial;
	
	public LocationRoute() {
		super();
	}

	public LocationRoute(int date_code, int loc_no, double latitude,
			double longitude, String isSpecial) {
		super();
		this.date_code = date_code;
		this.loc_no = loc_no;
		this.latLng = new LatLng(latitude, longitude);
		this.isSpecial = isSpecial;
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

	
	

	public LatLng getLatLng() {
		return latLng;
	}

	public void setLatLng(LatLng latLng) {
		this.latLng = latLng;
	}

	public String getIsSpecial() {
		return isSpecial;
	}

	public void setIsSpecial(String isSpecial) {
		this.isSpecial = isSpecial;
	}

	@Override
	public String toString() {
		return "LocationRoute [date_code=" + date_code + ", loc_no=" + loc_no
				+ ", isSpecial=" + isSpecial + "]";
	}


	
	
	
	
}
