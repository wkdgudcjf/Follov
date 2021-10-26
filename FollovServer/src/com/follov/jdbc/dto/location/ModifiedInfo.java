package com.follov.jdbc.dto.location;

public class ModifiedInfo
{
	private String last_sync_email;
	private String is_merged;
	
	public ModifiedInfo(){
		super();
	}

	public ModifiedInfo(String last_sync_email, String is_merged)
	{
		super();
		this.last_sync_email = last_sync_email;
		this.is_merged = is_merged;
	}

	public String getLast_sync_email()
	{
		return last_sync_email;
	}

	public void setLast_sync_email(String last_sync_email)
	{
		this.last_sync_email = last_sync_email;
	}

	public String getIs_merged()
	{
		return is_merged;
	}

	public void setIs_merged(String is_merged)
	{
		this.is_merged = is_merged;
	}

	@Override
	public String toString()
	{
		return "ModifiedInfo [last_sync_email=" + last_sync_email
				+ ", is_merged=" + is_merged + "]";
	}
	
	
	

}
