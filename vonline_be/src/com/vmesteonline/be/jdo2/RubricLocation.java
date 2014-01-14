package com.vmesteonline.be.jdo2;

import com.google.appengine.api.datastore.Key;

import javax.jdo.annotations.*;

/**
 * Created by brozer on 1/13/14.
 */
@PersistenceCapable
@Indices({@Index(name="LOC_IDX", members={"longitude", "lattitude", "attitude"}), @Index(name="ADDR_IDX", members={"postalAddress"})})
public class RubricLocation {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    private String name;

    @Persistent
    private float longitude;
    @Persistent
    private float lattitude;
    @Persistent
    private float attitude;
    @Persistent
    private String postalAddress;
}
