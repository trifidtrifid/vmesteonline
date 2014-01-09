include "bedata.thrift"
include "user.thrift"
namespace * com.vmesteonline.be

service GroupService {

	list<bedata.Group> getGroupsForRegistration() throws (1:user.InvalidOperation exc),
	list<bedata.Group> getUserGroups(1:i32 userId) throws (1:user.InvalidOperation exc),

}