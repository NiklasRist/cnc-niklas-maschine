/**
 * Prosys OPC UA Java SDK
 * Copyright (c) Prosys OPC Ltd.
 * <http://www.prosysopc.com>
 * All rights reserved.
 */
package com.prosysopc.ua.samples.server;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import com.prosysopc.ua.server.nodes.PlainVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.prosysopc.ua.ApplicationIdentity;
import com.prosysopc.ua.SecureIdentityException;
import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.UaApplication;
import com.prosysopc.ua.UaApplication.Protocol;
import com.prosysopc.ua.UserTokenPolicies;
import com.prosysopc.ua.nodes.UaProperty;
import com.prosysopc.ua.samples.server.compliancenodes.ComplianceNodeManager;
import com.prosysopc.ua.samples.server.compliancenodes.NonUaNodeComplianceNodeManager;
import com.prosysopc.ua.server.NodeBuilderException;
import com.prosysopc.ua.server.NodeManagerListener;
import com.prosysopc.ua.server.UaInstantiationException;
import com.prosysopc.ua.server.UaServer;
import com.prosysopc.ua.server.UaServerException;
import com.prosysopc.ua.server.UserValidator;
import com.prosysopc.ua.server.nodes.UaObjectNode;
import com.prosysopc.ua.server.nodes.UaVariableNode;
import com.prosysopc.ua.stack.builtintypes.DateTime;
import com.prosysopc.ua.stack.builtintypes.LocalizedText;
import com.prosysopc.ua.stack.builtintypes.QualifiedName;
import com.prosysopc.ua.stack.cert.DefaultCertificateValidator;
import com.prosysopc.ua.stack.cert.DefaultCertificateValidatorListener;
import com.prosysopc.ua.stack.cert.PkiDirectoryCertificateStore;
import com.prosysopc.ua.stack.core.ApplicationDescription;
import com.prosysopc.ua.stack.core.ApplicationType;
import com.prosysopc.ua.stack.core.EndpointDescription;
import com.prosysopc.ua.stack.core.MessageSecurityMode;
import com.prosysopc.ua.stack.core.ServerState;
import com.prosysopc.ua.stack.core.ServerStatusDataType;
import com.prosysopc.ua.stack.transport.security.HttpsSecurityPolicy;
import com.prosysopc.ua.stack.transport.security.KeyPair;
import com.prosysopc.ua.stack.transport.security.SecurityMode;
import com.prosysopc.ua.stack.transport.security.SecurityPolicy;
import com.prosysopc.ua.types.opcua.server.BuildInfoTypeNode;
import com.prosysopc.ua.types.opcua.server.ServerCapabilitiesTypeNode;
//import com.prosysopc.ua.samples.server.CncNodeManager;

/**
 * A sample OPC UA server application.
 */
public class SampleConsoleServer {
  protected CncNodeManager cncNodeManager;

  enum Action {
    ADD_NODE('a', "add a new node") {
      @Override
      ActionResult performAction(SampleConsoleServer s) {
        println("Enter the name of the new node (enter 'x' to cancel)");
        String name = readInput();
        if (!name.equals("x")) {
          s.myNodeManager.addNode(name);
        }
        return ActionResult.NOTHING;
      }
    },

    ENABLE_NODE_MANAGEMENT('N', "enable/disable node management services") {
      @Override
      ActionResult performAction(SampleConsoleServer s) {
        boolean value = !s.getServer().getAddressSpace().getNodeManagementEnabled();
        s.getServer().getAddressSpace().setNodeManagementEnabled(value);
        println("NodeManagement " + (value ? "enabled" : "disabled"));
        return ActionResult.NOTHING;
      }
    },

    ADD_REVERSE_CONNECTION('r', "Add a Reverse Connection to a Client") {
      @Override
      ActionResult performAction(SampleConsoleServer s) throws Exception {
        addReverseConnection(s);
        return ActionResult.NOTHING;
      }
    },

    TOGGLE_SERVERSTATE('S', "Toggle ServerState between Running and Failed") {
      @Override
      ActionResult performAction(SampleConsoleServer s) throws Exception {
        ServerStatusDataType serverStatus = s.getServer().getNodeManagerRoot().getServerData().getServerStatus();
        if (serverStatus.getState() == ServerState.Running) {
          serverStatus.setState(ServerState.Failed);
        } else {
          serverStatus.setState(ServerState.Running);
        }
        SampleConsoleServer.println("ServerState changed to " + serverStatus.getState());
        return ActionResult.NOTHING;
      }
    },

