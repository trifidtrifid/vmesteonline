include "bedata.thrift"
include "error.thrift"
namespace * com.vmesteonline.be.messageservice

enum MessageType { BASE=1, DIALOG=2, SHOP=3, NEWS=4, WALL=5 }

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
	6: i64 authorId, //'автор сообщения или темы' TODO удалить этот мембер и использовать userInfo
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
	17: i32 offset, //смещение сообщения для формирования древовидной структуры
	18: bedata.ShortUserInfo userInfo,
	19: list<string> images, 
	20: list<string> documents, 
	
} // 'сообщение';
		

struct UserTopic {
	1: bool archieved,
	2: bool unlikes,
	3: bool likes, 
	4: bool notIntrested, 
	5: i64 lastReadMessageId,
	6: i64 lastWroteMeessgeId,
	7: bool isread
}

struct Poll {
	1:	i64 pollId,
	2:	list<string> names,
	3:	list<i32> values,
	4: 	string subject,
	5:	bool alreadyPoll
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
	12: UserTopic usertTopic,
	13: bedata.ShortUserInfo userInfo,
	14: Poll poll, 	
}


struct TopicListPart {
	1:list<Topic> topics,
	2:i32	totalSize //size of full list
} 

struct MessageListPart {
	1:list<Message> messages,
	2:i32	totalSize //size of full list
} 

struct UserOpinion {
	1: i32 likes,
	2: i32 dislikes,
}

struct WallItem {
	1:list<Message> messages,
	2:Topic topic,
}

service MessageService {

list<WallItem> getWallItems(1:i64 groupId)	 throws (1:error.InvalidOperation exc)

/**
* Cоздание нового или обновление старого сообщения
**/	 
	Message postMessage( 1:Message msg ) throws (1:error.InvalidOperation exc),

	Poll doPoll( 1:i64 pollId, 2:i32 item) throws (1:error.InvalidOperation exc),
	Topic postTopic( 1: Topic topic ) throws (1:error.InvalidOperation exc),  
	 
	 /**
	 * checkUpdates запрашивает наличие обновлений с момента предыдущего запроса, который возвращает сервер в ответе
	 * если обновлений нет - в ответ приходит новое значение таймстампа формирования ответа на сервере. 
	 * При наличии обновлений возвращается 0 
	 **/
	i32 checkUpdates( 1:i32 lastResposeTimestamp ) throws (1:error.InvalidOperation exc),

	TopicListPart getTopics( 1:i64 groupId , 2:i64 rubricId, 3:i32 commmunityId, 4:i64 lastLoadedTopicId, 5:i32 length) throws (1:error.InvalidOperation exc),
	/**
	* Загрузка части преставления дерева сообщений в виде дерева. parentID указывает на сообщение топика или на сообщение первого уровня
	**/
	MessageListPart getMessages( 1:i64 topicId , 2:i64 groupId, 3:MessageType messageType, 4:i64 lastLoadedId, 5:bool archived, 6:i32 length) throws (1:error.InvalidOperation exc),

//получение сообщений первого уровня. если lastLoadedId = 0, то сообщения грузятся начиная с первого. если !=0, то после указанного.
	MessageListPart getFirstLevelMessages( 1:i64 topicId , 2:i64 groupId, 3:MessageType messageType, 4:i64 lastLoadedId, 5:bool archived, 6:i32 length) throws (1:error.InvalidOperation exc),

//получение сообщений в виде списка. сообщения отсортированы по дате создания. более позднии появляются первыми. значения параметров теже что и у функции getFirstLevelMessages. 
	MessageListPart getMessagesAsList( 1:i64 topicId , 2:i64 groupId, 3:MessageType messageType, 4:i64 lastLoadedId, 5:bool archived, 6:i32 length) throws (1:error.InvalidOperation exc),
	
		
	UserOpinion likeOrDislikeMessage(1:i64 messageId, 2:i32 opinion) throws (1:error.InvalidOperation exc),
	UserOpinion likeOrDislikeTopic(1:i64 topicId, 2:i32 opinion) throws (1:error.InvalidOperation exc),
	
	i64 markReadMessage(1:i64 messageId ) throws (1:error.InvalidOperation exc),
	i64 markReadTopic(1:i64 topicId ) throws (1:error.InvalidOperation exc),
	i64 moveTopicToArchive(1:i64 topicId ) throws (1:error.InvalidOperation exc),
	i64 restoreTopicFromArchive(1:i64 topicId) throws (1:error.InvalidOperation exc),
	i64 markTopicUnintrested(1:i64 topicId, 2:bool interested) throws (1:error.InvalidOperation exc),
	i64 makeMessageLinked(1:i64 message1Id, 2:i64 message2Id ) throws (1:error.InvalidOperation exc),
	
	
}