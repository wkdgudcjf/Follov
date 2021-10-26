package com.follov.user.dto;

public class Data_input_user_tb_VO
{
	private int date_code;
	private String couple_id;
	private String email;
	
	public Data_input_user_tb_VO()
	{
		super();
	}
	
	public Data_input_user_tb_VO(int date_code, String couple_id, String email)
	{
		super();
		this.date_code = date_code;
		this.couple_id = couple_id;
		this.email = email;
	}
	
	public int getDate_code()
	{
		return date_code;
	}
	public void setDate_code(int date_code)
	{
		this.date_code = date_code;
	}
	public String getCouple_id()
	{
		return couple_id;
	}
	public void setCouple_id(String couple_id)
	{
		this.couple_id = couple_id;
	}
	public String getEmail()
	{
		return email;
	}
	public void setEmail(String email)
	{
		this.email = email;
	}
	@Override
	public String toString()
	{
		return "Data_input_user_tb_VO [date_code=" + date_code + ", couple_id="
				+ couple_id + ", email=" + email + "]";
	}
	
	
}
