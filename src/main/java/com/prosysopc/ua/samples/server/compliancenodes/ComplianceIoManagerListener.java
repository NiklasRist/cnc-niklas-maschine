/**
 * Prosys OPC UA Java SDK
 * Copyright (c) Prosys OPC Ltd.
 * <http://www.prosysopc.com>
 * All rights reserved.
 */
package com.prosysopc.ua.samples.server.compliancenodes;

import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.nodes.UaMethod;
import com.prosysopc.ua.nodes.UaNode;
import com.prosysopc.ua.nodes.UaProperty;
import com.prosysopc.ua.nodes.UaValueNode;
import com.prosysopc.ua.nodes.UaVariable;
import com.prosysopc.ua.server.ServiceContext;
import com.prosysopc.ua.server.io.IoManagerListener;
import com.prosysopc.ua.stack.builtintypes.DataValue;
import com.prosysopc.ua.stack.builtintypes.DateTime;
import com.prosysopc.ua.stack.builtintypes.NodeId;
import com.prosysopc.ua.stack.builtintypes.QualifiedName;
import com.prosysopc.ua.stack.builtintypes.UnsignedByte;
import com.prosysopc.ua.stack.builtintypes.UnsignedInteger;
import com.prosysopc.ua.stack.builtintypes.UnsignedLong;
import com.prosysopc.ua.stack.builtintypes.UnsignedShort;
import com.prosysopc.ua.stack.builtintypes.Variant;
import com.prosysopc.ua.stack.core.AccessLevelType;
import com.prosysopc.ua.stack.core.AttributeWriteMask;
import com.prosysopc.ua.stack.core.EnumValueType;
import com.prosysopc.ua.stack.core.Identifiers;
import com.prosysopc.ua.stack.core.Range;
import com.prosysopc.ua.stack.core.StatusCodes;
import com.prosysopc.ua.stack.core.TimestampsToReturn;
import com.prosysopc.ua.stack.utils.MultiDimensionArrayUtils;
import com.prosysopc.ua.stack.utils.NumericRange;
import com.prosysopc.ua.types.opcua.MultiStateDiscreteType;
import com.prosysopc.ua.types.opcua.MultiStateValueDiscreteType;

/**
 * An IoManagerListener for ComplianceNodeManager.
 */

public class ComplianceIoManagerListener implements IoManagerListener {

  @Override
  public AccessLevelType onGetUserAccessLevel(ServiceContext serviceContext, NodeId nodeId, UaVariable node) {
    if (node != null) {
      return node.getUserAccessLevel();
    }
    return AccessLevelType.of(AccessLevelType.Options.values());
  }

  @Override
  public Boolean onGetUserExecutable(ServiceContext serviceContext, NodeId nodeId, UaMethod node) {
    if (node != null) {
      return node.getUserExecutable();
    }
    return false;
  }

  @Override
  public AttributeWriteMask onGetUserWriteMask(ServiceContext serviceContext, NodeId nodeId, UaNode node) {
    if (node != null) {
      return node.getUserWriteMask();
    }
    return AttributeWriteMask.of(AttributeWriteMask.Options.values());
  }

  @Override
  public boolean onReadNonValue(ServiceContext serviceContext, NodeId nodeId, UaNode node, UnsignedInteger attributeId,
      DataValue dataValue) throws StatusException {
    return false;
  }

  @Override
  public boolean onReadValue(ServiceContext serviceContext, NodeId nodeId, UaValueNode variable,
      NumericRange indexRange, TimestampsToReturn timestampsToReturn, DateTime minTimestamp, DataValue dataValue)
      throws StatusException {
    return false;
  }

  @Override
  public boolean onWriteNonValue(ServiceContext serviceContext, NodeId nodeId, UaNode node, UnsignedInteger attributeId,
      DataValue dataValue) throws StatusException {
    return false;
  }

