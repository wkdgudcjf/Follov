package com.follov.db.vo;

import java.io.*;
import java.util.*;

public class RecentDataResponse implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ArrayList<Integer> upload_complete_list;
	
	private ArrayList<MergedLoc_Info> merged_list;

	
	public RecentDataResponse()
	{
		super();
		this.upload_complete_list = new ArrayList<Integer>();
		this.merged_list = new ArrayList<MergedLoc_Info>();
	}

	public RecentDataResponse(ArrayList<Integer> upload_complete_list,
			ArrayList<MergedLoc_Info> merged_list)
	{
		super();
		this.upload_complete_list = upload_complete_list;
		this.merged_list = merged_list;
	}

	public ArrayList<Integer> getUpload_complete_list()
	{
		return upload_complete_list;
	}

	public void setUpload_complete_list(ArrayList<Integer> upload_complete_list)
	{
		this.upload_complete_list = upload_complete_list;
	}

	public ArrayList<MergedLoc_Info> getMerged_list()
	{
		return merged_list;
	}

	public void setMerged_list(ArrayList<MergedLoc_Info> merged_list)
	{
		this.merged_list = merged_list;
	}

	@Override
	public String toString()
	{
		return "RecentDataResponse [upload_complete_list="
				+ upload_complete_list + ", merged_list=" + merged_list + "]";
	}
	
	
	
	
	
	
}
