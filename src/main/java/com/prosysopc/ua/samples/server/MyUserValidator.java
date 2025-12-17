/**
 * Prosys OPC UA Java SDK
 * Copyright (c) Prosys OPC Ltd.
 * <http://www.prosysopc.com>
 * All rights reserved.
 */
package com.prosysopc.ua.samples.server;

import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.server.ServerUserIdentity;
import com.prosysopc.ua.server.Session;
import com.prosysopc.ua.server.UserValidator;
import com.prosysopc.ua.stack.builtintypes.StatusCode;
import com.prosysopc.ua.stack.core.StatusCodes;
import com.prosysopc.ua.stack.core.UserIdentityToken;
import com.prosysopc.ua.stack.core.UserTokenType;
import com.prosysopc.ua.stack.transport.security.CertificateValidator;

/**
 * A sample implementation of the UserValidator.
 */
public class MyUserValidator implements UserValidator {

  private final CertificateValidator validator;

  public MyUserValidator(CertificateValidator validator) {
    this.validator = validator;
  }

  @Override
  public boolean onValidate(Session session, ServerUserIdentity userIdentity) throws StatusException {
    // Return true, if the user is allowed access to the server
    // Note that the UserIdentity can be of different actual types,
    // depending on the selected authentication mode (by the client).
    SampleConsoleServer.println("onValidate: userIdentity=" + userIdentity);
    if (userIdentity.getType().equals(UserTokenType.UserName)) {
      if (userIdentity.getName().equals("opcua") && userIdentity.getPassword().equals("opcua")) {
        return true;
      } else if (userIdentity.getName().equals("opcua2") && userIdentity.getPassword().equals("opcua2")) {
        return true;
      } else if (userIdentity.getName().equals("opcua3") && userIdentity.getPassword().equals("opcua3")) {
        // CTT wants a case where a combination of user name and password is denied by the Server
        // Also, CTT wants this particular StatusCode instead of Bad_IdentityTokenRejected
        throw new StatusException(StatusCodes.Bad_UserAccessDenied);
      } else {
        // Perhaps Bad_UserAccessDenied should be thrown here as well?
        return false;
      }
    }
    if (userIdentity.getType().equals(UserTokenType.Certificate)) {
      // Get StatusCode for the certificate
      StatusCode code = this.validator.validateCertificate(userIdentity.getCertificate());
      if (code.isGood()) {
        return true;
      } else {
        // SessionManager will throw Bad_IdentityTokenRejected when this method returns false
        return false;
      }
    }

    return true;
  }

  @Override
  public void onValidationError(Session session, UserIdentityToken userToken, Exception exception) {
    SampleConsoleServer
        .println("onValidationError: User validation failed: userToken=" + userToken + " error=" + exception);
  }

}
