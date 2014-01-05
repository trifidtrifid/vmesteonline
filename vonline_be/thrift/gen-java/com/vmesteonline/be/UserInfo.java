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

public class UserInfo implements org.apache.thrift.TBase<UserInfo, UserInfo._Fields>, java.io.Serializable, Cloneable, Comparable<UserInfo> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("UserInfo");

  private static final org.apache.thrift.protocol.TField NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("name", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField SECOND_NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("secondName", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField DOB_FIELD_DESC = new org.apache.thrift.protocol.TField("dob", org.apache.thrift.protocol.TType.I32, (short)3);
  private static final org.apache.thrift.protocol.TField SEX_FIELD_DESC = new org.apache.thrift.protocol.TField("sex", org.apache.thrift.protocol.TType.BOOL, (short)4);
  private static final org.apache.thrift.protocol.TField INTRESTS_FIELD_DESC = new org.apache.thrift.protocol.TField("intrests", org.apache.thrift.protocol.TType.STRING, (short)5);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new UserInfoStandardSchemeFactory());
    schemes.put(TupleScheme.class, new UserInfoTupleSchemeFactory());
  }

  public String name; // required
  public String secondName; // required
  public int dob; // required
  public boolean sex; // optional
  public String intrests; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    NAME((short)1, "name"),
    SECOND_NAME((short)2, "secondName"),
    DOB((short)3, "dob"),
    SEX((short)4, "sex"),
    INTRESTS((short)5, "intrests");

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
        case 1: // NAME
          return NAME;
        case 2: // SECOND_NAME
          return SECOND_NAME;
        case 3: // DOB
          return DOB;
        case 4: // SEX
          return SEX;
        case 5: // INTRESTS
          return INTRESTS;
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
  private static final int __DOB_ISSET_ID = 0;
  private static final int __SEX_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  private _Fields optionals[] = {_Fields.SEX,_Fields.INTRESTS};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.NAME, new org.apache.thrift.meta_data.FieldMetaData("name", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.SECOND_NAME, new org.apache.thrift.meta_data.FieldMetaData("secondName", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.DOB, new org.apache.thrift.meta_data.FieldMetaData("dob", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.SEX, new org.apache.thrift.meta_data.FieldMetaData("sex", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    tmpMap.put(_Fields.INTRESTS, new org.apache.thrift.meta_data.FieldMetaData("intrests", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(UserInfo.class, metaDataMap);
  }

  public UserInfo() {
  }

  public UserInfo(
    String name,
    String secondName,
    int dob)
  {
    this();
    this.name = name;
    this.secondName = secondName;
    this.dob = dob;
    setDobIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public UserInfo(UserInfo other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetName()) {
      this.name = other.name;
    }
    if (other.isSetSecondName()) {
      this.secondName = other.secondName;
    }
    this.dob = other.dob;
    this.sex = other.sex;
    if (other.isSetIntrests()) {
      this.intrests = other.intrests;
    }
  }

  public UserInfo deepCopy() {
    return new UserInfo(this);
  }

  @Override
  public void clear() {
    this.name = null;
    this.secondName = null;
    setDobIsSet(false);
    this.dob = 0;
    setSexIsSet(false);
    this.sex = false;
    this.intrests = null;
  }

  public String getName() {
    return this.name;
  }

  public UserInfo setName(String name) {
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

  public String getSecondName() {
    return this.secondName;
  }

  public UserInfo setSecondName(String secondName) {
    this.secondName = secondName;
    return this;
  }

  public void unsetSecondName() {
    this.secondName = null;
  }

  /** Returns true if field secondName is set (has been assigned a value) and false otherwise */
  public boolean isSetSecondName() {
    return this.secondName != null;
  }

  public void setSecondNameIsSet(boolean value) {
    if (!value) {
      this.secondName = null;
    }
  }

  public int getDob() {
    return this.dob;
  }

  public UserInfo setDob(int dob) {
    this.dob = dob;
    setDobIsSet(true);
    return this;
  }

  public void unsetDob() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __DOB_ISSET_ID);
  }

  /** Returns true if field dob is set (has been assigned a value) and false otherwise */
  public boolean isSetDob() {
    return EncodingUtils.testBit(__isset_bitfield, __DOB_ISSET_ID);
  }

  public void setDobIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __DOB_ISSET_ID, value);
  }

  public boolean isSex() {
    return this.sex;
  }

  public UserInfo setSex(boolean sex) {
    this.sex = sex;
    setSexIsSet(true);
    return this;
  }

  public void unsetSex() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __SEX_ISSET_ID);
  }

  /** Returns true if field sex is set (has been assigned a value) and false otherwise */
  public boolean isSetSex() {
    return EncodingUtils.testBit(__isset_bitfield, __SEX_ISSET_ID);
  }

  public void setSexIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __SEX_ISSET_ID, value);
  }

  public String getIntrests() {
    return this.intrests;
  }

  public UserInfo setIntrests(String intrests) {
    this.intrests = intrests;
    return this;
  }

  public void unsetIntrests() {
    this.intrests = null;
  }

  /** Returns true if field intrests is set (has been assigned a value) and false otherwise */
  public boolean isSetIntrests() {
    return this.intrests != null;
  }

  public void setIntrestsIsSet(boolean value) {
    if (!value) {
      this.intrests = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case NAME:
      if (value == null) {
        unsetName();
      } else {
        setName((String)value);
      }
      break;

    case SECOND_NAME:
      if (value == null) {
        unsetSecondName();
      } else {
        setSecondName((String)value);
      }
      break;

    case DOB:
      if (value == null) {
        unsetDob();
      } else {
        setDob((Integer)value);
      }
      break;

    case SEX:
      if (value == null) {
        unsetSex();
      } else {
        setSex((Boolean)value);
      }
      break;

    case INTRESTS:
      if (value == null) {
        unsetIntrests();
      } else {
        setIntrests((String)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case NAME:
      return getName();

    case SECOND_NAME:
      return getSecondName();

    case DOB:
      return Integer.valueOf(getDob());

    case SEX:
      return Boolean.valueOf(isSex());

    case INTRESTS:
      return getIntrests();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case NAME:
      return isSetName();
    case SECOND_NAME:
      return isSetSecondName();
    case DOB:
      return isSetDob();
    case SEX:
      return isSetSex();
    case INTRESTS:
      return isSetIntrests();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof UserInfo)
      return this.equals((UserInfo)that);
    return false;
  }

  public boolean equals(UserInfo that) {
    if (that == null)
      return false;

    boolean this_present_name = true && this.isSetName();
    boolean that_present_name = true && that.isSetName();
    if (this_present_name || that_present_name) {
      if (!(this_present_name && that_present_name))
        return false;
      if (!this.name.equals(that.name))
        return false;
    }

    boolean this_present_secondName = true && this.isSetSecondName();
    boolean that_present_secondName = true && that.isSetSecondName();
    if (this_present_secondName || that_present_secondName) {
      if (!(this_present_secondName && that_present_secondName))
        return false;
      if (!this.secondName.equals(that.secondName))
        return false;
    }

    boolean this_present_dob = true;
    boolean that_present_dob = true;
    if (this_present_dob || that_present_dob) {
      if (!(this_present_dob && that_present_dob))
        return false;
      if (this.dob != that.dob)
        return false;
    }

    boolean this_present_sex = true && this.isSetSex();
    boolean that_present_sex = true && that.isSetSex();
    if (this_present_sex || that_present_sex) {
      if (!(this_present_sex && that_present_sex))
        return false;
      if (this.sex != that.sex)
        return false;
    }

    boolean this_present_intrests = true && this.isSetIntrests();
    boolean that_present_intrests = true && that.isSetIntrests();
    if (this_present_intrests || that_present_intrests) {
      if (!(this_present_intrests && that_present_intrests))
        return false;
      if (!this.intrests.equals(that.intrests))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public int compareTo(UserInfo other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

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
    lastComparison = Boolean.valueOf(isSetSecondName()).compareTo(other.isSetSecondName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSecondName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.secondName, other.secondName);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetDob()).compareTo(other.isSetDob());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetDob()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.dob, other.dob);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetSex()).compareTo(other.isSetSex());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSex()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.sex, other.sex);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetIntrests()).compareTo(other.isSetIntrests());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIntrests()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.intrests, other.intrests);
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
    StringBuilder sb = new StringBuilder("UserInfo(");
    boolean first = true;

    sb.append("name:");
    if (this.name == null) {
      sb.append("null");
    } else {
      sb.append(this.name);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("secondName:");
    if (this.secondName == null) {
      sb.append("null");
    } else {
      sb.append(this.secondName);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("dob:");
    sb.append(this.dob);
    first = false;
    if (isSetSex()) {
      if (!first) sb.append(", ");
      sb.append("sex:");
      sb.append(this.sex);
      first = false;
    }
    if (isSetIntrests()) {
      if (!first) sb.append(", ");
      sb.append("intrests:");
      if (this.intrests == null) {
        sb.append("null");
      } else {
        sb.append(this.intrests);
      }
      first = false;
    }
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

  private static class UserInfoStandardSchemeFactory implements SchemeFactory {
    public UserInfoStandardScheme getScheme() {
      return new UserInfoStandardScheme();
    }
  }

  private static class UserInfoStandardScheme extends StandardScheme<UserInfo> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, UserInfo struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.name = iprot.readString();
              struct.setNameIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // SECOND_NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.secondName = iprot.readString();
              struct.setSecondNameIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // DOB
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.dob = iprot.readI32();
              struct.setDobIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // SEX
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct.sex = iprot.readBool();
              struct.setSexIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // INTRESTS
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.intrests = iprot.readString();
              struct.setIntrestsIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, UserInfo struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.name != null) {
        oprot.writeFieldBegin(NAME_FIELD_DESC);
        oprot.writeString(struct.name);
        oprot.writeFieldEnd();
      }
      if (struct.secondName != null) {
        oprot.writeFieldBegin(SECOND_NAME_FIELD_DESC);
        oprot.writeString(struct.secondName);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(DOB_FIELD_DESC);
      oprot.writeI32(struct.dob);
      oprot.writeFieldEnd();
      if (struct.isSetSex()) {
        oprot.writeFieldBegin(SEX_FIELD_DESC);
        oprot.writeBool(struct.sex);
        oprot.writeFieldEnd();
      }
      if (struct.intrests != null) {
        if (struct.isSetIntrests()) {
          oprot.writeFieldBegin(INTRESTS_FIELD_DESC);
          oprot.writeString(struct.intrests);
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class UserInfoTupleSchemeFactory implements SchemeFactory {
    public UserInfoTupleScheme getScheme() {
      return new UserInfoTupleScheme();
    }
  }

  private static class UserInfoTupleScheme extends TupleScheme<UserInfo> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, UserInfo struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetName()) {
        optionals.set(0);
      }
      if (struct.isSetSecondName()) {
        optionals.set(1);
      }
      if (struct.isSetDob()) {
        optionals.set(2);
      }
      if (struct.isSetSex()) {
        optionals.set(3);
      }
      if (struct.isSetIntrests()) {
        optionals.set(4);
      }
      oprot.writeBitSet(optionals, 5);
      if (struct.isSetName()) {
        oprot.writeString(struct.name);
      }
      if (struct.isSetSecondName()) {
        oprot.writeString(struct.secondName);
      }
      if (struct.isSetDob()) {
        oprot.writeI32(struct.dob);
      }
      if (struct.isSetSex()) {
        oprot.writeBool(struct.sex);
      }
      if (struct.isSetIntrests()) {
        oprot.writeString(struct.intrests);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, UserInfo struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(5);
      if (incoming.get(0)) {
        struct.name = iprot.readString();
        struct.setNameIsSet(true);
      }
      if (incoming.get(1)) {
        struct.secondName = iprot.readString();
        struct.setSecondNameIsSet(true);
      }
      if (incoming.get(2)) {
        struct.dob = iprot.readI32();
        struct.setDobIsSet(true);
      }
      if (incoming.get(3)) {
        struct.sex = iprot.readBool();
        struct.setSexIsSet(true);
      }
      if (incoming.get(4)) {
        struct.intrests = iprot.readString();
        struct.setIntrestsIsSet(true);
      }
    }
  }

}

