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

public class OrderLine implements org.apache.thrift.TBase<OrderLine, OrderLine._Fields>, java.io.Serializable, Cloneable, Comparable<OrderLine> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("OrderLine");

  private static final org.apache.thrift.protocol.TField PRODUCT_FIELD_DESC = new org.apache.thrift.protocol.TField("product", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField QUANTITY_FIELD_DESC = new org.apache.thrift.protocol.TField("quantity", org.apache.thrift.protocol.TType.DOUBLE, (short)2);
  private static final org.apache.thrift.protocol.TField PRICE_FIELD_DESC = new org.apache.thrift.protocol.TField("price", org.apache.thrift.protocol.TType.DOUBLE, (short)3);
  private static final org.apache.thrift.protocol.TField PACKS_FIELD_DESC = new org.apache.thrift.protocol.TField("packs", org.apache.thrift.protocol.TType.MAP, (short)4);
  private static final org.apache.thrift.protocol.TField COMMENT_FIELD_DESC = new org.apache.thrift.protocol.TField("comment", org.apache.thrift.protocol.TType.STRING, (short)5);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new OrderLineStandardSchemeFactory());
    schemes.put(TupleScheme.class, new OrderLineTupleSchemeFactory());
  }

  public Product product; // required
  public double quantity; // required
  public double price; // required
  public Map<Double,Integer> packs; // optional
  public String comment; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    PRODUCT((short)1, "product"),
    QUANTITY((short)2, "quantity"),
    PRICE((short)3, "price"),
    PACKS((short)4, "packs"),
    COMMENT((short)5, "comment");

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
        case 1: // PRODUCT
          return PRODUCT;
        case 2: // QUANTITY
          return QUANTITY;
        case 3: // PRICE
          return PRICE;
        case 4: // PACKS
          return PACKS;
        case 5: // COMMENT
          return COMMENT;
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
  private static final int __QUANTITY_ISSET_ID = 0;
  private static final int __PRICE_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  private _Fields optionals[] = {_Fields.PACKS,_Fields.COMMENT};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.PRODUCT, new org.apache.thrift.meta_data.FieldMetaData("product", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, Product.class)));
    tmpMap.put(_Fields.QUANTITY, new org.apache.thrift.meta_data.FieldMetaData("quantity", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE)));
    tmpMap.put(_Fields.PRICE, new org.apache.thrift.meta_data.FieldMetaData("price", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE)));
    tmpMap.put(_Fields.PACKS, new org.apache.thrift.meta_data.FieldMetaData("packs", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.MapMetaData(org.apache.thrift.protocol.TType.MAP, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE), 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32))));
    tmpMap.put(_Fields.COMMENT, new org.apache.thrift.meta_data.FieldMetaData("comment", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(OrderLine.class, metaDataMap);
  }

  public OrderLine() {
  }

  public OrderLine(
    Product product,
    double quantity,
    double price)
  {
    this();
    this.product = product;
    this.quantity = quantity;
    setQuantityIsSet(true);
    this.price = price;
    setPriceIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public OrderLine(OrderLine other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetProduct()) {
      this.product = new Product(other.product);
    }
    this.quantity = other.quantity;
    this.price = other.price;
    if (other.isSetPacks()) {
      Map<Double,Integer> __this__packs = new HashMap<Double,Integer>(other.packs);
      this.packs = __this__packs;
    }
    if (other.isSetComment()) {
      this.comment = other.comment;
    }
  }

  public OrderLine deepCopy() {
    return new OrderLine(this);
  }

  @Override
  public void clear() {
    this.product = null;
    setQuantityIsSet(false);
    this.quantity = 0.0;
    setPriceIsSet(false);
    this.price = 0.0;
    this.packs = null;
    this.comment = null;
  }

  public Product getProduct() {
    return this.product;
  }

  public OrderLine setProduct(Product product) {
    this.product = product;
    return this;
  }

  public void unsetProduct() {
    this.product = null;
  }

  /** Returns true if field product is set (has been assigned a value) and false otherwise */
  public boolean isSetProduct() {
    return this.product != null;
  }

  public void setProductIsSet(boolean value) {
    if (!value) {
      this.product = null;
    }
  }

  public double getQuantity() {
    return this.quantity;
  }

  public OrderLine setQuantity(double quantity) {
    this.quantity = quantity;
    setQuantityIsSet(true);
    return this;
  }

  public void unsetQuantity() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __QUANTITY_ISSET_ID);
  }

  /** Returns true if field quantity is set (has been assigned a value) and false otherwise */
  public boolean isSetQuantity() {
    return EncodingUtils.testBit(__isset_bitfield, __QUANTITY_ISSET_ID);
  }

  public void setQuantityIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __QUANTITY_ISSET_ID, value);
  }

  public double getPrice() {
    return this.price;
  }

  public OrderLine setPrice(double price) {
    this.price = price;
    setPriceIsSet(true);
    return this;
  }

  public void unsetPrice() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __PRICE_ISSET_ID);
  }

  /** Returns true if field price is set (has been assigned a value) and false otherwise */
  public boolean isSetPrice() {
    return EncodingUtils.testBit(__isset_bitfield, __PRICE_ISSET_ID);
  }

  public void setPriceIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __PRICE_ISSET_ID, value);
  }

  public int getPacksSize() {
    return (this.packs == null) ? 0 : this.packs.size();
  }

  public void putToPacks(double key, int val) {
    if (this.packs == null) {
      this.packs = new HashMap<Double,Integer>();
    }
    this.packs.put(key, val);
  }

  public Map<Double,Integer> getPacks() {
    return this.packs;
  }

  public OrderLine setPacks(Map<Double,Integer> packs) {
    this.packs = packs;
    return this;
  }

  public void unsetPacks() {
    this.packs = null;
  }

  /** Returns true if field packs is set (has been assigned a value) and false otherwise */
  public boolean isSetPacks() {
    return this.packs != null;
  }

  public void setPacksIsSet(boolean value) {
    if (!value) {
      this.packs = null;
    }
  }

  public String getComment() {
    return this.comment;
  }

  public OrderLine setComment(String comment) {
    this.comment = comment;
    return this;
  }

  public void unsetComment() {
    this.comment = null;
  }

  /** Returns true if field comment is set (has been assigned a value) and false otherwise */
  public boolean isSetComment() {
    return this.comment != null;
  }

  public void setCommentIsSet(boolean value) {
    if (!value) {
      this.comment = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case PRODUCT:
      if (value == null) {
        unsetProduct();
      } else {
        setProduct((Product)value);
      }
      break;

    case QUANTITY:
      if (value == null) {
        unsetQuantity();
      } else {
        setQuantity((Double)value);
      }
      break;

    case PRICE:
      if (value == null) {
        unsetPrice();
      } else {
        setPrice((Double)value);
      }
      break;

    case PACKS:
      if (value == null) {
        unsetPacks();
      } else {
        setPacks((Map<Double,Integer>)value);
      }
      break;

    case COMMENT:
      if (value == null) {
        unsetComment();
      } else {
        setComment((String)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case PRODUCT:
      return getProduct();

    case QUANTITY:
      return Double.valueOf(getQuantity());

    case PRICE:
      return Double.valueOf(getPrice());

    case PACKS:
      return getPacks();

    case COMMENT:
      return getComment();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case PRODUCT:
      return isSetProduct();
    case QUANTITY:
      return isSetQuantity();
    case PRICE:
      return isSetPrice();
    case PACKS:
      return isSetPacks();
    case COMMENT:
      return isSetComment();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof OrderLine)
      return this.equals((OrderLine)that);
    return false;
  }

  public boolean equals(OrderLine that) {
    if (that == null)
      return false;

    boolean this_present_product = true && this.isSetProduct();
    boolean that_present_product = true && that.isSetProduct();
    if (this_present_product || that_present_product) {
      if (!(this_present_product && that_present_product))
        return false;
      if (!this.product.equals(that.product))
        return false;
    }

    boolean this_present_quantity = true;
    boolean that_present_quantity = true;
    if (this_present_quantity || that_present_quantity) {
      if (!(this_present_quantity && that_present_quantity))
        return false;
      if (this.quantity != that.quantity)
        return false;
    }

    boolean this_present_price = true;
    boolean that_present_price = true;
    if (this_present_price || that_present_price) {
      if (!(this_present_price && that_present_price))
        return false;
      if (this.price != that.price)
        return false;
    }

    boolean this_present_packs = true && this.isSetPacks();
    boolean that_present_packs = true && that.isSetPacks();
    if (this_present_packs || that_present_packs) {
      if (!(this_present_packs && that_present_packs))
        return false;
      if (!this.packs.equals(that.packs))
        return false;
    }

    boolean this_present_comment = true && this.isSetComment();
    boolean that_present_comment = true && that.isSetComment();
    if (this_present_comment || that_present_comment) {
      if (!(this_present_comment && that_present_comment))
        return false;
      if (!this.comment.equals(that.comment))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public int compareTo(OrderLine other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetProduct()).compareTo(other.isSetProduct());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetProduct()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.product, other.product);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetQuantity()).compareTo(other.isSetQuantity());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetQuantity()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.quantity, other.quantity);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetPrice()).compareTo(other.isSetPrice());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPrice()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.price, other.price);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetPacks()).compareTo(other.isSetPacks());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPacks()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.packs, other.packs);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetComment()).compareTo(other.isSetComment());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetComment()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.comment, other.comment);
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
    StringBuilder sb = new StringBuilder("OrderLine(");
    boolean first = true;

    sb.append("product:");
    if (this.product == null) {
      sb.append("null");
    } else {
      sb.append(this.product);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("quantity:");
    sb.append(this.quantity);
    first = false;
    if (!first) sb.append(", ");
    sb.append("price:");
    sb.append(this.price);
    first = false;
    if (isSetPacks()) {
      if (!first) sb.append(", ");
      sb.append("packs:");
      if (this.packs == null) {
        sb.append("null");
      } else {
        sb.append(this.packs);
      }
      first = false;
    }
    if (isSetComment()) {
      if (!first) sb.append(", ");
      sb.append("comment:");
      if (this.comment == null) {
        sb.append("null");
      } else {
        sb.append(this.comment);
      }
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
    if (product != null) {
      product.validate();
    }
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

  private static class OrderLineStandardSchemeFactory implements SchemeFactory {
    public OrderLineStandardScheme getScheme() {
      return new OrderLineStandardScheme();
    }
  }

  private static class OrderLineStandardScheme extends StandardScheme<OrderLine> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, OrderLine struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // PRODUCT
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.product = new Product();
              struct.product.read(iprot);
              struct.setProductIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // QUANTITY
            if (schemeField.type == org.apache.thrift.protocol.TType.DOUBLE) {
              struct.quantity = iprot.readDouble();
              struct.setQuantityIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // PRICE
            if (schemeField.type == org.apache.thrift.protocol.TType.DOUBLE) {
              struct.price = iprot.readDouble();
              struct.setPriceIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // PACKS
            if (schemeField.type == org.apache.thrift.protocol.TType.MAP) {
              {
                org.apache.thrift.protocol.TMap _map104 = iprot.readMapBegin();
                struct.packs = new HashMap<Double,Integer>(2*_map104.size);
                for (int _i105 = 0; _i105 < _map104.size; ++_i105)
                {
                  double _key106;
                  int _val107;
                  _key106 = iprot.readDouble();
                  _val107 = iprot.readI32();
                  struct.packs.put(_key106, _val107);
                }
                iprot.readMapEnd();
              }
              struct.setPacksIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // COMMENT
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.comment = iprot.readString();
              struct.setCommentIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, OrderLine struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.product != null) {
        oprot.writeFieldBegin(PRODUCT_FIELD_DESC);
        struct.product.write(oprot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(QUANTITY_FIELD_DESC);
      oprot.writeDouble(struct.quantity);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(PRICE_FIELD_DESC);
      oprot.writeDouble(struct.price);
      oprot.writeFieldEnd();
      if (struct.packs != null) {
        if (struct.isSetPacks()) {
          oprot.writeFieldBegin(PACKS_FIELD_DESC);
          {
            oprot.writeMapBegin(new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.DOUBLE, org.apache.thrift.protocol.TType.I32, struct.packs.size()));
            for (Map.Entry<Double, Integer> _iter108 : struct.packs.entrySet())
            {
              oprot.writeDouble(_iter108.getKey());
              oprot.writeI32(_iter108.getValue());
            }
            oprot.writeMapEnd();
          }
          oprot.writeFieldEnd();
        }
      }
      if (struct.comment != null) {
        if (struct.isSetComment()) {
          oprot.writeFieldBegin(COMMENT_FIELD_DESC);
          oprot.writeString(struct.comment);
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class OrderLineTupleSchemeFactory implements SchemeFactory {
    public OrderLineTupleScheme getScheme() {
      return new OrderLineTupleScheme();
    }
  }

  private static class OrderLineTupleScheme extends TupleScheme<OrderLine> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, OrderLine struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetProduct()) {
        optionals.set(0);
      }
      if (struct.isSetQuantity()) {
        optionals.set(1);
      }
      if (struct.isSetPrice()) {
        optionals.set(2);
      }
      if (struct.isSetPacks()) {
        optionals.set(3);
      }
      if (struct.isSetComment()) {
        optionals.set(4);
      }
      oprot.writeBitSet(optionals, 5);
      if (struct.isSetProduct()) {
        struct.product.write(oprot);
      }
      if (struct.isSetQuantity()) {
        oprot.writeDouble(struct.quantity);
      }
      if (struct.isSetPrice()) {
        oprot.writeDouble(struct.price);
      }
      if (struct.isSetPacks()) {
        {
          oprot.writeI32(struct.packs.size());
          for (Map.Entry<Double, Integer> _iter109 : struct.packs.entrySet())
          {
            oprot.writeDouble(_iter109.getKey());
            oprot.writeI32(_iter109.getValue());
          }
        }
      }
      if (struct.isSetComment()) {
        oprot.writeString(struct.comment);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, OrderLine struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(5);
      if (incoming.get(0)) {
        struct.product = new Product();
        struct.product.read(iprot);
        struct.setProductIsSet(true);
      }
      if (incoming.get(1)) {
        struct.quantity = iprot.readDouble();
        struct.setQuantityIsSet(true);
      }
      if (incoming.get(2)) {
        struct.price = iprot.readDouble();
        struct.setPriceIsSet(true);
      }
      if (incoming.get(3)) {
        {
          org.apache.thrift.protocol.TMap _map110 = new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.DOUBLE, org.apache.thrift.protocol.TType.I32, iprot.readI32());
          struct.packs = new HashMap<Double,Integer>(2*_map110.size);
          for (int _i111 = 0; _i111 < _map110.size; ++_i111)
          {
            double _key112;
            int _val113;
            _key112 = iprot.readDouble();
            _val113 = iprot.readI32();
            struct.packs.put(_key112, _val113);
          }
        }
        struct.setPacksIsSet(true);
      }
      if (incoming.get(4)) {
        struct.comment = iprot.readString();
        struct.setCommentIsSet(true);
      }
    }
  }

}

