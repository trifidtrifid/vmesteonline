/**
 * Autogenerated by Thrift Compiler (0.9.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.vmesteonline.be.shop;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Producer implements org.apache.thrift.TBase<Producer, Producer._Fields>, java.io.Serializable, Cloneable, Comparable<Producer> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("Producer");

  private static final org.apache.thrift.protocol.TField ID_FIELD_DESC = new org.apache.thrift.protocol.TField("id", org.apache.thrift.protocol.TType.I64, (short)1);
  private static final org.apache.thrift.protocol.TField NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("name", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField DESCR_FIELD_DESC = new org.apache.thrift.protocol.TField("descr", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField LOGO_URL_FIELD_DESC = new org.apache.thrift.protocol.TField("logoURL", org.apache.thrift.protocol.TType.STRING, (short)4);
  private static final org.apache.thrift.protocol.TField HOME_URL_FIELD_DESC = new org.apache.thrift.protocol.TField("homeURL", org.apache.thrift.protocol.TType.STRING, (short)5);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new ProducerStandardSchemeFactory());
    schemes.put(TupleScheme.class, new ProducerTupleSchemeFactory());
  }

  public long id; // required
  public String name; // required
  public String descr; // required
  public ByteBuffer logoURL; // required
  public String homeURL; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    ID((short)1, "id"),
    NAME((short)2, "name"),
    DESCR((short)3, "descr"),
    LOGO_URL((short)4, "logoURL"),
    HOME_URL((short)5, "homeURL");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // ID
          return ID;
        case 2: // NAME
          return NAME;
        case 3: // DESCR
          return DESCR;
        case 4: // LOGO_URL
          return LOGO_URL;
        case 5: // HOME_URL
          return HOME_URL;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __ID_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.ID, new org.apache.thrift.meta_data.FieldMetaData("id", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.NAME, new org.apache.thrift.meta_data.FieldMetaData("name", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.DESCR, new org.apache.thrift.meta_data.FieldMetaData("descr", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.LOGO_URL, new org.apache.thrift.meta_data.FieldMetaData("logoURL", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING        , true)));
    tmpMap.put(_Fields.HOME_URL, new org.apache.thrift.meta_data.FieldMetaData("homeURL", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(Producer.class, metaDataMap);
  }

  public Producer() {
  }

  public Producer(
    long id,
    String name,
    String descr,
    ByteBuffer logoURL,
    String homeURL)
  {
    this();
    this.id = id;
    setIdIsSet(true);
    this.name = name;
    this.descr = descr;
    this.logoURL = logoURL;
    this.homeURL = homeURL;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public Producer(Producer other) {
    __isset_bitfield = other.__isset_bitfield;
    this.id = other.id;
    if (other.isSetName()) {
      this.name = other.name;
    }
    if (other.isSetDescr()) {
      this.descr = other.descr;
    }
    if (other.isSetLogoURL()) {
      this.logoURL = org.apache.thrift.TBaseHelper.copyBinary(other.logoURL);
;
    }
    if (other.isSetHomeURL()) {
      this.homeURL = other.homeURL;
    }
  }

  public Producer deepCopy() {
    return new Producer(this);
  }

  @Override
  public void clear() {
    setIdIsSet(false);
    this.id = 0;
    this.name = null;
    this.descr = null;
    this.logoURL = null;
    this.homeURL = null;
  }

  public long getId() {
    return this.id;
  }

  public Producer setId(long id) {
    this.id = id;
    setIdIsSet(true);
    return this;
  }

  public void unsetId() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __ID_ISSET_ID);
  }

  /** Returns true if field id is set (has been assigned a value) and false otherwise */
  public boolean isSetId() {
    return EncodingUtils.testBit(__isset_bitfield, __ID_ISSET_ID);
  }

  public void setIdIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __ID_ISSET_ID, value);
  }

  public String getName() {
    return this.name;
  }

  public Producer setName(String name) {
    this.name = name;
    return this;
  }

  public void unsetName() {
    this.name = null;
  }

  /** Returns true if field name is set (has been assigned a value) and false otherwise */
  public boolean isSetName() {
    return this.name != null;
  }

  public void setNameIsSet(boolean value) {
    if (!value) {
      this.name = null;
    }
  }

  public String getDescr() {
    return this.descr;
  }

  public Producer setDescr(String descr) {
    this.descr = descr;
    return this;
  }

  public void unsetDescr() {
    this.descr = null;
  }

  /** Returns true if field descr is set (has been assigned a value) and false otherwise */
  public boolean isSetDescr() {
    return this.descr != null;
  }

  public void setDescrIsSet(boolean value) {
    if (!value) {
      this.descr = null;
    }
  }

  public byte[] getLogoURL() {
    setLogoURL(org.apache.thrift.TBaseHelper.rightSize(logoURL));
    return logoURL == null ? null : logoURL.array();
  }

  public ByteBuffer bufferForLogoURL() {
    return logoURL;
  }

  public Producer setLogoURL(byte[] logoURL) {
    setLogoURL(logoURL == null ? (ByteBuffer)null : ByteBuffer.wrap(logoURL));
    return this;
  }

  public Producer setLogoURL(ByteBuffer logoURL) {
    this.logoURL = logoURL;
    return this;
  }

  public void unsetLogoURL() {
    this.logoURL = null;
  }

  /** Returns true if field logoURL is set (has been assigned a value) and false otherwise */
  public boolean isSetLogoURL() {
    return this.logoURL != null;
  }

  public void setLogoURLIsSet(boolean value) {
    if (!value) {
      this.logoURL = null;
    }
  }

  public String getHomeURL() {
    return this.homeURL;
  }

  public Producer setHomeURL(String homeURL) {
    this.homeURL = homeURL;
    return this;
  }

  public void unsetHomeURL() {
    this.homeURL = null;
  }

  /** Returns true if field homeURL is set (has been assigned a value) and false otherwise */
  public boolean isSetHomeURL() {
    return this.homeURL != null;
  }

  public void setHomeURLIsSet(boolean value) {
    if (!value) {
      this.homeURL = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case ID:
      if (value == null) {
        unsetId();
      } else {
        setId((Long)value);
      }
      break;

    case NAME:
      if (value == null) {
        unsetName();
      } else {
        setName((String)value);
      }
      break;

    case DESCR:
      if (value == null) {
        unsetDescr();
      } else {
        setDescr((String)value);
      }
      break;

    case LOGO_URL:
      if (value == null) {
        unsetLogoURL();
      } else {
        setLogoURL((ByteBuffer)value);
      }
      break;

    case HOME_URL:
      if (value == null) {
        unsetHomeURL();
      } else {
        setHomeURL((String)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case ID:
      return Long.valueOf(getId());

    case NAME:
      return getName();

    case DESCR:
      return getDescr();

    case LOGO_URL:
      return getLogoURL();

    case HOME_URL:
      return getHomeURL();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case ID:
      return isSetId();
    case NAME:
      return isSetName();
    case DESCR:
      return isSetDescr();
    case LOGO_URL:
      return isSetLogoURL();
    case HOME_URL:
      return isSetHomeURL();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof Producer)
      return this.equals((Producer)that);
    return false;
  }

  public boolean equals(Producer that) {
    if (that == null)
      return false;

    boolean this_present_id = true;
    boolean that_present_id = true;
    if (this_present_id || that_present_id) {
      if (!(this_present_id && that_present_id))
        return false;
      if (this.id != that.id)
        return false;
    }

    boolean this_present_name = true && this.isSetName();
    boolean that_present_name = true && that.isSetName();
    if (this_present_name || that_present_name) {
      if (!(this_present_name && that_present_name))
        return false;
      if (!this.name.equals(that.name))
        return false;
    }

    boolean this_present_descr = true && this.isSetDescr();
    boolean that_present_descr = true && that.isSetDescr();
    if (this_present_descr || that_present_descr) {
      if (!(this_present_descr && that_present_descr))
        return false;
      if (!this.descr.equals(that.descr))
        return false;
    }

    boolean this_present_logoURL = true && this.isSetLogoURL();
    boolean that_present_logoURL = true && that.isSetLogoURL();
    if (this_present_logoURL || that_present_logoURL) {
      if (!(this_present_logoURL && that_present_logoURL))
        return false;
      if (!this.logoURL.equals(that.logoURL))
        return false;
    }

    boolean this_present_homeURL = true && this.isSetHomeURL();
    boolean that_present_homeURL = true && that.isSetHomeURL();
    if (this_present_homeURL || that_present_homeURL) {
      if (!(this_present_homeURL && that_present_homeURL))
        return false;
      if (!this.homeURL.equals(that.homeURL))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public int compareTo(Producer other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetId()).compareTo(other.isSetId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.id, other.id);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetName()).compareTo(other.isSetName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.name, other.name);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetDescr()).compareTo(other.isSetDescr());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetDescr()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.descr, other.descr);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetLogoURL()).compareTo(other.isSetLogoURL());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetLogoURL()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.logoURL, other.logoURL);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetHomeURL()).compareTo(other.isSetHomeURL());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetHomeURL()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.homeURL, other.homeURL);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Producer(");
    boolean first = true;

    sb.append("id:");
    sb.append(this.id);
    first = false;
    if (!first) sb.append(", ");
    sb.append("name:");
    if (this.name == null) {
      sb.append("null");
    } else {
      sb.append(this.name);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("descr:");
    if (this.descr == null) {
      sb.append("null");
    } else {
      sb.append(this.descr);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("logoURL:");
    if (this.logoURL == null) {
      sb.append("null");
    } else {
      org.apache.thrift.TBaseHelper.toString(this.logoURL, sb);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("homeURL:");
    if (this.homeURL == null) {
      sb.append("null");
    } else {
      sb.append(this.homeURL);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class ProducerStandardSchemeFactory implements SchemeFactory {
    public ProducerStandardScheme getScheme() {
      return new ProducerStandardScheme();
    }
  }

  private static class ProducerStandardScheme extends StandardScheme<Producer> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, Producer struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.id = iprot.readI64();
              struct.setIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.name = iprot.readString();
              struct.setNameIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // DESCR
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.descr = iprot.readString();
              struct.setDescrIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // LOGO_URL
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.logoURL = iprot.readBinary();
              struct.setLogoURLIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // HOME_URL
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.homeURL = iprot.readString();
              struct.setHomeURLIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, Producer struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(ID_FIELD_DESC);
      oprot.writeI64(struct.id);
      oprot.writeFieldEnd();
      if (struct.name != null) {
        oprot.writeFieldBegin(NAME_FIELD_DESC);
        oprot.writeString(struct.name);
        oprot.writeFieldEnd();
      }
      if (struct.descr != null) {
        oprot.writeFieldBegin(DESCR_FIELD_DESC);
        oprot.writeString(struct.descr);
        oprot.writeFieldEnd();
      }
      if (struct.logoURL != null) {
        oprot.writeFieldBegin(LOGO_URL_FIELD_DESC);
        oprot.writeBinary(struct.logoURL);
        oprot.writeFieldEnd();
      }
      if (struct.homeURL != null) {
        oprot.writeFieldBegin(HOME_URL_FIELD_DESC);
        oprot.writeString(struct.homeURL);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class ProducerTupleSchemeFactory implements SchemeFactory {
    public ProducerTupleScheme getScheme() {
      return new ProducerTupleScheme();
    }
  }

  private static class ProducerTupleScheme extends TupleScheme<Producer> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, Producer struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetId()) {
        optionals.set(0);
      }
      if (struct.isSetName()) {
        optionals.set(1);
      }
      if (struct.isSetDescr()) {
        optionals.set(2);
      }
      if (struct.isSetLogoURL()) {
        optionals.set(3);
      }
      if (struct.isSetHomeURL()) {
        optionals.set(4);
      }
      oprot.writeBitSet(optionals, 5);
      if (struct.isSetId()) {
        oprot.writeI64(struct.id);
      }
      if (struct.isSetName()) {
        oprot.writeString(struct.name);
      }
      if (struct.isSetDescr()) {
        oprot.writeString(struct.descr);
      }
      if (struct.isSetLogoURL()) {
        oprot.writeBinary(struct.logoURL);
      }
      if (struct.isSetHomeURL()) {
        oprot.writeString(struct.homeURL);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, Producer struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(5);
      if (incoming.get(0)) {
        struct.id = iprot.readI64();
        struct.setIdIsSet(true);
      }
      if (incoming.get(1)) {
        struct.name = iprot.readString();
        struct.setNameIsSet(true);
      }
      if (incoming.get(2)) {
        struct.descr = iprot.readString();
        struct.setDescrIsSet(true);
      }
      if (incoming.get(3)) {
        struct.logoURL = iprot.readBinary();
        struct.setLogoURLIsSet(true);
      }
      if (incoming.get(4)) {
        struct.homeURL = iprot.readString();
        struct.setHomeURLIsSet(true);
      }
    }
  }

}
