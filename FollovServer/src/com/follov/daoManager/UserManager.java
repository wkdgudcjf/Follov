package com.follov.daoManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import org.json.*;

import com.follov.db.DBManager;
import com.follov.user.dto.*;

public class UserManager {
	private static UserManager um = new UserManager();
	
	private UserManager(){}
	public static UserManager getInstance(){
		return um;
	}
	
	/**
	 * 중복 체크하는 메소드, 중복된 아이디 값이 있으면 true를 리턴한다.
	 * @param id
	 * @return
	 */
	public boolean dupIdCheck(Connection conn, String id){
		boolean isExist = false;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String readSQL = "SELECT * FROM user_tb WHERE email=?";
		
		try {
			pstmt = conn.prepareStatement(readSQL);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				isExist = true;
			}
			
			if(DBManager.DEBUG_MODE)
				System.out.println(String.format("SELECT * FROM user_tb WHERE "
						+ "email=%s",id));
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				DBManager.getInstance().dbClose(pstmt, rs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return isExist;
		
		
	}
	
	public void coupleInfomaion(Connection conn,String phone,ArrayList<String> str){

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String readSQL = "SELECT * FROM user_tb WHERE phone=?";
		try 
		{
			pstmt = conn.prepareStatement(readSQL);
			pstmt.setString(1, phone);
			rs = pstmt.executeQuery();
			rs.next();
			str.add(rs.getString("email"));
			str.add(rs.getString("regid"));
			if(DBManager.DEBUG_MODE)
			System.out.println(String.format("SELECT * FROM user_tb WHERE "
					+ "phone=%s",phone));
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		} 
		finally
		{
			try
			{
				DBManager.getInstance().dbClose(pstmt, rs);
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	public boolean checkPassword(Connection conn, String email, String password)
	{
		boolean isExist = false;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String readSQL = "SELECT * FROM user_tb WHERE email=? and pwd=?";
		
		try {
			pstmt = conn.prepareStatement(readSQL);
			pstmt.setString(1, email);
			pstmt.setString(2, password);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				isExist = true;
			}
			
			if(DBManager.DEBUG_MODE)
				System.out.println(String.format("SELECT * FROM user_tb WHERE "
						+ "email=%s pwd=%s",email,password));
			

		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				DBManager.getInstance().dbClose(pstmt, rs);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		return isExist;
	}
	
	public int insertUser(Connection conn, String email, String password,
			String phone, String regid) {
		PreparedStatement pstmt = null;
		String insertSQL = "INSERT INTO user_tb(email,pwd,regid,phone) values(?,?,?,?)";
		int res = 0;

		DBManager db = DBManager.getInstance();
		
		try{
			pstmt = conn.prepareStatement(insertSQL);
			pstmt.setString(1, email);
			pstmt.setString(2, password);
			pstmt.setString(3, regid);
			pstmt.setString(4, phone);
			res = pstmt.executeUpdate();
			
			if(DBManager.DEBUG_MODE)
				System.out.println(String.format("NSERT INTO user_tb(email,pwd,regid,phone)" +
						" values(%s,%s,%s,%s)",email,password,regid,phone));
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
	public boolean couplePhoneCheck(Connection conn, String phone) {
		boolean isExist = false;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String readSQL = "SELECT * FROM user_tb WHERE phone=?";
		
		try {
			pstmt = conn.prepareStatement(readSQL);
			pstmt.setString(1, phone);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				isExist = true;
			}
			
			if(DBManager.DEBUG_MODE)
				System.out.println(String.format("SELECT * FROM user_tb WHERE "
						+ "phone=%s",phone));
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				DBManager.getInstance().dbClose(pstmt, rs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return isExist;
	}
	public void userCoupleIdUpdate(Connection conn, String string,
			String string2, String couple_Id) {
		PreparedStatement pstmt = null;
		String updateSQL = "UPDATE user_tb SET couple_id=? WHERE email=?";
		int res = 0;
		DBManager db  = DBManager.getInstance();
		
		try {
			pstmt = conn.prepareStatement(updateSQL);
			pstmt.setString(1, couple_Id);
			pstmt.setString(2, string);
			
			res = pstmt.executeUpdate();
			System.out.println("res : "+res+"건이 수정되었습니다");
			
			pstmt = conn.prepareStatement(updateSQL);
			pstmt.setString(1, couple_Id);
			pstmt.setString(2, string2);
			
			res = pstmt.executeUpdate();
			System.out.println("res : "+res+"건이 수정되었습니다");
			
			if(DBManager.DEBUG_MODE)
			{
				System.out.println(String.format("UPDATE user_tb SET couple_id=%s WHERE child_id=%s", couple_Id, string));
				System.out.println(String.format("UPDATE user_tb SET couple_id=%s WHERE child_id=%s", couple_Id, string2));
			}
		} catch (Exception e) {
			System.out.println("여기서 터졌구나?????");
			e.printStackTrace();
		} finally {
			try {
				db.dbClose(pstmt);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public void couplematching(Connection conn, String myphone,
			String yourphone, String couple_Id) {
		PreparedStatement pstmt = null;
		String insertSQL = "INSERT INTO couple_tb(couple_id,male_email,female_email) values(?,?,?)";
		int res = 0;

		DBManager db = DBManager.getInstance();
		
		try{
			pstmt = conn.prepareStatement(insertSQL);
			pstmt.setString(1, couple_Id);
			pstmt.setString(2, myphone);
			pstmt.setString(3, yourphone);
			res = pstmt.executeUpdate();
			
			if(DBManager.DEBUG_MODE)
				System.out.println(String.format("INSERT INTO couple_tb(couple_id,male_email,female_email)" +
						"values(%s,%s,%s)",couple_Id,myphone,yourphone));
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				db.dbClose(pstmt);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	public void updateProfile(Connection conn, String email, int year,
			int month, int day, boolean zender) {
		PreparedStatement pstmt = null;
		String updateSQL = "UPDATE user_tb SET birth_year=?,birth_month=?,birth_day=?,gender=?  WHERE email=?";
		int res = 0;
		DBManager db  = DBManager.getInstance();
		
		try {
			pstmt = conn.prepareStatement(updateSQL);
			pstmt.setInt(1, year);
			pstmt.setInt(2, month);
			pstmt.setInt(3, day);
			String flag="m";
			if(zender)
			{
				flag="f";
			}
			pstmt.setString(4, flag);
			pstmt.setString(5, email);
						
			res = pstmt.executeUpdate();
			System.out.println("res : "+res+"건이 수정되었습니다");
			
			if(DBManager.DEBUG_MODE)
			{
				System.out.println(String.format("UPDATE user_tb SET birth_year=%s,birth_month=%s,birth_day=%s,zender=%s  WHERE email=%s", year, month,day,zender,email));
			}
		} catch (Exception e) {
			System.out.println("여기서 터졌구나?????");
			e.printStackTrace();
		} finally {
			try {
				db.dbClose(pstmt);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public String selectCoupleEmail(Connection conn, String email)
	{
		String str=null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String readSQL = "SELECT * FROM couple_tb WHERE male_email=?";
		
		try {
			pstmt = conn.prepareStatement(readSQL);
			pstmt.setString(1, email);
			rs = pstmt.executeQuery();
			
			if(rs.next())
			{
				if(DBManager.DEBUG_MODE)
					System.out.println(String.format("SELECT * FROM couple_tb WHERE male_email=%s",email));
				str =  rs.getString("female_email");
				return str;
			}
			readSQL = "SELECT * FROM couple_tb WHERE female_email=?";
			pstmt = conn.prepareStatement(readSQL);
			pstmt.setString(1, email);
			rs = pstmt.executeQuery();
			if(rs.next())
			{
				if(DBManager.DEBUG_MODE)
					System.out.println(String.format("SELECT * FROM couple_tb WHERE female_email=%s",email));
				str = rs.getString("male_email");
				return str;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				DBManager.getInstance().dbClose(pstmt, rs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return str;
	}
	public String selectGcm(Connection conn, String coupleEmail) {
		String str=null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String readSQL = "SELECT * FROM user_tb WHERE email=?";
		
		try {
			pstmt = conn.prepareStatement(readSQL);
			pstmt.setString(1, coupleEmail);
			rs = pstmt.executeQuery();
			
			if(rs.next())
			{
				str =  rs.getString("regid");
			}
			if(DBManager.DEBUG_MODE)
				System.out.println(String.format("SELECT * FROM user_tb WHERE email=%s",coupleEmail));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				DBManager.getInstance().dbClose(pstmt, rs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return str;
	}
	
	
	
	public boolean checkPassword(Connection conn, JSONObject json){
		String email = "";
		String password = "";
		try
		{
			email = json.get("email").toString();
			password = json.get("pw").toString();
		} catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if(UserManager.getInstance().checkPassword(conn, email, password)){
			return true;
		}
		if(DBManager.DEBUG_MODE)
			System.out.println("insertDate, 비빌번호가 일치하지 않습니다.");
		return false;
	}
	
	public User searchUser(Connection conn, String email, String pwd) {
		User returnUserData = new User();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String readSQL = "SELECT * FROM user_tb WHERE email=? and pwd=?";
		
		try {
			pstmt = conn.prepareStatement(readSQL);
			pstmt.setString(1, email);
			pstmt.setString(2, pwd);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				returnUserData.setEmail(email);
				returnUserData.setPwd(pwd);
				returnUserData.setBirthYear(rs.getInt("birth_year"));
				returnUserData.setBirthMonth(rs.getInt("birth_month"));
				returnUserData.setBirthDay(rs.getInt("birth_day"));
				returnUserData.setGender(rs.getString("gender"));
				returnUserData.setCoupleId(rs.getString("couple_id"));
				returnUserData.setGcmid(rs.getString("regid"));
				returnUserData.setProfilePhoto(rs.getString("profile_photo"));
			}
			
			if(DBManager.DEBUG_MODE)
				System.out.println(String.format("SELECT * FROM user_tb WHERE "
						+ "email=%s", email));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				DBManager.getInstance().dbClose(pstmt, rs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return returnUserData;
	}
	public void updateLogin(Connection conn, String email, String regid,
			String phone)
	{
		PreparedStatement pstmt = null;
		String updateSQL = "UPDATE user_tb SET regid=?,phone=? WHERE email=?";
		int res = 0;
		DBManager db  = DBManager.getInstance();
		
		try {
			pstmt = conn.prepareStatement(updateSQL);
			pstmt.setString(1, regid);
			pstmt.setString(2, phone);
			pstmt.setString(3, email);
						
			res = pstmt.executeUpdate();
			System.out.println("res : "+res+"건이 수정되었습니다");
			
			if(DBManager.DEBUG_MODE)
			{
				System.out.println(String.format("UPDATE user_tb SET regid=%s,phone=%s WHERE email=%s", regid, phone,email));
			}
		} catch (Exception e) {
			System.out.println("여기서 터졌구나?????");
			e.printStackTrace();
		} finally {
			try {
				db.dbClose(pstmt);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public User searchColpleUser(Connection conn, String email)
	{
		User returnUserData = new User();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String readSQL = "SELECT * FROM user_tb WHERE email=?";
		
		try {
			pstmt = conn.prepareStatement(readSQL);
			pstmt.setString(1, email);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				returnUserData.setEmail(email);
				returnUserData.setBirthYear(rs.getInt("birth_year"));
				returnUserData.setBirthMonth(rs.getInt("birth_month"));
				returnUserData.setBirthDay(rs.getInt("birth_day"));
				returnUserData.setGender(rs.getString("gender"));
				returnUserData.setCoupleId(rs.getString("couple_id"));
				returnUserData.setGcmid(rs.getString("regid"));
				returnUserData.setPhone(rs.getString("phone"));
				returnUserData.setProfilePhoto(rs.getString("profile_photo"));
			}
			
			if(DBManager.DEBUG_MODE)
				System.out.println(String.format("SELECT * FROM user_tb WHERE "
						+ "email=%s", email));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				DBManager.getInstance().dbClose(pstmt, rs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return returnUserData;
	}
	public String searchColpleid(Connection conn, String email)
	{
		String str=null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String readSQL = "SELECT * FROM couple_tb WHERE male_email=?";
		
		try {
			pstmt = conn.prepareStatement(readSQL);
			pstmt.setString(1, email);
			rs = pstmt.executeQuery();
			
			if(rs.next())
			{
				if(DBManager.DEBUG_MODE)
					System.out.println(String.format("SELECT * FROM couple_tb WHERE male_email=%s",email));
				str =  rs.getString("couple_id");
				return str;
			}
			readSQL = "SELECT * FROM couple_tb WHERE female_email=?";
			pstmt = conn.prepareStatement(readSQL);
			pstmt.setString(1, email);
			rs = pstmt.executeQuery();
			if(rs.next())
			{
				if(DBManager.DEBUG_MODE)
					System.out.println(String.format("SELECT * FROM couple_tb WHERE female_email=%s",email));
				str = rs.getString("couple_id");
				return str;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				DBManager.getInstance().dbClose(pstmt, rs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return str;
	}
	
}

