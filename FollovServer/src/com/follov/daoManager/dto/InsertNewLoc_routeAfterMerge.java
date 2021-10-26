package com.follov.daoManager.dto;

import com.follov.db.vo.*;

public class InsertNewLoc_routeAfterMerge
{
	private Loc_route_tb_VO client_loc_route;
	private Loc_route_tb_VO server_loc_route;
	
	public InsertNewLoc_routeAfterMerge()
	{
		super();
	}

	public InsertNewLoc_routeAfterMerge(
			Loc_route_tb_VO client_loc_route, Loc_route_tb_VO server_loc_route)
	{
		super();
		this.client_loc_route = client_loc_route;
		this.server_loc_route = server_loc_route;
	}

	public Loc_route_tb_VO getClient_loc_route()
	{
		return client_loc_route;
	}

	public void setClient_loc_route(Loc_route_tb_VO client_loc_route)
	{
		this.client_loc_route = client_loc_route;
	}

	public Loc_route_tb_VO getServer_loc_route()
	{
		return server_loc_route;
	}

	public void setServer_loc_route(Loc_route_tb_VO server_loc_route)
	{
		this.server_loc_route = server_loc_route;
	}

	@Override
	public String toString()
	{
		return "InsertNewLoc_routeAfterMergeIndexes [client_loc_route="
				+ client_loc_route + ", server_loc_route=" + server_loc_route
				+ "]";
	}
	
	
	
	
}
