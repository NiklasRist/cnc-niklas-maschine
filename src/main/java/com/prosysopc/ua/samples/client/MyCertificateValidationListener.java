/**
 * Prosys OPC UA Java SDK
 * Copyright (c) Prosys OPC Ltd.
 * <http://www.prosysopc.com>
 * All rights reserved.
 */
package com.prosysopc.ua.samples.client;

import java.security.cert.CertificateParsingException;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.prosysopc.ua.client.UaClient;
import com.prosysopc.ua.stack.cert.CertificateCheck;
import com.prosysopc.ua.stack.cert.DefaultCertificateValidatorListener;
import com.prosysopc.ua.stack.cert.ValidationResult;
import com.prosysopc.ua.stack.core.ApplicationDescription;
import com.prosysopc.ua.stack.transport.security.Cert;
import com.prosysopc.ua.stack.utils.CertificateUtils;

/**
 * A sampler listener for certificate validation results.
 */
public class MyCertificateValidationListener implements DefaultCertificateValidatorListener {

  private final UaClient client;
  private final boolean disableHostnameVerification;

  public MyCertificateValidationListener(UaClient client, boolean disableHostnameVerification) {
    this.client = client;
    this.disableHostnameVerification = disableHostnameVerification;
  }

  @Override
  public ValidationResult onValidate(Cert certificate, ApplicationDescription applicationDescription,
      EnumSet<CertificateCheck> passedChecks) {
    // Called whenever the PkiFileBasedCertificateValidator has
    // validated a certificate
    println("");
    println("*** The Server Certificate : ");
    println("");
    println("Subject   : " + certificate.getCertificate().getSubjectX500Principal().toString());
    println("Issued by : " + certificate.getCertificate().getIssuerX500Principal().toString());
    println("Valid from: " + certificate.getCertificate().getNotBefore().toString());
    println("        to: " + certificate.getCertificate().getNotAfter().toString());
    println("");
    if (!passedChecks.contains(CertificateCheck.Signature)) {
      println("* The Certificate is NOT SIGNED BY A TRUSTED SIGNER!");
    }
    if (!passedChecks.contains(CertificateCheck.Validity)) {
      Date today = new Date();
      final boolean isYoung = certificate.getCertificate().getNotBefore().compareTo(today) > 0;
      final boolean isOld = certificate.getCertificate().getNotAfter().compareTo(today) < 0;
      final String oldOrYoung = isOld ? "(anymore)" : (isYoung ? "(yet)" : "");

      println("* The Certificate time interval IS NOT VALID " + oldOrYoung + "!");
    }
    if (!passedChecks.contains(CertificateCheck.Uri)) {
      println("* The Certificate URI DOES NOT MATCH the ApplicationDescription URI!");
      println("    ApplicationURI in ApplicationDescription = " + applicationDescription.getApplicationUri());
      try {
        println("    ApplicationURI in Certificate            = "
            + CertificateUtils.getApplicationUriOfCertificate(certificate));
      } catch (CertificateParsingException e) {
        println("    ApplicationURI in Certificate is INVALID");
      }
    }
    if (passedChecks.contains(CertificateCheck.SelfSigned)) {
      println("* The Certificate is self-signed.");
    }

    /*
     * Validate that the cert is made for the host we are trying to connect. This is similar to what
     * web browsers do for https connections.
     * 
     * However, the PKI public key infrastructure only in practice works in the public network. OPC
     * UA also mostly doesn't run in public network, but instead on factory floors etc. and
     * connections are often made behind servers that are behind NAT (network address translation)
     * layers.
     * 
     * OPC UA spec says that the endpointUrl of the EndpointDescription should be checked, but also
     * allows clients to use the original address (the NAT case). Thus, this validation must be done
     * using the original connection address.
     * 
     * However, if the server doesn't know about the NAT and the it's certificate was not made with
     * this knowledge, then this check basically always fails. In an ideal world, the server should
     * always know it is behind a NAT and the cert be made include the "NAT address", but this may
     * prove to be close to impossible in practice in some cases. The specification allows this
     * check to be suppressed. Thus, you should build in your application a way to do this. The
     * following method asks the user if there is a mismatch with the option to allow connecting.
     * 
     * Note that the DefaultCertificateValidator works in both client and server side and this is a
     * client-side check only, thus for the time being this must be handled in the application layer
     * (as the DefaultCertificateValidator is not aware of the connection address). Some future SDK
     * version might offer alternative solutions.
     * 
     * Also, this happens if e.g. 'localhost' was used as a connection address, but the server only
     * offers EndpointDescriptions with the hostname part.
     */
    boolean accept = validateHostnameOfCertificate(certificate);
    if (!accept) {
      return ValidationResult.Reject;
    }

    println("");
    // If the certificate is trusted, valid and verified, accept it
    if (passedChecks.containsAll(CertificateCheck.COMPULSORY)) {
      return ValidationResult.AcceptPermanently;
    }
    while (true) {
      // println("Note: If the certificate is not OK,");
      // println("you will be prompted again, even if you answer 'Always' here.");
      // println("");
      println("Do you want to accept this certificate?\n" + " (A=Always, Y=Yes, this time, N=No)\n"
          + " (D=Show Details of the Certificate)");
      String input = readInput().toLowerCase();
      if (input.equalsIgnoreCase("a")) {
        // if the certificate is not valid anymore or the signature
        // is not verified, you will be prompted again, even if you
        // select always here
        return ValidationResult.AcceptPermanently;
      }

      if (input.equalsIgnoreCase("y")) {
        return ValidationResult.AcceptOnce;
      }
      if (input.equalsIgnoreCase("n")) {
        return ValidationResult.Reject;
      }
      if (input.equalsIgnoreCase("d")) {
        println("Certificate Details:" + certificate.getCertificate().toString());
      }
    }
  }

