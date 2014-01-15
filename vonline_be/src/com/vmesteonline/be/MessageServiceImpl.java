package com.vmesteonline.be;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.apache.thrift.TException;

import com.vmesteonline.be.MessageService.Iface;

public class MessageServiceImpl implements Iface {
	
	private static Logger logger = Logger.getLogger("com.vmesteonline.be.MessageServceImpl");

	@Override
	public Message createMessage(long topicId, long parentId, String subject,
			long groupId, long authorId, MessageType type, ByteBuffer content,
			long recipientId) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long postMessage(Message msg) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Topic createTopic(long rubricId, String subject, long groupId,
			long authorId, MessageType type, ByteBuffer content,
			long recipientId) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long postTopic(long rubricId, Message msg) throws InvalidOperation,
			TException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean checkUpdates(long userId) throws InvalidOperation,
			TException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public GroupUpdates getUpdates(long userId) throws InvalidOperation,
			TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TopicListPart getTopics(long groupId, long rubricId, long userId,
			MessageType messageType, int offset, int length)
			throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MessageListPart getMessages(long groupId, long rubricId,
			long userId, MessageType messageType, int offset, int length)
			throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long like(long messageId, long userId) throws InvalidOperation,
			TException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long dislike(long messageId, long userId) throws InvalidOperation,
			TException {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	
}
