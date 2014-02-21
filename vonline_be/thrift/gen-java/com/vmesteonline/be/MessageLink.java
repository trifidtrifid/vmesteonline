/**
 * Autogenerated by Thrift Compiler (0.9.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.vmesteonline.be;

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

public class MessageLink implements org.apache.thrift.TBase<MessageLink, MessageLink._Fields>, java.io.Serializable, Cloneable, Comparable<MessageLink> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("MessageLink");

  private static final org.apache.thrift.protocol.TField LINK_TYPE_FIELD_DESC = new org.apache.thrift.protocol.TField("linkType", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField LINKED_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("linkedId", org.apache.thrift.protocol.TType.I64, (short)2);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new MessageLinkStandardSchemeFactory());
    schemes.put(TupleScheme.class, new MessageLinkTupleSchemeFactory());
  }

  /**
   * 
   * @see MessageType
   */
  public MessageType linkType; // required
  public long linkedId; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    /**
     * 
     * @see MessageType
     */
    LINK_TYPE((short)1, "linkType"),
    LINKED_ID((short)2, "linkedId");

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
        case 1: // LINK_TYPE
          return LINK_TYPE;
        case 2: // LINKED_ID
          return LINKED_ID;
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
  private static final int __LINKEDID_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.LINK_TYPE, new org.apache.thrift.meta_data.FieldMetaData("linkType", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, MessageType.class)));
    tmpMap.put(_Fields.LINKED_ID, new org.apache.thrift.meta_data.FieldMetaData("linkedId", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(MessageLink.class, metaDataMap);
  }

  public MessageLink() {
  }

  public MessageLink(
    MessageType linkType,
    long linkedId)
  {
    this();
    this.linkType = linkType;
    this.linkedId = linkedId;
    setLinkedIdIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public MessageLink(MessageLink other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetLinkType()) {
      this.linkType = other.linkType;
    }
    this.linkedId = other.linkedId;
  }

  public MessageLink deepCopy() {
    return new MessageLink(this);
  }

  @Override
  public void clear() {
    this.linkType = null;
    setLinkedIdIsSet(false);
    this.linkedId = 0;
  }

  /**
   * 
   * @see MessageType
   */
  public MessageType getLinkType() {
    return this.linkType;
  }

  /**
   * 
   * @see MessageType
   */
  public MessageLink setLinkType(MessageType linkType) {
    this.linkType = linkType;
    return this;
  }

  public void unsetLinkType() {
    this.linkType = null;
  }

  /** Returns true if field linkType is set (has been assigned a value) and false otherwise */
  public boolean isSetLinkType() {
    return this.linkType != null;
  }

  public void setLinkTypeIsSet(boolean value) {
    if (!value) {
      this.linkType = null;
    }
  }

  public long getLinkedId() {
    return this.linkedId;
  }

  public MessageLink setLinkedId(long linkedId) {
    this.linkedId = linkedId;
    setLinkedIdIsSet(true);
    return this;
  }

  public void unsetLinkedId() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __LINKEDID_ISSET_ID);
  }

  /** Returns true if field linkedId is set (has been assigned a value) and false otherwise */
  public boolean isSetLinkedId() {
    return EncodingUtils.testBit(__isset_bitfield, __LINKEDID_ISSET_ID);
  }

  public void setLinkedIdIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __LINKEDID_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case LINK_TYPE:
      if (value == null) {
        unsetLinkType();
      } else {
        setLinkType((MessageType)value);
      }
      break;

    case LINKED_ID:
      if (value == null) {
        unsetLinkedId();
      } else {
        setLinkedId((Long)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case LINK_TYPE:
      return getLinkType();

    case LINKED_ID:
      return Long.valueOf(getLinkedId());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case LINK_TYPE:
      return isSetLinkType();
    case LINKED_ID:
      return isSetLinkedId();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof MessageLink)
      return this.equals((MessageLink)that);
    return false;
  }

  public boolean equals(MessageLink that) {
    if (that == null)
      return false;

    boolean this_present_linkType = true && this.isSetLinkType();
    boolean that_present_linkType = true && that.isSetLinkType();
    if (this_present_linkType || that_present_linkType) {
      if (!(this_present_linkType && that_present_linkType))
        return false;
      if (!this.linkType.equals(that.linkType))
        return false;
    }

    boolean this_present_linkedId = true;
    boolean that_present_linkedId = true;
    if (this_present_linkedId || that_present_linkedId) {
      if (!(this_present_linkedId && that_present_linkedId))
        return false;
      if (this.linkedId != that.linkedId)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public int compareTo(MessageLink other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetLinkType()).compareTo(other.isSetLinkType());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetLinkType()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.linkType, other.linkType);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetLinkedId()).compareTo(other.isSetLinkedId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetLinkedId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.linkedId, other.linkedId);
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
    StringBuilder sb = new StringBuilder("MessageLink(");
    boolean first = true;

    sb.append("linkType:");
    if (this.linkType == null) {
      sb.append("null");
    } else {
      sb.append(this.linkType);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("linkedId:");
    sb.append(this.linkedId);
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

  private static class MessageLinkStandardSchemeFactory implements SchemeFactory {
    public MessageLinkStandardScheme getScheme() {
      return new MessageLinkStandardScheme();
    }
  }

  private static class MessageLinkStandardScheme extends StandardScheme<MessageLink> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, MessageLink struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // LINK_TYPE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.linkType = MessageType.findByValue(iprot.readI32());
              struct.setLinkTypeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // LINKED_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.linkedId = iprot.readI64();
              struct.setLinkedIdIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, MessageLink struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.linkType != null) {
        oprot.writeFieldBegin(LINK_TYPE_FIELD_DESC);
        oprot.writeI32(struct.linkType.getValue());
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(LINKED_ID_FIELD_DESC);
      oprot.writeI64(struct.linkedId);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class MessageLinkTupleSchemeFactory implements SchemeFactory {
    public MessageLinkTupleScheme getScheme() {
      return new MessageLinkTupleScheme();
    }
  }

  private static class MessageLinkTupleScheme extends TupleScheme<MessageLink> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, MessageLink struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetLinkType()) {
        optionals.set(0);
      }
      if (struct.isSetLinkedId()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetLinkType()) {
        oprot.writeI32(struct.linkType.getValue());
      }
      if (struct.isSetLinkedId()) {
        oprot.writeI64(struct.linkedId);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, MessageLink struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        struct.linkType = MessageType.findByValue(iprot.readI32());
        struct.setLinkTypeIsSet(true);
      }
      if (incoming.get(1)) {
        struct.linkedId = iprot.readI64();
        struct.setLinkedIdIsSet(true);
      }
    }
  }

}

