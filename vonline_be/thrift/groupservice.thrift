include "bedata.thrift"
include "user.thrift"
namespace * com.vmesteonline.be

service GroupService {

	list<bedata.Group> getUserGroups() throws (1:user.InvalidOperation exc),

}