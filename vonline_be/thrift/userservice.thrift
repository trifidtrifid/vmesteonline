namespace * com.vmesteonline.be
include "bedata.thrift"
include "error.thrift"


struct Group{
	1: i64 id,
	2: string visibleName,
	3: string name,
	4: string description,
	5: i32 radius,
}

struct Rubric{
	1: i64 id,
	2: string visibleName,
	3: string name,
	4: string description,
}

struct FullAddressCatalogue {
	1:set<bedata.Country> countries,
	2:list<bedata.City> cities,
	3:list<bedata.Street> streets,
	4:list<bedata.Building> buildings
}
service UserService {

	list<Group> getUserGroups() throws (1:error.InvalidOperation exc),
	list<Rubric> getUserRubrics() throws (1:error.InvalidOperation exc),
	// implemented as a static list<string> getLocationCodesForRegistration() throws (1:error.InvalidOperation exc),
	
	list<bedata.Country> getCounties() throws (1:error.InvalidOperation exc),
	list<bedata.City> getCities(1:i64 countryId) throws (1:error.InvalidOperation exc),
	list<bedata.Street> getStreets(1:i64 cityId) throws (1:error.InvalidOperation exc),
	list<bedata.Building> getBuildings(1:i64 streetId) throws (1:error.InvalidOperation exc),
	FullAddressCatalogue getAddressCatalogue() throws (1:error.InvalidOperation exc),
	
	bedata.Country createNewCountry( 1:string name) throws (1:error.InvalidOperation exc),
	bedata.City createNewCity( 1:i64 countryId, 2:string name) throws (1:error.InvalidOperation exc),
	bedata.Street createNewStreet( 1:i64 cityId, 2:string name) throws (1:error.InvalidOperation exc),
	bedata.Building createNewBuilding( 1:i64 streetId, 2:string fullNo, 3:double longitude, 4:double lattitude) throws (1:error.InvalidOperation exc),
	
	//
	bool setUserAddress( 1:bedata.PostalAddress newAddress )throws (1:error.InvalidOperation exc),
	bool addUserAddress( 1:bedata.PostalAddress newAddress )throws (1:error.InvalidOperation exc),
	bedata.PostalAddress getUserHomeAddress( )throws (1:error.InvalidOperation exc),
	set<bedata.PostalAddress> getUserAddresses( )throws (1:error.InvalidOperation exc),
	
}