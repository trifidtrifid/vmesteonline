package com.vmesteonline.be;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.apache.thrift.TException;

import com.vmesteonline.be.MessageService.Iface;

public class MessageServiceImpl implements Iface {
	
	private static Logger logger = Logger.getLogger("com.vmesteonline.be.MessageServceImpl");
	
	
	@Override
	public Message createMessage(int topicId, int parentId, String subject,
			int groupId, int authorId, MessageType type, ByteBuffer content,
			int recipientId) throws InvalidOperation, TException {
		
		return null;
	}

	@Override
	public int postMessage(Message msg) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Topic createTopic(int rubricId, String subject, int groupId,
			int authorId, MessageType type, ByteBuffer content, int recipientId)
			throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int postTopic(int rubricId, Message msg) throws InvalidOperation,
			TException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean checkUpdates(int userId) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public GroupUpdates getUpdates(int userId) throws InvalidOperation,
			TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TopicListPart getTopics(int groupId, int rubricId, int userId,
			MessageType messageType, int offset, int length)
			throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MessageListPart getMessages(int groupId, int rubricId, int userId,
			MessageType messageType, int offset, int length)
			throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int like(int messageId, int userId) throws InvalidOperation,
			TException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int dislike(int messageId, int userId) throws InvalidOperation,
			TException {
		// TODO Auto-generated method stub
		return 0;
	}

}
