/**
 * Prosys OPC UA Java SDK
 * Copyright (c) Prosys OPC Ltd.
 * <http://www.prosysopc.com>
 * All rights reserved.
 */
package com.prosysopc.ua.samples.server.compliancenodes;

import java.util.EnumSet;

import com.prosysopc.ua.stack.builtintypes.LocalizedText;
import com.prosysopc.ua.stack.builtintypes.NodeId;
import com.prosysopc.ua.stack.core.EnumValueType;
import com.prosysopc.ua.stack.core.Identifiers;

/**
 * Enumeration for creating instances of MultiStateValueDiscreteType with different DataTypes.
 *
 */
public enum MultiStateValue implements CommonComplianceInfo {

  MULTIVALUE_1("MultiValue1", 0, Identifiers.Byte,
      new EnumValueType[] {
          new EnumValueType((long) 0, LocalizedText.english("State1"), LocalizedText.english("The first state")),
          new EnumValueType((long) 1, LocalizedText.english("State2"), LocalizedText.english("The second state")),
          new EnumValueType((long) 2, LocalizedText.english("State3"), LocalizedText.english("The third state")),
          new EnumValueType((long) 4, LocalizedText.english("State4"), LocalizedText.english("The fourth state"))}),

  MULTIVALUE_2("MultiValue2", 0, Identifiers.Int16,
      new EnumValueType[] {
          new EnumValueType((long) 0, LocalizedText.english("State1"), LocalizedText.english("The first state")),
          new EnumValueType((long) 1, LocalizedText.english("State2"), LocalizedText.english("The second state")),
          new EnumValueType((long) 2, LocalizedText.english("State3"), LocalizedText.english("The third state")),
          new EnumValueType((long) 4, LocalizedText.english("State4"), LocalizedText.english("The fourth state"))}),

  MULTIVALUE_3("MultiValue3", 0, Identifiers.Int32,
      new EnumValueType[] {
          new EnumValueType((long) 0, LocalizedText.english("State1"), LocalizedText.english("The first state")),
          new EnumValueType((long) 1, LocalizedText.english("State2"), LocalizedText.english("The second state")),
          new EnumValueType((long) 2, LocalizedText.english("State3"), LocalizedText.english("The third state")),
          new EnumValueType((long) 4, LocalizedText.english("State4"), LocalizedText.english("The fourth state"))}),

  MULTIVALUE_4("MultiValue4", 0, Identifiers.Int64,
      new EnumValueType[] {
          new EnumValueType((long) 0, LocalizedText.english("State1"), LocalizedText.english("The first state")),
          new EnumValueType((long) 1, LocalizedText.english("State2"), LocalizedText.english("The second state")),
          new EnumValueType((long) 2, LocalizedText.english("State3"), LocalizedText.english("The third state")),
          new EnumValueType((long) 4, LocalizedText.english("State4"), LocalizedText.english("The fourth state"))}),

  MULTIVALUE_5("MultiValue5", 0, Identifiers.SByte,
      new EnumValueType[] {
          new EnumValueType((long) 0, LocalizedText.english("State1"), LocalizedText.english("The first state")),
          new EnumValueType((long) 1, LocalizedText.english("State2"), LocalizedText.english("The second state")),
          new EnumValueType((long) 2, LocalizedText.english("State3"), LocalizedText.english("The third state")),
          new EnumValueType((long) 4, LocalizedText.english("State4"), LocalizedText.english("The fourth state"))}),

  MULTIVALUE_6("MultiValue6", 0, Identifiers.UInt16,
      new EnumValueType[] {
          new EnumValueType((long) 0, LocalizedText.english("State1"), LocalizedText.english("The first state")),
          new EnumValueType((long) 1, LocalizedText.english("State2"), LocalizedText.english("The second state")),
          new EnumValueType((long) 2, LocalizedText.english("State3"), LocalizedText.english("The third state")),
          new EnumValueType((long) 4, LocalizedText.english("State4"), LocalizedText.english("The fourth state"))}),

  MULTIVALUE_7("MultiValue7", 0, Identifiers.UInt32,
      new EnumValueType[] {
          new EnumValueType((long) 0, LocalizedText.english("State1"), LocalizedText.english("The first state")),
          new EnumValueType((long) 1, LocalizedText.english("State2"), LocalizedText.english("The second state")),
          new EnumValueType((long) 2, LocalizedText.english("State3"), LocalizedText.english("The third state")),
          new EnumValueType((long) 4, LocalizedText.english("State4"), LocalizedText.english("The fourth state"))}),

  MULTIVALUE_8("MultiValue8", 0, Identifiers.UInt64,
      new EnumValueType[] {
          new EnumValueType((long) 0, LocalizedText.english("State1"), LocalizedText.english("The first state")),
          new EnumValueType((long) 1, LocalizedText.english("State2"), LocalizedText.english("The second state")),
          new EnumValueType((long) 2, LocalizedText.english("State3"), LocalizedText.english("The third state")),
          new EnumValueType((long) 4, LocalizedText.english("State4"), LocalizedText.english("The fourth state"))});

  public static final EnumSet<MultiStateValue> MULTISTATEVALUE_ITEMS = EnumSet.of(MULTIVALUE_1, MULTIVALUE_2,
      MULTIVALUE_3, MULTIVALUE_4, MULTIVALUE_5, MULTIVALUE_6, MULTIVALUE_7, MULTIVALUE_8);

  private String name;
  private long initialValue;
  private EnumValueType[] states;
  private NodeId dataType;

  private MultiStateValue(String name, long initialValue, NodeId dataType, EnumValueType... states) {
    this.name = name;
    this.initialValue = initialValue;
    this.dataType = dataType;
    this.states = states;
  }

  @Override
  public String getBaseName() {
    return this.name;
  }

  @Override
  public NodeId getDataTypeId() {
    return this.dataType;
  }

  @Override
  public Long getInitialValue() {
    return this.initialValue;
  }

  public String getName() {
    return this.name;
  }

  public EnumValueType[] getStates() {
    return this.states;
  }

}
