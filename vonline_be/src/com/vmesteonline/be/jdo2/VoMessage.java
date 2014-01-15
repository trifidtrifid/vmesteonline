package com.vmesteonline.be.jdo2;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.apache.log4j.Logger;

import com.google.appengine.api.datastore.Key;
import com.vmesteonline.be.data.PMF;

/**
 * Created by brozer on 1/12/14.
 */
@PersistenceCapable
public class VoMessage extends com.vmesteonline.be.Message {

	private static Logger logger = Logger.getLogger("com.vmesteonline.be.jdo2.VoMessage");
	
	public VoMessage(com.vmesteonline.be.Message msg ){
		if( 0!=msg.getParentId() ){
			PersistenceManagerFactory pmf = PMF.get();
			PersistenceManager pm = pmf.getPersistenceManager();
			try {
				//Key parentKey = KeyFactory.createKey(VoMessage.class.getSimpleName(), msg.getParentId());
				VoMessage parentMsg = pm.getObjectById(VoMessage.class,	msg.getParentId());
			} catch (Exception e) {
				//throw new 
			}
			
			
		}
	}
	
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    private Key key;

    @Persistent
    private String streetAddress;

    @Persistent
    private long parentId; // 'идентификатор родительского сообщения, NULL для корневого со',
    @Persistent
    private long topicId;

    @Persistent
    private VoUser author; //'автор сообщения или темы',

    @Persistent
    private VoUser recipientId; // 'адресат задан только для личных сообщений, иначе NULL',
    @Persistent
    private int created; // 'дата создания',
    @Persistent
    private int edited;
    @Persistent
    private VoUser approved; // 'идентификатор пользователя промодерировавшего сообщение',
    @Persistent
    private byte[] content; // 'содержание сообщения',
    @Persistent
    private int likes;
    @Persistent
    private int unlikes;
    @Persistent
    private long idForum;
    @Persistent
    private long idShop;
    @Persistent
    private long idDialog;
    @Persistent
    private long idNews;

    @Persistent
    private RubricLocation location;
}
