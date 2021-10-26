package com.follov.user.dto;

public class User {
	private String email;
	private String coupleId;
	private String pwd;
	private String gender;
	private int birthYear;
	private int birthMonth;
	private int birthDay;
	private String profilePhoto;
	private String gcmid;
	private String phone;
	public String getPhone()
	{
		return phone;
	}
	public void setPhone(String phone)
	{
		this.phone = phone;
	}
	public String getGcmid() {
		return gcmid;
	}
	public void setGcmid(String gcmid) {
		this.gcmid = gcmid;
	}
	public User() {}
	public User(String email, String pwd) {
		this.email = email;
		this.pwd = pwd;
	}
	public User(String email, String pwd, String coupleId) {
		this.email = email;
		this.pwd = pwd;
		this.coupleId = coupleId;
	}	
	public User(String email, String coupleId, String pwd, String gender,
			int birthYear, int birthMonth, int birthDay) {
		super();
		this.email = email;
		this.coupleId = coupleId;
		this.pwd = pwd;
		this.gender = gender;
		this.birthYear = birthYear;
		this.birthMonth = birthMonth;
		this.birthDay = birthDay;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCoupleId() {
		return coupleId;
	}
	public void setCoupleId(String coupleId) {
		this.coupleId = coupleId;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public int getBirthYear() {
		return birthYear;
	}
	public void setBirthYear(int birthYear) {
		this.birthYear = birthYear;
	}
	public int getBirthMonth() {
		return birthMonth;
	}
	public void setBirthMonth(int birthMonth) {
		this.birthMonth = birthMonth;
	}
	public int getBirthDay() {
		return birthDay;
	}
	public void setBirthDay(int birthDay) {
		this.birthDay = birthDay;
	}
	public String getProfilePhoto() {
		return profilePhoto;
	}
	public void setProfilePhoto(String profilePhoto) {
		this.profilePhoto = profilePhoto;
	}

}
