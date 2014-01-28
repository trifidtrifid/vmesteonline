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
	
	//
	bool setUserAddress( 1:bedata.PostalAddress newAddress );
}