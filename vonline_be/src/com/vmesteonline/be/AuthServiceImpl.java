package com.vmesteonline.be;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;

import org.apache.thrift.TException;

import java.sql.ResultSet;

import com.vmesteonline.be.data.JDBCConnector;

public class AuthServiceImpl extends ServiceImpl implements AuthService.Iface {
	
	@Override
	public Session login(final String uname, final String password)
			throws InvalidOperation, TException {
		try {
			return con.getResult( new JDBCConnector.ResultCreator<Session>() {
				
				public Session createResult(Connection conn) throws java.lang.Exception {
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
						rs.close();
						loadUserInfo(con, usr);
						return sess;
					} else {
						throw new InvalidOperation(1,"No user found by login and password");
					}
				}
			}, con );
			
		} catch (java.lang.Exception e) {
			if( e instanceof InvalidOperation ) throw (InvalidOperation)e;
			throw new TException( e.getMessage() );
		}
	}

	@Override
	public Session getSession(final String salt) throws InvalidOperation, TException {
		try {
			return con.getResult( new JDBCConnector.ResultCreator<Session>() {
				public Session createResult(Connection conn) throws java.lang.Exception {
					ResultSet rs = con.executeQuery("SELECT uid,created,cookie,userAgent from session where salt="+salt);
					if( rs.next() ){ //session exists
						User usr = new User(rs.getInt(1),0);
						rs.close();
						loadUserInfo(con, usr);
						return new Session(salt, usr, 0);
					}
					rs.close();
					throw new InvalidOperation(2,"Session not found by salt "+salt);
				}
			}, con );
			
		} catch (java.lang.Exception e) {
			if( e instanceof InvalidOperation ) throw (InvalidOperation)e;
			throw new TException( e.getMessage() );
		}
	}
	
	private boolean loadUserInfo( JDBCConnector con, User usr) throws SQLException {
		ResultSet  rs = con.executeQuery("SELECT location,firstName,secondName,DOB,sex,intrests FROM user WHERE id="+usr.id);
		if (rs.next()){
			usr.locationId = rs.getInt(1);
			usr.userInfo = new UserInfo( rs.getString(2), rs.getString(3),rs.getInt(4));
			rs.close();
			return true;
		}
		rs.close();
		return false;
	}
	
	private static String generateSalt(){
		String str = (""+Math.random()*( Calendar.getInstance().getTimeInMillis() ) * 1000000000.0);
		return (str + "fgthstnthrewqntf").substring(0, 16); 
	} 

}
