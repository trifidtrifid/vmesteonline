include "user.thrift"
namespace * com.vmesteonline.be

struct Group {
	1: i32 id,
	2: i32 locationId,
	3: i32 creatorId, //идентификатор создателя шруппы для пользовательских групп
	4: optional string comment, //'расширенное описание группы или коммент'
	5: string shortName, // размер не более 16 символов
    6: optional string name //до 200 символов
}

enum MessageType { BASE=1, DIALOG=2, SHOP=3, NEWS=4 }

struct Message {
	1: i32 id,
	2: i32 parentId, // 'идентификатор родительского сообщения, NULL для корневого со',
	3: MessageType type, // 'тип один из (сообщение, чат)',
	4: i32 topicId,	
	5: i32 authorId, //'автор сообщения или темы',
	6: optional i32 recipientId, // 'адресат задан только для личных сообщений, иначе NULL',
	7: i32 created, // 'дата создания',
	8: i32 edited,
	9: optional i32 approved, // 'идентификатор пользователя промодерировавшего сообщение',
	10: string content, // 'содержание сообщения',
	11: i32 likes,
	12: i32 unlikes,
	13: i32 groupId,
	14: optional i32 idForum,
	15: optional i32 idShop	
	16: optional i32 idDialog,
	17: optional i32 idNews,
 } // 'сообщение';
		
struct Topic {
	1: i32 id,
	2: Message message, // 'сообщение',
	3: i32 messageNum, // 'число сообщений в теме',
	4: i32 viewers, // 'число пользоателей, просматривающих сообщение',
	5: i32 usersNum, // 'число пользователей оставивших сообщения в теме',
	6: i32 lastUpdate, //'время создания последнего дочернего сообщения',
	7: i32 likes, 
	8: i32 unlikes,
	9: optional i32 rubric, //ссылка на рубрику
}

struct UserMessage {
	1: i32 userId,
	2: i32 messageId,
	3: bool read,
	4: bool unintrested	
	5: bool like	
	6: bool unlike
} //'отношение пользователя к сообщению';

struct UserRubric {
	1: i32 userId,
	2: i32 rubricId,
	3: optional i32 grouId,
	4: i32 subscribed,
	5: i32 topicsNum, //'число тем в рубрике от пользователя',
	6: i32 messagesNum// 'число сообщений пользователя в рубрике',
} // 'Рубрики интересные пользователю';

struct Rubric {
	1: i32 id,
	2: string name,
	3: string description, // COMMENT 'Описание рубрики',
	4: i32 topicsNum, // 'число тем в рубрике',
	5: i32 messagesNum// 'число сообщений в рубрике'
}

enum FriendshipType { UNCONFIRMED=0, CONFIRMED=1, REQUESTED=2, WAIT_CONFIRMATION=3, HIDE=4 }

struct Friendship {
	1: i32 userId,
	2: i32 friendId, //'идентификатор друга',
	3: FriendshipType state // 'состояние - запрос, подтверждено, отклонено ',
} // 'список друзей';


struct UserTopic {
	1: i32 userId,
	2: i32 topicId,
	3: bool archived,
	4: i32 messagesNum, // 'число сообщкний пользоваткля в топике',
	5: i32 lastActivity, //TIMESTAMP,
	6: byte dolike,
	7: i32 readMessageNum,
} // 'активность пользователя в теме';


