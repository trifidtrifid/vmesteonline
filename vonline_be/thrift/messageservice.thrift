include "bedata.thrift"
include "error.thrift"
namespace * com.vmesteonline.be

enum MessageType { BASE=1, DIALOG=2, SHOP=3, NEWS=4 }

struct MessageLink {
	1: MessageType linkType,
	2: i64 linkedId
}

struct UserMessage {
	1: bool isread, //флаг прочитанности сообщения пользователем
	2: bool likes,
	3: bool unlikes 
}
struct Message {
	1: i64 id,
	2: i64 parentId, // 'идентификатор родительского сообщения, NULL для корневого со',
	3: MessageType type, // 'тип один из (сообщение, чат)',
	4: i64 topicId,
	5: i64 groupId,	
	6: i64 authorId, //'автор сообщения или темы',
	7: optional i64 recipientId, // 'адресат задан только для личных сообщений, иначе NULL',
	8: i32 created, // 'дата создания',
	9: i32 edited,
	10: optional i64 approvedBy, // 'идентификатор пользователя промодерировавшего сообщение',
	11: string content, // 'содержание сообщения',
	12: i32 likesNum,
	13: i32 unlikesNum,
	14: map<MessageType,i64> linkedMessages,
	15: map<i64,string> tags, //идентификаторы тегов с их значениями
	16: UserMessage userMessage, //how user treats the message
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
	3: Message message, // 'сообщение',
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
	2: string visibleName,
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
	1:list<Topic> topics,
	2:i32	totalSize //size of full list
} 

struct MessageListPart {
	1:list<Message> messages,
	2:i32	totalSize //size of full list
} 

service MessageService {

/**
* МЕтод для создаия нового сообщения
*
**/
	Message createMessage( 1:i64 topicId,  
		2: i64 parentId, // 'идентификатор родительского сообщения, NULL для корневого со',
		3: i64 groupId, //идентификатор пользовтельской группы, в которой он размещает сообщение
		4: MessageType type, // 'тип один из (сообщение, чат)',
		5: string content, // 'содержание сообщения',
		6: map<MessageType,i64> linkedMessages,
		7: map<i64,string> tags,
		8: i64 recipientId // 'адресат задан только для личных сообщений, иначе NULL',
		) throws (1:error.InvalidOperation exc),
/**
* Cоздание нового или обновление старого сообщения
**/	 
	i64 postMessage( 1:Message msg ) throws (1:error.InvalidOperation exc),
	  
	Topic createTopic(
		1: i64 groupId, //идентификатор пользовтельской группы, в которой он размещает топик 
		2: string subject, 
		3: MessageType type, // 'тип один из (сообщение, чат)',
		4: string content, // 'содержание сообщения',
		5: map<MessageType,i64> linkedMessages,
		6: map<i64,string> tags
		7: i64 rubricId, //ссылка на рубрику
		8: i64 communityId) //ссылка на сообщество
	
	i64 postTopic( 1: Topic topic ) throws (1:error.InvalidOperation exc),  
	 
	 /**
	 * checkUpdates запрашивает наличие обновлений с момента предыдущего запроса, который возвращает сервер в ответе
	 * если обновлений нет - в ответ приходит новое значение таймстампа формирования ответа на сервере. 
	 * При наличии обновлений возвращается 0 
	 **/
	i32 checkUpdates( 1:i32 lastResposeTimestamp ) throws (1:error.InvalidOperation exc),
	GroupUpdates getUpdates() throws (1:error.InvalidOperation exc),

	TopicListPart getTopics( 1:i64 groupId , 2:i64 rubricId, 3:i32 commmunityId, 4:i64 lastLoadedTopicId, 5:i32 length) throws (1:error.InvalidOperation exc),
	/**
	* Загрузка части преставления дерева сообщений в виде дерева. parentID указывает на сообщение топика или на сообщение первого уровня
	**/
	MessageListPart getMessages( 1:i64 topicId , 2:i64 groupId 3:MessageType messageType, 4:i64 parentId, 5:bool archived, 6:i32 offset, 7:i32 length) throws (1:error.InvalidOperation exc),
	
	i64 like(1:i64 messageId ) throws (1:error.InvalidOperation exc),
	i64 dislike(1:i64 messageId ) throws (1:error.InvalidOperation exc),
	i64 markReadMessage(1:i64 messageId ) throws (1:error.InvalidOperation exc),
	i64 markReadTopic(1:i64 topicId ) throws (1:error.InvalidOperation exc),
	i64 moveTopicToArchive(1:i64 topicId ) throws (1:error.InvalidOperation exc),
	i64 restoreTopicFromArchive(1:i64 topicId) throws (1:error.InvalidOperation exc),
	i64 markTopicUnintrested(1:i64 topicId, 2:bool interested) throws (1:error.InvalidOperation exc),
	i64 makeMessageLinked(1:i64 message1Id, 2:i64 message2Id ) throws (1:error.InvalidOperation exc),
	i64 likeTopic(1:i64 topicId ) throws (1:error.InvalidOperation exc),
	i64 dislikeTopic(1:i64 topicId ) throws (1:error.InvalidOperation exc),
	
	
}