    CLOSE('x', "close the server") {
      @Override
      ActionResult performAction(SampleConsoleServer s) {
        return ActionResult.CLOSE_SERVER;
      }
    },

    DELETE_NODE('d', "delete a node") {
      @Override
      ActionResult performAction(SampleConsoleServer s) throws StatusException {
        println("Enter the name of the node to delete (enter 'x' to cancel)");
        String input = readInput();
        if (!input.equals("x")) {
          QualifiedName nodeName = new QualifiedName(s.myNodeManager.getNamespaceIndex(), input);
          s.myNodeManager.deleteNode(nodeName);
        }
        return ActionResult.NOTHING;
      }
    },

    ENABLE_DIAGNOSTICS('D', "enable/disable server diagnostics") {
      @Override
      ActionResult performAction(SampleConsoleServer s) throws StatusException {
        final UaProperty enabledFlag =
                s.server.getNodeManagerRoot().getServerData().getServerDiagnosticsNode().getEnabledFlagNode();
        boolean newValue = !((Boolean) enabledFlag.getValue().getValue().getValue());
        enabledFlag.setValue(Boolean.valueOf(newValue));
        println("Server Diagnostics " + (newValue ? "Enabled" : "Disabled"));
        return ActionResult.NOTHING;
      }
    },

    SEND_EVENT('e', "send an event") {
      @Override
      ActionResult performAction(SampleConsoleServer s) {
        s.sendEvent();
        return ActionResult.NOTHING;
      }
    };

    static Map<Character, Action> actionMap = new TreeMap<Character, Action>();

    static {
      for (Action a : Action.values()) {
        actionMap.put(a.getKey(), a);
      }
    }

    public static Action parseAction(Character s) {
      return actionMap.get(s);
    }

    private static void addReverseConnection(SampleConsoleServer s) {
      try {
        println("Enter address a Client is listening on Server-side initiated connections, or 'x' to cancel.");
        String radds = readInput();
        if ("x".equalsIgnoreCase(radds)) {
          return;
        }

        /*
         * NOTE! This method automatically figures out the EndpointUrl of the server for
         * ReverseHello Message purposes (which clients sends back in Hello message, this emulates a
         * normal connection). By default and normally servers only have a single EndpointUrl, which
         * is typically based on the hostname of the machine. If there would be multiple, then this
         * method choses one of them, but if more control is needed, or you wish to listen to the
         * status of the connection, please see the overloads of the method.
         */
        s.getServer().addReverseConnection(radds);
      } catch (Exception e) {
        logger.error("Cannot add Reverse Connection", e);
      }
    }

    private final String description;
    private final Character key;

    Action(Character key, String description) {
      this.key = key;
      this.description = description;
    }

    public String getDescription() {
      return description;
    }

    /**
     * @return the key
     */
    public Character getKey() {
      return key;
    }

    /**
     * Perform the Action
     *
     * @param s the SampleConsoleServer instance (inner enums are static, so this is a "trick" to
     *        access SampleConsoleServer's fields from the inner enum)
     * @return ActionResult
     * @throws Exception
     */
    abstract ActionResult performAction(SampleConsoleServer s) throws Exception;
  }

  enum ActionResult {
    CLOSE_SERVER, NOTHING;
  }

  /**
   * Number of nodes to create for the Big Node Manager. This can be modified from the command line.
   */
  private static int bigAddressSpaceNodes = 1000;
  private static Logger logger = LoggerFactory.getLogger(SampleConsoleServer.class);
  private static boolean stackTraceOnException = false;
  protected static int certKeySize = 2048;
  protected static String APP_NAME = "SampleConsoleServer";

  protected static String discoveryServerUrl = "";

  protected static int port = 52520;
  protected static int httpsPort = 0;

  private static boolean enableServerDiagnostics = false;

  private static Scanner scanner = new Scanner(System.in);

  /**
   * @param args command line arguments for the application
   * @throws StatusException if the server address space creation fails
   * @throws UaServerException if the server initialization parameters are invalid
   * @throws CertificateException if the application certificate or private key, cannot be loaded
   *         from the files due to certificate errors
   */
  public static void main(String[] args) throws Exception {
    if (!parseCmdLineArgs(args)) {
      return;
    }
    logger.info("Starting {}", APP_NAME);

    // *** Initialization and Start Up
    SampleConsoleServer sampleConsoleServer = new SampleConsoleServer();

    // Initialize the server
    sampleConsoleServer.initialize(port, httpsPort, APP_NAME);

    // Create the address space
    sampleConsoleServer.createAddressSpace();

    // TCP Buffer size parameters - this may help with high traffic
    // situations.
    // See http://fasterdata.es.net/host-tuning/background/ for some hints
    // how to use it
    // UATcpServer.setReceiveBufferSize(700000);

    // Start the server, when you have finished your own initializations
    // This will allow connections from the clients
    // Start up the server

    sampleConsoleServer.run();
  }

