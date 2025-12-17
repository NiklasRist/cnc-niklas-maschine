/**
 * Prosys OPC UA Java SDK
 * Copyright (c) Prosys OPC Ltd.
 * <http://www.prosysopc.com>
 * All rights reserved.
 */
package com.prosysopc.ua.samples.server.compliancenodes;

import java.util.EnumSet;

/**
 * Methods for CTT tests.
 *
 */
public enum ComplianceMethod {

  IO("InputOutputMethod", true, true),

  InO("InputMethod", true, false),

  nIO("OutputMethod", false, true),

  nInO("NoInputNoOutputMethod", false, false);

  public static final EnumSet<ComplianceMethod> COMPLIANCE_METHODS = EnumSet.of(IO, InO, nIO, nInO);

  private final String name;
  private final boolean input;
  private final boolean output;

  private ComplianceMethod(String name, boolean input, boolean output) {
    this.name = name;
    this.input = input;
    this.output = output;
  }

  public String getName() {
    return this.name;
  }

  public boolean hasInput() {
    return this.input;
  }

  public boolean hasOutput() {
    return this.output;
  }

}
