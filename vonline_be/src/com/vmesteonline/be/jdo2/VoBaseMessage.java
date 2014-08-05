package com.vmesteonline.be.jdo2;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.datanucleus.annotations.Unindexed;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.messageservice.Attach;
import com.vmesteonline.be.messageservice.Mark;
import com.vmesteonline.be.messageservice.Message;
import com.vmesteonline.be.messageservice.MessageType;
import com.vmesteonline.be.utils.StorageHelper;
import com.vmesteonline.be.utils.StorageHelper.FileSource;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public abstract class VoBaseMessage extends GeoLocation {
	
	public VoBaseMessage(Message msg) throws IOException, InvalidOperation {
		// super(msg.getLikesNum(), msg.getUnlikesNum());
		content = msg.getContent().getBytes();
		links = msg.getLinkedMessages();
		type = msg.getType();
		authorId = KeyFactory.createKey(VoUser.class.getSimpleName(), msg.getAuthorId());
		createdAt = msg.getCreated();
		images = new ArrayList<Long>();
		PersistenceManager pm = PMF.getPm();
		images = new ArrayList<Long>();
		documents = new ArrayList<Long>();

		try {
			if (msg.images != null) {
				List<Attach> savedImages = new ArrayList<Attach>();
				for (Attach img : msg.images) {
					VoFileAccessRecord cfar;
					try {
						FileSource fs = StorageHelper.createFileSource( img );
						cfar = StorageHelper.saveAttach( fs.fname, fs.contentType, authorId.getId(), true, fs.is, pm);
					} catch (IOException e) {
						throw new InvalidOperation(VoError.IncorrectParametrs, "FAiled to upload content. "+e);
					}
					images.add( cfar.getId());
					savedImages.add(cfar.getAttach());
				}
				msg.images = savedImages;
			}

			if (msg.documents != null) {
				List<Attach> savedDocs = new ArrayList<Attach>();
				for (Attach doc : msg.documents) {
					FileSource fs = StorageHelper.createFileSource( doc );
					VoFileAccessRecord cfar = StorageHelper.saveAttach( fs.fname, fs.contentType, authorId.getId(), true, fs.is, pm);
					documents.add( cfar.getId());
					savedDocs.add(cfar.getAttach());
				}
				msg.documents = savedDocs;
			}

		} finally {
			pm.close();
		}
	}

	public void setCreatedAt(int createdAt) {
		this.createdAt = createdAt;
	}

	public void setAuthorId(Key authorId) {
		this.authorId = authorId;
	}

	public VoBaseMessage() {
	}

	/*
	 * public Key getId() { return id; }
	 */
	public Key getAuthorId() {
		return authorId;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	/*
	 * public void setId(Key key) { this.id = key; }
	 */
	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public int getCreatedAt() {
		return createdAt;
	}

	public int getChildMessageNum() {
		return childMessageNum;
	}

	public void setChildMessageNum(int childMessageNum) {
		this.childMessageNum = childMessageNum;
	}

	public int getEditedAt() {
		return editedAt;
	}

	public void setEditedAt(int editedAt) {
		this.editedAt = editedAt;
	}

	/*
	 * public VoUserAttitude(int likes, int unlikes) { likesNum = likes; unlikesNum = unlikes; }
	 */
	public int getLikes() {
		return null==likes ? 0 : likes.size();
	}


	public int markLikes( VoUser user, VoUser author, PersistenceManager pm) {
		if( null == likes ) likes = new HashSet<Long>();
		if( !likes.contains(user.getId())) {
			int up = user.getPopularuty();
			popularityScore += up;
			likes.add(user.getId());
			try{
				if( null==author && null==authorId )
					author = pm.getObjectById(VoUser.class,authorId);
				int ap = author.getPopularuty();
				int pDelta = (int) (((float)( up * up )) / ((float)ap * 10.0F));
				author.setPopularuty( ap + pDelta );
			} catch(Exception e){ 
			}
		}
		return popularityScore;
	}

	public int markImportant( VoUser user, VoUser author, boolean isImportant, PersistenceManager pm) {
		if( null == important ) important = new HashSet<Long>();
		if( null == unimportant ) unimportant = new HashSet<Long>();
		
		long userId = user.getId();
		
		if( !important.contains(userId) && !unimportant.contains(userId)) {
			int ui = user.getImportancy();
			importantScore += ui * ( isImportant ? 1 : -1 ) ;
			
			if( isImportant ) 
				important.add(userId);
			else 
				unimportant.add( userId);
			
			try{
				if( null==author && null==authorId )
					author = pm.getObjectById(VoUser.class,authorId);
				int ai = author.getImportancy();
				int importancyDelta = (int) (((float)( ui * ui )) / ((float)ai * 10.0F) * ( isImportant ? 1F : -1F ));
				author.setImportancy( ai + importancyDelta );
			} catch(Exception e){ 
			}
		}
		return importantScore;
	}


	
	public Integer getImportantScore() {
		return importantScore;
	}

	public Integer getPopularityScore() {
		return popularityScore;
	}

	public Mark isImportant( long userId ){
		return important!=null && important.contains( userId ) ? Mark.POSITIVE : unimportant !=null && unimportant.contains( userId ) ? Mark.NEGATIVE : Mark.NOTMARKED;
	}

	public Mark isLiked( long userId ){
		return likes != null && likes.contains( userId ) ? Mark.POSITIVE : Mark.NOTMARKED;
	}

	
	/*
	 * @PrimaryKey
	 * 
	 * @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY) protected Key id;
	 */
	@Persistent(serialized = "true")
	@Unindexed
	protected byte[] content;

	@Persistent
	@Unindexed
	protected Map<MessageType, Long> links;

	@Persistent
	@Unindexed
	protected MessageType type;

	@Persistent
	@Unindexed
	protected List<Long> images;

	@Persistent
	@Unindexed
	protected List<Long> documents;

	@Persistent
	@Unindexed
	protected Key authorId;

	@Persistent
	@Unindexed
	protected int createdAt;

	@Persistent
	@Unindexed
	protected int editedAt;

	protected int childMessageNum;
	
	@Persistent
	@Unindexed
	protected Set<Long> likes;
	
	@Persistent
	@Unindexed
	protected Set<Long> important;
	@Persistent
	@Unindexed
	protected Set<Long> unimportant;
	
	@Persistent
	protected Integer importantScore;
	@Persistent
	protected Integer popularityScore;
}
