include "user.thrift"
namespace * com.vmesteonline.be

struct Group {
	1: i32 id,
	2: i32 location,
	3: string comment //'расширенное описание группы или коммент'
}

		
struct Topic {
	1: i32 id,
	2: i32 message, // 'сообщение',
	3: i32 messageNum, // 'число сообщений в теме',
	4: i32 viewers, // 'число пользоателей, просматривающих сообщение',
	5: i32 usersNum, // 'число пользователей оставивших сообщения в теме',
	6: i32 lastUpdate, //'время создания последнего дочернего сообщения',
	7: i32 likes, 
	8: i32 unlikes,
	9: optional i32 rubric, //ссылка на рубрику
}

enum MessageType { base=1, dialogue=2 }

struct Message {
	1: i32 id,
	2: i32 parent, // 'идентификатор родительского сообщения, NULL для корневого со',
	3: MessageType type, // 'тип один из (сообщение, чат)',
	4: i32 topic,	
	5: i32 author, //'автор сообщения или темы',
	6: optional i32 recipient, // 'адресат задан только для личных сообщений, иначе NULL',
	7: i32 created, // 'дата создания',
	8: i32 edited,
	9: optional i32 approved, // 'идентификатор пользователя промодерировавшего сообщение',
	10: string content, // 'содержание сообщения',
	11: i32 likes,
	12: i32 unlikes,
 } // 'сообщение';

struct UserMessage {
	1: i32 user,
	2: i32 message,
	3: bool read,
	4: bool unintrested	
	5: bool like	
	6: bool unlike
} //'отношение пользователя к сообщению';

struct UserRubric {
	1: i32 user,
	2: i32 rubric,
	3: optional i32 grp,
	4: i32 subscribed,
	5: i32 topics, //'число тем в рубрике от пользователя',
	6: i32 messages// 'число сообщений пользователя в рубрике',
} // 'Рубрики интересные пользователю';

struct Rubric {
	1: i32 id,
	2: string name,
	3: string description, // COMMENT 'Описание рубрики',
	4: i32 topics, // 'число тем в рубрике',
	5: i32 messages// 'число сообщений в рубрике'
}

enum FriendshipType { UNCONFIRMED=0, CONFIRMED=1, REQUESTED=2, WAIT_CONFIRMATION=3, HIDE=4 }

struct Friendship {
	1: i32 user,
	2: i32 friend, //'идентификатор друга',
	3: FriendshipType state // 'состояние - запрос, подтверждено, отклонено ',
} // 'список друзей';

struct MessageGroup {
	1: i32 message,
	2: i32 group,
	3: bool cansee, // 'видно в группе',
	4: bool canresponse, // 'члены группы могут отвечать',
} // 'отношение сообщений к группам';

struct UserTopic {
	1: i32 user,
	2: i32 topic,
	3: bool archived,
	4: i32 messages, // 'число сообщкний пользоваткля в топике',
	5: i32 lastActivity, //TIMESTAMP,
	6: byte dolike
} // 'активность пользователя в теме';


