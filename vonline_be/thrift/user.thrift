namespace * com.vmesteonline.be

struct UserInfo {
	1: string name,
	2: string secondName,
	3: i32 dob,
	4: optional bool sex,
	5: optional string intrests
}

struct User {
	1: i32 id,
	2: i32 locationId,
	3: optional UserInfo userInfo
}
struct Session {
	1: string salt,
	2: User user,
	3: i32 created,
	4: optional string userAgent,
	5: optional string cookie
}

exception InvalidOperation {
  1: i32 what,
  2: string why
}

service AuthService {
	Session login( 1:string uname, 2:string password ) throws (1:InvalidOperation exc),
	Session getSession(1:string salt) throws (1:InvalidOperation exc),
}