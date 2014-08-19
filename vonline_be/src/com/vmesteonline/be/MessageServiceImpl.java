package com.vmesteonline.be;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import org.apache.thrift.TException;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.vmesteonline.be.data.JDBCConnector;
import com.vmesteonline.be.data.MySQLJDBCConnector;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoFileAccessRecord;
import com.vmesteonline.be.jdo2.VoMessage;
import com.vmesteonline.be.jdo2.VoPoll;
import com.vmesteonline.be.jdo2.VoRubric;
import com.vmesteonline.be.jdo2.VoSession;
import com.vmesteonline.be.jdo2.VoTopic;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.VoUserGroup;
import com.vmesteonline.be.messageservice.Attach;
import com.vmesteonline.be.messageservice.Message;
import com.vmesteonline.be.messageservice.MessageListPart;
import com.vmesteonline.be.messageservice.MessageService.Iface;
import com.vmesteonline.be.messageservice.MessageType;
import com.vmesteonline.be.messageservice.Poll;
import com.vmesteonline.be.messageservice.Topic;
import com.vmesteonline.be.messageservice.TopicListPart;
import com.vmesteonline.be.messageservice.WallItem;
import com.vmesteonline.be.utils.EMailHelper;
import com.vmesteonline.be.utils.StorageHelper;
import com.vmesteonline.be.utils.VoHelper;

public class MessageServiceImpl extends ServiceImpl implements Iface {

	public MessageServiceImpl() throws InvalidOperation {
		initDb();
	}

	public MessageServiceImpl(String sessId) throws InvalidOperation {
		super(sessId);
		initDb();
	}

	@Override
	public void sendInfoEmail(String email, String name, String content) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();

