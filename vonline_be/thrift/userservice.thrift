namespace * com.vmesteonline.be
include "bedata.thrift"
include "error.thrift"


struct FullAddressCatalogue {
	1:set<bedata.Country> countries,
	2:list<bedata.City> cities,
	3:list<bedata.Street> streets,
	4:list<bedata.Building> buildings
}
service UserService {

//получение групп пользователя
	list<bedata.Group> getUserGroups() throws (1:error.InvalidOperation exc),
	list<bedata.Rubric> getUserRubrics() throws (1:error.InvalidOperation exc),
	
	
	bool setUserAddress( 1:bedata.PostalAddress newAddress )throws (1:error.InvalidOperation exc),
	bool addUserAddress( 1:bedata.PostalAddress newAddress )throws (1:error.InvalidOperation exc),
	bedata.PostalAddress getUserHomeAddress( )throws (1:error.InvalidOperation exc),
	set<bedata.PostalAddress> getUserAddresses( )throws (1:error.InvalidOperation exc),
	
//для отображения короткой информации о пользователе в верху страницы самому пользователю. 
	bedata.ShortUserInfo getShortUserInfo() throws (1:error.InvalidOperation exc),
//для отображения короткой информации о пользователе в сообщениях, топиках и т.д. другим пользователям
	bedata.ShortProfile getShortProfile() throws (1:error.InvalidOperation exc),

//для отображения информации о пользователе на странице профайла. 
	bedata.UserInfo getUserInfo() throws (1:error.InvalidOperation exc),

//для отображения контактов пользователя на странице профайла. 
	bedata.UserContacts getUserContacts() throws (1:error.InvalidOperation exc),
	
//для обновления пользовательского аватара в профайле. 
	void updateUserAvatar(1:string url) throws (1:error.InvalidOperation exc),
	
	
	
	list<bedata.Country> getCounties() throws (1:error.InvalidOperation exc),
	list<bedata.City> getCities(1:i64 countryId) throws (1:error.InvalidOperation exc),
	list<bedata.Street> getStreets(1:i64 cityId) throws (1:error.InvalidOperation exc),
	list<bedata.Building> getBuildings(1:i64 streetId) throws (1:error.InvalidOperation exc),
	FullAddressCatalogue getAddressCatalogue() throws (1:error.InvalidOperation exc),
	
	bedata.Country createNewCountry( 1:string name) throws (1:error.InvalidOperation exc),
	bedata.City createNewCity( 1:i64 countryId, 2:string name) throws (1:error.InvalidOperation exc),
	bedata.Street createNewStreet( 1:i64 cityId, 2:string name) throws (1:error.InvalidOperation exc),
	bedata.Building createNewBuilding( 1:i64 streetId, 2:string fullNo, 3:string longitude, 4:string lattitude) throws (1:error.InvalidOperation exc),
	
	
}