package com.follov.db;

import java.sql.*;



public class DBManager {
	
	public static boolean DEBUG_MODE = true;
	private static DBManager sInstance;

	private DBManager() {}
	
	
	public static DBManager getInstance(){
		if (sInstance == null) {
			sInstance = new DBManager();
		}

		return sInstance;
	}
	
	/**
	 * MYsql DB접속
	 * @return Connection
	 */
	public Connection dbConn(){
		Connection conn = null;
		String url = "jdbc:mysql://127.0.0.1:3306/follov";
		
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url,"root","ekaqo1");
		}catch(Exception e){
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 * Mysql DB 접속해제
	 * @param conn
	 * @param pstmt
	 * @param rs
	 */
	public void dbClose(Connection conn, PreparedStatement pstmt)
	{
		try{
			if(pstmt != null)pstmt.close();
			if(conn != null)conn.close();
		}catch(Throwable e){
			System.out.println(e.toString());
		}finally{
			try{
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			}catch(Exception e){
				System.out.println(e.toString());
			}
		}
	}
	
	/**
	 * Mysql DB 접속해제
	 * @param conn
	 * @param pstmt
	 * @param rs
	 */
	public void dbClose(Connection conn)
	{
		try{
			if(conn != null)conn.close();
		}catch(Throwable e){
			System.out.println(e.toString());
		}finally{
			try{
				if(conn != null) conn.close();
			}catch(Exception e){
				System.out.println(e.toString());
			}
		}
	}
	
	/**
	 * Mysql DB select후 접속해제
	 * @param conn
	 * @param pstmt
	 * @param rs
	 */
	public void dbClose(Connection conn, PreparedStatement pstmt, ResultSet rs)
	{
		try{
			if(rs != null) 	rs.close();
			if(pstmt != null)pstmt.close();
			if(conn != null)conn.close();
		}catch(Throwable e){
			System.out.println(e.toString());
		}finally{
			try{
				if(rs != null) 	rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			}catch(Exception e){
				System.out.println(e.toString());
			}
		}
	}
	public void dbClose(PreparedStatement pstmt, ResultSet rs)
	{
		try{
			if(rs != null) 	rs.close();
			if(pstmt != null)pstmt.close();
		}catch(Throwable e){
			System.out.println(e.toString());
		}finally{
			try{
				if(rs != null) 	rs.close();
				if(pstmt != null) pstmt.close();
			
			}catch(Exception e){
				System.out.println(e.toString());
			}
		}
	}


	public void dbClose(PreparedStatement pstmt)
	{
		try{
			if(pstmt != null)pstmt.close();
		}catch(Throwable e){
			System.out.println(e.toString());
		}finally{
			try{
				if(pstmt != null) pstmt.close();
			
			}catch(Exception e){
				System.out.println(e.toString());
			}
		}
	}
	

	
	
}