  /**
   * @param e
   */
  public static void printException(Exception e) {
    if (stackTraceOnException) {
      e.printStackTrace();
    } else {
      println(e.toString());
      if (e.getCause() != null) {
        println("Caused by: " + e.getCause());
      }
    }
  }

  /**
   * @param string
   */
  public static void println(String string) {
    System.out.println(string);
  }

  /**
   * @return
   */
  private static Action readAction() {
    return Action.parseAction(readInput().charAt(0));
  }

  /**
   * @return
   */
  private static String readInput() {
    return scanner.nextLine();
  }

  private static void waitAtStart() {
    // Can be used for e.g. for debugging or waiting while attaching a profiler
    println("Press enter to start...");
    readInput();
  }

  /**
   * Parse command line arguments. See {@link #usage()} for a description of the options.
   *
   * @param args the arguments
   * @return true, if valid options were given.
   */
  protected static boolean parseCmdLineArgs(String[] args) throws IllegalArgumentException {
    int i = 0;
    while ((args.length > i) && ((args[i].startsWith("-") || args[i].startsWith("/")))) {
      if (args[i].equals("-t")) {
        stackTraceOnException = true;
      } else if (args[i].equals("-b")) {
        bigAddressSpaceNodes = Integer.parseInt(args[++i]);
      } else if (args[i].equals("-k")) {
        certKeySize = Integer.parseInt(args[++i]);
      } else if (args[i].equals("-d")) {
        discoveryServerUrl = args[++i];
      } else if (args[i].equals("-d-")) {
        discoveryServerUrl = "";
      } else if ((args[i].equals("-D") || (args[i].equals("--enablesessiondiags")))) {
        enableServerDiagnostics = true;
      } else if (args[i].equals("-P")) {
        httpsPort = Integer.parseInt(args[++i]);
      } else if (args[i].equals("-p")) {
        port = Integer.parseInt(args[++i]);
      } else if (args[i].equals("-w")) {
        waitAtStart();
      } else if (args[i].equals("-?")) {
        usage();
        return false;
      } else {
        println("Invalid cmd line argument: " + args[i]);
        usage();
        return false;
      }
      i++;
    }
    return true;
  }


  protected static void usage() {
    println("Usage: " + APP_NAME + " [-b] [-t] [serverUri]");
    println("   -b n       Define number of nodes to create in the BigNodeManager (default=1000)");
    println("   -k keySize Define the size of the public key of the "
            + "application certificate (default 2048; other valid values 1024, 4096)");
    println("   -d url     Define the DiscoveryServerUrl to register the application to");
    println("   -d-        Define that the application should not be registered to a DiscoveryServer");
    println("   -p port    Define the port number for UA TCP protocol (default=52520)");
    println(
            "   -P port    Define the port number for UA HTTPS protocol (default=0 (not initialized), use 52521 for example)");
    println("   -t         Output stack trace for errors");
    println("   -w         Wait for input before starting");
    println("   -D, --enablesessiondiags  Enable server diagnostics by default");
    println("   -?         Show this help text");
    println("");
  }

  static void printMenu() {
    println("");
    println("");
    println("");
    println("-------------------------------------------------------");
    for (Entry<Character, Action> a : Action.actionMap.entrySet()) {
      println("- Enter " + a.getKey() + " to " + a.getValue().getDescription());
    }
  }

  private final Runnable simulationTask = new Runnable() {

    @Override
    public void run() {
      if (server.isRunning()) {
        logger.debug("Simulating");
        simulate();
      }
    }
  };
  private final ScheduledExecutorService simulator = Executors.newScheduledThreadPool(10);
  protected ComplianceNodeManager complianceNodeManager;
  protected MyBigNodeManager myBigNodeManager;
  protected MyHistorian myHistorian;
  protected MyNodeManager myNodeManager;
  protected NodeManagerListener myNodeManagerListener = new MyNodeManagerListener();
  protected NonUaNodeComplianceNodeManager nonUaNodeComplianceManager;
  protected UaServer server;
  protected UserValidator userValidator;
  protected final DefaultCertificateValidatorListener validationListener = new MyCertificateValidationListener();
  protected final DefaultCertificateValidatorListener userCertificateValidationListener =
          new MyCertificateValidationListener();

