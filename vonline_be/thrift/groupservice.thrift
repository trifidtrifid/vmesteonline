include "bedata.thrift"
include "error.thrift"
namespace * com.vmesteonline.be

enum GroupType { ENTER, HOME, BLOCK, DISTRICT, CITY, CUSTOM } 
struct Group {
	1: i64 id
	2: string shortName,
	3: string description,
	4: GroupType type
}
service GroupService {
	list<Group> getGroupsForRegistration() throws (1:error.InvalidOperation exc),
	list<Group> getUserGroups() throws (1:error.InvalidOperation exc),
}