/**
 * Autogenerated by Thrift Compiler (1.0.0-dev)
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
import javax.annotation.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked"})
@Generated(value = "Autogenerated by Thrift Compiler (1.0.0-dev)", date = "2014-2-4")
public class GroupUpdates implements org.apache.thrift.TBase<GroupUpdates, GroupUpdates._Fields>, java.io.Serializable, Cloneable, Comparable<GroupUpdates> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("GroupUpdates");

  private static final org.apache.thrift.protocol.TField GROUP_COUNTERS_FIELD_DESC = new org.apache.thrift.protocol.TField("groupCounters", org.apache.thrift.protocol.TType.MAP, (short)1);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new GroupUpdatesStandardSchemeFactory());
    schemes.put(TupleScheme.class, new GroupUpdatesTupleSchemeFactory());
  }

  public Map<Long,RubricCounter> groupCounters; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    GROUP_COUNTERS((short)1, "groupCounters");

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
        case 1: // GROUP_COUNTERS
          return GROUP_COUNTERS;
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
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.GROUP_COUNTERS, new org.apache.thrift.meta_data.FieldMetaData("groupCounters", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.MapMetaData(org.apache.thrift.protocol.TType.MAP, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64), 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, RubricCounter.class))));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(GroupUpdates.class, metaDataMap);
  }

  public GroupUpdates() {
  }

  public GroupUpdates(
    Map<Long,RubricCounter> groupCounters)
  {
    this();
    this.groupCounters = groupCounters;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public GroupUpdates(GroupUpdates other) {
    if (other.isSetGroupCounters()) {
      Map<Long,RubricCounter> __this__groupCounters = new HashMap<Long,RubricCounter>(other.groupCounters.size());
      for (Map.Entry<Long, RubricCounter> other_element : other.groupCounters.entrySet()) {

        Long other_element_key = other_element.getKey();
        RubricCounter other_element_value = other_element.getValue();

        Long __this__groupCounters_copy_key = other_element_key;

        RubricCounter __this__groupCounters_copy_value = new RubricCounter(other_element_value);

        __this__groupCounters.put(__this__groupCounters_copy_key, __this__groupCounters_copy_value);
      }
      this.groupCounters = __this__groupCounters;
    }
  }

  public GroupUpdates deepCopy() {
    return new GroupUpdates(this);
  }

  @Override
  public void clear() {
    this.groupCounters = null;
  }

  public int getGroupCountersSize() {
    return (this.groupCounters == null) ? 0 : this.groupCounters.size();
  }

  public void putToGroupCounters(long key, RubricCounter val) {
    if (this.groupCounters == null) {
      this.groupCounters = new HashMap<Long,RubricCounter>();
    }
    this.groupCounters.put(key, val);
  }

  public Map<Long,RubricCounter> getGroupCounters() {
    return this.groupCounters;
  }

  public GroupUpdates setGroupCounters(Map<Long,RubricCounter> groupCounters) {
    this.groupCounters = groupCounters;
    return this;
  }

  public void unsetGroupCounters() {
    this.groupCounters = null;
  }

  /** Returns true if field groupCounters is set (has been assigned a value) and false otherwise */
  public boolean isSetGroupCounters() {
    return this.groupCounters != null;
  }

  public void setGroupCountersIsSet(boolean value) {
    if (!value) {
      this.groupCounters = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case GROUP_COUNTERS:
      if (value == null) {
        unsetGroupCounters();
      } else {
        setGroupCounters((Map<Long,RubricCounter>)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case GROUP_COUNTERS:
      return getGroupCounters();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case GROUP_COUNTERS:
      return isSetGroupCounters();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof GroupUpdates)
      return this.equals((GroupUpdates)that);
    return false;
  }

  public boolean equals(GroupUpdates that) {
    if (that == null)
      return false;

    boolean this_present_groupCounters = true && this.isSetGroupCounters();
    boolean that_present_groupCounters = true && that.isSetGroupCounters();
    if (this_present_groupCounters || that_present_groupCounters) {
      if (!(this_present_groupCounters && that_present_groupCounters))
        return false;
      if (!this.groupCounters.equals(that.groupCounters))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_groupCounters = true && (isSetGroupCounters());
    list.add(present_groupCounters);
    if (present_groupCounters)
      list.add(groupCounters);

    return list.hashCode();
  }

  @Override
  public int compareTo(GroupUpdates other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetGroupCounters()).compareTo(other.isSetGroupCounters());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetGroupCounters()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.groupCounters, other.groupCounters);
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
    StringBuilder sb = new StringBuilder("GroupUpdates(");
    boolean first = true;

    sb.append("groupCounters:");
    if (this.groupCounters == null) {
      sb.append("null");
    } else {
      sb.append(this.groupCounters);
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
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class GroupUpdatesStandardSchemeFactory implements SchemeFactory {
    public GroupUpdatesStandardScheme getScheme() {
      return new GroupUpdatesStandardScheme();
    }
  }

  private static class GroupUpdatesStandardScheme extends StandardScheme<GroupUpdates> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, GroupUpdates struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // GROUP_COUNTERS
            if (schemeField.type == org.apache.thrift.protocol.TType.MAP) {
              {
                org.apache.thrift.protocol.TMap _map20 = iprot.readMapBegin();
                struct.groupCounters = new HashMap<Long,RubricCounter>(2*_map20.size);
                for (int _i21 = 0; _i21 < _map20.size; ++_i21)
                {
                  long _key22;
                  RubricCounter _val23;
                  _key22 = iprot.readI64();
                  _val23 = new RubricCounter();
                  _val23.read(iprot);
                  struct.groupCounters.put(_key22, _val23);
                }
                iprot.readMapEnd();
              }
              struct.setGroupCountersIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, GroupUpdates struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.groupCounters != null) {
        oprot.writeFieldBegin(GROUP_COUNTERS_FIELD_DESC);
        {
          oprot.writeMapBegin(new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.I64, org.apache.thrift.protocol.TType.STRUCT, struct.groupCounters.size()));
          for (Map.Entry<Long, RubricCounter> _iter24 : struct.groupCounters.entrySet())
          {
            oprot.writeI64(_iter24.getKey());
            _iter24.getValue().write(oprot);
          }
          oprot.writeMapEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class GroupUpdatesTupleSchemeFactory implements SchemeFactory {
    public GroupUpdatesTupleScheme getScheme() {
      return new GroupUpdatesTupleScheme();
    }
  }

  private static class GroupUpdatesTupleScheme extends TupleScheme<GroupUpdates> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, GroupUpdates struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetGroupCounters()) {
        optionals.set(0);
      }
      oprot.writeBitSet(optionals, 1);
      if (struct.isSetGroupCounters()) {
        {
          oprot.writeI32(struct.groupCounters.size());
          for (Map.Entry<Long, RubricCounter> _iter25 : struct.groupCounters.entrySet())
          {
            oprot.writeI64(_iter25.getKey());
            _iter25.getValue().write(oprot);
          }
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, GroupUpdates struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(1);
      if (incoming.get(0)) {
        {
          org.apache.thrift.protocol.TMap _map26 = new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.I64, org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
          struct.groupCounters = new HashMap<Long,RubricCounter>(2*_map26.size);
          for (int _i27 = 0; _i27 < _map26.size; ++_i27)
          {
            long _key28;
            RubricCounter _val29;
            _key28 = iprot.readI64();
            _val29 = new RubricCounter();
            _val29.read(iprot);
            struct.groupCounters.put(_key28, _val29);
          }
        }
        struct.setGroupCountersIsSet(true);
      }
    }
  }

}

