package com.follov.jdbc.dao;

import java.sql.*;
import java.util.*;

import org.json.*;

import com.follov.db.*;
import com.follov.db.vo.*;

public class Loc_photo_tb_DAOImpl
{

	private static Loc_photo_tb_DAOImpl sInstance = new Loc_photo_tb_DAOImpl();

	private Loc_photo_tb_DAOImpl(){};

	public static Loc_photo_tb_DAOImpl getInstance(){
		return sInstance;
	}

	public int recentPhoto_no(Connection conn, String couple_id){

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String readSQL = "SELECT max(photo_no) FROM loc_photo_tb WHERE couple_id = ?";

		int maxPhotoNo = 0;
		DBManager db = DBManager.getInstance();



		try {
			pstmt = conn.prepareStatement(readSQL);
			pstmt.setString(1, couple_id);
			rs = pstmt.executeQuery();

			while(rs.next()) {
				maxPhotoNo = rs.getInt(1);
			}

			if(DBManager.DEBUG_MODE){
				System.out.println(pstmt.toString());
				System.out.println("maxPhoto_no : "+maxPhotoNo);
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

		return maxPhotoNo;

	}

	public int insert(Connection conn, Loc_photo_tb_VO vo, String couple_id) throws SQLException{

		//int maxPhotoNo = maxPhoto_no(conn, vo.getCouple_id());
		PreparedStatement pstmt = null;

		String insertSQL = "INSERT INTO loc_photo_tb(couple_id, date_code , loc_no , " +
				"name  , date, photo_taken_email, is_img_uploaded)  values(?,?,?,?,?,?,?)";
		int res = 0;

		DBManager db = DBManager.getInstance();

		try{

			pstmt = conn.prepareStatement(insertSQL);
			pstmt.setString(1, couple_id);
			//pstmt.setInt(2, maxPhotoNo);
			pstmt.setInt(2, vo.getDate_code());
			pstmt.setInt(3, vo.getLoc_no());
			pstmt.setString(4, vo.getName());
			pstmt.setString(5, vo.getDate());
			pstmt.setString(6, vo.getPhoto_taken_email());
			pstmt.setString(7, vo.getIs_img_uploaded());

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
	
	public boolean updateLoc_photo(Connection conn, String couple_id, String imgName, String is_img_uploaded){
		
		PreparedStatement pstmt = null;

		String insertSQL =  "UPDATE loc_photo_tb SET is_img_uploaded = ? where couple_id=? AND name = ?";
		int res = 0;

		DBManager db = DBManager.getInstance();

		try{

			pstmt = conn.prepareStatement(insertSQL);
			pstmt.setString(1, is_img_uploaded);
			pstmt.setString(2, couple_id);
			pstmt.setString(3, imgName);
		
			if(DBManager.DEBUG_MODE)
				System.out.println(pstmt.toString());
			res = pstmt.executeUpdate();


		}catch(Exception e){
			e.printStackTrace();
			return false;
		}finally{
			try{
				db.dbClose(pstmt);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return true;
		
	}

	public ArrayList<Loc_photo_tb_VO> selectAllToArrayList(Connection conn, String couple_id, int date_code){

		ArrayList<Loc_photo_tb_VO> loc_photo_list = new ArrayList<Loc_photo_tb_VO>();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String readSQL = "SELECT date_code, loc_no, name, date, photo_taken_email, is_img_uploaded  "
				+ "FROM loc_photo_tb " +
				"WHERE couple_id = ? AND date_code = ?";

		DBManager db  = DBManager.getInstance();

		//		ArrayList<Date_tb_VO> newDateList = new ArrayList<Date_tb_VO>();

		try {
			pstmt = conn.prepareStatement(readSQL);
			pstmt.setString(1, couple_id);
			pstmt.setInt(2, date_code);

			rs = pstmt.executeQuery();

			while(rs.next()) {

				Loc_photo_tb_VO vo = new Loc_photo_tb_VO(
						date_code, rs.getInt("loc_no"), 
						rs.getString("name"), rs.getString("date"), 
						rs.getString("photo_taken_email"), rs.getString("is_img_uploaded"));

				loc_photo_list.add(vo);
			}

			if(DBManager.DEBUG_MODE){
				System.out.println(pstmt.toString());
				System.out.println(loc_photo_list.toString());
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

		return loc_photo_list;
	}

	public JSONArray selectAllLoc_photos(Connection conn, String couple_id){

		JSONArray jsonArray = new JSONArray();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String readSQL = "SELECT date_code, loc_no, name, date, photo_taken_email, is_img_uploaded  "
				+ "FROM loc_photo_tb " +
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

	public JSONArray selectLoc_photos(Connection conn, JSONArray jsonArray, String couple_id, int date_code){


		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String readSQL = "SELECT date_code, loc_no, name, date, photo_taken_email, is_img_uploaded  "
				+ "FROM loc_photo_tb " +
				"WHERE couple_id = ? AND date_code = ? ";

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

	public JSONArray selectLoc_photos(Connection conn, String couple_id, 
			int photo_no){

		JSONArray jsonArray = new JSONArray();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String readSQL = "SELECT date_code  , loc_no  , name  , date, photo_taken_email, is_img_uploaded  "
				+ "FROM loc_photo_tb " +
				"WHERE couple_id = ? " +
				"AND photo_no > ?";

		DBManager db  = DBManager.getInstance();

		//		ArrayList<Date_tb_VO> newDateList = new ArrayList<Date_tb_VO>();

		try {
			pstmt = conn.prepareStatement(readSQL);
			pstmt.setString(1, couple_id);
			pstmt.setInt(2, photo_no);

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

	public int delete(Connection conn,String couple_id, int date_code) throws SQLException{

		PreparedStatement pstmt = null;

		String insertSQL = "DELETE FROM loc_photo_tb where couple_id = ? and date_code = ?";
		int res = 0;

		pstmt = conn.prepareStatement(insertSQL);
		pstmt.setString(1, couple_id);
		pstmt.setInt(2, date_code);


		if(DBManager.DEBUG_MODE)
			System.out.println(pstmt.toString());
		res = pstmt.executeUpdate();


		
		return res;
	}

}
