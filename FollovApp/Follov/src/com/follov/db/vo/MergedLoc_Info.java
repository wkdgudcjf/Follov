package com.follov.db.vo;

import java.io.*;
import java.util.*;

public class MergedLoc_Info implements Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int date_code;
	private ArrayList<Loc_route_tb_VO> loc_route_list;
	private ArrayList<Loc_photo_tb_VO> loc_photo_list;
	
	public MergedLoc_Info()
	{
		super();
		this.date_code = 0;
		this.loc_route_list = new ArrayList<Loc_route_tb_VO>();
		this.loc_photo_list = new ArrayList<Loc_photo_tb_VO>();
	}
	public MergedLoc_Info(int date_code,
			ArrayList<Loc_route_tb_VO> loc_route_list,
			ArrayList<Loc_photo_tb_VO> loc_photo_list)
	{
		super();
		this.date_code = date_code;
		this.loc_route_list = loc_route_list;
		this.loc_photo_list = loc_photo_list;
	}

	public int getDate_code()
	{
		return date_code;
	}
	public void setDate_code(int date_code)
	{
		this.date_code = date_code;
	}
	public ArrayList<Loc_route_tb_VO> getLoc_route_list()
	{
		return loc_route_list;
	}

	public void setLoc_route_list(ArrayList<Loc_route_tb_VO> loc_route_list)
	{
		this.loc_route_list = loc_route_list;
	}

	public ArrayList<Loc_photo_tb_VO> getLoc_photo_list()
	{
		return loc_photo_list;
	}

	public void setLoc_photo_list(ArrayList<Loc_photo_tb_VO> loc_photo_list)
	{
		this.loc_photo_list = loc_photo_list;
	}
	@Override
	public String toString()
	{
		return "MergedLoc_Info [date_code=" + date_code + ", loc_route_list="
				+ loc_route_list + ", loc_photo_list=" + loc_photo_list + "]";
	}
}
