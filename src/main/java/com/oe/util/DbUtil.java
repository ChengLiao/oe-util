package com.oe.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbUtil {

	public static Connection getConnection(String jdbcDriver,String jdbcUrl,String jdbcUser, String jdbcPwd) {
		Connection con = null;
		try {
			Class.forName(jdbcDriver);
			con = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPwd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}

	/**
	 * 执行sql语句
	 * 
	 * @param conn
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public static boolean executeSql(Connection conn, String sql)
			throws Exception {
		Statement st = null;
		try {
			st = conn.createStatement();
			return st.execute(sql);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (st != null) {
					st.close();
				}
			} catch (SQLException e) {
			}
		}
	}

	/**
	 * 执行带参数的sql
	 * 
	 * @param conn
	 * @param sql
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public static boolean executeSql(Connection conn, String sql, Object[] args)
			throws Exception {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(sql);
			for (int i = 0; i < args.length; i++) {
				st.setObject(i + 1, args[i]);
			}
			return st.execute();
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (st != null) {
					st.close();
				}
			} catch (SQLException e) {
			}
		}
	}

	public static void close(ResultSet rs, Statement st, Connection con) {
		close(rs, st);
		close(con);
	}

	public static void close(ResultSet rs, Statement st) {
		close(rs);
		close(st);
	}

	public static void close(ResultSet rs) {
		if(rs!=null){
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void close(Statement st) {
		if(st!=null){
			try {
				st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void  close(Connection con) {
		if(con!=null){
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