  private void println(String string) {
    SampleConsoleClient.println(string);
  }

  private String readInput() {
    return SampleConsoleClient.readInput(false);
  }

  private boolean validateHostnameOfCertificate(Cert certificate) {
    // NOTE please read the comments on 'onValidate' (where this method is called) first.

    if (disableHostnameVerification) {
      return true;
    }

    // Only done in the normal connection case
    if (client.getReverseAddress() != null) {
      return true;
    }

    try {
      // Check if the Endpoint URL hostname matches the certificate's DNS name or IP address
      String host = client.getAddress().getHost();
      // If the host is a IPv6 address, it may have brackets, removing them
      if (host.startsWith("[")) {
        host = host.substring(1);
      }
      if (host.endsWith("]")) {
        host = host.substring(0, host.length() - 1);
      }

      /*
       * The purpose of this check is basically if you trust a CA certificate (and thus do not check
       * the certs manually) to ensure the CA signed the cert for the address you are trying to
       * connect. Thus an assumption: it is not possible to get a proper CA-signed cert for
       * 'localhost' (https://letsencrypt.org/docs/certificates-for-localhost/). And in general if
       * you are connecting to localhost or a loopback address, the local machine is assumed to be
       * safe.
       * 
       * NOTE! If you feel this assumption is inadequate, let uajava-support know the reasoning.
       * Just note that if you decide to enable this check in your own application also for
       * localhost and loopback address, it will basically fail everywhere, as applications
       * typically make a self-signed cert for the hostname only, thus using 'localhost' or e.g.
       * '127.0.0.1' would fail each time.
       */
      // Based on
      // https://stackoverflow.com/questions/8426171/what-regex-will-match-all-loopback-addresses
      Pattern localhost = Pattern.compile("^localhost$|^127(?:\\.[0-9]+){0,2}\\.[0-9]+$|^(?:0*\\:)*?:?0*1$");
      Matcher matcher = localhost.matcher(host);
      if (matcher.find()) {
        return true;
      }

      List<String> certificateDnsNames = CertificateUtils.getDnsOfCertificate(certificate);
      List<String> certificateIpAddresses = CertificateUtils.getIpOfCertificate(certificate);

      if (certificateDnsNames.contains(host) || certificateIpAddresses.contains(host)) {
        return true;
      }

      // Not found, ask should this check be suppressed
      println("Could not find the hostname '" + host + "' of the connection address '" + client.getAddress()
          + "' in the certificate. ");
      println("Certificate has " + certificateDnsNames.size() + " dns name(s): " + certificateDnsNames);
      println("Certificate has " + certificateIpAddresses.size() + " ip address(es): " + certificateIpAddresses);

      println("It is possible to suppress this validation step (for this connection),"
          + " but it will be asked again on next connection. "
          + "You can use the '-H' flag to disable this check completely");
      while (true) {
        println("Continue? (Y=yes, continue connecting, N=No, close the connection and mark the cert as rejected)");
        String input = readInput().toLowerCase();
        if (input.equalsIgnoreCase("y")) {
          return true;
        }
        if (input.equalsIgnoreCase("n")) {
          return false;
        }
      }
    } catch (CertificateParsingException e) {
      println("* Cannot resolve certificate hostnames, closing connection" + e);
      return false;
    }
  }

}
