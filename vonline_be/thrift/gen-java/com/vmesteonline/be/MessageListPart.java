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

public class MessageListPart implements org.apache.thrift.TBase<MessageListPart, MessageListPart._Fields>, java.io.Serializable, Cloneable, Comparable<MessageListPart> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("MessageListPart");

  private static final org.apache.thrift.protocol.TField MESSAGES_FIELD_DESC = new org.apache.thrift.protocol.TField("messages", org.apache.thrift.protocol.TType.LIST, (short)1);
  private static final org.apache.thrift.protocol.TField TOTAL_SIZE_FIELD_DESC = new org.apache.thrift.protocol.TField("totalSize", org.apache.thrift.protocol.TType.I32, (short)2);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new MessageListPartStandardSchemeFactory());
    schemes.put(TupleScheme.class, new MessageListPartTupleSchemeFactory());
  }

  public List<Message> messages; // required
  public int totalSize; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    MESSAGES((short)1, "messages"),
    TOTAL_SIZE((short)2, "totalSize");

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
        case 1: // MESSAGES
          return MESSAGES;
        case 2: // TOTAL_SIZE
          return TOTAL_SIZE;
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
  private static final int __TOTALSIZE_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.MESSAGES, new org.apache.thrift.meta_data.FieldMetaData("messages", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, Message.class))));
    tmpMap.put(_Fields.TOTAL_SIZE, new org.apache.thrift.meta_data.FieldMetaData("totalSize", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(MessageListPart.class, metaDataMap);
  }

  public MessageListPart() {
  }

  public MessageListPart(
    List<Message> messages,
    int totalSize)
  {
    this();
    this.messages = messages;
    this.totalSize = totalSize;
    setTotalSizeIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public MessageListPart(MessageListPart other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetMessages()) {
      List<Message> __this__messages = new ArrayList<Message>(other.messages.size());
      for (Message other_element : other.messages) {
        __this__messages.add(new Message(other_element));
      }
      this.messages = __this__messages;
    }
    this.totalSize = other.totalSize;
  }

  public MessageListPart deepCopy() {
    return new MessageListPart(this);
  }

  @Override
  public void clear() {
    this.messages = null;
    setTotalSizeIsSet(false);
    this.totalSize = 0;
  }

  public int getMessagesSize() {
    return (this.messages == null) ? 0 : this.messages.size();
  }

  public java.util.Iterator<Message> getMessagesIterator() {
    return (this.messages == null) ? null : this.messages.iterator();
  }

  public void addToMessages(Message elem) {
    if (this.messages == null) {
      this.messages = new ArrayList<Message>();
    }
    this.messages.add(elem);
  }

  public List<Message> getMessages() {
    return this.messages;
  }

  public MessageListPart setMessages(List<Message> messages) {
    this.messages = messages;
    return this;
  }

  public void unsetMessages() {
    this.messages = null;
  }

  /** Returns true if field messages is set (has been assigned a value) and false otherwise */
  public boolean isSetMessages() {
    return this.messages != null;
  }

  public void setMessagesIsSet(boolean value) {
    if (!value) {
      this.messages = null;
    }
  }

  public int getTotalSize() {
    return this.totalSize;
  }

  public MessageListPart setTotalSize(int totalSize) {
    this.totalSize = totalSize;
    setTotalSizeIsSet(true);
    return this;
  }

  public void unsetTotalSize() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __TOTALSIZE_ISSET_ID);
  }

  /** Returns true if field totalSize is set (has been assigned a value) and false otherwise */
  public boolean isSetTotalSize() {
    return EncodingUtils.testBit(__isset_bitfield, __TOTALSIZE_ISSET_ID);
  }

  public void setTotalSizeIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __TOTALSIZE_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case MESSAGES:
      if (value == null) {
        unsetMessages();
      } else {
        setMessages((List<Message>)value);
      }
      break;

    case TOTAL_SIZE:
      if (value == null) {
        unsetTotalSize();
      } else {
        setTotalSize((Integer)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case MESSAGES:
      return getMessages();

    case TOTAL_SIZE:
      return Integer.valueOf(getTotalSize());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case MESSAGES:
      return isSetMessages();
    case TOTAL_SIZE:
      return isSetTotalSize();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof MessageListPart)
      return this.equals((MessageListPart)that);
    return false;
  }

  public boolean equals(MessageListPart that) {
    if (that == null)
      return false;

    boolean this_present_messages = true && this.isSetMessages();
    boolean that_present_messages = true && that.isSetMessages();
    if (this_present_messages || that_present_messages) {
      if (!(this_present_messages && that_present_messages))
        return false;
      if (!this.messages.equals(that.messages))
        return false;
    }

    boolean this_present_totalSize = true;
    boolean that_present_totalSize = true;
    if (this_present_totalSize || that_present_totalSize) {
      if (!(this_present_totalSize && that_present_totalSize))
        return false;
      if (this.totalSize != that.totalSize)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public int compareTo(MessageListPart other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetMessages()).compareTo(other.isSetMessages());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMessages()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.messages, other.messages);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetTotalSize()).compareTo(other.isSetTotalSize());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTotalSize()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.totalSize, other.totalSize);
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
    StringBuilder sb = new StringBuilder("MessageListPart(");
    boolean first = true;

    sb.append("messages:");
    if (this.messages == null) {
      sb.append("null");
    } else {
      sb.append(this.messages);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("totalSize:");
    sb.append(this.totalSize);
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

  private static class MessageListPartStandardSchemeFactory implements SchemeFactory {
    public MessageListPartStandardScheme getScheme() {
      return new MessageListPartStandardScheme();
    }
  }

  private static class MessageListPartStandardScheme extends StandardScheme<MessageListPart> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, MessageListPart struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // MESSAGES
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list38 = iprot.readListBegin();
                struct.messages = new ArrayList<Message>(_list38.size);
                for (int _i39 = 0; _i39 < _list38.size; ++_i39)
                {
                  Message _elem40;
                  _elem40 = new Message();
                  _elem40.read(iprot);
                  struct.messages.add(_elem40);
                }
                iprot.readListEnd();
              }
              struct.setMessagesIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // TOTAL_SIZE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.totalSize = iprot.readI32();
              struct.setTotalSizeIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, MessageListPart struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.messages != null) {
        oprot.writeFieldBegin(MESSAGES_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.messages.size()));
          for (Message _iter41 : struct.messages)
          {
            _iter41.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(TOTAL_SIZE_FIELD_DESC);
      oprot.writeI32(struct.totalSize);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class MessageListPartTupleSchemeFactory implements SchemeFactory {
    public MessageListPartTupleScheme getScheme() {
      return new MessageListPartTupleScheme();
    }
  }

  private static class MessageListPartTupleScheme extends TupleScheme<MessageListPart> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, MessageListPart struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetMessages()) {
        optionals.set(0);
      }
      if (struct.isSetTotalSize()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetMessages()) {
        {
          oprot.writeI32(struct.messages.size());
          for (Message _iter42 : struct.messages)
          {
            _iter42.write(oprot);
          }
        }
      }
      if (struct.isSetTotalSize()) {
        oprot.writeI32(struct.totalSize);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, MessageListPart struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        {
          org.apache.thrift.protocol.TList _list43 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
          struct.messages = new ArrayList<Message>(_list43.size);
          for (int _i44 = 0; _i44 < _list43.size; ++_i44)
          {
            Message _elem45;
            _elem45 = new Message();
            _elem45.read(iprot);
            struct.messages.add(_elem45);
          }
        }
        struct.setMessagesIsSet(true);
      }
      if (incoming.get(1)) {
        struct.totalSize = iprot.readI32();
        struct.setTotalSizeIsSet(true);
      }
    }
  }

}