  public UaServer getServer() {
    return server;
  }

  /**
   * Create a sample node manager, which does not use UaNode objects. These are suitable for
   * managing big address spaces for data that is in practice available from another existing
   * subsystem.
   */
  private void createBigNodeManager() {
    myBigNodeManager =
            new MyBigNodeManager(server, "http://www.prosysopc.com/OPCUA/SampleBigAddressSpace", bigAddressSpaceNodes);
  }

  private void printConnectionAddresses() {
    // Prints connection address that clients can use.
    // NOTE! if multiple SecurityModes are supported, each will have their own EndpointDescription
    EndpointDescription prev = null;
    List<String> lines = new ArrayList<>();
    for (EndpointDescription ed : server.getEndpoints()) {
      if (prev == null || ed.getEndpointUrl() != prev.getEndpointUrl()) {
        lines.add(ed.getEndpointUrl());
        prev = ed;
      }
    }
    if (lines.size() > 1) {
      println("Server started with connection addresses:");
    } else {
      println("Server started with connection address:");
    }
    lines.forEach(line -> println(line));
  }

  /**
   * Create a sample address space with a new folder, a device object, a level variable, and an
   * alarm condition.
   *
   * <p>
   * The method demonstrates the basic means to create the nodes and references into the address
   * space.
   *
   * <p>
   * Simulation of the level measurement is defined in {@link #startSimulation()}
   *
   * @throws StatusException if the referred type nodes are not found from the address space
   * @throws UaInstantiationException if some of the the nodes could not be instantiated
   * @throws NodeBuilderException
   *
   */
  protected void createAddressSpace() throws StatusException, UaInstantiationException, NodeBuilderException {
    // My Node Manager
    cncNodeManager = new CncNodeManager(server, "http://example.com/CNC");

    // My I/O Manager Listener
    //myNodeManager.getIoManager().addListeners(new MyIoManagerListener());

    // My HistoryManager
    //myNodeManager.getHistoryManager().setListener(myHistorian);

    // ComplianceNodeManagers
    complianceNodeManager = new ComplianceNodeManager(server, "http://www.prosysopc.com/OPCUA/ComplianceNodes");
    nonUaNodeComplianceManager =
            new NonUaNodeComplianceNodeManager(server, "http://www.prosysopc.com/OPCUA/ComplianceNonUaNodes");

    // A sample node manager that can handle a big amount of UA nodes
    // without creating UaNode objects in memory
    createBigNodeManager();

    // Load the standard information models
    loadInformationModels();

    logger.info("Address space created.");
  }

  /**
   * The ApplicationDescription provides information about this application to other applications.
   */
  protected ApplicationDescription initApplicationDescription(String applicationName, ApplicationType applicationType) {
    ApplicationDescription applicationDescription = new ApplicationDescription();
    // 'localhost' in the ApplicationName and ApplicationURI is converted to the actual host name of
    // the computer in which the application is run.
    applicationDescription.setApplicationName(new LocalizedText(applicationName + "@localhost"));
    // ApplicationUri defines a unique identifier for each application instance-. Therefore, we use
    // the actual computer name to ensure that it gets assigned differently in every installation.
    applicationDescription.setApplicationUri("urn:localhost:OPCUA:" + applicationName);
    // ProductUri should refer to your own company, since it identifies your product
    applicationDescription.setProductUri("urn:prosysopc.com:OPCUA:" + applicationName);
    applicationDescription.setApplicationType(applicationType);
    return applicationDescription;
  }

