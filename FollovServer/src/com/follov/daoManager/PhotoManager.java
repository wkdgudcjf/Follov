package com.follov.daoManager;

import java.sql.*;

import com.follov.db.vo.*;
import com.follov.jdbc.dao.*;

public class PhotoManager
{
	private static PhotoManager sInstance = new PhotoManager();
	
	private PhotoManager(){}
	
	public static PhotoManager getInstance(){
		return sInstance;
	}
	
	public boolean savePhotoToDB(Connection conn, String coupleid, String date_code, String loc_no, String name, String date){
		
//		Loc_photo_tb_DAOImpl photoDAO = Loc_photo_tb_DAOImpl.getInstance();
//		Loc_route_tb_DAOImpl locationDAO =Loc_route_tb_DAOImpl.getInstance();
//		
//		boolean photoInsertSuccess;
//		boolean locationUpdateSuccess;
//		
//		int date_code_int = Integer.valueOf(date_code);
//		int loc_no_int = Integer.valueOf(loc_no);
//		
//		Loc_photo_tb_VO photoVO = new Loc_photo_tb_VO(date_code_int, loc_no_int, name, date);
//		
//		try
//		{
//			if(photoDAO.insert(conn, photoVO, coupleid) > 0){
//				if(locationDAO.update(conn, coupleid, date_code_int, loc_no_int) > 0)
//				{
//					return true;
//				}
//				else
//				{
//					try
//					{
//						photoDAO.delete(conn, coupleid, date_code_int);
//					} catch (SQLException e)
//					{
//						e.printStackTrace();
//					}
//					return false;
//				}
//			}
//			
//			else
//			{
//				return false;
//			}
//		} catch (SQLException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
		return false;
		

		
	}
	
	
}
