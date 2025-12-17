/**
 * Prosys OPC UA Java SDK
 * Copyright (c) Prosys OPC Ltd.
 * <http://www.prosysopc.com>
 * All rights reserved.
 */
package com.prosysopc.ua.samples.server;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.nodes.UaMethod;
import com.prosysopc.ua.nodes.UaNode;
import com.prosysopc.ua.server.CallableListener;
import com.prosysopc.ua.server.MethodManager;
import com.prosysopc.ua.server.ServiceContext;
import com.prosysopc.ua.stack.builtintypes.DiagnosticInfo;
import com.prosysopc.ua.stack.builtintypes.NodeId;
import com.prosysopc.ua.stack.builtintypes.StatusCode;
import com.prosysopc.ua.stack.builtintypes.Variant;
import com.prosysopc.ua.stack.core.StatusCodes;

/**
 * CallableListener for compliance Methods used in CTT tests.
 *
 */

public class ComplianceMethodListener implements CallableListener {

  private static Logger logger = LoggerFactory.getLogger(ComplianceMethodListener.class);

  private final UaNode complianceMethod;
  private final boolean hasInput;
  private final boolean hasOutput;

  /**
   * Creates a new ComplianceMethodListener for the provided Method Node.
   * 
   * @param complianceMethod the compliance Method Node
   * @param hasInput is true if the compliance Method has input arguments
   * @param hasOutput is true if the compliance Method has output arguments
   */

  public ComplianceMethodListener(UaNode complianceMethod, boolean hasInput, boolean hasOutput) {
    super();
    this.complianceMethod = complianceMethod;
    this.hasInput = hasInput;
    this.hasOutput = hasOutput;
  }

  @Override
  public boolean onCall(ServiceContext serviceContext, NodeId objectId, UaNode object, NodeId methodId, UaMethod method,
      final Variant[] inputArguments, final StatusCode[] inputArgumentResults,
      final DiagnosticInfo[] inputArgumentDiagnosticInfos, final Variant[] outputs) throws StatusException {

    if (methodId.equals(complianceMethod.getNodeId())) {

      // Log Method call
      if (hasInput) {
        logger.info("{} : {}", method.getDisplayName().getText(), Arrays.toString(inputArguments));
      } else {
        logger.info("{} : no input arguments", method.getDisplayName().getText());
      }

      double input;
      double result;

      if (hasInput) {
        // Check the inputs
        MethodManager.checkInputArguments(new Class[] {Double.class}, inputArguments, inputArgumentResults,
            inputArgumentDiagnosticInfos, false);
        try {
          // Try to perform a simple calculation with input
          input = inputArguments[0].doubleValue();
          result = -(2 * input + 1);
        } catch (ClassCastException e) {
          throw inputError(1, e.getMessage(), inputArgumentResults, inputArgumentDiagnosticInfos);
        }
      } else {
        // If the Method has no input arguments, return 1.2345
        result = 1.2345;
      }

      if (hasOutput) {
        // Add result to output arguments
        outputs[0] = new Variant(result);
      } else {
        // If the Method has no output arguments, just print the result
        System.out.println(complianceMethod.getDisplayName().getText() + " output: " + result);
      }

      return true;
    } else {
      return false;
    }
  }

  /**
   * Handle an error in method inputs. Same as MyMethodManagerListener.inputError.
   *
   * @param index index of the failing input
   * @param message error message
   * @param inputArgumentResults the results array to fill in
   * @param inputArgumentDiagnosticInfos the diagnostics array to fill in
   * @return StatusException that can be thrown to break further method handling
   */
  private StatusException inputError(final int index, final String message, StatusCode[] inputArgumentResults,
      DiagnosticInfo[] inputArgumentDiagnosticInfos) {
    logger.info("inputError: #{} message={}", index, message);
    inputArgumentResults[index] = StatusCode.valueOf(StatusCodes.Bad_InvalidArgument);
    final DiagnosticInfo di = new DiagnosticInfo();
    di.setAdditionalInfo(message);
    inputArgumentDiagnosticInfos[index] = di;
    return new StatusException(StatusCodes.Bad_InvalidArgument);
  }

}