  /**
   * Initialize the ApplicationIdentity and ApplicationInstanceCertificate
   *
   * @param privatePath
   */
  protected ApplicationIdentity initApplicationIdentity(ApplicationDescription applicationDescription,
                                                        final PkiDirectoryCertificateStore applicationCertificateStore, File privatePath)
          throws SecureIdentityException, IOException {
    /*
     * Every OPC UA application requires an Application Instance Certificate and a related Private
     * Key.
     *
     * They are used to Sign and Encrypt messages, but also to authenticate the applications with
     * each other. You can also configure, which applications may communicate with your application
     * - with the help of the CertificateValidator that was configured above.
     *
     * Typically, applications can create a Self-Signed Certificate automatically without any
     * Issuer.
     *
     * In a proper installation, you would use an external Certificate Authority (CA) to sign the
     * Application Instance Certificates and provide them to the applications.
     */

    logger.info("Initializing certificates..");

    // Optional Issuer Certificate. In this context the Issuer Keys are created and used for testing
    KeyPair issuerCertificate = null;

    // The size of the security keys to create. The KeySize should be either 2048 (default) or 4096.
    int[] keySizes = new int[] {certKeySize};
    // If you wish to use big certificates (4096 bits), you will need to
    // define two certificates for your application, since older applications and security policies
    // may not be able to use keys of that size.
    // keySizes = new int[] { 2048, 4096 };

    // Password to be used in generated private keys, or null if password not used. NOTE! A real
    // application should allow using 3rd-party generated certificates, e.g. one can create them
    // with external tools. Thus, either they must be made without a password, the same password if
    // set here or the application must have a way to enter the password.
    String privateKeyPassword = null;

    // loadOrCreateCertificate will create a new private key and self-signed certificate at first
    // run and saves them to the given folder (privatePath). On the next runs, it will just load
    // them.
    final ApplicationIdentity identity =
            ApplicationIdentity.loadOrCreateCertificate(applicationDescription, "Sample Organisation", privateKeyPassword,
                    privatePath, issuerCertificate, keySizes, /* Enable renewing the certificate */true);

    // NOTE that you can always create the ApplicationIdentity yourself, instead of using
    // loadOrCreateCertificate.

    // Cert certificate = Cert.load(certFile);
    // PrivKey privateKey = PrivKey.loadFromKeyStore(privKeyFile, privateKeyPassword);
    // identity = new ApplicationIdentity(certificate, privateKey);
    return identity;
  }

  /**
   * Initialize the information to the Server BuildInfo structure.
   */
  protected void initBuildInfo() {
    // Initialize BuildInfo - using the version info from the SDK
    // You should replace this with your own build information

    final BuildInfoTypeNode buildInfo =
            server.getNodeManagerRoot().getServerData().getServerStatusNode().getBuildInfoNode();

    buildInfo.setProductName(APP_NAME);

    final String implementationVersion = UaApplication.getSdkVersion();
    if (implementationVersion != null) {
      int splitIndex = implementationVersion.lastIndexOf("-");
      final String softwareVersion = splitIndex == -1 ? "dev" : implementationVersion.substring(0, splitIndex);
      String buildNumber = splitIndex == -1 ? "dev" : implementationVersion.substring(splitIndex + 1);

      buildInfo.setManufacturerName("Prosys OPC Ltd");
      buildInfo.setSoftwareVersion(softwareVersion);
      buildInfo.setBuildNumber(buildNumber);

    }

    final URL classURL = UaServer.class.getResource("/com/prosysopc/ua/samples/server/SampleConsoleServer.class");
    if (classURL != null && classURL.getFile() != null) {
      final File classFile = new File(classURL.getFile());
      buildInfo.setBuildDate(DateTime.fromMillis(classFile.lastModified()));
    }
  }


  /**
   * Initialize History Collection as configured in {@link MyNodeManager}.
   */
  protected void initHistory() {
    for (UaVariableNode v : myNodeManager.getHistorizableVariables()) {
      myHistorian.addVariableHistory(v);
    }
    for (UaObjectNode o : myNodeManager.getHistorizableEvents()) {
      myHistorian.addEventHistory(o);
    }
  }

