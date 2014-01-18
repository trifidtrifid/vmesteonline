include "bedata.thrift"
include "error.thrift"
namespace * com.vmesteonline.be

enum MessageType { BASE=1, DIALOG=2, SHOP=3, NEWS=4 }

struct MessageLink {
	1: MessageType linkType,
	2: i64 linkedId
}

struct UserMessage {
	17: bool read, //флаг прочитанности сообщения пользователем
	18: bool likes,
	19: bool unlikes 
}
struct Message {
	1: i64 id,
	2: i64 parentId, // 'идентификатор родительского сообщения, NULL для корневого со',
	3: MessageType type, // 'тип один из (сообщение, чат)',
	4: i64 topicId,	
	5: i64 authorId, //'автор сообщения или темы',
	6: optional i64 recipientId, // 'адресат задан только для личных сообщений, иначе NULL',
	7: i32 created, // 'дата создания',
	8: i32 edited,
	9: optional i64 approvedBy, // 'идентификатор пользователя промодерировавшего сообщение',
	10: string content, // 'содержание сообщения',
	11: i32 likesNum,
	12: i32 unlikesNum,
	13: map<MessageType,i64> linkedMessages,
	14: map<i64,string> tags //идентификаторы тегов с их значениями
} // 'сообщение';
		

struct UserTopic {
	1: bool archieved,
	2: bool unlikes,
	3: bool likes, 
	4: bool notIntrested, 
	5: i64 lastReadMessageId,
	6: i64 lastWroteMeessgeId
}

struct Topic {
	1: i64 id,
	2: string subject, 
	3: i64 messageId, // 'сообщение',
	4: i32 messageNum, // 'число сообщений в теме',
	5: i32 viewers, // 'число пользоателей, просматривающих сообщение',
	6: i32 usersNum, // 'число пользователей оставивших сообщения в теме',
	7: i32 lastUpdate, //'время создания последнего дочернего сообщения',
	8: i32 likesNum, 
	9: i32 unlikesNum,
	10: optional i64 rubricId, //ссылка на рубрику
	11: optional i64 communityId, //ссылка на сообщество
	//отношение пользователя к топику
	12: UserTopic usertTopic
}

struct Rubric {
	1: i64 id,
	2: string name,
	3: string description, // COMMENT 'Описание рубрики',
	4: i32 topicsNum, // 'число тем в рубрике',
	5: i32 messagesNum// 'число сообщений в рубрике'
}

struct RubricCounter {
	1:	i64 rubric,
	2:	MessageType messageType,
	3:	i32 newTopicNum,
	4:	i32 newMessageNum
}

struct GroupUpdates {
	1:map<i64,RubricCounter> groupCounters 
}

struct TopicListPart {
	1:set<Topic> topics,
	2:i32	totalSize //size of full list
} 

struct MessageListPart {
	1:set<Message> topics,
	2:i32	totalSize //size of full list
} 

service MessageService {

/**
* МЕтод для создаия нового сообщения
*
**/
	Message createMessage( 1: i64 parentId, // 'идентификатор родительского сообщения, NULL для корневого со',
		2: MessageType type, // 'тип один из (сообщение, чат)',
		3: i64 topicId,	
		4: string content, // 'содержание сообщения',
		5: map<MessageType,i64> linkedMessages,
		6: map<i64,string> tags,
		7: optional i64 recipientId // 'адресат задан только для личных сообщений, иначе NULL',
		) throws (1:error.InvalidOperation exc),
/**
* Cоздание нового или обновление старого сообщения
**/	 
	i64 postMessage( 1:Message msg ) throws (1:error.InvalidOperation exc),
	  
	Topic createTopic( 1: string subject, 
		2: i64 messageId, // 'сообщение',
		3: optional i64 rubricId, //ссылка на рубрику
		4: optional i64 communityId) //ссылка на сообщество
	
	i64 postTopic( 1: Topic topic ) throws (1:error.InvalidOperation exc),  
	 
	bool checkUpdates( ) throws (1:error.InvalidOperation exc),
	GroupUpdates getUpdates() throws (1:error.InvalidOperation exc),

	TopicListPart getTopics( 1:i64 groupId , 2:i64 rubricId, 3:MessageType messageType, 4:i32 commmunityId,  5:i32 offset, 6:i32 length) throws (1:error.InvalidOperation exc),
	MessageListPart getMessages( 1:i64 groupId , 2:i64 rubricId, 3:MessageType messageType, 4:i32 commmunityId, 5:i32 offset, 6:i32 length) throws (1:error.InvalidOperation exc),
	
	i64 like(1:i64 messageId, 2:i64 userId ) throws (1:error.InvalidOperation exc),
	i64 dislike(1:i64 messageId, 2:i64 userId ) throws (1:error.InvalidOperation exc),
}