include "bedata.thrift"
include "user.thrift"
namespace * com.vmesteonline.be

struct RubricCounter {
	1:	i64 rubric,
	2:	bedata.MessageType messageType,
	3:	i32 newTopicNum,
	4:	i32 newMessageNum
}

struct GroupUpdates {
	1:map<i64,RubricCounter> groupCounters 
}

struct TopicListPart {
	1:set<bedata.Topic> topics,
	2:i32	totalSize //size of full list
} 

struct MessageListPart {
	1:set<bedata.Message> topics,
	2:i32	totalSize //size of full list
} 

service MessageService {

	bedata.Message createMessage( 1:i64 topicId, 2:i64 parentId, 3:string subject, 4:i64 groupId, 
	 5:i64 authorId, 6:bedata.MessageType type, 7:binary content,8:i64 recipientId) throws (1:user.InvalidOperation exc),
	i64 postMessage( 1:bedata.Message msg ) throws (1:user.InvalidOperation exc),
	  
	bedata.Topic createTopic( 1:i64 rubricId, 3:string subject, 4:i64 groupId, 
	 5:i64 authorId, 6:bedata.MessageType type, 7:binary content,8:i64 recipientId) throws (1:user.InvalidOperation exc), 
	i64 postTopic( 1:i64 rubricId, 2:bedata.Message msg) throws (1:user.InvalidOperation exc),  
	 
	bool checkUpdates( 1:i64 userId ) throws (1:user.InvalidOperation exc),
	GroupUpdates getUpdates( 1:i64 userId) throws (1:user.InvalidOperation exc),

	TopicListPart getTopics( 1:i64 groupId , 2:i64 rubricId, 3:i64 userId, 4:bedata.MessageType messageType, 5:i32 offset, 6:i32 length) throws (1:user.InvalidOperation exc),
	MessageListPart getMessages( 1:i64 groupId , 2:i64 rubricId, 3:i64 userId, 4:bedata.MessageType messageType, 5:i32 offset, 6:i32 length) throws (1:user.InvalidOperation exc),
	
	i64 like(1:i64 messageId, 2:i64 userId ) throws (1:user.InvalidOperation exc),
	i64 dislike(1:i64 messageId, 2:i64 userId ) throws (1:user.InvalidOperation exc),
}