  /**
   * Initialize OPC UA HTTPS Endpoints for the Server.
   */
  protected void initHttps(ApplicationDescription appDescription, ApplicationIdentity identity, File privatePath,
                           DefaultCertificateValidator certValidator, Set<SecurityPolicy> supportedSecurityPolicies)
          throws IOException, SecureIdentityException, UaServerException {

    // Only initialize OPC UA HTTPS Endpoint, if the Port is defined
    if (httpsPort == 0) {
      return;
    }

    // *** HTTPS Certificate

    // Define a certificate for a Certificate Authority (CA) which is used
    // to issue the keys. Especially
    // the HTTPS certificate should be signed by a CA certificate, in order
    // to make the .NET applications trust it.
    //

    // Password to be used in generated private keys, or null if password not used. NOTE! A real
    // application should allow using 3rd-party generated certificates, e.g. one can create them
    // with external tools. Thus, either they must be made without a password, the same password if
    // set here or the application must have a way to enter the password.
    String privateKeyPassword = null;


    // If you have a real CA, you should use that instead of this sample CA
    // and create the keys with it.
    // Here we use the IssuerCertificate only to sign the HTTPS certificate
    // (below) and not the Application Instance Certificate.
    KeyPair issuerCertificate = ApplicationIdentity.loadOrCreateIssuerCertificate(
            "ProsysSampleCA@" + ApplicationIdentity.getActualHostNameWithoutDomain() + "_https_" + certKeySize, privatePath,
            privateKeyPassword, 3650, false, certKeySize);

    // Create the HTTPS certificate bound to the hostname.
    // The HTTPS certificate must be created, if you enable HTTPS.
    String hostName = ApplicationIdentity.getActualHostName();
    identity.setHttpsCertificate(ApplicationIdentity.loadOrCreateHttpsCertificate(appDescription, hostName,
            privateKeyPassword, issuerCertificate, privatePath, true, certKeySize));

    // *** Port Number

    server.setPort(Protocol.OpcHttps, httpsPort);

    // *** Security Settings

    /*
     *
     * NOTE! The MessageSecurityMode.None for HTTPS means Application level authentication is not
     * used. If used in combination with the UserTokenPolicy ANONYMOUS anyone can access the server
     * (but the traffic is encrypted). HTTPS mode is always encrypted, therefore the given
     * MessageSecurityMode only affects if the UA certificates are exchanged when forming the
     * Session.
     */
    server.getHttpsSecurityModes().addAll(SecurityMode
            .combinations(EnumSet.of(MessageSecurityMode.None, MessageSecurityMode.Sign), supportedSecurityPolicies));

    // The TLS security policies to use for HTTPS
    Set<HttpsSecurityPolicy> supportedHttpsSecurityPolicies = new HashSet<HttpsSecurityPolicy>();
    // (HTTPS was defined starting from OPC UA Specification 1.02)
    supportedHttpsSecurityPolicies.addAll(HttpsSecurityPolicy.ALL_102);
    supportedHttpsSecurityPolicies.addAll(HttpsSecurityPolicy.ALL_103);
    // Only these are recommended by the 1.04 Specification
    supportedHttpsSecurityPolicies.addAll(HttpsSecurityPolicy.ALL_104);
    // 1.05 didn't add any new policies
    supportedHttpsSecurityPolicies.addAll(HttpsSecurityPolicy.ALL_105);
    server.getHttpsSettings().setHttpsSecurityPolicies(supportedHttpsSecurityPolicies);

    // Number of threads to reserve for the HTTPS server, default is 10
    // server.setHttpsWorkerThreadCount(10);

    // Define the certificate validator for the HTTPS certificates;
    // we use the same validator that we use for Application Instance Certificates
    server.getHttpsSettings().setCertificateValidator(certValidator);


  }