  @Override
  public boolean onWriteValue(ServiceContext serviceContext, NodeId nodeId, UaValueNode valueNode,
      NumericRange indexRange, DataValue dataValue) throws StatusException {

    /*
     * JSDK-1638: Check if the Variable Node has InstrumentRange Property and if it does, compare
     * new value to High and Low limits of InstrumentRange and throw Bad_OutOfRange if the new value
     * is outside the range.
     */
    if (dataValue != null) {
      UaProperty instrumentRange = valueNode.getProperty(new QualifiedName(0, "InstrumentRange"));
      if (instrumentRange != null && instrumentRange.getValue().getValue().getValue() != null) {
        // Convert the value parameter to a Variant
        Variant variantValue = dataValue.getValue();
        Range range = (Range) instrumentRange.getValue().getValue().getValue();
        // Validate the Range and abort if there is something wrong with it
        if (range.getLow() < range.getHigh() && range.getLow() != Double.NaN && range.getHigh() != Double.NaN) {
          if (valueNode.getArrayDimensions() == null) {
            // Scalars
            Double doubleValue = objectToDouble(variantValue.getValue(), valueNode);
            if (doubleValue != null) {
              if (doubleValue < range.getLow() || doubleValue > range.getHigh()) {
                throw new StatusException(StatusCodes.Bad_OutOfRange);
              }
            }
          } else if (valueNode.getArrayDimensions().length == 1) {
            // One-dimensional arrays
            Object[] valueArray = (Object[]) variantValue.getValue();
            for (int i = 0; i < valueArray.length; i++) {
              Double doubleValue = objectToDouble(valueArray[i], valueNode);
              if (doubleValue != null) {
                if (doubleValue < range.getLow() || doubleValue > range.getHigh()) {
                  throw new StatusException(StatusCodes.Bad_OutOfRange);
                }
              }
            }
          } else if (valueNode.getArrayDimensions().length > 1) {
            // Multidimensional arrays
            if (!MultiDimensionArrayUtils.checkValueAgainstInstrumentRange(variantValue, range,
                valueNode.getDataTypeId())) {
              throw new StatusException(StatusCodes.Bad_OutOfRange);
            }
          }
        }
      }
      // The checks are complete, the new value is within the InstrumentRange
    }

    // When writing to MultiStateValueDiscreteType Nodes, check value to be written and update
    // ValueAsText
    if (valueNode instanceof MultiStateValueDiscreteType) {
      MultiStateValueDiscreteType item = (MultiStateValueDiscreteType) valueNode;
      NodeId dataType = valueNode.getDataTypeId();

      // Separate handlers for different DataTypes
      if (dataType.equals(Identifiers.Int64)) {
        Long value = (Long) dataValue.getValue().getValue();
        for (EnumValueType e : item.getEnumValues()) {
          if (e.getValue().equals(value)) {
            item.setValueAsText(e.getDisplayName());
            return false;
          }
        }
        throw new StatusException(StatusCodes.Bad_OutOfRange);
      } else if (dataType.equals(Identifiers.Int32)) {
        Integer value = (Integer) dataValue.getValue().getValue();
        for (EnumValueType e : item.getEnumValues()) {
          if (e.getValue().intValue() == value) {
            item.setValueAsText(e.getDisplayName());
            return false;
          }
        }
        throw new StatusException(StatusCodes.Bad_OutOfRange);
      } else if (dataType.equals(Identifiers.Int16)) {
        Short value = (Short) dataValue.getValue().getValue();
        for (EnumValueType e : item.getEnumValues()) {
          if (e.getValue().shortValue() == value) {
            item.setValueAsText(e.getDisplayName());
            return false;
          }
        }
        throw new StatusException(StatusCodes.Bad_OutOfRange);
      } else if (dataType.equals(Identifiers.SByte)) {
        Byte value = (Byte) dataValue.getValue().getValue();
        for (EnumValueType e : item.getEnumValues()) {
          if (e.getValue().byteValue() == value) {
            item.setValueAsText(e.getDisplayName());
            return false;
          }
        }
        throw new StatusException(StatusCodes.Bad_OutOfRange);
      } else if (dataType.equals(Identifiers.Byte)) {
        UnsignedByte value = (UnsignedByte) dataValue.getValue().getValue();
        for (EnumValueType e : item.getEnumValues()) {
          if (UnsignedByte.valueOf(e.getValue()) == value) {
            item.setValueAsText(e.getDisplayName());
            return false;
          }
        }
        throw new StatusException(StatusCodes.Bad_OutOfRange);
      } else if (dataType.equals(Identifiers.UInt16)) {
        UnsignedShort value = (UnsignedShort) dataValue.getValue().getValue();
        for (EnumValueType e : item.getEnumValues()) {
          if (UnsignedShort.valueOf(e.getValue()) == value) {
            item.setValueAsText(e.getDisplayName());
            return false;
          }
        }
        throw new StatusException(StatusCodes.Bad_OutOfRange);
      } else if (dataType.equals(Identifiers.UInt32)) {
        UnsignedInteger value = (UnsignedInteger) dataValue.getValue().getValue();
        for (EnumValueType e : item.getEnumValues()) {
          if (UnsignedInteger.valueOf(e.getValue()) == value) {
            item.setValueAsText(e.getDisplayName());
            return false;
          }
        }
        throw new StatusException(StatusCodes.Bad_OutOfRange);
      } else if (dataType.equals(Identifiers.UInt64)) {
        UnsignedLong value = (UnsignedLong) dataValue.getValue().getValue();
        for (EnumValueType e : item.getEnumValues()) {
          if (UnsignedLong.valueOf(e.getValue()) == value) {
            item.setValueAsText(e.getDisplayName());
            return false;
          }
        }
        throw new StatusException(StatusCodes.Bad_OutOfRange);
      } else {
        // Allows anything to be written for other DataTypes
        return false;
      }
    }

    // Similar handler for MultiStateDiscreteType
    if (valueNode instanceof MultiStateDiscreteType) {
      MultiStateDiscreteType item = (MultiStateDiscreteType) valueNode;
      UnsignedInteger value = (UnsignedInteger) dataValue.getValue().getValue();
      if (value.intValue() < item.getEnumStrings().length) {
        return false;
      } else {
        throw new StatusException(StatusCodes.Bad_OutOfRange);
      }
    }
    return false;
  }

