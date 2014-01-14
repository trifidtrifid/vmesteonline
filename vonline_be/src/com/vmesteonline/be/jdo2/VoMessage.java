package com.vmesteonline.be.jdo2;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.vmesteonline.be.data.PMF;

import javax.jdo.PersistenceManagerFactory;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;


/**
 * Created by brozer on 1/12/14.
 */
@PersistenceCapable
public class VoMessage extends com.vmesteonline.be.Message {

	public VoMessage(com.vmesteonline.be.Message msg ){
		if( 0!=msg.getParentId() ){
			PersistenceManagerFactory pm = PMF.get();
			//Key parentKey = KeyFactory.createKey(VoMessage.class.getSimpleName(), msg.getParentId());
			//VoMessage parentMsg = ((VoMessage) pm).getObjectById(VoMessage.class, msg.getParentId());
			
		}
	}
	
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    private Key key;

    @Persistent
    private String streetAddress;

    @Persistent
    private Key parentId; // 'идентификатор родительского сообщения, NULL для корневого со',
    @Persistent
    private Key topicId;

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
    private Key idForum;
    @Persistent
    private Key idShop;
    @Persistent
    private Key idDialog;
    @Persistent
    private Key idNews;

    @Persistent
    private RubricLocation location;
}
