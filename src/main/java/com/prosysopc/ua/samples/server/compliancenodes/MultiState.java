/**
 * Prosys OPC UA Java SDK
 * Copyright (c) Prosys OPC Ltd.
 * <http://www.prosysopc.com>
 * All rights reserved.
 */
package com.prosysopc.ua.samples.server.compliancenodes;

import java.util.EnumSet;

import com.prosysopc.ua.stack.builtintypes.NodeId;
import com.prosysopc.ua.stack.builtintypes.UnsignedInteger;
import com.prosysopc.ua.stack.core.Identifiers;

public enum MultiState implements CommonComplianceInfo {
  // MULTI_1 keeps its old name for backwards compatibility
  MULTI_1("MultiStateDiscreteItem", UnsignedInteger.ZERO, "STATE1", "STATE2", "STATE3", "STATE4"),

  MULTI_2("MultiStateDiscreteItem2", UnsignedInteger.ZERO, "STATE1", "STATE2", "STATE3", "STATE4"),

  MULTI_3("MultiStateDiscreteItem3", UnsignedInteger.ZERO, "STATE1", "STATE2", "STATE3", "STATE4"),

  MULTI_4("MultiStateDiscreteItem4", UnsignedInteger.ZERO, "STATE1", "STATE2", "STATE3", "STATE4"),

  MULTI_5("MultiStateDiscreteItem5", UnsignedInteger.ZERO, "STATE1", "STATE2", "STATE3", "STATE4");

  public static final EnumSet<MultiState> MULTISTATE_ITEMS = EnumSet.of(MULTI_1, MULTI_2, MULTI_3, MULTI_4, MULTI_5);
  private final String name;
  private final String[] states;

  private final UnsignedInteger initialValue;

  private MultiState(String name, UnsignedInteger initialValue, String... states) {
    this.name = name;
    this.initialValue = initialValue;
    this.states = states;
  }

  @Override
  public String getBaseName() {
    return name;
  }

  @Override
  public NodeId getDataTypeId() {
    return Identifiers.UInt32;
  }

  @Override
  public UnsignedInteger getInitialValue() {
    return initialValue;
  }

  public String getName() {
    return name;
  }

  public String[] getStates() {
    return states;
  }

}
