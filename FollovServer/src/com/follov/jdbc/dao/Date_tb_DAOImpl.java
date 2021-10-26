package com.follov.jdbc.dao;

import java.sql.*;
import java.util.ArrayList;

import org.json.*;

import com.follov.db.DBManager;
import com.follov.jdbc.dto.location.*;

public class Date_tb_DAOImpl {

	private static Date_tb_DAOImpl sInstance = new Date_tb_DAOImpl();

	private Date_tb_DAOImpl(){};

	public static Date_tb_DAOImpl getInstance(){
		return sInstance;
	}

	public String getSyncTime(Connection conn, String couple_id, int date_code){
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String readSQL = "SELECT sync_time "
				+ "FROM date_tb WHERE couple_id = ? AND date_code = ?";

		DBManager db  = DBManager.getInstance();


		try {
			pstmt = conn.prepareStatement(readSQL);
			pstmt.setString(1, couple_id);
			pstmt.setInt(2, date_code);
			rs = pstmt.executeQuery();

			while(rs.next()) {
				return rs.getString(1);
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

		return "";

	}
	
	public ModifiedInfo selectUnMergedDate(Connection conn, String couple_id, int date_code){
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String readSQL = "SELECT last_sync_email, is_merged "
				+ "FROM date_tb WHERE couple_id = ? AND date_code = ?";


		ModifiedInfo vo = null;

		DBManager db  = DBManager.getInstance();

		//		ArrayList<Date_tb_VO> newDateList = new ArrayList<Date_tb_VO>();

		try {
			pstmt = conn.prepareStatement(readSQL);
			pstmt.setString(1, couple_id);
			pstmt.setInt(2, date_code);
			rs = pstmt.executeQuery();

			while(rs.next()) {
				vo = new ModifiedInfo(
						rs.getString("last_sync_email"),
						rs.getString("is_merged"));

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

		return vo;
	}

	public Date_tb_VO recentDateCodeAndSyncTime(Connection conn, String couple_id){
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String readSQL = "SELECT max(date_code), sync_time "
				+ "FROM date_tb WHERE couple_id = ?";


		Date_tb_VO vo = new Date_tb_VO();

		DBManager db  = DBManager.getInstance();

		//		ArrayList<Date_tb_VO> newDateList = new ArrayList<Date_tb_VO>();

		try {
			pstmt = conn.prepareStatement(readSQL);
			pstmt.setString(1, couple_id);
			rs = pstmt.executeQuery();

			while(rs.next()) {
				vo.setDate_code(rs.getInt(1));

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

		return vo;
	}

	public int recentDateCode(Connection conn, String couple_id){
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String readSQL = "SELECT max(date_code) "
				+ "FROM date_tb WHERE couple_id = ?";

		int recentDateCode = 0;
		DBManager db  = DBManager.getInstance();

		//		ArrayList<Date_tb_VO> newDateList = new ArrayList<Date_tb_VO>();

		try {
			pstmt = conn.prepareStatement(readSQL);
			pstmt.setString(1, couple_id);
			rs = pstmt.executeQuery();

			while(rs.next()) {
				recentDateCode = rs.getInt(1);
			}

			if(DBManager.DEBUG_MODE){
				System.out.println(pstmt.toString());
				System.out.println("server recent date_code : "+recentDateCode);
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

		return recentDateCode;
	}


	public int insert(Connection conn, Date_tb_VO vo, String couple_id){

		PreparedStatement pstmt = null;

		String insertSQL = "INSERT INTO date_tb(date_code, couple_id, year, " +
				"month, day, day_of_week, start_time, end_time, last_sync_email, is_modified, is_merged, weather) values(?,?,?,?,?,?,?,?,?,?,?,?)";
		int res = 0;

		DBManager db = DBManager.getInstance();

		try{

			pstmt = conn.prepareStatement(insertSQL);
			pstmt.setInt(1, vo.getDate_code());
			pstmt.setString(2, couple_id);
			pstmt.setInt(3, vo.getYear());
			pstmt.setInt(4, vo.getMonth());
			pstmt.setInt(5, vo.getDay());
			pstmt.setString(6, vo.getDay_of_week());
			pstmt.setString(7, vo.getStart_time());
			pstmt.setString(8, vo.getEnd_time());
			pstmt.setString(9, vo.getLast_sync_email());
			pstmt.setString(10, vo.getIs_modified());
			pstmt.setString(11, vo.getIs_merged());
			pstmt.setString(12, vo.getWeather());

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


	public JSONArray selectAllDates(Connection conn, String couple_id){
		JSONArray jsonArray = new JSONArray();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String readSQL = "SELECT date_code, year, month, day, day_of_week, start_time, end_time, last_sync_email, is_modified, is_merged, weather "
				+ "FROM date_tb WHERE couple_id = ?";

		DBManager db  = DBManager.getInstance();

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
	
	
	public JSONArray selectUnSynchronizedDataAfterMerge(Connection conn, String couple_id, String email){
		JSONArray jsonArray = new JSONArray();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
	

		String readSQL = "SELECT date_code, year, month, day, day_of_week, start_time, end_time, last_sync_email, is_modified, is_merged, weather "
				+ "FROM date_tb " 
				+ "WHERE " +
				"(couple_id = ? AND is_modified = 'y' AND is_merged='y') " +
				"AND (last_sync_email = ? AND get_data_after_merge != 'y' ) ";
		
		DBManager db  = DBManager.getInstance();

		try {
			pstmt = conn.prepareStatement(readSQL);
			pstmt.setString(1, couple_id);
			pstmt.setString(2, email);
		
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
	
	
	
	public JSONArray selectModifiedDates(Connection conn, String couple_id, String email){
		JSONArray jsonArray = new JSONArray();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		//잠시 백업
		String readSQL = "SELECT date_code, year, month, day, day_of_week, start_time, end_time, last_sync_email, is_modified, is_merged, weather "
				+ "FROM date_tb " 
				+ "WHERE couple_id = ? AND last_sync_email != ? AND is_modified = 'y' AND is_merged='y' ";

		
		DBManager db  = DBManager.getInstance();

		try {
			pstmt = conn.prepareStatement(readSQL);
			pstmt.setString(1, couple_id);
			pstmt.setString(2, email);
		
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

	public JSONArray getNewDates(Connection conn, String couple_id, int date_code){

		JSONArray jsonArray = new JSONArray();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String readSQL = "SELECT date_code, year, month, day, day_of_week, start_time, end_time, weather "
				+ "FROM date_tb WHERE couple_id = ? AND date_code > ?";

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
				//				Date_tb_VO vo = new Date_tb_VO();
				//				vo.setDate_code(rs.getInt("date_code"));
				//				vo.setYear(rs.getInt("year"));
				//				vo.setMonth(rs.getInt("month"));
				//				vo.setDay(rs.getInt("day"));
				//				vo.setDay_of_week(rs.getString("day_of_week"));
				//				vo.setStart_time(rs.getString("start_time"));
				//				vo.setEnd_time(rs.getString("end_time"));
				//				vo.setWeather(rs.getString("weather"));
				//				
				//				newDateList.add(vo);
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

		return jsonArray;

	}
	
	
	public void updateGet_Data_After_Merge(Connection conn, String couple_id, int date_code, String get_data_after_merge){
		PreparedStatement pstmt = null;
		String updateSQL = "UPDATE date_tb SET get_data_after_merge = ? "
				+ " WHERE couple_id=? AND date_code=?";
		int res = 0;
		DBManager db  = DBManager.getInstance();

		try {
			pstmt = conn.prepareStatement(updateSQL);
			pstmt.setString(1, get_data_after_merge);
			pstmt.setString(2, couple_id);
			pstmt.setInt(3, date_code);
			res = pstmt.executeUpdate();

			if(DBManager.DEBUG_MODE){
				System.out.println(pstmt.toString());
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				db.dbClose(pstmt);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	
	public void updateDateIsMerged(Connection conn, String couple_id, String email, int date_code) throws SQLException{
		PreparedStatement pstmt = null;
		String updateSQL = "UPDATE date_tb SET is_modified = 'y', last_sync_email = ?, is_merged = 'y' "
				+ " WHERE couple_id=? AND date_code=?";
		int res = 0;
		DBManager db  = DBManager.getInstance();

		try {
			pstmt = conn.prepareStatement(updateSQL);
			pstmt.setString(1, email);
			pstmt.setString(2, couple_id);
			pstmt.setInt(3, date_code);
			res = pstmt.executeUpdate();

			if(DBManager.DEBUG_MODE){
				System.out.println(pstmt.toString());
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				db.dbClose(pstmt);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	
	public int updateDateModified(Connection conn, String couple_id, 
			int date_code, String is_modified){
		
		PreparedStatement pstmt = null;
		String updateSQL = "UPDATE date_tb SET is_modified=?"
				+ " WHERE couple_id=? AND date_code=?";
		int res = 0;
		DBManager db  = DBManager.getInstance();

		try {
			pstmt = conn.prepareStatement(updateSQL);
			pstmt.setString(1, is_modified);
			pstmt.setString(2, couple_id);
			pstmt.setInt(3, date_code);

			//System.out.println(vo.getSeq());
			res = pstmt.executeUpdate();

			if(DBManager.DEBUG_MODE){
				System.out.println("res : "+res+"건이 수정되었습니다");
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

		return res;
	}
	

	public int updateGet_data_after_mergeToY(Connection conn, String couple_id, 
			int date_code, String get_data_after_merge ){
		
		PreparedStatement pstmt = null;
		String updateSQL = "UPDATE date_tb SET get_data_after_merge = ?"
				+ " WHERE couple_id=? AND date_code=?";
		int res = 0;
		DBManager db  = DBManager.getInstance();

		try {
			pstmt = conn.prepareStatement(updateSQL);
			pstmt.setString(1, get_data_after_merge);
			pstmt.setString(2, couple_id);
			pstmt.setInt(3, date_code);

			//System.out.println(vo.getSeq());
			res = pstmt.executeUpdate();

			if(DBManager.DEBUG_MODE){
				System.out.println("res : "+res+"건이 수정되었습니다");
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

		return res;
	}

	public int updateEnd_Time(Connection conn, String couple_id, int date_code, String end_time){

		PreparedStatement pstmt = null;
		String updateSQL = "UPDATE date_tb SET end_time=?"
				+ " WHERE couple_id=? AND date_code=?";
		int res = 0;
		DBManager db  = DBManager.getInstance();

		try {
			pstmt = conn.prepareStatement(updateSQL);
			pstmt.setString(1, end_time);
			pstmt.setString(2, couple_id);
			pstmt.setInt(3, date_code);

			//System.out.println(vo.getSeq());
			res = pstmt.executeUpdate();

			if(DBManager.DEBUG_MODE){
				System.out.println("res : "+res+"건이 수정되었습니다");
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

		return res;
	}
}
