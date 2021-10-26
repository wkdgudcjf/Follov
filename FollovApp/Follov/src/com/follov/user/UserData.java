package com.follov.user;

public class UserData {
	private String couplePhone;
	private String email;
	private String coupleemail;
	private String password;
	private String coupleGcm;
	private String myGcm;
	private String coupleid;
	public String getCoupleGcm() {
		return coupleGcm;
	}
	public String getCoupleid()
	{
		return coupleid;
	}
	public void setCoupleid(String coupleid)
	{
		this.coupleid = coupleid;
	}
	public void setCoupleGcm(String coupleGcm) {
		this.coupleGcm = coupleGcm;
	}
	public String getMyGcm() {
		return myGcm;
	}
	public void setMyGcm(String myGcm) {
		this.myGcm = myGcm;
	}
	private int myyear;
	private int mymonth;
	private int myday;
	private int coupleyear;
	private int couplemonth;
	private int coupleday;
	private boolean sex;
	public String getCoupleemail() {
		return coupleemail;
	}
	public void setCoupleemail(String coupleemail) {
		this.coupleemail = coupleemail;
	}
	public UserData() {
		super();
	}
	public boolean isSex() {
		return sex;
	}
	public void setSex(boolean sex) {
		this.sex = sex;
	}
	public UserData(String couplePhone, String email,
			String password, int myyear, int mymonth, int myday,
			int coupleyear, int couplemonth, int coupleday) {
		super();
		
		this.couplePhone = couplePhone;
		this.email = email;
		this.password = password;
		this.myyear = myyear;
		this.mymonth = mymonth;
		this.myday = myday;
		this.coupleyear = coupleyear;
		this.couplemonth = couplemonth;
		this.coupleday = coupleday;
	}
	
	public String getCouplePhone() {
		return couplePhone;
	}
	public void setCouplePhone(String couplePhone) {
		this.couplePhone = couplePhone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getMyyear() {
		return myyear;
	}
	public void setMyyear(int myyear) {
		this.myyear = myyear;
	}
	public int getMymonth() {
		return mymonth;
	}
	public void setMymonth(int mymonth) {
		this.mymonth = mymonth;
	}
	public int getMyday() {
		return myday;
	}
	public void setMyday(int myday) {
		this.myday = myday;
	}
	public int getCoupleyear() {
		return coupleyear;
	}
	public void setCoupleyear(int coupleyear) {
		this.coupleyear = coupleyear;
	}
	public int getCouplemonth() {
		return couplemonth;
	}
	public void setCouplemonth(int couplemonth) {
		this.couplemonth = couplemonth;
	}
	public int getCoupleday() {
		return coupleday;
	}
	public void setCoupleday(int coupleday) {
		this.coupleday = coupleday;
	}
	
}
