namespace * com.vmesteonline.be

struct Country {
	1:i64 id,
	2:string name
}

struct City {
	1:i64 id,
	2:i64 countryId,
	3:string name
}

struct Street {
	1:i64 id,
	2:i64 cityId,
	3:string name
}

struct Building {
	1:i64 id,
	2:i64 streetId,
	3:string fullNo
}

struct PostalAddress {
	1:Country country,
	2:City city,
	3:Street street,
	4:Building building, 
	5:byte staircase,
	6:byte floor,
	7:i32 flatNo,
	8:string comment
}

enum FriendshipType { UNCONFIRMED=0, CONFIRMED=1, REQUESTED=2, WAIT_CONFIRMATION=3, HIDE=4 }

struct Friendship {
	1: i64 userId,
	2: i64 friendId, //'идентификатор друга',
	3: FriendshipType state // 'состояние - запрос, подтверждено, отклонено ',
} // 'список друзей';

struct ShortUserInfo{
	1: i64 id,
	2: string firstName,
	3: string lastName,
	4: i32 rating
	5: string avatar,
}

struct ShortProfile{
	1: i64 id,
	2: string firstName,
	3: string lastName,
	4: i32 rating
	5: string avatar,
	6: string address,
	7: string balance,
}

enum RelationsType { MARRIED=0, NOTMARRIED=1, UNKNOWN=3 }

struct UserInfo{
	1: i64 id,
	2: string firstName,
	3: string lastName,
	4: i32 rating
	5: string avatar,
	6: string birthday,
	7: RelationsType relations,
}

enum UserStatus { UNCONFIRMED=0, CONFIRMED=1, REQUESTED=2, WAIT_CONFIRMATION=3, HIDE=4 }

struct UserContacts{
	1: UserStatus addressStatus,
	2: PostalAddress homeAddress,
	3: string mobilePhone,
	4: string email, 
}


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

struct MatrixAsList {
	1:i32 rowCount,
	2:list<string> elems
}

struct IdName {
	1:i64 id,
	2:string name,
}

struct IdNameChilds {
	1:i64 id,
	2:string name,
	3:list<IdName> childs,
}


