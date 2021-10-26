package com.follov.jdbc.dao;

import java.sql.*;

import com.follov.db.*;
import com.follov.jdbc.dto.location.*;
import com.follov.user.dto.*;

public class Data_input_user_tb_DAOImpl
{
	private static Data_input_user_tb_DAOImpl sInstance = new Data_input_user_tb_DAOImpl();
	
	private Data_input_user_tb_DAOImpl(){};
	
	public static Data_input_user_tb_DAOImpl getInstance(){
		return sInstance;
	}
	
	public int insert(Connection conn, Data_input_user_tb_VO vo){

		PreparedStatement pstmt = null;

		String insertSQL = "INSERT INTO data_input_user_tb(date_code, couple_id, email) " +
				" values(?,?,?)";
		int res = 0;

		DBManager db = DBManager.getInstance();

		try{

			pstmt = conn.prepareStatement(insertSQL);
			pstmt.setInt(1, vo.getDate_code());
			pstmt.setString(2, vo.getCouple_id());
			pstmt.setString(3, vo.getEmail());
			

			if(DBManager.DEBUG_MODE)
				System.out.println(pstmt.toString());
			res = pstmt.executeUpdate();


		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				db.dbClose(pstmt);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return res;
	}
	
	public boolean isDataInputedDateCode(Connection conn, String email, int date_code){
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String readSQL = "SELECT email "
				+ "FROM data_input_user_tb WHERE email = ? AND date_code = ?";

		DBManager db  = DBManager.getInstance();


		try {
			pstmt = conn.prepareStatement(readSQL);
			pstmt.setString(1, email);
			pstmt.setInt(2, date_code);
			rs = pstmt.executeQuery();

			while(rs.next()) {
				return true;
			}

			if(DBManager.DEBUG_MODE){
				System.out.println(pstmt.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				db.dbClose(pstmt);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return false;
		
	}
	

}
