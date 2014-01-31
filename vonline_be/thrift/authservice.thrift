namespace * com.vmesteonline.be
include "bedata.thrift"
include "error.thrift"

enum CurrentAttributeType { 
	/* FORUM ATTRIBUTES*/ CATEGORY=1, RUBRIC=2, GROUP=3, 
	/* Shop  attributes*/ SHOP=14, PRODUCT_CATEGORY=15,ORDER=16  
}

service AuthService {

	bool login( 1:string email, 2:string password ) throws (1:error.InvalidOperation exc),
	i64 registerNewUser(1:string firstname, 2:string lastname, 3:string password, 4:string email, 5:string locationId) throws (1:error.InvalidOperation exc)
	void logout() throws (1:error.InvalidOperation exc),
	
	//session as a storage of current user position
	void setCurrentAttribute( 1:map<i32,i64> typeValueMap) throws (1:error.InvalidOperation exc),
	map<i32,i64> getCurrentAttributes( ) throws (1:error.InvalidOperation exc),
}