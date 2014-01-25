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

service UserService {

	list<Group> getUserGroups() throws (1:error.InvalidOperation exc),
	list<Rubric> getUserRubrics() throws (1:error.InvalidOperation exc),
	//list<string> getLocationCodesForRegistration() throws (1:error.InvalidOperation exc),
}