  /**
   * Initialize the OPC UA Server.
   */
  protected void initialize(int port, int httpsPort, String applicationName)
          throws SecureIdentityException, IOException, UaServerException {

    // *** Create the server
    server = new UaServer();

    myHistorian = new MyHistorian(server.getAggregateCalculator());

    /*
     * Enable or disable IPv6 networking (enabled by default).
     */
    // server.setEnableIPv6(false);

    // Use PKI files to keep track of the trusted and rejected client
    // certificates...

    final PkiDirectoryCertificateStore applicationCertificateStore = new PkiDirectoryCertificateStore("PKI/CA");
    final PkiDirectoryCertificateStore applicationIssuerCertificateStore =
            new PkiDirectoryCertificateStore("PKI/CA/issuers");
    final DefaultCertificateValidator applicationCertificateValidator =
            new DefaultCertificateValidator(applicationCertificateStore, applicationIssuerCertificateStore);

    server.setCertificateValidator(applicationCertificateValidator);
    // ...and react to validation results with a custom handler
    applicationCertificateValidator.setValidationListener(validationListener);

    // The folder in which to save the Applications own certificate and private key
    File privatePath = new File(applicationCertificateStore.getBaseDir(), "private");

    // *** Application Description

    ApplicationDescription applicationDescription = initApplicationDescription(applicationName, ApplicationType.Server);

    // *** Application Identity

    final ApplicationIdentity identity =
            initApplicationIdentity(applicationDescription, applicationCertificateStore, privatePath);

    server.setApplicationIdentity(identity);

    // *** Server Endpoints
    // TCP Port number for the UA TCP protocol
    server.setPort(Protocol.OpcTcp, port);

    // optional server name part of the URI (default for all protocols)
    server.setServerName("OPCUA/" + applicationName);

    server.setBindAddresses(Collections.singleton(InetAddress.getByName("0.0.0.0")));




    // Optionally restrict the InetAddresses to which the server is bound.
    // You may also specify the addresses for each Protocol.

    // The default is binding to IPv6 wildcard '[::]' when isEnableIPv6 is true
    // or to IPv4 wildcard '0.0.0.0' otherwise.

    // Alternatively, the Server can be bound to all available InetAddresses.
    // isEnableIPv6 defines whether IPv6 address should be included in the bound addresses.

    // server.setBindAddresses(EndpointUtil.getInetAddresses(server.isEnableIPv6()));



    // *** Security settings
    /*
     * Define the security modes to support for the Binary protocol.
     *
     * Note that different versions of the specification might add/deprecate some modes, in this
     * example all the modes are added, but you should add some way in your application to configure
     * these. The set is empty by default, you must add at least one SecurityMode for the server to
     * start.
     */
    Set<SecurityPolicy> supportedSecurityPolicies = new HashSet<SecurityPolicy>();

    /*
     * This policy does not support any security. Should only be used in isolated networks.
     */
    supportedSecurityPolicies.add(SecurityPolicy.NONE);

    // Modes defined in previous versions of the specification
    supportedSecurityPolicies.addAll(SecurityPolicy.ALL_SECURE_101);
    supportedSecurityPolicies.addAll(SecurityPolicy.ALL_SECURE_102);
    supportedSecurityPolicies.addAll(SecurityPolicy.ALL_SECURE_103);

    /*
     * Per the 1.05 specification, only these policies should be supported and the older ones should
     * be considered as deprecated. However, in practice this list only contains very new security
     * policies, which most of the client applications as of today that are used might not be unable
     * to (yet) use. Thus, you should build a way to select these in your application configuration.
     *
     * Note that the 1.05 list has the same contents as the 1.04 list.
     */
    supportedSecurityPolicies.addAll(SecurityPolicy.ALL_SECURE_104);
    supportedSecurityPolicies.addAll(SecurityPolicy.ALL_SECURE_105);

    Set<MessageSecurityMode> supportedMessageSecurityModes = new HashSet<MessageSecurityMode>();

    /*
     * This mode does not support any security. Should only be used in isolated networks. This is
     * also the only mode, which does not require certificate exchange between the client and server
     * application (when used in conjunction of only ANONYMOUS UserTokenPolicy).
     */
    supportedMessageSecurityModes.add(MessageSecurityMode.None);

    /*
     * This mode support signing, so it is possible to detect if messages are tampered. Note that
     * they are not encrypted.
     */
    supportedMessageSecurityModes.add(MessageSecurityMode.Sign);

    /*
     * This mode signs and encrypts the messages. Only this mode is recommended outside of isolated
     * networks.
     */
    supportedMessageSecurityModes.add(MessageSecurityMode.SignAndEncrypt);

    /*
     * This creates all possible combinations (NONE pairs only with None) of the configured
     * MessageSecurityModes and SecurityPolicies) for opc.tcp communication.
     */
    server.getSecurityModes()
            .addAll(SecurityMode.combinations(supportedMessageSecurityModes, supportedSecurityPolicies));

    // *** OPC UA HTTPS

    initHttps(applicationDescription, identity, privatePath, applicationCertificateValidator,
            supportedSecurityPolicies);


    // Define the supported user authentication methods
    server.addUserTokenPolicy(UserTokenPolicies.ANONYMOUS);
    server.addUserTokenPolicy(UserTokenPolicies.SECURE_USERNAME_PASSWORD);
    server.addUserTokenPolicy(UserTokenPolicies.SECURE_CERTIFICATE);

    // Handle user certificates
    final PkiDirectoryCertificateStore userCertificateStore = new PkiDirectoryCertificateStore("USERS_PKI/CA");
    final PkiDirectoryCertificateStore userIssuerCertificateStore =
            new PkiDirectoryCertificateStore("USERS_PKI/CA/issuers");

    final DefaultCertificateValidator userCertificateValidator =
            new DefaultCertificateValidator(userCertificateStore, userIssuerCertificateStore);

    userValidator = new MyUserValidator(userCertificateValidator);
    // ...and react to validation results with a custom handler
    userCertificateValidator.setValidationListener(userCertificateValidationListener);

    // Define a validator for checking the user accounts
    server.setUserValidator(userValidator);

    // Register to the local discovery server (if present)
    try {
      server.setDiscoveryServerUrl(discoveryServerUrl);
    } catch (URISyntaxException e) {
      logger.error("DiscoveryURL is not valid", e);
    }

    // *** 'init' creates the service handlers and the default endpoints
    // *** according to the settings defined above
    server.init();

    initBuildInfo();

    // "Safety limits" for ill-behaving clients, these can be set after server.init
    server.getSessionManager().setMaxSessionCount(500);
    server.getSessionManager().setMaxSessionTimeout(3600000); // one hour
    server.getSubscriptionManager().setMaxSubscriptionCount(50);
    // By default the MaxSessionCount above also controls the max number of opc.tcp connections (10%
    // more connections, at least 1, are allowed), you can use below to set a different value.
    // server.setMaxOpcTcpConnections(500);

    // Also works the same way for opc.https.
    // server.setMaxOpcHttpsConnections(500);


    /*
     * Safety limits for XXXContinuationPoints. Note! These are the current defaults. Technically a
     * value of 0 (unlimited) is allowed by the OPC UA Specification, but our implementation does
     * allocate server-side memory, thus do not use value of 0 (or you can run out of memory).
     * Future SDK releases may improve this.
     */
    ServerCapabilitiesTypeNode serverCapabilities =
            server.getAddressSpace().getNodeManagerRoot().getServerData().getServerCapabilitiesNode();
    serverCapabilities.setMaxBrowseContinuationPoints(1000);
    serverCapabilities.setMaxQueryContinuationPoints(1000);
    serverCapabilities.setMaxHistoryContinuationPoints(1000);

    // You can do your own additions to server initializations here

  }

