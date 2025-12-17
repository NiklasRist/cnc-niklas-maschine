/**
 * Prosys OPC UA Java SDK
 * Copyright (c) Prosys OPC Ltd.
 * <http://www.prosysopc.com>
 * All rights reserved.
 */
package com.prosysopc.ua.samples.server.compliancenodes;

import java.util.EnumSet;

import com.prosysopc.ua.stack.builtintypes.NodeId;
import com.prosysopc.ua.stack.core.Identifiers;
import com.prosysopc.ua.stack.core.XVType;

/**
 * Enumeration for creating instances of the subtypes of ArrayItemType.
 *
 */
public enum ArrayItem implements CommonComplianceInfo {

  CUBE("Cube", Identifiers.Int32,
      new Integer[][][] {{{1, 2}, {3, 4}, {5, 6}}, {{7, 8}, {9, 10}, {11, 12}}, {{13, 14}, {15, 16}, {17, 18}}}),

  IMAGE("Image", Identifiers.Int32, new Integer[][] {{1, 2, 3, 4, 5}, {6, 7, 8, 9, 10}}),

  NDIMENSION("NDimension", Identifiers.Int32,
      new Integer[][][][] {{{{1, 2}, {3, 4}}, {{5, 6}, {7, 8}}}, {{{9, 10}, {11, 12}}, {{13, 14}, {15, 16}}}}),

  XY("XY", Identifiers.XVType, new XVType[] {new XVType(1.0, 1f), new XVType(2.0, 2f), new XVType(3.0, 3f),
      new XVType(4.0, 4f), new XVType(5.0, 5f)}),

  Y("Y", Identifiers.Int32, new Integer[] {1, 2, 3, 4, 5});

  public static final EnumSet<ArrayItem> ARRAY_ITEMS = EnumSet.of(CUBE, IMAGE, NDIMENSION, XY, Y);

  private String baseName;
  private NodeId dataTypeId;
  private Object initialValue;

  private ArrayItem(String baseName, NodeId dataTypeId, Object initialValue) {
    this.baseName = baseName;
    this.dataTypeId = dataTypeId;
    this.initialValue = initialValue;
  }

  @Override
  public String getBaseName() {
    return this.baseName;
  }

  @Override
  public NodeId getDataTypeId() {
    return this.dataTypeId;

  }

  @Override
  public Object getInitialValue() {
    return this.initialValue;
  }
}