  /**
   * Converts an Object to Double to compare it against Range.
   * 
   * @param value the Object to convert
   * @return the Object as Double
   */

  private Double objectToDouble(Object value, UaValueNode node) {
    // Return null when value or node is null
    if (value == null || node == null) {
      return null;
    }
    Double doubleValue;
    // TODO do node more efficiently and safely
    if (node.getDataTypeId() == null) {
      doubleValue = null;
    } else if (node.getDataTypeId().equals(Identifiers.Double)) {
      doubleValue = (Double) value;
    } else if (node.getDataTypeId().equals(Identifiers.Byte)) {
      UnsignedByte unsignedByteValue = (UnsignedByte) value;
      doubleValue = unsignedByteValue.doubleValue();
    } else if (node.getDataTypeId().equals(Identifiers.Float)) {
      Float floatValue = (Float) value;
      doubleValue = floatValue.doubleValue();
    } else if (node.getDataTypeId().equals(Identifiers.Int16)) {
      Short shortValue = (Short) value;
      doubleValue = shortValue.doubleValue();
    } else if (node.getDataTypeId().equals(Identifiers.Int32)) {
      Integer integerValue = (Integer) value;
      doubleValue = integerValue.doubleValue();
    } else if (node.getDataTypeId().equals(Identifiers.Int64)) {
      Long longValue = (Long) value;
      doubleValue = longValue.doubleValue();
    } else if (node.getDataTypeId().equals(Identifiers.SByte)) {
      Byte signedByteValue = (Byte) value;
      doubleValue = signedByteValue.doubleValue();
    } else if (node.getDataTypeId().equals(Identifiers.UInt16)) {
      UnsignedShort unsignedShortValue = (UnsignedShort) value;
      doubleValue = unsignedShortValue.doubleValue();
    } else if (node.getDataTypeId().equals(Identifiers.UInt32)) {
      UnsignedInteger unsignedIntegerValue = (UnsignedInteger) value;
      doubleValue = unsignedIntegerValue.doubleValue();
    } else if (node.getDataTypeId().equals(Identifiers.UInt64)) {
      UnsignedLong unsignedLongValue = (UnsignedLong) value;
      doubleValue = unsignedLongValue.doubleValue();
    } else {
      doubleValue = null;
    }
    return doubleValue;
  }

}
