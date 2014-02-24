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

public class ProductCategory implements org.apache.thrift.TBase<ProductCategory, ProductCategory._Fields>, java.io.Serializable, Cloneable, Comparable<ProductCategory> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("ProductCategory");

  private static final org.apache.thrift.protocol.TField ID_FIELD_DESC = new org.apache.thrift.protocol.TField("id", org.apache.thrift.protocol.TType.I64, (short)1);
  private static final org.apache.thrift.protocol.TField PARENT_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("parentId", org.apache.thrift.protocol.TType.I64, (short)2);
  private static final org.apache.thrift.protocol.TField NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("name", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField DESCR_FIELD_DESC = new org.apache.thrift.protocol.TField("descr", org.apache.thrift.protocol.TType.STRING, (short)4);
  private static final org.apache.thrift.protocol.TField LOGO_URLSET_FIELD_DESC = new org.apache.thrift.protocol.TField("logoURLset", org.apache.thrift.protocol.TType.LIST, (short)5);
  private static final org.apache.thrift.protocol.TField TOPIC_SET_FIELD_DESC = new org.apache.thrift.protocol.TField("topicSet", org.apache.thrift.protocol.TType.LIST, (short)6);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new ProductCategoryStandardSchemeFactory());
    schemes.put(TupleScheme.class, new ProductCategoryTupleSchemeFactory());
  }

  public long id; // required
  public long parentId; // required
  public String name; // required
  public String descr; // required
  public List<String> logoURLset; // required
  public List<Long> topicSet; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    ID((short)1, "id"),
    PARENT_ID((short)2, "parentId"),
    NAME((short)3, "name"),
    DESCR((short)4, "descr"),
    LOGO_URLSET((short)5, "logoURLset"),
    TOPIC_SET((short)6, "topicSet");

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
        case 2: // PARENT_ID
          return PARENT_ID;
        case 3: // NAME
          return NAME;
        case 4: // DESCR
          return DESCR;
        case 5: // LOGO_URLSET
          return LOGO_URLSET;
        case 6: // TOPIC_SET
          return TOPIC_SET;
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
  private static final int __PARENTID_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.ID, new org.apache.thrift.meta_data.FieldMetaData("id", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.PARENT_ID, new org.apache.thrift.meta_data.FieldMetaData("parentId", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.NAME, new org.apache.thrift.meta_data.FieldMetaData("name", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.DESCR, new org.apache.thrift.meta_data.FieldMetaData("descr", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.LOGO_URLSET, new org.apache.thrift.meta_data.FieldMetaData("logoURLset", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING))));
    tmpMap.put(_Fields.TOPIC_SET, new org.apache.thrift.meta_data.FieldMetaData("topicSet", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64))));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(ProductCategory.class, metaDataMap);
  }

  public ProductCategory() {
  }

  public ProductCategory(
    long id,
    long parentId,
    String name,
    String descr,
    List<String> logoURLset,
    List<Long> topicSet)
  {
    this();
    this.id = id;
    setIdIsSet(true);
    this.parentId = parentId;
    setParentIdIsSet(true);
    this.name = name;
    this.descr = descr;
    this.logoURLset = logoURLset;
    this.topicSet = topicSet;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public ProductCategory(ProductCategory other) {
    __isset_bitfield = other.__isset_bitfield;
    this.id = other.id;
    this.parentId = other.parentId;
    if (other.isSetName()) {
      this.name = other.name;
    }
    if (other.isSetDescr()) {
      this.descr = other.descr;
    }
    if (other.isSetLogoURLset()) {
      List<String> __this__logoURLset = new ArrayList<String>(other.logoURLset);
      this.logoURLset = __this__logoURLset;
    }
    if (other.isSetTopicSet()) {
      List<Long> __this__topicSet = new ArrayList<Long>(other.topicSet);
      this.topicSet = __this__topicSet;
    }
  }

  public ProductCategory deepCopy() {
    return new ProductCategory(this);
  }

  @Override
  public void clear() {
    setIdIsSet(false);
    this.id = 0;
    setParentIdIsSet(false);
    this.parentId = 0;
    this.name = null;
    this.descr = null;
    this.logoURLset = null;
    this.topicSet = null;
  }

  public long getId() {
    return this.id;
  }

  public ProductCategory setId(long id) {
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

  public long getParentId() {
    return this.parentId;
  }

  public ProductCategory setParentId(long parentId) {
    this.parentId = parentId;
    setParentIdIsSet(true);
    return this;
  }

  public void unsetParentId() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __PARENTID_ISSET_ID);
  }

  /** Returns true if field parentId is set (has been assigned a value) and false otherwise */
  public boolean isSetParentId() {
    return EncodingUtils.testBit(__isset_bitfield, __PARENTID_ISSET_ID);
  }

  public void setParentIdIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __PARENTID_ISSET_ID, value);
  }

  public String getName() {
    return this.name;
  }

  public ProductCategory setName(String name) {
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

  public ProductCategory setDescr(String descr) {
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

  public int getLogoURLsetSize() {
    return (this.logoURLset == null) ? 0 : this.logoURLset.size();
  }

  public java.util.Iterator<String> getLogoURLsetIterator() {
    return (this.logoURLset == null) ? null : this.logoURLset.iterator();
  }

  public void addToLogoURLset(String elem) {
    if (this.logoURLset == null) {
      this.logoURLset = new ArrayList<String>();
    }
    this.logoURLset.add(elem);
  }

  public List<String> getLogoURLset() {
    return this.logoURLset;
  }

  public ProductCategory setLogoURLset(List<String> logoURLset) {
    this.logoURLset = logoURLset;
    return this;
  }

  public void unsetLogoURLset() {
    this.logoURLset = null;
  }

  /** Returns true if field logoURLset is set (has been assigned a value) and false otherwise */
  public boolean isSetLogoURLset() {
    return this.logoURLset != null;
  }

  public void setLogoURLsetIsSet(boolean value) {
    if (!value) {
      this.logoURLset = null;
    }
  }

  public int getTopicSetSize() {
    return (this.topicSet == null) ? 0 : this.topicSet.size();
  }

  public java.util.Iterator<Long> getTopicSetIterator() {
    return (this.topicSet == null) ? null : this.topicSet.iterator();
  }

  public void addToTopicSet(long elem) {
    if (this.topicSet == null) {
      this.topicSet = new ArrayList<Long>();
    }
    this.topicSet.add(elem);
  }

  public List<Long> getTopicSet() {
    return this.topicSet;
  }

  public ProductCategory setTopicSet(List<Long> topicSet) {
    this.topicSet = topicSet;
    return this;
  }

  public void unsetTopicSet() {
    this.topicSet = null;
  }

  /** Returns true if field topicSet is set (has been assigned a value) and false otherwise */
  public boolean isSetTopicSet() {
    return this.topicSet != null;
  }

  public void setTopicSetIsSet(boolean value) {
    if (!value) {
      this.topicSet = null;
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

    case PARENT_ID:
      if (value == null) {
        unsetParentId();
      } else {
        setParentId((Long)value);
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

    case LOGO_URLSET:
      if (value == null) {
        unsetLogoURLset();
      } else {
        setLogoURLset((List<String>)value);
      }
      break;

    case TOPIC_SET:
      if (value == null) {
        unsetTopicSet();
      } else {
        setTopicSet((List<Long>)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case ID:
      return Long.valueOf(getId());

    case PARENT_ID:
      return Long.valueOf(getParentId());

    case NAME:
      return getName();

    case DESCR:
      return getDescr();

    case LOGO_URLSET:
      return getLogoURLset();

    case TOPIC_SET:
      return getTopicSet();

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
    case PARENT_ID:
      return isSetParentId();
    case NAME:
      return isSetName();
    case DESCR:
      return isSetDescr();
    case LOGO_URLSET:
      return isSetLogoURLset();
    case TOPIC_SET:
      return isSetTopicSet();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof ProductCategory)
      return this.equals((ProductCategory)that);
    return false;
  }

  public boolean equals(ProductCategory that) {
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

    boolean this_present_parentId = true;
    boolean that_present_parentId = true;
    if (this_present_parentId || that_present_parentId) {
      if (!(this_present_parentId && that_present_parentId))
        return false;
      if (this.parentId != that.parentId)
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

    boolean this_present_logoURLset = true && this.isSetLogoURLset();
    boolean that_present_logoURLset = true && that.isSetLogoURLset();
    if (this_present_logoURLset || that_present_logoURLset) {
      if (!(this_present_logoURLset && that_present_logoURLset))
        return false;
      if (!this.logoURLset.equals(that.logoURLset))
        return false;
    }

    boolean this_present_topicSet = true && this.isSetTopicSet();
    boolean that_present_topicSet = true && that.isSetTopicSet();
    if (this_present_topicSet || that_present_topicSet) {
      if (!(this_present_topicSet && that_present_topicSet))
        return false;
      if (!this.topicSet.equals(that.topicSet))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public int compareTo(ProductCategory other) {
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
    lastComparison = Boolean.valueOf(isSetParentId()).compareTo(other.isSetParentId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetParentId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.parentId, other.parentId);
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
    lastComparison = Boolean.valueOf(isSetLogoURLset()).compareTo(other.isSetLogoURLset());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetLogoURLset()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.logoURLset, other.logoURLset);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetTopicSet()).compareTo(other.isSetTopicSet());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTopicSet()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.topicSet, other.topicSet);
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
    StringBuilder sb = new StringBuilder("ProductCategory(");
    boolean first = true;

    sb.append("id:");
    sb.append(this.id);
    first = false;
    if (!first) sb.append(", ");
    sb.append("parentId:");
    sb.append(this.parentId);
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
    sb.append("logoURLset:");
    if (this.logoURLset == null) {
      sb.append("null");
    } else {
      sb.append(this.logoURLset);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("topicSet:");
    if (this.topicSet == null) {
      sb.append("null");
    } else {
      sb.append(this.topicSet);
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

  private static class ProductCategoryStandardSchemeFactory implements SchemeFactory {
    public ProductCategoryStandardScheme getScheme() {
      return new ProductCategoryStandardScheme();
    }
  }

  private static class ProductCategoryStandardScheme extends StandardScheme<ProductCategory> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, ProductCategory struct) throws org.apache.thrift.TException {
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
          case 2: // PARENT_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.parentId = iprot.readI64();
              struct.setParentIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.name = iprot.readString();
              struct.setNameIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // DESCR
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.descr = iprot.readString();
              struct.setDescrIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // LOGO_URLSET
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list36 = iprot.readListBegin();
                struct.logoURLset = new ArrayList<String>(_list36.size);
                for (int _i37 = 0; _i37 < _list36.size; ++_i37)
                {
                  String _elem38;
                  _elem38 = iprot.readString();
                  struct.logoURLset.add(_elem38);
                }
                iprot.readListEnd();
              }
              struct.setLogoURLsetIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // TOPIC_SET
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list39 = iprot.readListBegin();
                struct.topicSet = new ArrayList<Long>(_list39.size);
                for (int _i40 = 0; _i40 < _list39.size; ++_i40)
                {
                  long _elem41;
                  _elem41 = iprot.readI64();
                  struct.topicSet.add(_elem41);
                }
                iprot.readListEnd();
              }
              struct.setTopicSetIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, ProductCategory struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(ID_FIELD_DESC);
      oprot.writeI64(struct.id);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(PARENT_ID_FIELD_DESC);
      oprot.writeI64(struct.parentId);
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
      if (struct.logoURLset != null) {
        oprot.writeFieldBegin(LOGO_URLSET_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, struct.logoURLset.size()));
          for (String _iter42 : struct.logoURLset)
          {
            oprot.writeString(_iter42);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.topicSet != null) {
        oprot.writeFieldBegin(TOPIC_SET_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.I64, struct.topicSet.size()));
          for (long _iter43 : struct.topicSet)
          {
            oprot.writeI64(_iter43);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class ProductCategoryTupleSchemeFactory implements SchemeFactory {
    public ProductCategoryTupleScheme getScheme() {
      return new ProductCategoryTupleScheme();
    }
  }

  private static class ProductCategoryTupleScheme extends TupleScheme<ProductCategory> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, ProductCategory struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetId()) {
        optionals.set(0);
      }
      if (struct.isSetParentId()) {
        optionals.set(1);
      }
      if (struct.isSetName()) {
        optionals.set(2);
      }
      if (struct.isSetDescr()) {
        optionals.set(3);
      }
      if (struct.isSetLogoURLset()) {
        optionals.set(4);
      }
      if (struct.isSetTopicSet()) {
        optionals.set(5);
      }
      oprot.writeBitSet(optionals, 6);
      if (struct.isSetId()) {
        oprot.writeI64(struct.id);
      }
      if (struct.isSetParentId()) {
        oprot.writeI64(struct.parentId);
      }
      if (struct.isSetName()) {
        oprot.writeString(struct.name);
      }
      if (struct.isSetDescr()) {
        oprot.writeString(struct.descr);
      }
      if (struct.isSetLogoURLset()) {
        {
          oprot.writeI32(struct.logoURLset.size());
          for (String _iter44 : struct.logoURLset)
          {
            oprot.writeString(_iter44);
          }
        }
      }
      if (struct.isSetTopicSet()) {
        {
          oprot.writeI32(struct.topicSet.size());
          for (long _iter45 : struct.topicSet)
          {
            oprot.writeI64(_iter45);
          }
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, ProductCategory struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(6);
      if (incoming.get(0)) {
        struct.id = iprot.readI64();
        struct.setIdIsSet(true);
      }
      if (incoming.get(1)) {
        struct.parentId = iprot.readI64();
        struct.setParentIdIsSet(true);
      }
      if (incoming.get(2)) {
        struct.name = iprot.readString();
        struct.setNameIsSet(true);
      }
      if (incoming.get(3)) {
        struct.descr = iprot.readString();
        struct.setDescrIsSet(true);
      }
      if (incoming.get(4)) {
        {
          org.apache.thrift.protocol.TList _list46 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, iprot.readI32());
          struct.logoURLset = new ArrayList<String>(_list46.size);
          for (int _i47 = 0; _i47 < _list46.size; ++_i47)
          {
            String _elem48;
            _elem48 = iprot.readString();
            struct.logoURLset.add(_elem48);
          }
        }
        struct.setLogoURLsetIsSet(true);
      }
      if (incoming.get(5)) {
        {
          org.apache.thrift.protocol.TList _list49 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.I64, iprot.readI32());
          struct.topicSet = new ArrayList<Long>(_list49.size);
          for (int _i50 = 0; _i50 < _list49.size; ++_i50)
          {
            long _elem51;
            _elem51 = iprot.readI64();
            struct.topicSet.add(_elem51);
          }
        }
        struct.setTopicSetIsSet(true);
      }
    }
  }

}

