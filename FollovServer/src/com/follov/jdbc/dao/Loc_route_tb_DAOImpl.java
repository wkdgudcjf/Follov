package com.follov.jdbc.dao;

import java.sql.*;
import java.util.*;

import org.json.*;

import com.follov.db.*;
import com.follov.db.vo.*;

public class Loc_route_tb_DAOImpl {

	private static Loc_route_tb_DAOImpl sInstance = new Loc_route_tb_DAOImpl();

	private Loc_route_tb_DAOImpl(){}

	public static Loc_route_tb_DAOImpl getInstance(){
		return sInstance;
	}
	
	public int recentLoc_no(Connection conn, String couple_id, int date_code){
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String readSQL = "SELECT max(loc_no) "
				+ "FROM loc_route_tb WHERE couple_id = ? AND date_code = ?";

		int recentLoc_no = 0;
		DBManager db  = DBManager.getInstance();

//		ArrayList<Date_tb_VO> newDateList = new ArrayList<Date_tb_VO>();

		try {
			pstmt = conn.prepareStatement(readSQL);
			pstmt.setString(1, couple_id);
			pstmt.setInt(2, date_code);
			rs = pstmt.executeQuery();

			while(rs.next()) {
				recentLoc_no = rs.getInt(1);
			}

			if(DBManager.DEBUG_MODE){
				System.out.println(pstmt.toString());
				System.out.println("server recent date_code : "+recentLoc_no);
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

		return recentLoc_no;
	}


	public int insert(Connection conn, Loc_route_tb_VO vo, String couple_id) throws SQLException{

		PreparedStatement pstmt = null;

		String insertSQL = "INSERT INTO loc_route_tb(couple_id, date_code, loc_no, " +
				"latitude, longitude, isSpecial, loc_time) values(?,?,?,?,?,?,?)";
		int res = 0;

		DBManager db = DBManager.getInstance();
	
		try{

			pstmt = conn.prepareStatement(insertSQL);
			pstmt.setString(1, couple_id);
			pstmt.setInt(2, vo.getDate_code());
			pstmt.setInt(3, vo.getLoc_no());
			pstmt.setDouble(4, vo.getLatitude());
			pstmt.setDouble(5, vo.getLongitude());
			pstmt.setString(6, vo.getIsSpecial());
			pstmt.setString(7, vo.getLoc_time());

			if(DBManager.DEBUG_MODE)
				System.out.println(pstmt.toString());
			res = pstmt.executeUpdate();


		}catch(SQLException e){
			e.printStackTrace();
			throw e;
		}finally{
			try{
				db.dbClose(pstmt);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return res;
	}
	
	
	public int update(Connection conn, String couple_id, int date_code, int loc_no){

		PreparedStatement pstmt = null;

		String insertSQL =  "UPDATE loc_route_tb SET isSpecial=? where couple_id=? and date_code = ? and loc_no = ?";
		int res = 0;

		DBManager db = DBManager.getInstance();

		try{

			pstmt = conn.prepareStatement(insertSQL);
			pstmt.setString(1, "y");
			pstmt.setString(2, couple_id);
			pstmt.setInt(3, date_code);
			pstmt.setInt(4, loc_no);
		
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
	
	public ArrayList<Loc_route_tb_VO> selectAllToArrayList(Connection conn, String couple_id, int date_code){
		
		ArrayList<Loc_route_tb_VO> loc_route_list = new ArrayList<Loc_route_tb_VO>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String readSQL = "SELECT date_code, loc_no , latitude , longitude , isSpecial, loc_time "
				+ "FROM loc_route_tb " +
				"WHERE couple_id = ? AND date_code = ?";

		DBManager db  = DBManager.getInstance();

//		ArrayList<Date_tb_VO> newDateList = new ArrayList<Date_tb_VO>();

		try {
			pstmt = conn.prepareStatement(readSQL);
			pstmt.setString(1, couple_id);
			pstmt.setInt(2, date_code);
		
			rs = pstmt.executeQuery();

			while(rs.next()) {
				
				Loc_route_tb_VO vo = new Loc_route_tb_VO(
						date_code, rs.getInt("loc_no"), 
						rs.getDouble("latitude"), rs.getDouble("longitude"), 
						rs.getString("isSpecial"), rs.getString("loc_time"));
				
				loc_route_list.add(vo);
			}

			if(DBManager.DEBUG_MODE){
				System.out.println(pstmt.toString());
				System.out.println(loc_route_list.toString());
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

		return loc_route_list;
	}
	
	public JSONArray selectAllLoc_routes(Connection conn, String couple_id){

		JSONArray jsonArray = new JSONArray();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String readSQL = "SELECT date_code, loc_no , latitude , longitude , isSpecial, loc_time "
				+ "FROM loc_route_tb " +
				"WHERE couple_id = ? ";

		DBManager db  = DBManager.getInstance();

//		ArrayList<Date_tb_VO> newDateList = new ArrayList<Date_tb_VO>();

		try {
			pstmt = conn.prepareStatement(readSQL);
			pstmt.setString(1, couple_id);
		
			rs = pstmt.executeQuery();

			while(rs.next()) {
				
				JSONObject jo = new JSONObject();
				ResultSetMetaData rmd = rs.getMetaData();
				
				int size = rmd.getColumnCount() + 1;
				
				for(int i = 1; i < size; i++){
					String columnName = rmd.getColumnName(i);
					jo.put(columnName, rs.getString(columnName));
				}
				
				jsonArray.put(jo);
			}

			if(DBManager.DEBUG_MODE){
				System.out.println(pstmt.toString());
				System.out.println(jsonArray.toString());
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

		return jsonArray;
	}
	
	public JSONArray selectLoc_routes(Connection conn, JSONArray jsonArray, String couple_id, int date_code){
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String readSQL = "SELECT date_code, loc_no , latitude , longitude , isSpecial, loc_time "
				+ "FROM loc_route_tb " +
				"WHERE couple_id = ? AND date_code = ?";

		DBManager db  = DBManager.getInstance();

//		ArrayList<Date_tb_VO> newDateList = new ArrayList<Date_tb_VO>();

		try {
			pstmt = conn.prepareStatement(readSQL);
			pstmt.setString(1, couple_id);
			pstmt.setInt(2, date_code);
		
			rs = pstmt.executeQuery();

			while(rs.next()) {
				
				JSONObject jo = new JSONObject();
				ResultSetMetaData rmd = rs.getMetaData();
				
				int size = rmd.getColumnCount() + 1;
				
				for(int i = 1; i < size; i++){
					String columnName = rmd.getColumnName(i);
					jo.put(columnName, rs.getString(columnName));
				}
				
				jsonArray.put(jo);
			}

			if(DBManager.DEBUG_MODE){
				System.out.println(pstmt.toString());
				System.out.println(jsonArray.toString());
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

		return jsonArray;
	}
	
	
	
	public JSONArray selectLoc_routes(Connection conn, String couple_id, 
			int date_code, int loc_no){

		JSONArray jsonArray = new JSONArray();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String readSQL = "SELECT date_code, loc_no , latitude , longitude , isSpecial "
				+ "FROM loc_route_tb " +
				"WHERE couple_id = ? " +
				"AND ( (date_code > ?) OR (date_code = ? AND loc_no > ?))";

		DBManager db  = DBManager.getInstance();

//		ArrayList<Date_tb_VO> newDateList = new ArrayList<Date_tb_VO>();

		try {
			pstmt = conn.prepareStatement(readSQL);
			pstmt.setString(1, couple_id);
			pstmt.setInt(2, date_code);
			pstmt.setInt(3, date_code);
			pstmt.setInt(4, loc_no);
			rs = pstmt.executeQuery();

			while(rs.next()) {
				
				JSONObject jo = new JSONObject();
				ResultSetMetaData rmd = rs.getMetaData();
				
				int size = rmd.getColumnCount() + 1;
				
				for(int i = 1; i < size; i++){
					String columnName = rmd.getColumnName(i);
					jo.put(columnName, rs.getString(columnName));
				}
				
				jsonArray.put(jo);
			}

			if(DBManager.DEBUG_MODE){
				System.out.println(pstmt.toString());
				System.out.println(jsonArray.toString());
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

		return jsonArray;
	}
	
	public int delete(Connection conn, String couple_id, int date_code) throws SQLException{

		PreparedStatement pstmt = null;

		String insertSQL =  "DELETE FROM loc_route_tb where couple_id=? and date_code = ?";
		int res = 0;

		pstmt = conn.prepareStatement(insertSQL);
		pstmt.setString(1, couple_id);
		pstmt.setInt(2, date_code);

		if(DBManager.DEBUG_MODE)
			System.out.println(pstmt.toString());
		res = pstmt.executeUpdate();
		
		return res;
	}

//	public int updateEnd_Time(Connection conn, String couple_id, int date_code, String end_time){
//
//		PreparedStatement pstmt = null;
//		String updateSQL = "UPDATE date_tb SET end_time=?"
//				+ " WHERE couple_id=? AND date_code=?";
//		int res = 0;
//		DBManager db  = DBManager.getInstance();
//
//		try {
//			pstmt = conn.prepareStatement(updateSQL);
//			pstmt.setString(1, end_time);
//			pstmt.setString(2, couple_id);
//			pstmt.setInt(3, date_code);
//
//			//System.out.println(vo.getSeq());
//			res = pstmt.executeUpdate();
//
//			if(DBManager.DEBUG_MODE){
//				System.out.println("res : "+res+"건이 수정되었습니다");
//				System.out.println(pstmt.toString());
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				db.dbClose(pstmt);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//
//		return res;
//	}

}
