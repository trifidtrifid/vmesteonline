namespace * com.vmesteonline.be
include "bedata.thrift"

struct Session {
	1: string salt,
	2: string userId,
	4: optional string userAgent,
	5: optional string cookie,
	6: bool accessGranted,
	7: string error,
}
 
exception InvalidOperation {
  1: i32 what,
  2: string why
}

service AuthService {
	Session login( 1:string email, 2:string password ) throws (1:InvalidOperation exc),
	Session getSession(1:string salt) throws (1:InvalidOperation exc),
	i32 registerNewUser(1:string uname, 2:string password, 3:string groupId, 4:string email) throws (1:InvalidOperation exc)
}