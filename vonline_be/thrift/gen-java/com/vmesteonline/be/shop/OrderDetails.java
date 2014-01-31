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

public class OrderDetails implements org.apache.thrift.TBase<OrderDetails, OrderDetails._Fields>, java.io.Serializable, Cloneable, Comparable<OrderDetails> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("OrderDetails");

  private static final org.apache.thrift.protocol.TField CREATED_AT_FIELD_DESC = new org.apache.thrift.protocol.TField("createdAt", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField DELIVERY_FIELD_DESC = new org.apache.thrift.protocol.TField("delivery", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField DELIVERY_COST_FIELD_DESC = new org.apache.thrift.protocol.TField("deliveryCost", org.apache.thrift.protocol.TType.DOUBLE, (short)3);
  private static final org.apache.thrift.protocol.TField DELIVERY_TO_FIELD_DESC = new org.apache.thrift.protocol.TField("deliveryTo", org.apache.thrift.protocol.TType.STRUCT, (short)4);
  private static final org.apache.thrift.protocol.TField PAYMENT_TYPE_FIELD_DESC = new org.apache.thrift.protocol.TField("paymentType", org.apache.thrift.protocol.TType.I32, (short)5);
  private static final org.apache.thrift.protocol.TField PAYMENT_STATUS_FIELD_DESC = new org.apache.thrift.protocol.TField("paymentStatus", org.apache.thrift.protocol.TType.I32, (short)6);
  private static final org.apache.thrift.protocol.TField ODRER_LINES_FIELD_DESC = new org.apache.thrift.protocol.TField("odrerLines", org.apache.thrift.protocol.TType.LIST, (short)7);
  private static final org.apache.thrift.protocol.TField COMMENT_FIELD_DESC = new org.apache.thrift.protocol.TField("comment", org.apache.thrift.protocol.TType.STRING, (short)8);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new OrderDetailsStandardSchemeFactory());
    schemes.put(TupleScheme.class, new OrderDetailsTupleSchemeFactory());
  }

  public int createdAt; // required
  /**
   * 
   * @see DeliveryType
   */
  public DeliveryType delivery; // required
  public double deliveryCost; // required
  public com.vmesteonline.be.PostalAddress deliveryTo; // required
  /**
   * 
   * @see PaymentType
   */
  public PaymentType paymentType; // required
  /**
   * 
   * @see PaymentStatus
   */
  public PaymentStatus paymentStatus; // required
  public List<OrderLine> odrerLines; // required
  public String comment; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    CREATED_AT((short)1, "createdAt"),
    /**
     * 
     * @see DeliveryType
     */
    DELIVERY((short)2, "delivery"),
    DELIVERY_COST((short)3, "deliveryCost"),
    DELIVERY_TO((short)4, "deliveryTo"),
    /**
     * 
     * @see PaymentType
     */
    PAYMENT_TYPE((short)5, "paymentType"),
    /**
     * 
     * @see PaymentStatus
     */
    PAYMENT_STATUS((short)6, "paymentStatus"),
    ODRER_LINES((short)7, "odrerLines"),
    COMMENT((short)8, "comment");

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
        case 1: // CREATED_AT
          return CREATED_AT;
        case 2: // DELIVERY
          return DELIVERY;
        case 3: // DELIVERY_COST
          return DELIVERY_COST;
        case 4: // DELIVERY_TO
          return DELIVERY_TO;
        case 5: // PAYMENT_TYPE
          return PAYMENT_TYPE;
        case 6: // PAYMENT_STATUS
          return PAYMENT_STATUS;
        case 7: // ODRER_LINES
          return ODRER_LINES;
        case 8: // COMMENT
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
  private static final int __CREATEDAT_ISSET_ID = 0;
  private static final int __DELIVERYCOST_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.CREATED_AT, new org.apache.thrift.meta_data.FieldMetaData("createdAt", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.DELIVERY, new org.apache.thrift.meta_data.FieldMetaData("delivery", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, DeliveryType.class)));
    tmpMap.put(_Fields.DELIVERY_COST, new org.apache.thrift.meta_data.FieldMetaData("deliveryCost", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE)));
    tmpMap.put(_Fields.DELIVERY_TO, new org.apache.thrift.meta_data.FieldMetaData("deliveryTo", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, com.vmesteonline.be.PostalAddress.class)));
    tmpMap.put(_Fields.PAYMENT_TYPE, new org.apache.thrift.meta_data.FieldMetaData("paymentType", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, PaymentType.class)));
    tmpMap.put(_Fields.PAYMENT_STATUS, new org.apache.thrift.meta_data.FieldMetaData("paymentStatus", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, PaymentStatus.class)));
    tmpMap.put(_Fields.ODRER_LINES, new org.apache.thrift.meta_data.FieldMetaData("odrerLines", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, OrderLine.class))));
    tmpMap.put(_Fields.COMMENT, new org.apache.thrift.meta_data.FieldMetaData("comment", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(OrderDetails.class, metaDataMap);
  }

  public OrderDetails() {
  }

  public OrderDetails(
    int createdAt,
    DeliveryType delivery,
    double deliveryCost,
    com.vmesteonline.be.PostalAddress deliveryTo,
    PaymentType paymentType,
    PaymentStatus paymentStatus,
    List<OrderLine> odrerLines,
    String comment)
  {
    this();
    this.createdAt = createdAt;
    setCreatedAtIsSet(true);
    this.delivery = delivery;
    this.deliveryCost = deliveryCost;
    setDeliveryCostIsSet(true);
    this.deliveryTo = deliveryTo;
    this.paymentType = paymentType;
    this.paymentStatus = paymentStatus;
    this.odrerLines = odrerLines;
    this.comment = comment;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public OrderDetails(OrderDetails other) {
    __isset_bitfield = other.__isset_bitfield;
    this.createdAt = other.createdAt;
    if (other.isSetDelivery()) {
      this.delivery = other.delivery;
    }
    this.deliveryCost = other.deliveryCost;
    if (other.isSetDeliveryTo()) {
      this.deliveryTo = new com.vmesteonline.be.PostalAddress(other.deliveryTo);
    }
    if (other.isSetPaymentType()) {
      this.paymentType = other.paymentType;
    }
    if (other.isSetPaymentStatus()) {
      this.paymentStatus = other.paymentStatus;
    }
    if (other.isSetOdrerLines()) {
      List<OrderLine> __this__odrerLines = new ArrayList<OrderLine>(other.odrerLines.size());
      for (OrderLine other_element : other.odrerLines) {
        __this__odrerLines.add(new OrderLine(other_element));
      }
      this.odrerLines = __this__odrerLines;
    }
    if (other.isSetComment()) {
      this.comment = other.comment;
    }
  }

  public OrderDetails deepCopy() {
    return new OrderDetails(this);
  }

  @Override
  public void clear() {
    setCreatedAtIsSet(false);
    this.createdAt = 0;
    this.delivery = null;
    setDeliveryCostIsSet(false);
    this.deliveryCost = 0.0;
    this.deliveryTo = null;
    this.paymentType = null;
    this.paymentStatus = null;
    this.odrerLines = null;
    this.comment = null;
  }

  public int getCreatedAt() {
    return this.createdAt;
  }

  public OrderDetails setCreatedAt(int createdAt) {
    this.createdAt = createdAt;
    setCreatedAtIsSet(true);
    return this;
  }

  public void unsetCreatedAt() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __CREATEDAT_ISSET_ID);
  }

  /** Returns true if field createdAt is set (has been assigned a value) and false otherwise */
  public boolean isSetCreatedAt() {
    return EncodingUtils.testBit(__isset_bitfield, __CREATEDAT_ISSET_ID);
  }

  public void setCreatedAtIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __CREATEDAT_ISSET_ID, value);
  }

  /**
   * 
   * @see DeliveryType
   */
  public DeliveryType getDelivery() {
    return this.delivery;
  }

  /**
   * 
   * @see DeliveryType
   */
  public OrderDetails setDelivery(DeliveryType delivery) {
    this.delivery = delivery;
    return this;
  }

  public void unsetDelivery() {
    this.delivery = null;
  }

  /** Returns true if field delivery is set (has been assigned a value) and false otherwise */
  public boolean isSetDelivery() {
    return this.delivery != null;
  }

  public void setDeliveryIsSet(boolean value) {
    if (!value) {
      this.delivery = null;
    }
  }

  public double getDeliveryCost() {
    return this.deliveryCost;
  }

  public OrderDetails setDeliveryCost(double deliveryCost) {
    this.deliveryCost = deliveryCost;
    setDeliveryCostIsSet(true);
    return this;
  }

  public void unsetDeliveryCost() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __DELIVERYCOST_ISSET_ID);
  }

  /** Returns true if field deliveryCost is set (has been assigned a value) and false otherwise */
  public boolean isSetDeliveryCost() {
    return EncodingUtils.testBit(__isset_bitfield, __DELIVERYCOST_ISSET_ID);
  }

  public void setDeliveryCostIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __DELIVERYCOST_ISSET_ID, value);
  }

  public com.vmesteonline.be.PostalAddress getDeliveryTo() {
    return this.deliveryTo;
  }

  public OrderDetails setDeliveryTo(com.vmesteonline.be.PostalAddress deliveryTo) {
    this.deliveryTo = deliveryTo;
    return this;
  }

  public void unsetDeliveryTo() {
    this.deliveryTo = null;
  }

  /** Returns true if field deliveryTo is set (has been assigned a value) and false otherwise */
  public boolean isSetDeliveryTo() {
    return this.deliveryTo != null;
  }

  public void setDeliveryToIsSet(boolean value) {
    if (!value) {
      this.deliveryTo = null;
    }
  }

  /**
   * 
   * @see PaymentType
   */
  public PaymentType getPaymentType() {
    return this.paymentType;
  }

  /**
   * 
   * @see PaymentType
   */
  public OrderDetails setPaymentType(PaymentType paymentType) {
    this.paymentType = paymentType;
    return this;
  }

  public void unsetPaymentType() {
    this.paymentType = null;
  }

  /** Returns true if field paymentType is set (has been assigned a value) and false otherwise */
  public boolean isSetPaymentType() {
    return this.paymentType != null;
  }

  public void setPaymentTypeIsSet(boolean value) {
    if (!value) {
      this.paymentType = null;
    }
  }

  /**
   * 
   * @see PaymentStatus
   */
  public PaymentStatus getPaymentStatus() {
    return this.paymentStatus;
  }

  /**
   * 
   * @see PaymentStatus
   */
  public OrderDetails setPaymentStatus(PaymentStatus paymentStatus) {
    this.paymentStatus = paymentStatus;
    return this;
  }

  public void unsetPaymentStatus() {
    this.paymentStatus = null;
  }

  /** Returns true if field paymentStatus is set (has been assigned a value) and false otherwise */
  public boolean isSetPaymentStatus() {
    return this.paymentStatus != null;
  }

  public void setPaymentStatusIsSet(boolean value) {
    if (!value) {
      this.paymentStatus = null;
    }
  }

  public int getOdrerLinesSize() {
    return (this.odrerLines == null) ? 0 : this.odrerLines.size();
  }

  public java.util.Iterator<OrderLine> getOdrerLinesIterator() {
    return (this.odrerLines == null) ? null : this.odrerLines.iterator();
  }

  public void addToOdrerLines(OrderLine elem) {
    if (this.odrerLines == null) {
      this.odrerLines = new ArrayList<OrderLine>();
    }
    this.odrerLines.add(elem);
  }

  public List<OrderLine> getOdrerLines() {
    return this.odrerLines;
  }

  public OrderDetails setOdrerLines(List<OrderLine> odrerLines) {
    this.odrerLines = odrerLines;
    return this;
  }

  public void unsetOdrerLines() {
    this.odrerLines = null;
  }

  /** Returns true if field odrerLines is set (has been assigned a value) and false otherwise */
  public boolean isSetOdrerLines() {
    return this.odrerLines != null;
  }

  public void setOdrerLinesIsSet(boolean value) {
    if (!value) {
      this.odrerLines = null;
    }
  }

  public String getComment() {
    return this.comment;
  }

  public OrderDetails setComment(String comment) {
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
    case CREATED_AT:
      if (value == null) {
        unsetCreatedAt();
      } else {
        setCreatedAt((Integer)value);
      }
      break;

    case DELIVERY:
      if (value == null) {
        unsetDelivery();
      } else {
        setDelivery((DeliveryType)value);
      }
      break;

    case DELIVERY_COST:
      if (value == null) {
        unsetDeliveryCost();
      } else {
        setDeliveryCost((Double)value);
      }
      break;

    case DELIVERY_TO:
      if (value == null) {
        unsetDeliveryTo();
      } else {
        setDeliveryTo((com.vmesteonline.be.PostalAddress)value);
      }
      break;

    case PAYMENT_TYPE:
      if (value == null) {
        unsetPaymentType();
      } else {
        setPaymentType((PaymentType)value);
      }
      break;

    case PAYMENT_STATUS:
      if (value == null) {
        unsetPaymentStatus();
      } else {
        setPaymentStatus((PaymentStatus)value);
      }
      break;

    case ODRER_LINES:
      if (value == null) {
        unsetOdrerLines();
      } else {
        setOdrerLines((List<OrderLine>)value);
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
    case CREATED_AT:
      return Integer.valueOf(getCreatedAt());

    case DELIVERY:
      return getDelivery();

    case DELIVERY_COST:
      return Double.valueOf(getDeliveryCost());

    case DELIVERY_TO:
      return getDeliveryTo();

    case PAYMENT_TYPE:
      return getPaymentType();

    case PAYMENT_STATUS:
      return getPaymentStatus();

    case ODRER_LINES:
      return getOdrerLines();

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
    case CREATED_AT:
      return isSetCreatedAt();
    case DELIVERY:
      return isSetDelivery();
    case DELIVERY_COST:
      return isSetDeliveryCost();
    case DELIVERY_TO:
      return isSetDeliveryTo();
    case PAYMENT_TYPE:
      return isSetPaymentType();
    case PAYMENT_STATUS:
      return isSetPaymentStatus();
    case ODRER_LINES:
      return isSetOdrerLines();
    case COMMENT:
      return isSetComment();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof OrderDetails)
      return this.equals((OrderDetails)that);
    return false;
  }

  public boolean equals(OrderDetails that) {
    if (that == null)
      return false;

    boolean this_present_createdAt = true;
    boolean that_present_createdAt = true;
    if (this_present_createdAt || that_present_createdAt) {
      if (!(this_present_createdAt && that_present_createdAt))
        return false;
      if (this.createdAt != that.createdAt)
        return false;
    }

    boolean this_present_delivery = true && this.isSetDelivery();
    boolean that_present_delivery = true && that.isSetDelivery();
    if (this_present_delivery || that_present_delivery) {
      if (!(this_present_delivery && that_present_delivery))
        return false;
      if (!this.delivery.equals(that.delivery))
        return false;
    }

    boolean this_present_deliveryCost = true;
    boolean that_present_deliveryCost = true;
    if (this_present_deliveryCost || that_present_deliveryCost) {
      if (!(this_present_deliveryCost && that_present_deliveryCost))
        return false;
      if (this.deliveryCost != that.deliveryCost)
        return false;
    }

    boolean this_present_deliveryTo = true && this.isSetDeliveryTo();
    boolean that_present_deliveryTo = true && that.isSetDeliveryTo();
    if (this_present_deliveryTo || that_present_deliveryTo) {
      if (!(this_present_deliveryTo && that_present_deliveryTo))
        return false;
      if (!this.deliveryTo.equals(that.deliveryTo))
        return false;
    }

    boolean this_present_paymentType = true && this.isSetPaymentType();
    boolean that_present_paymentType = true && that.isSetPaymentType();
    if (this_present_paymentType || that_present_paymentType) {
      if (!(this_present_paymentType && that_present_paymentType))
        return false;
      if (!this.paymentType.equals(that.paymentType))
        return false;
    }

    boolean this_present_paymentStatus = true && this.isSetPaymentStatus();
    boolean that_present_paymentStatus = true && that.isSetPaymentStatus();
    if (this_present_paymentStatus || that_present_paymentStatus) {
      if (!(this_present_paymentStatus && that_present_paymentStatus))
        return false;
      if (!this.paymentStatus.equals(that.paymentStatus))
        return false;
    }

    boolean this_present_odrerLines = true && this.isSetOdrerLines();
    boolean that_present_odrerLines = true && that.isSetOdrerLines();
    if (this_present_odrerLines || that_present_odrerLines) {
      if (!(this_present_odrerLines && that_present_odrerLines))
        return false;
      if (!this.odrerLines.equals(that.odrerLines))
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
  public int compareTo(OrderDetails other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetCreatedAt()).compareTo(other.isSetCreatedAt());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetCreatedAt()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.createdAt, other.createdAt);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetDelivery()).compareTo(other.isSetDelivery());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetDelivery()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.delivery, other.delivery);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetDeliveryCost()).compareTo(other.isSetDeliveryCost());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetDeliveryCost()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.deliveryCost, other.deliveryCost);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetDeliveryTo()).compareTo(other.isSetDeliveryTo());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetDeliveryTo()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.deliveryTo, other.deliveryTo);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetPaymentType()).compareTo(other.isSetPaymentType());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPaymentType()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.paymentType, other.paymentType);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetPaymentStatus()).compareTo(other.isSetPaymentStatus());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPaymentStatus()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.paymentStatus, other.paymentStatus);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetOdrerLines()).compareTo(other.isSetOdrerLines());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetOdrerLines()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.odrerLines, other.odrerLines);
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
    StringBuilder sb = new StringBuilder("OrderDetails(");
    boolean first = true;

    sb.append("createdAt:");
    sb.append(this.createdAt);
    first = false;
    if (!first) sb.append(", ");
    sb.append("delivery:");
    if (this.delivery == null) {
      sb.append("null");
    } else {
      sb.append(this.delivery);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("deliveryCost:");
    sb.append(this.deliveryCost);
    first = false;
    if (!first) sb.append(", ");
    sb.append("deliveryTo:");
    if (this.deliveryTo == null) {
      sb.append("null");
    } else {
      sb.append(this.deliveryTo);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("paymentType:");
    if (this.paymentType == null) {
      sb.append("null");
    } else {
      sb.append(this.paymentType);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("paymentStatus:");
    if (this.paymentStatus == null) {
      sb.append("null");
    } else {
      sb.append(this.paymentStatus);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("odrerLines:");
    if (this.odrerLines == null) {
      sb.append("null");
    } else {
      sb.append(this.odrerLines);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("comment:");
    if (this.comment == null) {
      sb.append("null");
    } else {
      sb.append(this.comment);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
    if (deliveryTo != null) {
      deliveryTo.validate();
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

  private static class OrderDetailsStandardSchemeFactory implements SchemeFactory {
    public OrderDetailsStandardScheme getScheme() {
      return new OrderDetailsStandardScheme();
    }
  }

  private static class OrderDetailsStandardScheme extends StandardScheme<OrderDetails> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, OrderDetails struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // CREATED_AT
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.createdAt = iprot.readI32();
              struct.setCreatedAtIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // DELIVERY
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.delivery = DeliveryType.findByValue(iprot.readI32());
              struct.setDeliveryIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // DELIVERY_COST
            if (schemeField.type == org.apache.thrift.protocol.TType.DOUBLE) {
              struct.deliveryCost = iprot.readDouble();
              struct.setDeliveryCostIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // DELIVERY_TO
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.deliveryTo = new com.vmesteonline.be.PostalAddress();
              struct.deliveryTo.read(iprot);
              struct.setDeliveryToIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // PAYMENT_TYPE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.paymentType = PaymentType.findByValue(iprot.readI32());
              struct.setPaymentTypeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // PAYMENT_STATUS
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.paymentStatus = PaymentStatus.findByValue(iprot.readI32());
              struct.setPaymentStatusIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 7: // ODRER_LINES
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list96 = iprot.readListBegin();
                struct.odrerLines = new ArrayList<OrderLine>(_list96.size);
                for (int _i97 = 0; _i97 < _list96.size; ++_i97)
                {
                  OrderLine _elem98;
                  _elem98 = new OrderLine();
                  _elem98.read(iprot);
                  struct.odrerLines.add(_elem98);
                }
                iprot.readListEnd();
              }
              struct.setOdrerLinesIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 8: // COMMENT
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, OrderDetails struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(CREATED_AT_FIELD_DESC);
      oprot.writeI32(struct.createdAt);
      oprot.writeFieldEnd();
      if (struct.delivery != null) {
        oprot.writeFieldBegin(DELIVERY_FIELD_DESC);
        oprot.writeI32(struct.delivery.getValue());
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(DELIVERY_COST_FIELD_DESC);
      oprot.writeDouble(struct.deliveryCost);
      oprot.writeFieldEnd();
      if (struct.deliveryTo != null) {
        oprot.writeFieldBegin(DELIVERY_TO_FIELD_DESC);
        struct.deliveryTo.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct.paymentType != null) {
        oprot.writeFieldBegin(PAYMENT_TYPE_FIELD_DESC);
        oprot.writeI32(struct.paymentType.getValue());
        oprot.writeFieldEnd();
      }
      if (struct.paymentStatus != null) {
        oprot.writeFieldBegin(PAYMENT_STATUS_FIELD_DESC);
        oprot.writeI32(struct.paymentStatus.getValue());
        oprot.writeFieldEnd();
      }
      if (struct.odrerLines != null) {
        oprot.writeFieldBegin(ODRER_LINES_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.odrerLines.size()));
          for (OrderLine _iter99 : struct.odrerLines)
          {
            _iter99.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.comment != null) {
        oprot.writeFieldBegin(COMMENT_FIELD_DESC);
        oprot.writeString(struct.comment);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class OrderDetailsTupleSchemeFactory implements SchemeFactory {
    public OrderDetailsTupleScheme getScheme() {
      return new OrderDetailsTupleScheme();
    }
  }

  private static class OrderDetailsTupleScheme extends TupleScheme<OrderDetails> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, OrderDetails struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetCreatedAt()) {
        optionals.set(0);
      }
      if (struct.isSetDelivery()) {
        optionals.set(1);
      }
      if (struct.isSetDeliveryCost()) {
        optionals.set(2);
      }
      if (struct.isSetDeliveryTo()) {
        optionals.set(3);
      }
      if (struct.isSetPaymentType()) {
        optionals.set(4);
      }
      if (struct.isSetPaymentStatus()) {
        optionals.set(5);
      }
      if (struct.isSetOdrerLines()) {
        optionals.set(6);
      }
      if (struct.isSetComment()) {
        optionals.set(7);
      }
      oprot.writeBitSet(optionals, 8);
      if (struct.isSetCreatedAt()) {
        oprot.writeI32(struct.createdAt);
      }
      if (struct.isSetDelivery()) {
        oprot.writeI32(struct.delivery.getValue());
      }
      if (struct.isSetDeliveryCost()) {
        oprot.writeDouble(struct.deliveryCost);
      }
      if (struct.isSetDeliveryTo()) {
        struct.deliveryTo.write(oprot);
      }
      if (struct.isSetPaymentType()) {
        oprot.writeI32(struct.paymentType.getValue());
      }
      if (struct.isSetPaymentStatus()) {
        oprot.writeI32(struct.paymentStatus.getValue());
      }
      if (struct.isSetOdrerLines()) {
        {
          oprot.writeI32(struct.odrerLines.size());
          for (OrderLine _iter100 : struct.odrerLines)
          {
            _iter100.write(oprot);
          }
        }
      }
      if (struct.isSetComment()) {
        oprot.writeString(struct.comment);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, OrderDetails struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(8);
      if (incoming.get(0)) {
        struct.createdAt = iprot.readI32();
        struct.setCreatedAtIsSet(true);
      }
      if (incoming.get(1)) {
        struct.delivery = DeliveryType.findByValue(iprot.readI32());
        struct.setDeliveryIsSet(true);
      }
      if (incoming.get(2)) {
        struct.deliveryCost = iprot.readDouble();
        struct.setDeliveryCostIsSet(true);
      }
      if (incoming.get(3)) {
        struct.deliveryTo = new com.vmesteonline.be.PostalAddress();
        struct.deliveryTo.read(iprot);
        struct.setDeliveryToIsSet(true);
      }
      if (incoming.get(4)) {
        struct.paymentType = PaymentType.findByValue(iprot.readI32());
        struct.setPaymentTypeIsSet(true);
      }
      if (incoming.get(5)) {
        struct.paymentStatus = PaymentStatus.findByValue(iprot.readI32());
        struct.setPaymentStatusIsSet(true);
      }
      if (incoming.get(6)) {
        {
          org.apache.thrift.protocol.TList _list101 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
          struct.odrerLines = new ArrayList<OrderLine>(_list101.size);
          for (int _i102 = 0; _i102 < _list101.size; ++_i102)
          {
            OrderLine _elem103;
            _elem103 = new OrderLine();
            _elem103.read(iprot);
            struct.odrerLines.add(_elem103);
          }
        }
        struct.setOdrerLinesIsSet(true);
      }
      if (incoming.get(7)) {
        struct.comment = iprot.readString();
        struct.setCommentIsSet(true);
      }
    }
  }

}
