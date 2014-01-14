package com.vmesteonline.be.jdo2;

import com.google.appengine.api.datastore.Key;
import com.vmesteonline.be.Message;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;


/**
 * Created by brozer on 1/12/14.
 */
@PersistenceCapable
public class Messge extends com.vmesteonline.be.Message {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    private String streetAddress;

    @Persistent
    private int parentId; // 'идентификатор родительского сообщения, NULL для корневого со',
    @Persistent
    private int topicId;

//    @Persistent
//    private User author; //'автор сообщения или темы',

//    @Persistent
//    private User recipientId; // 'адресат задан только для личных сообщений, иначе NULL',
    @Persistent
    private int created; // 'дата создания',
    @Persistent
    private int edited;
    //@Persistent
    //private User approved; // 'идентификатор пользователя промодерировавшего сообщение',
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
