package com.vmesteonline.be;

import java.sql.SQLException;
import java.util.Calendar;

import org.apache.thrift.TException;

import java.sql.ResultSet;
import com.vmesteonline.be.data.JDBCConnector;
import com.vmesteonline.be.data.MySQLJDBCConnector;

public class AuthServiceImpl implements AuthService.Iface {

	@Override
	public Session login(String uname, String password)
			throws InvalidOperation, TException {
		JDBCConnector con = null;
		try {
			con = new MySQLJDBCConnector();
			ResultSet rs = con.executeQuery("SELECT id from user where login='"+uname+"' AND password='"+password+"'");
			if( rs.next() ){
				int uid = rs.getInt(1);
				rs = con.executeQuery("SELECT salt,created,cookie,userAgent from session where user="+uid);
				Session sess = new Session();
				User usr = new User();
				usr.id = uid;
				sess.user = usr;
				if( !rs.next() ){ //session exists
					sess.salt = generateSalt();
					con.execute("INSERT INTO session(salt,user,created,userAgent) "
							+ "values('"+sess.salt+"', "+uid+", NOW(),'Unknown')");
					sess.created = 0;
				} else { //create new session
					sess.salt = rs.getString(1);
				}
				return sess;
			} else {
				throw new InvalidOperation(1,"No user found by login and password");
			}
		} catch (JDBCConnector.Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(99,"Database error. "+e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
			throw new InvalidOperation(99,"Database error. "+e.getMessage());
		} finally {
			if(null!=con) con.close();
		}
	}

	@Override
	public Session getSession(String salt) throws InvalidOperation, TException {
		JDBCConnector con = null;
		try {
			con = new MySQLJDBCConnector();
			ResultSet  rs = con.executeQuery("SELECT uid,created,cookie,userAgent from session where salt="+salt);
			Session sess = new Session();
			sess.salt = salt;
			if( rs.next() ){ //session exists
				User usr = new User();
				usr.id = rs.getInt(1);
				sess.user = usr;
				sess.created = 0;
			} else { 
				throw new InvalidOperation(2,"Session not found by salt "+salt);
			}
			return sess;
		
		} catch (JDBCConnector.Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(99,"Database error. "+e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
			throw new InvalidOperation(99,"Database error. "+e.getMessage());
		} finally {
			if(null!=con) con.close();
		}
	}
	
	private static String generateSalt(){
		String str = (""+Math.random()*( Calendar.getInstance().getTimeInMillis() ) * 1000000000.0);
		return (str + "fgthstnthrewqntf").substring(0, 16); 
	} 

}
