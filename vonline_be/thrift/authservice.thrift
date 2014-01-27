namespace * com.vmesteonline.be
include "bedata.thrift"
include "error.thrift"

service AuthService {

	bool login( 1:string email, 2:string password ) throws (1:error.InvalidOperation exc),
	i64 registerNewUser(1:string firstname, 2:string lastname, 3:string password, 4:string email, 5:string locationId) throws (1:error.InvalidOperation exc)
	void logout() throws (1:error.InvalidOperation exc),

}