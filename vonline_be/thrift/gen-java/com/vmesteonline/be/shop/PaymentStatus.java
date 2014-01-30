/**
 * Autogenerated by Thrift Compiler (0.9.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.vmesteonline.be.shop;


import java.util.Map;
import java.util.HashMap;
import org.apache.thrift.TEnum;

public enum PaymentStatus implements org.apache.thrift.TEnum {
  UNKNOWN(0),
  WIAT(1),
  PENDING(2),
  COMPLETE(3),
  CREDIT(4);

  private final int value;

  private PaymentStatus(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  public static PaymentStatus findByValue(int value) { 
    switch (value) {
      case 0:
        return UNKNOWN;
      case 1:
        return WIAT;
      case 2:
        return PENDING;
      case 3:
        return COMPLETE;
      case 4:
        return CREDIT;
      default:
        return null;
    }
  }
}