		try {
			String subj = "Contacts: ";
			try {
				VoUser voUser = getCurrentUser(pm);
				subj += "registered user " + voUser.getName() + " " + voUser.getLastName() + " " + voUser.getContacts();
			} catch (Exception e) {
				if (email == null || name == null || email.length() == 0 || name.length() == 0)
					throw new InvalidOperation(VoError.IncorrectParametrs, "email and name can't be empty or null");
				subj += "unregistered user " + name + " " + email;
			}
			EMailHelper.sendSimpleEMail("trifid@gmail.com", subj, content);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning("warning when try to send email from contacts. user " + name + " email " + email + " content " + content);
		} finally {
			pm.close();
		}

	}


	@Override
	public List<WallItem> getWallItems(long groupId, long lastLoadedIdTopicId, int length) throws InvalidOperation, TException {
		List<WallItem> wallItems = new ArrayList<WallItem>();
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {

			try {
				VoUser user = getCurrentUser(pm);
				pm.retrieve(user);
				
				List<Long> userGroups = user.getGroups();
				for( Long ugId : userGroups ){
					
					VoUserGroup group = pm.getObjectById(VoUserGroup.class,ugId);
			
					// todo add last loaded and length
					List<VoTopic> topics = getTopics(group, MessageType.WALL, lastLoadedIdTopicId, length, false, pm);
	
					
					for (VoTopic voTopic : topics) {
						Topic tpc = voTopic.getTopic(user.getId(), pm);
	
						tpc.userInfo = UserServiceImpl.getShortUserInfo(voTopic.getAuthorId().getId());
	
						MessageListPart mlp = getMessagesAsList(tpc.id, MessageType.BASE, 0, false, 10000);
						if (mlp.totalSize > 0)
							logger.info("find msgs " + mlp.messages.size());
	
						WallItem wi = new WallItem(mlp.messages, tpc);
						wallItems.add(wi);
					}
					if( ugId == groupId)
						break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} finally {
			pm.close();
		}

		return wallItems;
	}

	@SuppressWarnings("unchecked")
	@Override
	public MessageListPart getMessagesAsList(long topicId, MessageType messageType, long lastLoadedId, boolean archived, int length)
			throws InvalidOperation {
		long userId = 0;
		if (messageType != MessageType.BLOG)
			userId = getCurrentUserId();

		PersistenceManager pm = PMF.getPm();
		try {

			Query q = pm.newQuery(VoMessage.class);
			q.setFilter("topicId == " + topicId);
			List<VoMessage> voMsgs = (List<VoMessage>) q.execute();
			Collections.sort(voMsgs, new VoMessage.ComparatorByCreateDate());

			if (lastLoadedId != 0) {
				List<VoMessage> subLst = null;
				for (int i = 0; i < voMsgs.size() - 1; i++) {
					if (voMsgs.get(i).getId() == lastLoadedId)
						subLst = voMsgs.subList(i + 1, voMsgs.size());
				}
				voMsgs = (subLst == null) ? new ArrayList<VoMessage>() : subLst;
			}
			return createMlp(voMsgs, userId, pm, length);
		} finally {
			pm.close();
		}

	}

	@Override
	public MessageListPart getFirstLevelMessages(long topicId, long groupId, MessageType messageType, long lastLoadedId, boolean archived, int length)
			throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoUser user = getCurrentUser(pm);
			MessagesTree tree = MessagesTree.createMessageTree(topicId, pm);
			List<VoMessage> voMsgs = tree.getTreeMessagesFirstLevel(new MessagesTree.Filters(user.getId(), 
					pm.getObjectById(VoUserGroup.class,groupId)));

			if (lastLoadedId != 0) {
				List<VoMessage> subLst = null;
				for (int i = 0; i < voMsgs.size() - 1; i++) {
					if (voMsgs.get(i).getId() == lastLoadedId)
						subLst = voMsgs.subList(i + 1, voMsgs.size());
				}
				voMsgs = (subLst == null) ? new ArrayList<VoMessage>() : subLst;
			}

			return createMlp(voMsgs, user.getId(), pm, length);
		} finally {
			pm.close();
		}

	}
	
	// ===================================================================================================================================
	private static String mlpKeyPrefix = "MessageListPartByGroupAndTopic";
	@Override
	public MessageListPart getMessages(long topicId, long groupId, MessageType messageType, long lastLoadedMsgId, boolean archived, int length)
			throws InvalidOperation, TException {

		String key = mlpKeyPrefix+":"+topicId+":"+groupId+":"+messageType+":"+lastLoadedMsgId+":"+archived+":"+length;
		
		PersistenceManager pm = PMF.getPm();
		try {
			int lastUpdate = pm.getObjectById(VoTopic.class, topicId).getLastUpdate();
			
			Object objectFromCache = getObjectFromCache(key);
			if( null!=objectFromCache && objectFromCache instanceof VoHelper.CacheObjectUnit<?> 
				&& ((VoHelper.CacheObjectUnit<?>)objectFromCache).timestamp == lastUpdate )
				return ((VoHelper.CacheObjectUnit<MessageListPart>)objectFromCache).object;
			
			VoUser user = getCurrentUser(pm);
			MessagesTree tree = MessagesTree.createMessageTree(topicId, pm);
			List<VoMessage> voMsgs = tree.getTreeMessagesAfter(lastLoadedMsgId, new MessagesTree.Filters(user.getId(), 
					pm.getObjectById(VoUserGroup.class,groupId)));
			MessageListPart mlp = createMlp(voMsgs, user.getId(), pm, length);
			putObjectToCache(key, new VoHelper.CacheObjectUnit<MessageListPart>(lastUpdate,mlp));
			return mlp;
		} finally {
			pm.close();
		}

	}
	
	public static List<VoTopic> getTopics(VoUserGroup group, MessageType type, long lastLoadedTopicId, int length, boolean importantOnly,
			 PersistenceManager pm) {

		List<VoTopic> topics = new ArrayList<VoTopic>();
		try {
		
			Query tQuery = pm.newQuery( VoTopic.class );
			String filter = "";
			
			if( group.getGroupType() > GroupType.BUILDING.getValue()){
				filter = "visibleGroups=="+group.getId();
			} else {
				filter = "userGroupId=="+group.getId();
			}
			if( importantOnly ){
				int minimumCreateDate = (int) (System.currentTimeMillis()/1000L - 86400L * 14L); //two only last week important
				filter = " isImportant == true && lastUpdate > "+minimumCreateDate+" && " + filter;
			}
			if( type == MessageType.WALL )
				filter += " && (type=='WALL' || type=='BASE')";
			else 
				filter += " && type=='"+type+"'";
			
			tQuery.setFilter(filter);
			tQuery.setOrdering("lastUpdate DESC");
			
			List<VoTopic> allTopics = (List<VoTopic>) tQuery.execute( );
			boolean addTopic = 0 == lastLoadedTopicId ? true : false;
			for (VoTopic topic : allTopics) {
				
				if (addTopic ) {
					topics.add(topic);
					
				} else if (topic.getId() == lastLoadedTopicId) {
					addTopic = true;
				}
				
				if( topics.size() == length)
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return topics;
	}

	@Override
	public TopicListPart getBlog(long lastLoadedTopicId, int length) throws InvalidOperation {

		PersistenceManager pm = PMF.get().getPersistenceManager();
		List<VoTopic> topics = getTopics(null, MessageType.BLOG, lastLoadedTopicId, length, false, pm);
		TopicListPart mlp = new TopicListPart();
		mlp.totalSize = topics.size();

		for (VoTopic voTopic : topics) {
			Topic tpc = voTopic.getTopic(0, pm);
			mlp.addToTopics(tpc);
		}
		return mlp;

	}

	@Override
	public TopicListPart getTopics(long groupId, long rubricId, int commmunityId, long lastLoadedTopicId, int length) throws InvalidOperation {
		return getTopics(groupId, rubricId, commmunityId, lastLoadedTopicId, length, MessageType.BASE, false);
	}

	@Override
	public TopicListPart getImportantTopics(long groupId, long rubricId, int commmunityId, int length) throws InvalidOperation {
		return getTopics(groupId, rubricId, commmunityId, 0, 1000, MessageType.WALL, true);
	}

	@Override
	public TopicListPart getAdverts(long groupId, long lastLoadedTopicId, int length) throws InvalidOperation {
		return getTopics(groupId, 0, 0, lastLoadedTopicId, length, MessageType.ADVERT, false);
	}

	private TopicListPart getTopics(long groupId, long rubricId, int commmunityId, long lastLoadedTopicId, int length, MessageType type,
			boolean importantOnly) {

		
		TopicListPart mlp = new TopicListPart();
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {

				VoUser user = getCurrentUser(pm);
				pm.retrieve(user);
				
				List<Long> userGroups = user.getGroups();
				for( Long ugId : userGroups ){
					
					VoUserGroup group = pm.getObjectById(VoUserGroup.class,ugId);
					List<VoTopic> topics = getTopics(group, type, lastLoadedTopicId, length, importantOnly, pm);
				
				
					mlp.totalSize += topics.size();
					for (VoTopic voTopic : topics) {
						Topic tpc = voTopic.getTopic(user.getId(), pm);
	
						tpc.userInfo = UserServiceImpl.getShortUserInfo(voTopic.getAuthorId().getId());
						tpc.setMessageNum( voTopic.getMessageNum());
						mlp.addToTopics(tpc);
					}
					if( ugId == groupId ) //usergGroups MUST be ordered from smaller to bigger one, so if topics of current group are added, it's time to finish collecting
						break;
				}
		} catch (Exception e) {
				e.printStackTrace();

		} finally {
			pm.close();
		}
		orderTopicsByLastUpdate(mlp.topics);
		return mlp; 

	}

	private List<Topic> orderTopicsByLastUpdate(List<Topic> topicsl) {
		if( null!=topicsl )
			Collections.sort( topicsl, new Comparator<Topic>(){

			@Override
			public int compare(Topic o1, Topic o2) {
				return -Integer.compare( o1.getLastUpdate(), o2.getLastUpdate());
			}
			
		});
		return topicsl;
	}

	@Override
	public Topic postTopic(Topic topic) throws InvalidOperation {

		PersistenceManager pm = PMF.getPm();
		try {
			try {
				if (0 == topic.getId()) {
					int now = (int) (System.currentTimeMillis() / 1000L);
					topic.lastUpdate = now;
					topic.message.created = now;
					topic.message.authorId = getCurrentUserId();

					VoTopic votopic = new VoTopic(topic, pm);
					votopic.setSubject(topic.getSubject());

					if (topic.poll != null) {

						VoPoll poll = VoPoll.create(topic.poll);
						pm.makePersistent(poll);
						votopic.setPollId(poll.getId());
						topic.poll.pollId = poll.getId();
					}

					pm.makePersistent(votopic);
					topic.setId(votopic.getId());

					VoUser user = getCurrentUser(pm);
					pm.getObjectById( VoUserGroup.class, votopic.getUserGroupId() );
					topic.userInfo = user.getShortUserInfo();


				} else {
					updateTopic(topic);
				}
				return topic;
			} catch (Exception e) {
				e.printStackTrace();
				throw new InvalidOperation(VoError.GeneralError, "can't create topic. " + e);
			}
		} finally {
			pm.close();
		}
	}

	/**
	 * checkUpdates запрашивает наличие обновлений с момента предыдущего запроса, который возвращает сервер в ответе, если обновлений нет - в ответ
	 * приходит новое значение таймстампа формирования ответа на сервере. При наличии обновлений возвращается 0
	 **/
	@Override
	public int checkUpdates(int lastRequest) throws InvalidOperation {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		VoSession sess = getCurrentSession(pm);
		int now = (int) (System.currentTimeMillis() / 1000L);
		if (now - sess.getLastActivityTs() > 60) { /*
																								 * Update last Activity once per minute
																								 */
			sess.setLastActivityTs(now);
			try {
				pm.makePersistent(sess);
			} finally {
				pm.close();
			}
		}
		return sess.getLastUpdateTs() > lastRequest ? 0 : now;
	}

	@Override
	public Message postBlogMessage(Message msg) throws InvalidOperation {
		if( 0!=msg.getId() ){
			updateMessage(msg);
			return msg;
		}
		PersistenceManager pm = PMF.getPm();
		try {
			try {

				VoUser voUser = getCurrentUser();
				msg.anonName += voUser.getName() + " " + voUser.getLastName();
				msg.userInfo = voUser.getShortUserInfo();
				msg.authorId = voUser.getId();
			} catch (InvalidOperation e) {
				if (msg.getAnonName() == null || msg.getAnonName().isEmpty())
					throw new InvalidOperation(VoError.IncorrectParametrs, "has no user name");
			}

			VoMessage vomsg = new VoMessage(msg, MessageType.BLOG);
			pm.makePersistent(vomsg);
			msg.setId(vomsg.getId());
			return msg;

		} finally {
			pm.close();
		}

	}

	@Override
	public Message postMessage(Message msg) throws InvalidOperation {
		long userId = getCurrentUserId();
		msg.setAuthorId(userId);
		VoMessage vomsg;

		if (0 == msg.getId()) {
			PersistenceManager pm = PMF.getPm();
			try {
				try {

					vomsg = new VoMessage(msg, pm);
					VoTopic topic = pm.getObjectById(VoTopic.class, msg.getTopicId());
					topic.setMessageNum(topic.getMessageNum() + 1);
					topic.setLastUpdate((int) (System.currentTimeMillis() / 1000));
					pm.makePersistent(topic);

					if (msg.type != MessageType.BLOG)
						msg.userInfo = getCurrentUser(pm).getShortUserInfo();

				} catch (Exception e) {
					e.printStackTrace();
					throw new InvalidOperation(VoError.IncorrectParametrs, "can't create message " + e.toString());
				}
			} finally {
				pm.close();
			}
			msg.setId(vomsg.getId());

		} else {
			updateMessage(msg);
		}
		return msg;
	}

	private void initDb() throws InvalidOperation {
		
	}

	@Override
	public Poll doPoll(long pollId, int item) throws InvalidOperation {
		long userId = getCurrentUserId();
		PersistenceManager pm = PMF.getPm();
		try {
			VoPoll poll = pm.getObjectById(VoPoll.class, pollId);
			if (!poll.isAlreadyPoll(userId)) {
				poll.getValues().set(item, poll.getValues().get(item) + 1);
				poll.doPoll(userId);
			}
			return poll.getPoll(userId);
		} catch (Exception e) {
			logger.severe("can't do poll. " + e.getMessage());
			e.printStackTrace();
			throw new InvalidOperation(VoError.IncorrectParametrs, "incorect parametr for poll");
		} finally {
			pm.close();
		}
	}

	private static MessageListPart createMlp(List<VoMessage> lst, long userId, PersistenceManager pm, int length) throws InvalidOperation {

		if (lst.size() > length)
			lst = lst.subList(0, length);

		MessageListPart mlp = new MessageListPart();
		if (lst == null) {
			logger.warning("try to create MessagePartList from null object");
			return mlp;
		}
		mlp.totalSize = lst.size();
		for (VoMessage voMessage : lst) {
			Message msg = voMessage.getMessage(userId, pm);
			if (voMessage.getAuthorId() != null)
				msg.userInfo = UserServiceImpl.getShortUserInfo(voMessage.getAuthorId().getId());
			mlp.addToMessages(msg);
		}
		return mlp;
	}

	private void updateMessage(Message msg) throws InvalidOperation {

		int now = (int) (System.currentTimeMillis() / 1000);
		PersistenceManager pm = PMF.getPm();
		try {
			VoMessage storedMsg = pm.getObjectById(VoMessage.class, msg.getId());
			
			if (storedMsg.getAuthorId().getId() != getCurrentUserId(pm) )
				throw new InvalidOperation(com.vmesteonline.be.VoError.IncorrectParametrs, "User is not author of message");

			VoTopic topic = pm.getObjectById(VoTopic.class, storedMsg.getTopicId());

			/* Check if content changed, then update edit date */
			if (!storedMsg.getContent().equals( msg.getContent())) {
				//int editedAt = 0 == msg.getEdited() ? now : msg.getEdited();
				storedMsg.setEditedAt(now);
				storedMsg.setContent(msg.getContent());
			}

			if (storedMsg.getTopicId() != msg.getTopicId() || storedMsg.getAuthorId().getId() != msg.getAuthorId()
					|| storedMsg.getRecipient() != msg.getRecipientId() || storedMsg.getCreatedAt() != msg.getCreated() || storedMsg.getType() != msg.getType())
				throw new InvalidOperation(com.vmesteonline.be.VoError.IncorrectParametrs,
						"Parameters: topic, author, recipient, createdAt, type could not be changed!");
			
			storedMsg.setImages( updateAttachments( storedMsg.getImages(), msg.getImages(),  storedMsg.getAuthorId().getId(), pm ));
			storedMsg.setDocuments( updateAttachments( storedMsg.getDocuments(), msg.getDocuments(),  storedMsg.getAuthorId().getId(), pm ));
			
			pm.makePersistent(storedMsg);
			pm.makePersistent(topic);

		} catch( JDOObjectNotFoundException onfe ){
			throw new InvalidOperation(VoError.IncorrectParametrs,
					"Message not found");

		} finally {
			pm.close();
		}
	}

	private void updateTopicMessage(VoTopic topic, Message msg, PersistenceManager pm) throws InvalidOperation {

		int now = (int) (System.currentTimeMillis() / 1000);
	
		if (topic.getAuthorId().getId() != getCurrentUserId(pm) )
			throw new InvalidOperation(com.vmesteonline.be.VoError.IncorrectParametrs, "User is not author of message");


		/* Check if content changed, then update edit date */
		if (!topic.getContent().equals( msg.getContent())) {
			//int editedAt = 0 == msg.getEdited() ? now : msg.getEdited();
			topic.setEditedAt(now);
			topic.setLastUpdate(now);
			topic.setContent(msg.getContent());
		}

		if (topic.getAuthorId().getId() != msg.getAuthorId() || topic.getCreatedAt() != msg.getCreated() || topic.getType() != msg.getType())
			throw new InvalidOperation(com.vmesteonline.be.VoError.IncorrectParametrs,
					"Parameters: topic, author, recipient, createdAt, type could not be changed!");

		pm.makePersistent(topic);
	
	}
	private void updateTopic(Topic topic) throws InvalidOperation {

		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();

		try {
			VoTopic theTopic;
			try {
				theTopic = pm.getObjectById(VoTopic.class, topic.getId());
			} catch (Exception e1) {
				throw new InvalidOperation(com.vmesteonline.be.VoError.IncorrectParametrs, "FAiled to update Topic. No topic found by ID" + topic.getId());
			}

			if(0!=topic.getRubricId())
				try {
					pm.getObjectById(VoRubric.class, KeyFactory.createKey(VoRubric.class.getSimpleName(), topic.getRubricId()));
				} catch (Exception e) {
					throw new InvalidOperation(com.vmesteonline.be.VoError.IncorrectParametrs, "Failed to move topic No Rubric found by id="
							+ topic.getRubricId());
				}
			updateTopicMessage( theTopic, topic.getMessage(), pm );
			theTopic.setImages( updateAttachments( theTopic.getImages(), topic.getMessage().getImages(),  theTopic.getAuthorId().getId(), pm ));
			theTopic.setDocuments( updateAttachments( theTopic.getDocuments(), topic.getMessage().getDocuments(),  theTopic.getAuthorId().getId(), pm ));
			theTopic.setUsersNum(topic.usersNum);
			theTopic.setViewers(topic.viewers);
			theTopic.setUserGroupId(topic.getMessage().getGroupId());
			
			updatePoll(theTopic, topic, pm); 
					
			pm.makePersistent(theTopic);

		} finally {
			pm.close();
		}
	}

	private void updatePoll(VoTopic theTopic, Topic topic, PersistenceManager pm) throws InvalidOperation {
		if( topic.poll == null && 0!=theTopic.getPollId() || topic.poll !=null && topic.poll.pollId != theTopic.getPollId()){
			if( theTopic.getPollId() == 0 ) {//poll changed so the old one should be removed
				pm.deletePersistent(pm.getObjectById(VoPoll.class, theTopic.getPollId()));
				theTopic.setPollId(0L);
			} 
			if( topic.poll!=null ){
				VoPoll poll = VoPoll.create(topic.poll);
				pm.makePersistent(poll);
				theTopic.setPollId(poll.getId());
				topic.poll.pollId = poll.getId();
			}
		} else if( topic.poll != null && topic.poll.pollId == theTopic.getPollId()) { //check changes
			VoPoll newPoll = VoPoll.create(topic.poll);
			if(0!=theTopic.getPollId()) {
				VoPoll oldPoll = pm.getObjectById(VoPoll.class,theTopic.getPollId());
				newPoll.setId(theTopic.getPollId());
				newPoll.setValues( oldPoll.getValues());
				newPoll.setAlreadyPoll(oldPoll.getAlreadyPoll());
			}
			pm.makePersistent(newPoll);
			theTopic.setPollId( newPoll.getId() );
		}
		if( null!=topic.poll ) topic.poll.pollId = theTopic.getPollId();
	}

//======================================================================================================================
	
	public static List<Long> updateAttachments(List<Long> oldFileIds, List<Attach> updatedAttaches, long userId, PersistenceManager pm) {
		
		Set<Attach> onlyNewAttaches = new HashSet<Attach>();
		onlyNewAttaches.addAll(updatedAttaches);
		ArrayList<Long> updatedFileIdList = new ArrayList<Long>();
		
		//delete old files
		for( long fileId: oldFileIds){
			VoFileAccessRecord far = pm.getObjectById(VoFileAccessRecord.class,fileId);
			String url = far.getURL();

			for(Attach attach: updatedAttaches){
				if( null!=attach.getURL() && attach.getURL().startsWith(url)){
					onlyNewAttaches.remove(attach); //it's not a new one
					updatedFileIdList.add(fileId);  //leave it in updated version
					break;
				}
			}
		}
		
		//upload new Files
		for(Attach attach: onlyNewAttaches){
			try {
				VoFileAccessRecord cfar = StorageHelper.loadAttach(pm, userId, attach);
				updatedFileIdList.add(cfar.getId());
			} catch (InvalidOperation e) {
				logger.severe("Failed to load Attach. "+e);
				e.printStackTrace();
			}
		}
		
		return updatedFileIdList;
		
	}
//======================================================================================================================

	protected JDBCConnector con;
	private static Logger logger = Logger.getLogger("com.vmesteonline.be.MessageServceImpl");

	@Override
	public boolean isPublicMethod(String method) {
		return true;// publicMethods.contains(method);
	}

	// ======================================================================================================================

	@Override
	public long categoryId() {
		return ServiceCategoryID.MESSAGE_SI.ordinal();
	}

	// ======================================================================================================================

	@Override
	public int markMessageImportant(long messageId, boolean isImportant) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoTopic topic = pm.getObjectById(VoTopic.class, messageId);
			VoUser author = null == topic.getAuthorId() ? null : pm.getObjectById(VoUser.class, topic.getAuthorId());
			int impScore = topic.markImportant(getCurrentUser(), author, isImportant, pm);

			VoUserGroup topicGroup = pm.getObjectById(VoUserGroup.class, topic.getUserGroupId());
			boolean isReallyImportant = (impScore >= topicGroup.getImportantScore());
			topic.setImportant( isReallyImportant );
			pm.makePersistent( topic );

			// time to send notification if not sent?
			if (isReallyImportant && 0 == topic.getImportantNotificationSentDate()) {
				
				topic.setImportant( true );
				pm.makePersistent( topic );
				
				Queue queue = QueueFactory.getDefaultQueue();
	      queue.add(withUrl("/tasks/notification").param("rt", "mbi")
	      		.param("it", ""+topic.getId())
	      		.param("ug", ""+topicGroup.getId()));
				
				//Notification.messageBecomeImportantNotification(msg, topicGroup);
				topic.setImportantNotificationSentDate((int) (System.currentTimeMillis() / 1000L));
			}
			return impScore;
		} catch(JDOObjectNotFoundException onfe){
			throw new InvalidOperation(VoError.IncorrectParametrs, "No message found by ID:"+messageId);
		} finally {
			pm.close();
		}
	}

	// ======================================================================================================================

	@Override
	public int markMessageLike(long messageId) throws InvalidOperation{
		PersistenceManager pm = PMF.getPm();
		try {
			VoTopic msg = pm.getObjectById(VoTopic.class, messageId);
			if( msg.getAuthorId() == null || msg.getAuthorId().getId() == getCurrentUserId())
				return msg.getPopularityScore();
			
			VoUser author = null == msg.getAuthorId() ? null : pm.getObjectById(VoUser.class, msg.getAuthorId());
			return msg.markLikes(getCurrentUser(), author, pm);
			
		} catch(JDOObjectNotFoundException onfe){
			throw new InvalidOperation(VoError.IncorrectParametrs, "No message found by ID:"+messageId);
		} finally {
			pm.close();
		}
	}

	// ======================================================================================================================

	@Override
	public Message deleteMessage(long msgId) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		boolean isModerator = false;
		try {
			VoMessage msg = pm.getObjectById(VoMessage.class, msgId);
			VoUser cu = getCurrentUser();
			long topicId = msg.getTopicId();
			VoTopic topic = pm.getObjectById(VoTopic.class, topicId);
			
			if( msg.getAuthorId().getId() != cu.getId() && 
					(isModerator = cu.isGroupModerator(topic.getUserGroupId())))
				throw new InvalidOperation(VoError.IncorrectParametrs, "USer is not the author and not moderator");
			
			topic.setMessageNum( topic.getMessageNum() - 1 );
			topic.setChildMessageNum( topic.getChildMessageNum() - 1);
			topic.setLastUpdate((int) (System.currentTimeMillis()/1000L));
			
			
			deleteAttachments(pm, msg.getImages());
			deleteAttachments(pm, msg.getDocuments());
			
			//check if message can be deleted
			List<VoMessage> msgsOfTopic = (List<VoMessage>) pm.newQuery(VoMessage.class,"topicId=="+topicId ).execute();
			boolean canDelete = true;
			for( VoMessage msgot : msgsOfTopic){
				if( msgot.getParentId() == msgId){
					canDelete = false;
					break;
				}
			}
			if(canDelete){
				pm.deletePersistent(msg);
				return null;
			} else {
				msg.setContent("Сообщение удалено "+ (isModerator ? "модератором." : "пользователем."));
				return msg.getMessage(cu.getId(), pm);
			}
			
		} catch( JDOObjectNotFoundException onfe ){
			throw new InvalidOperation(VoError.IncorrectParametrs,
					"Message not found");

		} finally {
			pm.close();
		}
	}

	// ======================================================================================================================

	private void deleteAttachments(PersistenceManager pm, List<Long> imgs) {
		for( Long attachId: imgs ){
			try {
				VoFileAccessRecord att = pm.getObjectById(VoFileAccessRecord.class, attachId);
				StorageHelper.deleteImage(att.getGSFileName());
				pm.deletePersistent(att);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// ======================================================================================================================

	@Override
	public Topic deleteTopic(long topicId) throws InvalidOperation, TException {
		boolean isModerator = false;
		PersistenceManager pm = PMF.getPm();
		try {
			VoTopic tpc = pm.getObjectById(VoTopic.class, topicId);
			VoUser cu = getCurrentUser();
			if( tpc.getAuthorId().getId() != cu.getId() && (isModerator = cu.isGroupModerator(tpc.getUserGroupId())))
				throw new InvalidOperation(VoError.IncorrectParametrs, "USer is not the author and not a moderator");
			
			deleteAttachments(pm, tpc.getImages());
			deleteAttachments(pm, tpc.getDocuments());
			
			if(0==tpc.getMessageNum()){
				try {
					pm.deletePersistent(tpc);
					return null;
				} catch (Exception e) {
					logger.severe("Failed to delete Topic: "+e.getMessage());
					e.printStackTrace();
					throw new InvalidOperation(VoError.GeneralError,
							"Topic not deleted. "+e.getMessage());
				}
			} else {
				tpc.setContent("Тема удалена пользователем "+ (isModerator ? "модератором." : "пользователем."));
				tpc.setLastUpdate((int) (System.currentTimeMillis()/1000L));
				return tpc.getTopic(cu.getId(), pm);
			}
			
		} catch( JDOObjectNotFoundException onfe ){
			throw new InvalidOperation(VoError.IncorrectParametrs,
					"Topic not found");

		} finally {
			pm.close();
		}
	}
}