  /**
   * Load information models into the address space. Also register classes, to be able to use the
   * respective Java classes with NodeManagerUaNode.createInstance().
   *
   * See the Codegen Manual on instructions how to use your own models.
   */
  protected void loadInformationModels() {
    // Uncomment to take the extra information models in use.

    /*
     * NOTE! requires that code for the the respective DI/ADI/PLC models are generated first using
     * the Codegen.
     */

    // // Register generated classes
    // server.registerModel(DiServerInformationModel.MODEL);
    // server.registerModel(AdiServerInformationModel.MODEL);
    //
    // // Load the standard information models
    // try {
    // // You can reference these bundled models either directly
    // server.getAddressSpace().loadModel(DiServerInformationModel.class.getResource("Opc.Ua.Di.NodeSet2.xml").toURI());
    //
    // // or via the codegenerated helper getLocationURI() method
    // server.getAddressSpace().loadModel(AdiServerInformationModel.getLocationURI());
    //
    // // You can also register and load model in one call
    // server.registerAndLoadModel(PlcServerInformationModel.MODEL,
    // PlcServerInformationModel.getLocationURI());
    // } catch (Exception e) {
    // throw new RuntimeException(e);
    // }
  }

  /*
   * Main loop for user selecting OPC UA calls
   */
  protected void mainMenu() {

    /******************************************************************************/
    /* Wait for user command to execute next action. */
    do {
      printMenu();

      try {
        Action action = readAction();
        if (action != null) {
          ActionResult actionResult = action.performAction(this);
          switch (actionResult) {
            case CLOSE_SERVER:
              return; // closes server
            case NOTHING:
              continue; // continue looping menu
          }
        }
      } catch (Exception e) {
        printException(e);
      }

    } while (true);
    /******************************************************************************/
  }

  /**
   * Run the server.
   *
   * @param enableServerDiagnostics
   * @throws UaServerException
   * @throws StatusException
   */
  protected void run() throws UaServerException, StatusException {
    server.start();
    //initHistory();
    if (enableServerDiagnostics) {
      server.getNodeManagerRoot().getServerData().getServerDiagnosticsNode().setEnabled(true);
    }
    startSimulation();

    printConnectionAddresses();

//    // *** Main Menu Loop
//    mainMenu();
//
//    // *** End
//    stopSimulation();
//    // Notify the clients about a shutdown, with a 5 second delay
//    println("Shutting down...");
//    server.shutdown(5, new LocalizedText("Closed by user", Locale.ENGLISH));
//    println("Closed.");

    // Server läuft dauerhaft ohne Benutzerinteraktion
    println("✅ OPC UA Server running. Waiting for clients...");
    while (true) {
      try {
        Thread.sleep(60_000);
      } catch (InterruptedException e) {
        break;
      }
    }

  }

  /**
   *
   */
  protected void sendEvent() {
    myNodeManager.sendEvent();
  }

  protected void simulate() {
    try {
      if (cncNodeManager != null) {
        cncNodeManager.simulateCycle();
      }
    } catch (Exception e) {
      logger.error("Simulation error", e);
    }
  }


  /**
   * Starts the simulation of the level measurement.
   */
  protected void startSimulation() {
    simulator.scheduleAtFixedRate(simulationTask, 1000, 1000, TimeUnit.MILLISECONDS);
    logger.info("Simulation started.");
  }

  /**
   * Ends simulation.
   */
  protected void stopSimulation() {
    simulator.shutdown();
    logger.info("Simulation stopped.");
  }
}