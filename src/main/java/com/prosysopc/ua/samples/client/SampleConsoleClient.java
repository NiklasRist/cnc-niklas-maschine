/**
 * Prosys OPC UA Java SDK
 * Copyright (c) Prosys OPC Ltd.
 * <http://www.prosysopc.com>
 * All rights reserved.
 */
package com.prosysopc.ua.samples.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.prosysopc.ua.ApplicationIdentity;
import com.prosysopc.ua.ContentFilterBuilder;
import com.prosysopc.ua.DataTypeConversionException;
import com.prosysopc.ua.MethodCallStatusException;
import com.prosysopc.ua.MonitoredItemBase;
import com.prosysopc.ua.SecureIdentityException;
import com.prosysopc.ua.ServiceException;
import com.prosysopc.ua.SessionActivationException;
import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.UaAddress;
import com.prosysopc.ua.UaApplication.Protocol;
import com.prosysopc.ua.UserIdentity;
import com.prosysopc.ua.client.AddressSpaceException;
import com.prosysopc.ua.client.GlobalServerList;
import com.prosysopc.ua.client.InvalidServerEndpointException;
import com.prosysopc.ua.client.MonitoredDataItem;
import com.prosysopc.ua.client.MonitoredDataItemListener;
import com.prosysopc.ua.client.MonitoredEventItem;
import com.prosysopc.ua.client.MonitoredEventItemListener;
import com.prosysopc.ua.client.MonitoredItem;
import com.prosysopc.ua.client.ServerConnectionException;
import com.prosysopc.ua.client.ServerList;
import com.prosysopc.ua.client.ServerListBase;
import com.prosysopc.ua.client.ServerListException;
import com.prosysopc.ua.client.ServerStatusListener;
import com.prosysopc.ua.client.Subscription;
import com.prosysopc.ua.client.SubscriptionAliveListener;
import com.prosysopc.ua.client.SubscriptionNotificationListener;
import com.prosysopc.ua.client.UaClient;
import com.prosysopc.ua.client.UaClientListener;
import com.prosysopc.ua.nodes.MethodArgumentException;
import com.prosysopc.ua.nodes.UaDataType;
import com.prosysopc.ua.nodes.UaInstance;
import com.prosysopc.ua.nodes.UaMethod;
import com.prosysopc.ua.nodes.UaNode;
import com.prosysopc.ua.nodes.UaObject;
import com.prosysopc.ua.nodes.UaReferenceType;
import com.prosysopc.ua.nodes.UaType;
import com.prosysopc.ua.nodes.UaVariable;
import com.prosysopc.ua.stack.builtintypes.DataValue;
import com.prosysopc.ua.stack.builtintypes.DateTime;
import com.prosysopc.ua.stack.builtintypes.DiagnosticInfo;
import com.prosysopc.ua.stack.builtintypes.ExpandedNodeId;
import com.prosysopc.ua.stack.builtintypes.LocalizedText;
import com.prosysopc.ua.stack.builtintypes.NodeId;
import com.prosysopc.ua.stack.builtintypes.QualifiedName;
import com.prosysopc.ua.stack.builtintypes.StatusCode;
import com.prosysopc.ua.stack.builtintypes.UnsignedByte;
import com.prosysopc.ua.stack.builtintypes.UnsignedInteger;
import com.prosysopc.ua.stack.builtintypes.UnsignedShort;
import com.prosysopc.ua.stack.builtintypes.Variant;
import com.prosysopc.ua.stack.cert.DefaultCertificateValidator;
import com.prosysopc.ua.stack.cert.DefaultCertificateValidator.IgnoredChecks;
import com.prosysopc.ua.stack.cert.DefaultCertificateValidatorListener;
import com.prosysopc.ua.stack.cert.PkiDirectoryCertificateStore;
import com.prosysopc.ua.stack.common.ServiceResultException;
import com.prosysopc.ua.stack.core.AccessLevelType;
import com.prosysopc.ua.stack.core.AggregateConfiguration;
import com.prosysopc.ua.stack.core.AggregateFilter;
import com.prosysopc.ua.stack.core.ApplicationDescription;
import com.prosysopc.ua.stack.core.ApplicationType;
import com.prosysopc.ua.stack.core.Argument;
import com.prosysopc.ua.stack.core.Attributes;
import com.prosysopc.ua.stack.core.BrowseDirection;
import com.prosysopc.ua.stack.core.BrowsePathTarget;
import com.prosysopc.ua.stack.core.DataChangeFilter;
import com.prosysopc.ua.stack.core.DataChangeTrigger;
import com.prosysopc.ua.stack.core.DeadbandType;
import com.prosysopc.ua.stack.core.EUInformation;
import com.prosysopc.ua.stack.core.ElementOperand;
import com.prosysopc.ua.stack.core.EndpointDescription;
import com.prosysopc.ua.stack.core.EventFilter;
import com.prosysopc.ua.stack.core.EventNotifierType;
import com.prosysopc.ua.stack.core.FilterOperator;
import com.prosysopc.ua.stack.core.HistoryEventFieldList;
import com.prosysopc.ua.stack.core.Identifiers;
import com.prosysopc.ua.stack.core.LiteralOperand;
import com.prosysopc.ua.stack.core.MonitoringMode;
import com.prosysopc.ua.stack.core.NodeAttributesMask;
import com.prosysopc.ua.stack.core.NodeClass;
import com.prosysopc.ua.stack.core.ObjectAttributes;
import com.prosysopc.ua.stack.core.Range;
import com.prosysopc.ua.stack.core.ReferenceDescription;
import com.prosysopc.ua.stack.core.RelativePathElement;
import com.prosysopc.ua.stack.core.ServerCapability;
import com.prosysopc.ua.stack.core.ServerOnNetwork;
import com.prosysopc.ua.stack.core.SimpleAttributeOperand;
import com.prosysopc.ua.stack.core.StatusCodes;
import com.prosysopc.ua.stack.core.TimestampsToReturn;
import com.prosysopc.ua.stack.core.UserTokenPolicy;
import com.prosysopc.ua.stack.encoding.EncodingException;
import com.prosysopc.ua.stack.transport.security.HttpsSecurityPolicy;
import com.prosysopc.ua.stack.transport.security.KeyPair;
import com.prosysopc.ua.stack.transport.security.SecurityMode;
import com.prosysopc.ua.stack.utils.AttributesUtil;
import com.prosysopc.ua.stack.utils.MultiDimensionArrayUtils;
import com.prosysopc.ua.stack.utils.NumericRange;
import com.prosysopc.ua.types.opcua.AnalogItemType;

/**
 * A sample OPC UA client, running from the console.
 */
public class SampleConsoleClient {

  private static final Logger logger = LoggerFactory.getLogger(SampleConsoleClient.class);

  // Action codes for readAction, etc.
  protected static final int ACTION_ALL = -4;
  protected static final int ACTION_BACK = -2;
  protected static final int ACTION_RETURN = -1;
  protected static final int ACTION_ROOT = -3;
  protected static final int ACTION_TRANSLATE = -6;
  protected static final int ACTION_UP = -5;

  /**
   * The name of the application.
   */
  protected static String APP_NAME = "SampleConsoleClient";

  protected static final List<String> cmdSequence = new ArrayList<String>();

  protected static boolean stackTraceOnException = false;

  public static void main(String[] args) throws Exception {
    // Check if wait requested
    // Can be used for e.g. for debugging or waiting while attaching a profiler
    boolean waitAtStart = parseWaitNeededAtStart(args);
    if (waitAtStart) {
      logger.info("Wait requested via '-w' CLI flag, press enter to continue");
      Scanner sc = new Scanner(System.in);
      sc.nextLine();
      sc.close();
      logger.info("Starting {}", APP_NAME);
    }

    SampleConsoleClient sampleConsoleClient = new SampleConsoleClient();
    try {
      if (!sampleConsoleClient.parseCmdLineArgs(args)) {
        usage();
        return;
      }
    } catch (IllegalArgumentException e) {
      // If message is not defined, the command line was empty and the
      // user did not enter any URL when prompted. Otherwise, the
      // exception is used to notify of an invalid argument.
      if (e.getMessage() != null) {
        println("Invalid cmd line argument: " + e.getMessage());
      }
      usage();
      return;
    } catch (ArrayIndexOutOfBoundsException e1) {
      println("Not enough arguments.");
      usage();
      return;

    }

    sampleConsoleClient.initialize();
    // Show the menu, which is the main loop of the client application
    sampleConsoleClient.mainMenu();

    println(APP_NAME + ": Closed");

  }

  protected static String dateTimeToString(String title, DateTime timestamp, UnsignedShort picoSeconds) {
    if ((timestamp != null) && !timestamp.equals(DateTime.MIN_VALUE)) {
      SimpleDateFormat format = new SimpleDateFormat("yyyy MMM dd (zzz) HH:mm:ss.SSS");
      StringBuilder sb = new StringBuilder(title);
      sb.append(format.format(timestamp.getCalendar(TimeZone.getDefault()).getTime()));
      if ((picoSeconds != null) && !picoSeconds.equals(UnsignedShort.valueOf(0))) {
        sb.append(String.format("/%d picos", picoSeconds.getValue()));
      }
      return sb.toString();
    }
    return "";
  }

  protected static int parseAction(String s) {
    if (s.equals("x")) {
      return ACTION_RETURN;
    }
    if (s.equals("b")) {
      return ACTION_BACK;
    }
    if (s.equals("r")) {
      return ACTION_ROOT;
    }
    if (s.equals("a")) {
      return ACTION_ALL;
    }
    if (s.equals("u")) {
      return ACTION_UP;
    }
    if (s.equals("t")) {
      return ACTION_TRANSLATE;
    }
    return Integer.parseInt(s);
  }

  protected static boolean parseWaitNeededAtStart(String[] args) {
    if (args == null || args.length == 0) {
      return false;
    }

    for (String arg : args) {
      if ("-w".equals(arg)) {
        return true;
      }
    }

    return false;
  }

  protected static void print(String string) {
    System.out.print(string);

  }

  protected static void printException(Exception e) {
    if (stackTraceOnException) {
      e.printStackTrace();
    } else {
      println(e.toString());
      if (e instanceof MethodCallStatusException) {
        MethodCallStatusException me = (MethodCallStatusException) e;
        final StatusCode[] results = me.getInputArgumentResults();
        if (results != null) {
          for (int i = 0; i < results.length; i++) {
            StatusCode s = results[i];
            if (s.isBad()) {
              println("Status for Input #" + i + ": " + s);
              DiagnosticInfo d = me.getInputArgumentDiagnosticInfos()[i];
              if (d != null) {
                println("  DiagnosticInfo:" + i + ": " + d);
              }
            }
          }
        }
      }
      if (e.getCause() != null) {
        println("Caused by: " + e.getCause());
      }
    }
  }

  protected static void printf(String format, Object... args) {
    System.out.printf(format, args);

  }

  protected static void println(String string) {
    System.out.println(string);
  }

  protected static int readAction() {
    return parseAction(readInput(true).toLowerCase());
  }

  protected static String readInput(boolean useCmdSequence) {
    return readInput(useCmdSequence, null);
  }

  protected static String readInput(boolean useCmdSequence, String defaultValue) {
    // You can provide "commands" already from the command line, in which
    // case they will be kept in cmdSequence
    if (useCmdSequence && !cmdSequence.isEmpty()) {
      String cmd = cmdSequence.remove(0);
      try {
        // Negative int values are used to pause for n seconds
        int i = Integer.parseInt(cmd);
        if (i < 0) {
          try {
            TimeUnit.SECONDS.sleep(-i);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
          return readInput(useCmdSequence, defaultValue);
        }
      } catch (NumberFormatException e) {
        // never mind
      }
      return cmd;
    }
    BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
    String s = null;
    do {
      try {
        s = stdin.readLine();
        if ((s == null) || (s.length() == 0)) {
          s = defaultValue;
          break;
        }
      } catch (IOException e) {
        printException(e);
      }
    } while ((s == null) || (s.length() == 0));
    return s;
  }

  /**
   *
   */
  protected static void usage() {
    println("Usage: " + APP_NAME + " [Options] [ServerAddress]");
    println("   -d            Connect to a discovery server");
    println(
        "   -r port       Reverse connection. Starts waiting for connections from the server in the defined TCP port.");
    println("   -n nodeId     Define the NodeId to select after connect (requires serverUri)");
    println("   -s n|s|e[1-5] Define the SecurityMode (n=None/s=Sign/e=SignAndEncrypt). Default is None.");
    println(
        "                    Optionally, together with s/e, you can define the security policy with a number between 1-5.");
    println(
        "                    (1=Basic128..., 2=Basic256, 3=Basic256Sha256, 4=Aes128..., 5=Aes256...). Default is 3.");
    println(
        "   -k keySize    Define the size of the new public key of the application certificate (default 2048; other valid values 1024, 4096)");
    println("   -m nodeId     Subscribe to the given node at start up");
    println("   -t            Output stack trace for errors");
    println("   -dt           Show the DataType of read values When displaying them.");
    println(
        "   -H  (or --disable-hostname-verification) Disable default hostname verification, when SecurityMode is Sign or SignAndEncrypt.");
    println("   -?            Show this help text");
    println(
        "   ServerAdress  The address of the server to connect to. If you do not specify it, you will be prompted for it.");
    println("                    Examples of valid addresses:");
    println(
        "                    opc.tcp://localhost:52520/OPCUA/SampleConsoleServer (Prosys OPC UA SDK for Java Sample Console Server)");
    println(
        "                    opc.tcp://localhost:53530/OPCUA/SimulationServer    (Prosys OPC UA Simulation Server)");
    println(
        "                    opc.tcp://localhost:48010                           (Unified Automation OPC UA C++ Demo Server)");
    println("                    opc.tcp://localhost:51210/UA/SampleServer           (OPC Foundation Sample Server)");
    println("                    -d opc.tcp://localhost:4840/UADiscovery             (Local Discovery Server)");
  }

  protected UaClient client;
  protected UaClientListener clientListener = new MyUaClientListener();

  protected boolean connectToDiscoveryServer = false;
  protected MonitoredDataItemListener dataChangeListener = new MyMonitoredDataItemListener(this);
  protected String defaultServerAddress = "opc.tcp://localhost:52520/OPCUA/SampleConsoleServer";
  // requested fields for event subscriptions
  // the last two fields are reserved for our custom fields
  protected final QualifiedName[] eventFieldNames =
      {new QualifiedName("EventType"), new QualifiedName("Message"), new QualifiedName("SourceName"),
          new QualifiedName("Time"), new QualifiedName("Severity"), new QualifiedName("ActiveState/Id"), null, null};
  protected final MonitoredEventItemListener eventListener = new MyMonitoredEventItemListener(this, eventFieldNames);

  protected final List<String> initialMonitoredItems = new ArrayList<String>();

  protected NodeId nodeId = null;
  protected String passWord;
  protected SecurityMode securityMode = SecurityMode.NONE;

  protected ServerStatusListener serverStatusListener = new MyServerStatusListener();
  protected String serverAddress = null;

  protected int sessionCount = 0;
  protected boolean showReadValueDataType = false;
  protected int certKeySize = 2048;
  protected Subscription subscription;

  protected SubscriptionAliveListener subscriptionAliveListener = new MySubscriptionAliveListener();

  protected SubscriptionNotificationListener subscriptionListener = new MySubscriptionNotificationListener();

  protected String userName;

  protected DefaultCertificateValidatorListener validationListener;
  protected int reversePort = 0;

  private boolean disableHostnameVerification = false;

  public SampleConsoleClient() {

  }

  private NodeClass getNodeClass(NodeId nodeId) throws ServiceException, StatusException {
    return (NodeClass) client.readAttribute(nodeId, Attributes.NodeClass).getValue().asEnum(NodeClass.class);

    // An alternative implementation
    // return client.getAddressSpace().getNode(nodeId).getNodeClass();
  }

  private String nodeClassToStr(NodeClass nodeClass) {
    return "[" + nodeClass + "]";
  }

  private void validateSamplingInterval(double requestedSamplingInterval, double revisedSamplingInterval) {
    if (Double.isInfinite(revisedSamplingInterval)) {
      println("Error, got infinite number as revised sampling interval");
    } else if (Double.isNaN(revisedSamplingInterval)) {
      println("Error, got NaN as revised sampling interval");
    } else if (revisedSamplingInterval < 0) {
      println("Warning, got negative number as revised sampling interval");
    } else if (Math.abs(requestedSamplingInterval - revisedSamplingInterval) > 0.01) {
      println(String.format("The requested sampling inteval is different than requested: %s, got: %s",
          requestedSamplingInterval, revisedSamplingInterval));
    }
  }

  protected void addNode(NodeId parentNodeId) throws ServiceException, EncodingException, StatusException {
    println("Adding a new node to the current node");
    String name = prompt("Name:");
    NodeId typeDefinitionId = Identifiers.BaseObjectType;
    NodeId referenceTypeId = Identifiers.HasComponent;

    int ns = parentNodeId.getNamespaceIndex();
    NodeId newNodeId = new NodeId(ns, parentNodeId.getValue().toString() + "/" + name);
    QualifiedName browseName = new QualifiedName(ns, name);
    LocalizedText displayName = new LocalizedText(name);
    UnsignedInteger attributesMask = NodeAttributesMask.getMask(NodeAttributesMask.DisplayName);
    ObjectAttributes nodeAttributes = new ObjectAttributes(attributesMask, displayName, null, null, null, null);
    client.getAddressSpace().addNode(parentNodeId, referenceTypeId, newNodeId, browseName, NodeClass.Object,
        nodeAttributes, typeDefinitionId);
    println("OK");
  }


  /**
   * Browse the references for a node.
   *
   * @param nodeId
   * @param prevId
   * @throws ServiceException
   * @throws StatusException
   */
  protected NodeId browse(NodeId nodeId, NodeId prevId) throws ServiceException, StatusException {
    printCurrentNode(nodeId);
    // client.getAddressSpace().setReferenceTypeId(ReferencesToReturn);
    List<ReferenceDescription> references;
    // Find the reference to use for browsing up: prefer the previous node,
    // but otherwise accept any hierarchical inverse reference
    List<ReferenceDescription> upReferences;
    try {
      client.getAddressSpace().setMaxReferencesPerNode(1000);
      references = client.getAddressSpace().browse(nodeId);
      for (int i = 0; i < references.size(); i++) {
        printf("%d - %s\n", i, referenceToString(references.get(i)));
      }
      upReferences = client.getAddressSpace().browseUp(nodeId);
    } catch (Exception e) {
      printException(e);
      references = new ArrayList<ReferenceDescription>();
      upReferences = new ArrayList<ReferenceDescription>();
    }

    System.out.println("-------------------------------------------------------");
    println("- Enter node number to browse into that");
    println("- Enter a to show/hide all references");
    if (prevId != null) {
      String prevName = null;
      try {
        UaNode prevNode = client.getAddressSpace().getNode(prevId);
        if (prevNode != null) {
          prevName = prevNode.getDisplayName().getText();
        }
      } catch (AddressSpaceException e) {
        prevName = prevId.toString();
      }
      if (prevName != null) {
        println("- Enter b to browse back to the previous node (" + prevName + ")");
      }
    }
    if (!upReferences.isEmpty()) {
      println("- Enter u to browse up to the 'parent' node");
    }
    println("- Enter r to browse back to the root node");
    println("- Enter t to translate a BrowsePath to NodeId");
    System.out.println("- Enter x to select the current node and return to previous menu");
    System.out.println("-------------------------------------------------------");
    do {
      int action = readAction();
      switch (action) {
        case ACTION_RETURN:
          return nodeId;
        case ACTION_BACK:
          if (prevId == null) {
            continue;
          }
          return prevId;
        case ACTION_UP:
          NodeId upId = browseUp(nodeId, upReferences);
          if (upId != null) {
            return upId;
          }
        case ACTION_ROOT:
          return browse(Identifiers.RootFolder, nodeId);
        case ACTION_ALL:
          return toggleBrowseAll(nodeId, prevId);
        case ACTION_TRANSLATE:
          translateBrowsePathToNodeId(nodeId);
          break;

        default:
          try {
            ReferenceDescription r = references.get(action);
            NodeId target;
            try {
              target = browse(client.getAddressSpace().getNamespaceTable().toNodeId(r.getNodeId()), nodeId);
            } catch (ServiceResultException e) {
              throw new ServiceException(e);
            }
            if (target != nodeId) {
              return target;
            }
            return browse(nodeId, prevId);
          } catch (IndexOutOfBoundsException e) {
            System.out.println("No such item: " + action);
          }
      }
    } while (true);
  }

  /**
   * Follow an inverse reference up in the hierarchy. If there are several, prompt the user which
   * one to use.
   */
  protected NodeId browseUp(NodeId nodeId, List<ReferenceDescription> upReferences)
      throws ServerConnectionException, ServiceException, StatusException {
    if ((!upReferences.isEmpty())) {
      try {
        ReferenceDescription upReference = null;
        if (upReferences.size() == 1) {
          upReference = upReferences.get(0);
        } else {
          println("Which inverse reference do you wish to go up?");
          for (int i = 0; i < upReferences.size(); i++) {
            printf("%d - %s\n", i, referenceToString(upReferences.get(i)));
          }
          while (upReference == null) {
            int upIndex = readAction();
            try {
              upReference = upReferences.get(upIndex);
            } catch (Exception e) {
              printException(e);
            }
          }
        }
        if (!upReference.getNodeId().isLocal()) {
          println("Not a local node");
        } else {
          return browse(client.getAddressSpace().getNamespaceTable().toNodeId(upReference.getNodeId()), nodeId);
        }
      } catch (ServiceResultException e1) {
        printException(e1);
      }
    }
    return null;
  }

  /**
   * Call a method on an object. Note that methods are called on objects, so you need to define the
   * NodeId for both the object and the method.
   *
   * @param nodeId The ID of the object
   * @param methodId The ID of the method of the object
   * @throws ServiceException
   * @throws AddressSpaceException
   * @throws ServerConnectionException
   * @throws MethodArgumentException
   * @throws StatusException
   *
   */
  protected void callMethod(NodeId nodeId) throws ServiceException, ServerConnectionException, AddressSpaceException,
      MethodArgumentException, StatusException {
    // // Example values to call "condition acknowledge" using the standard
    // // methodId:
    // NodeId methodId = Identifiers.AcknowledgeableConditionType_Acknowledge;
    // // change this to the ID of the event you are acknowledging:
    // ByteString eventId = null;
    // LocalizedText comment = new LocalizedText("Your comment",
    // Locale.ENGLISH);
    // final Variant[] inputs = new Variant[] { new Variant(eventId),
    // new Variant(comment) };

    NodeId methodId = promptMethodId(nodeId);
    if (methodId != null) {
      UaMethod method = client.getAddressSpace().getMethod(methodId);
      Variant[] inputs = promptInputArguments(method);
      Variant[] outputs = client.call(nodeId, methodId, inputs);
      printOutputArguments(method, outputs);
    } else {
      printCancelled();
    }
  }

  /**
   * Connect to the server.
   */
  protected void connect() {
    if (!client.isConnected()) {
      try {
        if (Protocol.OpcHttps == (client.getAddress() == null ? null : client.getAddress().getProtocol())) {
          println(
              "Using HttpsSecurityPolicies " + Arrays.toString(client.getHttpsSettings().getHttpsSecurityPolicies()));
        } else {
          String securityPolicy =
              client.getEndpoint() == null ? client.getSecurityMode().getSecurityPolicy().getPolicyUri()
                  : client.getEndpoint().getSecurityPolicyUri();
          println("Using SecurityPolicy " + securityPolicy);
        }
        println("Using UserIdentity " + client.getUserIdentity());

        // Define the session name that is visible in the server
        client.setSessionName(String.format("%s@%s Session%d", APP_NAME,
            ApplicationIdentity.getActualHostNameWithoutDomain(), ++sessionCount));

        client.connect();
        try {
          println("ServerStatus: " + client.getServerStatus());
          // println("Endpoint: " + client.getEndpoint());
        } catch (StatusException ex) {
          printException(ex);
        }
      } catch (InvalidServerEndpointException e) {
        print("Invalid Endpoint: ");
        printException(e);
        try {
          // In case we have selected a wrong endpoint, print out the
          // supported ones
          printEndpoints(client.discoverEndpoints());
        } catch (Exception ex) {
          // never mind, if the endpoints are not available
        }
      } catch (ServerConnectionException e) {
        printException(e);
        try {
          // In case we have selected an unavailable security mode,
          // print out the supported ones
          printSecurityModes(client.getSupportedSecurityModes());
        } catch (ServerConnectionException e1) {
          // never mind, if the security modes are not available
        } catch (ServiceException e1) {
          // never mind, if the security modes are not available
        }
      } catch (SessionActivationException e) {
        printException(e);
        try {
          printUserIdentityTokens(client.getSupportedUserIdentityTokens());
        } catch (ServiceException e1) {
          // never mind, if not available
        }
        return; // No point to continue
      } catch (ServiceException e) {
        if (reversePort > 0) {
          throw new RuntimeException(e); // Stop the application
        }
        printException(e);
        if (StatusCode.valueOf(StatusCodes.Bad_SecurityChecksFailed).equals(e.getServiceResult())) {
          if (serverAddress.equals(defaultServerAddress)) {
            // Advice for SampleConsoleServer
            println("");
            println(
                "To connect to SampleConsoleServer using Sign or SignAndEncrypt, it must trust the certificate of SampleConsoleClient.");
            println(
                "Copy the DER file from SampleConsoleClient's PKI\\CA\\private folder to SampleConsoleServer's PKI\\CA\\certs folder.");
            println(
                "Also, make sure that another copy of this file doesn't exist with a different filename in SampleConsoleServer's PKI\\CA\\rejected folder.");
            println(
                "After copying the certificate to the correct folder, type \"1\" and press enter to try connecting to the server.");
          } else if (serverAddress.endsWith(":53530/OPCUA/SimulationServer")) {
            // Advice for Prosys OPC UA Simulation Server
            println("");
            println(
                "To connect to Simulation Server using Sign or SignAndEncrypt, it must trust the certificate of SampleConsoleClient.");
            println("In Simulation Server, enable the Expert Mode from Options and open the Certificates Tab.");
            println("Find the certificate of SampleConsoleClient, right click on it and select the Trust option.");
            println(
                "After trusting the certificate in Simulation Server, type \"1\" and press enter to try connecting to the server.");
          } else if (serverAddress.endsWith(":56560/OPCUA/Forge")) {
            // Advice for Prosys OPC UA Forge
            println("");
            println(
                "To connect to Forge using Sign or SignAndEncrypt, it must trust the certificate of SampleConsoleClient.");
            println(
                "Login to Forge's web user interface as administrator and navigate to OPC UA Server > Certificates.");
            println("Find the certificate of SampleConsoleClient in Rejected Certificates and press its Trust button.");
            println(
                "After trusting the certificate in Forge, type \"1\" and press enter to try connecting to the server.");
          }
        }
      }
    }
  }

  protected void createAggregateFilter(MonitoredDataItem dataItem) throws ServiceException, StatusException {
    dataItem.setSamplingInterval(100.0);

    AggregateFilter afilter = new AggregateFilter();
    // afilter.setStartTime(DateTime.currentTime());

    afilter.setAggregateType(Identifiers.AggregateFunction_Count);
    afilter.setProcessingInterval(500.0);

    // By default, the MonitoredDataItme a Configuration with the serverDefaults,
    // so we don't need to define it

    // AggregateConfiguration aconfiguration = new AggregateConfiguration();
    // afilter.setAggregateConfiguration(aconfiguration );

    dataItem.setAggregateFilter(afilter);
  }

  /**
   * @param qualifiedName
   * @return
   */
  protected QualifiedName[] createBrowsePath(QualifiedName qualifiedName) {
    if (!qualifiedName.getName().contains("/")) {
      return new QualifiedName[] {qualifiedName};
    }
    int namespaceIndex = qualifiedName.getNamespaceIndex();
    String[] names = qualifiedName.getName().split("/");
    QualifiedName[] result = new QualifiedName[names.length];
    for (int i = 0; i < names.length; i++) {
      result[i] = new QualifiedName(namespaceIndex, names[i]);
    }
    return result;
  }

  protected void createDataChangeFilter(MonitoredDataItem dataItem) throws ServiceException, StatusException {
    DataChangeFilter filter = new DataChangeFilter();
    filter.setDeadbandValue(1.00);
    filter.setTrigger(DataChangeTrigger.StatusValue);
    filter.setDeadbandType(UnsignedInteger.valueOf(DeadbandType.Percent.getValue()));
    dataItem.setDataChangeFilter(filter);
  }

  /**
   * Create an EventFilter that will pick all events from the node, except for ModelChange events,
   * and requests the specific event fields that are defined in 'eventFieldNames'.
   *
   * @return a new EventFilter
   */
  protected EventFilter createEventFilter(QualifiedName[] eventFields) {


    // This defines the event type of the fields.
    // It should be defined per browsePath, but for example
    // the Java SDK servers ignore the value at the moment
    NodeId eventTypeId = Identifiers.BaseEventType;
    UnsignedInteger eventAttributeId = Attributes.Value;
    String indexRange = null;
    SimpleAttributeOperand[] selectClauses = new SimpleAttributeOperand[eventFields.length + 1];
    for (int i = 0; i < eventFields.length; i++) {
      QualifiedName[] browsePath = createBrowsePath(eventFields[i]);
      selectClauses[i] = new SimpleAttributeOperand(eventTypeId, browsePath, eventAttributeId, indexRange);
    }

    /*
     * Add a field to get the NodeId of the event source, this is done by selecting an empty path so
     * it is the node itself. Note that only Conditions do have a source in the address space.
     * 
     * NOTE! Previously we have used null array instead of new QualifiedName[0] for the browsePath
     * selector. Most servers accept null, but not all and using empty array form is more correct.
     */
    selectClauses[eventFields.length] =
        new SimpleAttributeOperand(Identifiers.ConditionType, new QualifiedName[0], Attributes.NodeId, null);

    // Create the filter
    EventFilter filter = new EventFilter();
    // Select the event fields
    filter.setSelectClauses(selectClauses);

    // Event filtering: the following sample creates a
    // "Not OfType GeneralModelChangeEventType" filter
    ContentFilterBuilder fb = new ContentFilterBuilder();
    // The element operand refers to another operand -
    // operand #1 in this case which is the next,
    // LiteralOperand
    fb.add(FilterOperator.Not, new ElementOperand(UnsignedInteger.valueOf(1)));
    final LiteralOperand filteredType = new LiteralOperand(Identifiers.GeneralModelChangeEventType);
    fb.add(FilterOperator.OfType, filteredType);

    // // Another example:
    // // OfType(custom NodeId) And (Severity > 800)
    // // Comment the previous example out if you try this
    // // Element #0
    // fb.add(FilterOperator.And, new ElementOperand(
    // UnsignedInteger.valueOf(1)),
    // new ElementOperand(UnsignedInteger.valueOf(2)));
    // // Element #1
    // final LiteralOperand filteredType = new
    // LiteralOperand(
    // new Variant(new NodeId(3, 1101)));
    // fb.add(FilterOperator.OfType, filteredType);
    // // Element #2
    // QualifiedName[] severityPath = { new QualifiedName(
    // "Severity") };
    // fb.add(FilterOperator.GreaterThan,
    // new SimpleAttributeOperand(
    // Identifiers.ConditionType,
    // severityPath, Attributes.Value, null),
    // new LiteralOperand(new Variant(800)));

    // Apply the filter to Where-clause - this is optional, so you can also leave it unset to get
    // all events
    filter.setWhereClause(fb.getContentFilter());
    return filter;
  }

  /**
   * Create a new MonitoredDataItem.
   */
  protected MonitoredDataItem createMonitoredDataItem(NodeId nodeId) throws ServiceException, StatusException {
    /*
     * Creating MonitoredDataItem, could also use the constructor without the sampling interval
     * parameter, i.e. it would be default -1 and use the publishing interval of the subscription,
     * but CTT expects positive values here for some tests.
     */
    MonitoredDataItem dataItem =
        new MonitoredDataItem(nodeId, Attributes.Value, MonitoringMode.Reporting, subscription.getPublishingInterval());

    dataItem.setDataChangeListener(dataChangeListener);

    // Enable the following to set the DataChangeFilter for the items

    if (false) {
      createDataChangeFilter(dataItem);
    }

    // Enable the following to set the AggegateFilter for the item
    // NOTE: That you cannot have both a DataChangeFilter and AggregateFilter at the same time

    else if (false) {
      createAggregateFilter(dataItem);
    }

    return dataItem;
  }

  /**
   * Create a new MonitoredEventItem.
   */
  protected MonitoredEventItem createMonitoredEventItem(NodeId nodeId) throws StatusException {
    initEventFieldNames();
    EventFilter filter = createEventFilter(eventFieldNames);

    // Create the item to monitor the current Node.
    // *Note* that you can always use Identifiers.Server as the NodeId, to listen to all events from
    // the server, if you don't know any specific node to monitor.
    MonitoredEventItem eventItem = new MonitoredEventItem(nodeId, filter);
    eventItem.setEventListener(eventListener);
    return eventItem;
  }

  /**
   * Create a Monitored Item for the current node, unless one already exists.
   * 
   * <p>
   * Creates a MonitoredDataItem or MonitoredEventItem depdning on whether the current node is a
   * Variable or Object, respectively.
   */
  protected void createMonitoredItem(Subscription sub, NodeId nodeId) throws ServiceException, StatusException {
    UnsignedInteger monitoredItemId = null;

    // Create the monitored item, if it is not already in the
    // subscription
    if (!sub.hasItem(nodeId)) {
      // Event or DataChange?
      final MonitoredItem item;

      NodeClass nodeClass = getNodeClass(nodeId);
      switch (nodeClass) {
        case Variable:
          MonitoredDataItem dataItem = createMonitoredDataItem(nodeId);
          println("Monitoring Variable node for DataChanges");
          item = dataItem;
          break;
        case Object:
          MonitoredEventItem eventItem = createMonitoredEventItem(nodeId);
          println("Monitoring Object node for Events");
          item = eventItem;
          break;
        default:
          println("ERROR: Cannot monitor " + nodeClass + " nodes!");
          return;
      }
      double requestedSamplingInterval = item.getSamplingInterval();
      sub.addItem(item);
      monitoredItemId = item.getMonitoredItemId();
      double revisedSamplingInterval = item.getSamplingInterval();

      validateSamplingInterval(requestedSamplingInterval, revisedSamplingInterval);

      if (nodeClass == NodeClass.Object) {
        try {
          // Refresh the current state of conditions
          subscription.conditionRefresh();
        } catch (Exception e) {
          // Ignore the errors, if the server does not support the service
        }
      }
    }
    println("-------------------------------------------------------");
    println("Subscription: Id=" + sub.getSubscriptionId() + " ItemId=" + monitoredItemId);
  }

  /**
   * @param subscription
   * @param nodeIds
   * @return
   * @throws ServiceException
   * @throws StatusException
   */
  protected void createMonitoredItemsForChildren(Subscription subscription, String nodeIds)
      throws ServiceException, StatusException {
    {
      String parentNodeId = nodeIds.substring(0, nodeIds.length() - 2);
      NodeId nodeId = NodeId.parseNodeId(parentNodeId);
      List<ReferenceDescription> refs = client.getAddressSpace().browse(nodeId, BrowseDirection.Forward,
          Identifiers.HasChild, true, NodeClass.Variable);
      List<MonitoredItem> items = new ArrayList<MonitoredItem>(refs.size());
      println("Subscribing to " + refs.size() + " items under node " + parentNodeId);
      for (ReferenceDescription r : refs) {
        try {
          items.add(createMonitoredDataItem(client.getNamespaceTable().toNodeId(r.getNodeId())));
        } catch (ServiceResultException e) {
          printException(e);
        }
      }
      try {
        subscription.addItems(items.toArray(new MonitoredItem[items.size()]));
        println("done");
      } catch (Exception e) {
        printException(e);
      }
    }
  }

  /**
   * @return
   * @throws ServiceException
   * @throws StatusException
   */
  protected Subscription createSubscription() throws ServiceException, StatusException {
    // Create the subscription
    Subscription subscription = new Subscription();

    // Default PublishingInterval is 1000 ms

    // subscription.setPublishingInterval(1000);

    // LifetimeCount should be at least 3 times KeepAliveCount

    // subscription.setLifetimeCount(1000);
    // subscription.setMaxKeepAliveCount(50);

    // If you are expecting big data changes, it may be better to break the
    // notifications to smaller parts

    // subscription.setMaxNotificationsPerPublish(1000);

    // Listen to the alive and timeout events of the subscription

    subscription.addAliveListener(subscriptionAliveListener);

    // Listen to notifications - the data changes and events are
    // handled using the item listeners (see below), but in many
    // occasions, it may be best to use the subscription
    // listener also to handle those notifications

    subscription.addNotificationListener(subscriptionListener);

    // Add it to the client
    client.addSubscription(subscription);
    return subscription;
  }

  /**
   * @param attributeId
   * @param nodeId
   * @param value
   * @return
   */
  protected String dataValueToString(NodeId nodeId, UnsignedInteger attributeId, DataValue value) {
    StringBuilder sb = new StringBuilder();
    sb.append("Node: ");
    sb.append(nodeId);
    sb.append(".");
    sb.append(AttributesUtil.toString(attributeId));
    sb.append(" | Status: ");
    sb.append(value.getStatusCode());
    if (value.getStatusCode().isNotBad()) {
      sb.append(" | Value: ");
      if (value.isNull()) {
        sb.append("NULL");
      } else {
        if (showReadValueDataType && Attributes.Value.equals(attributeId)) {
          try {
            UaVariable variable = (UaVariable) client.getAddressSpace().getNode(nodeId);
            if (variable == null) {
              sb.append("(Cannot read node datatype from the server) ");
            } else {

              NodeId dataTypeId = variable.getDataTypeId();
              UaType dataType = variable.getDataType();
              if (dataType == null) {
                dataType = client.getAddressSpace().getType(dataTypeId);
              }

              Variant variant = value.getValue();
              variant.getCompositeClass();
              if (attributeId.equals(Attributes.Value)) {
                if (dataType != null) {
                  sb.append("(" + dataType.getDisplayName().getText() + ")");
                } else {
                  sb.append("(DataTypeId: " + dataTypeId + ")");
                }
              }
            }
          } catch (ServiceException e) {
          } catch (AddressSpaceException e) {
          }
        }
        final Object v = value.getValue().getValue();
        if (value.getValue().isArray()) {
          sb.append(MultiDimensionArrayUtils.toString(v));
        } else {
          sb.append(v);
        }
      }
    }
    sb.append(dateTimeToString(" | ServerTimestamp: ", value.getServerTimestamp(), value.getServerPicoseconds()));
    sb.append(dateTimeToString(" | SourceTimestamp: ", value.getSourceTimestamp(), value.getSourcePicoseconds()));
    return sb.toString();
  }

  /**
   * Disconnect from the server.
   */
  protected void disconnect() {
    client.disconnect();
  }

  /**
   * Example of discovering applications from a discovery server (LDS or GDS or the server itself)
   * and selecting an endpoint.
   * 
   * @return true if a new endpoint was selected
   * @throws ServerListException
   * @throws URISyntaxException
   */
  protected boolean discover() throws ServerListException {
    String[] discoveryUrls;
    discoveryUrls = discoverServer(client.getAddress() == null ? "" : client.getAddress().getAddress());
    if (discoveryUrls != null) {
      EndpointDescription endpoint = discoverEndpoints(discoveryUrls);
      if (endpoint != null) {
        client.disconnect();
        client.setEndpoint(endpoint);
        return true;
      }
    }
    return false;
  }

  /**
   * Discover available endpoints from a server and let the user select one.
   * 
   * @param discoveryUrls the discovery addresses to use to request the available endpoints.
   * @return An EndpointDescription selected by the user..
   */
  protected EndpointDescription discoverEndpoints(String[] discoveryUrls) {
    if (discoveryUrls != null) {
      UaClient discoveryClient = new UaClient();
      int i = 0;
      List<EndpointDescription> edList = new ArrayList<EndpointDescription>();

      println("Available endpoints: ");
      println(String.format("%s - %-50s - %-20s - %-20s - %s", "#", "URI", "Security Mode", "Security Policy",
          "Transport Profile"));
      for (String url : discoveryUrls) {
        discoveryClient.setAddress(UaAddress.parse(url));
        try {
          for (EndpointDescription ed : discoveryClient.discoverEndpoints()) {
            println(String.format("%s - %-50s - %-20s - %-20s - %s", i++, ed.getEndpointUrl(), ed.getSecurityMode(),
                ed.getSecurityPolicyUri().replaceFirst("http://opcfoundation.org/UA/SecurityPolicy#", ""),
                ed.getTransportProfileUri().replaceFirst("http://opcfoundation.org/UA-Profile/Transport/", "")));
            edList.add(ed);
          }
        } catch (Exception e) {
          println("Cannot discover Endpoints from URL " + url + ": " + e.getMessage());
        }
      }
      System.out.println("-------------------------------------------------------");
      println("- Enter endpoint number to select that one");
      println("- Enter x to return to cancel");
      System.out.println("-------------------------------------------------------");
      // // Select an endpoint with the same protocol as the
      // // original request, if available
      // URI uri = new URI(url);
      // if (uri.getScheme().equals(client.getProtocol().toString()))
      // {
      // connectUrl = url;
      // println("Selected application "
      // + serverApp.getApplicationName().getText()
      // + " at " + url);
      // break;
      // } else if (connectUrl == null)
      // connectUrl = url;

      EndpointDescription endpoint = null;
      while (endpoint == null) {
        try {
          int n = readAction();
          if (n == ACTION_RETURN) {
            return null;
          } else {
            return edList.get(n);
          }
        } catch (Exception e) {

        }
      }
    } else {
      println("No suitable discoveryUrl available: using the current Url");
    }
    return null;
  }

  /**
   * Discover server applications from a GDS, LDS or the server itself.
   * 
   * @return The DiscoveryUrls of the servers.
   * @throws ServerListException if the server list cannot be retrieved.
   *
   */
  protected String[] discoverServer(String uri) throws ServerListException {
    // Discover a new server list from a discovery server at URI
    ServerListBase serverList;
    try {
      // try to use as a GDS first
      serverList = new GlobalServerList(uri);
    } catch (ServerListException e) {
      // otherwise, use as LDS or standard server
      serverList = new ServerList(uri);
    }
    println("");
    if (serverList.size() == 0 && serverList.getServersOnNetwork().length == 0) {
      println("No servers found");
      return null;
    }

    if (!serverList.isEmpty()) {
      println("Local servers:");
      println(String.format("%s - %-40s - %-15s - %-50s - %s", "#", "Name", "Type", "ProductUri", "ApplicationUri"));
      for (int i = 0; i < serverList.size(); i++) {
        final ApplicationDescription s = serverList.get(i);
        println(String.format("%d - %-40s - %-15s - %-50s - %s", i, s.getApplicationName().getText(),
            s.getApplicationType(), s.getProductUri(), s.getApplicationUri()));
      }
    }
    int n = serverList.size();
    List<ServerOnNetwork> serversOnNetwork = Arrays.asList(serverList.getServersOnNetwork());
    if (!serversOnNetwork.isEmpty()) {
      println("Servers on network:");
      println(String.format("%s - %-40s - %-20s - %s", "#", "Name", "Capabilities", "DiscoveryUrl"));
      for (int i = 0; i < serversOnNetwork.size(); i++) {
        final ServerOnNetwork s = serversOnNetwork.get(i);
        String capabilitiesString;
        try {
          EnumSet<ServerCapability> capabilities = ServerCapability.getSet(s.getServerCapabilities());
          capabilitiesString = ServerCapability.toString(capabilities);
        } catch (IllegalArgumentException e) {
          // If the capabilities is invalid, display the original as list, and the exception
          capabilitiesString = Arrays.toString(s.getServerCapabilities()) + " (NOTE! " + e.getMessage() + ")";
        }
        println(String.format("%d - %-40s - %-20s - %s", n + i, s.getServerName(), capabilitiesString,
            s.getDiscoveryUrl()));
      }
    }
    println("-------------------------------------------------------");
    println("- Enter client number to select that one");
    println("- Enter x to return to cancel");
    println("-------------------------------------------------------");
    do {
      int action = readAction();
      switch (action) {
        case ACTION_RETURN:
          return null;
        default:
          if (action < n) {
            return serverList.get(action).getDiscoveryUrls();
          } else {
            return new String[] {serversOnNetwork.get(action - n).getDiscoveryUrl()};
          }
      }
    } while (true);
  }

  /**
   * Convert the event field values to a string, together with their field names.
   */
  protected String eventFieldsToString(QualifiedName[] fieldNames, Variant[] fieldValues) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < fieldValues.length; i++) {
      Object fieldValue = fieldValues[i] == null ? null : fieldValues[i].getValue();
      // Find the BrowseName of the node corresponding to NodeId values
      try {
        UaNode node = null;
        if (fieldValue instanceof NodeId) {
          node = client.getAddressSpace().getNode((NodeId) fieldValue);
        } else if (fieldValue instanceof ExpandedNodeId) {
          node = client.getAddressSpace().getNode((ExpandedNodeId) fieldValue);
        }
        if (node != null) {
          fieldValue = String.format("%s {%s}", node.getBrowseName(), fieldValue);
        }
      } catch (Exception e) {
        // Node not found, just use fieldValue
      }
      if (i < fieldNames.length) {
        QualifiedName fieldName = fieldNames[i];
        sb.append(fieldName.getName() + "=" + fieldValue + "; ");
      } else {
        sb.append("ConditionId=" + fieldValue);
      }
    }
    return sb.toString();
  }

  protected String eventToString(NodeId nodeId, QualifiedName[] fieldNames, Variant[] fieldValues) {
    return String.format("Node: %s Fields: %s", nodeId, eventFieldsToString(fieldNames, fieldValues));
  }

  /**
   * Get a string that represents the current node, used in {@link #printCurrentNode(NodeId)}.
   */
  protected String getCurrentNodeAsString(UaNode node) {
    String nodeStr = "";
    String typeStr = "";
    String analogInfoStr = "";
    nodeStr = node.getDisplayName().getText();
    UaType type = null;
    if (node instanceof UaInstance) {
      type = ((UaInstance) node).getTypeDefinition();
    }
    typeStr = (type == null ? nodeClassToStr(node.getNodeClass()) : type.getDisplayName().getText());

    // This is the way to access type specific nodes and their
    // properties, for example to show the engineering units and
    // range for all AnalogItems
    if (node instanceof AnalogItemType) {
      try {
        AnalogItemType analogNode = (AnalogItemType) node;
        EUInformation units = analogNode.getEngineeringUnits();
        analogInfoStr = units == null ? "" : " Units=" + units.getDisplayName().getText();
        Range range = analogNode.getEURange();
        analogInfoStr =
            analogInfoStr + (range == null ? "" : String.format(" Range=(%f; %f)", range.getLow(), range.getHigh()));
      } catch (Exception e) {
        printException(e);
      }
    }

    String currentNodeStr =
        String.format("*** Current Node: %s: %s (ID: %s)%s", nodeStr, typeStr, node.getNodeId(), analogInfoStr);
    return currentNodeStr;
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
   */
  protected ApplicationIdentity initApplicationIdentity(final ApplicationDescription applicationDescription,
      final PkiDirectoryCertificateStore applicationCertificateStore, final File privatePath)
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
   * Initialize the global EventFieldNames that is used in
   * {@link #createEventFilter(QualifiedName[])}.
   */
  protected void initEventFieldNames() {
    if (eventFieldNames[eventFieldNames.length - 1] == null) {
      // Define the custom fields, against the MyEventType definition of
      // the SampleConsoleServer.
      // For other servers we should get null values in response.

      // First find the namespaceIndex for our custom fields
      final String SAMPLE_ADDRESS_SPACE = "http://www.prosysopc.com/OPCUA/SampleAddressSpace";
      int namespaceIndex;
      namespaceIndex = client.getNamespaceTable().getIndex(SAMPLE_ADDRESS_SPACE);

      if (namespaceIndex < 0) {
        // We are connected to a server other than SampleConsoleServer
        // Setting the namespaceIndex to some valid index

        // Note that the new Qualified Names will not be recognized by the server, anyways
        namespaceIndex = 0;
      }
      eventFieldNames[eventFieldNames.length - 2] = new QualifiedName(namespaceIndex, "MyVariable");
      eventFieldNames[eventFieldNames.length - 1] = new QualifiedName(namespaceIndex, "MyProperty");

    }
  }

  protected void initHttps(DefaultCertificateValidator certValidator) {
    // The TLS security policies to use for HTTPS
    Set<HttpsSecurityPolicy> supportedHttpsModes = new HashSet<HttpsSecurityPolicy>();
    // HTTPS was added in UA 1.02
    supportedHttpsModes.addAll(HttpsSecurityPolicy.ALL_102);
    supportedHttpsModes.addAll(HttpsSecurityPolicy.ALL_103);
    supportedHttpsModes.addAll(HttpsSecurityPolicy.ALL_104);
    supportedHttpsModes.addAll(HttpsSecurityPolicy.ALL_105);
    client.getHttpsSettings().setHttpsSecurityPolicies(supportedHttpsModes);

    // Define a custom certificate validator for the HTTPS certificates
    client.getHttpsSettings().setCertificateValidator(certValidator);
  }

  /**
   * Initialize the client.
   */
  protected void initialize()
      throws SecureIdentityException, IOException, SessionActivationException, ServerListException {

    // *** Create the UaClient
    client = new UaClient();

    // If connection is in reverse mode (cmdline argument '-r'), must set these as well.
    if (reversePort > 0) {

      // You could also use setReverseAddress, if you wish to only bind on a specific local address
      // instead of all of them.
      client.setReversePort(reversePort);

      // This is set to validate incoming reverse connections
      client.setReverseConnectionListener(new MyReverseConnectionListener());
    } else {
      client.setAddress(serverAddress);
    }

    // Add listener
    client.setListener(clientListener);

    // *** Certificate Validator

    // PkiDirectoryCertificateStore is a default implementation that keeps track of trusted and
    // rejected certificates of other applications within the file system.
    final PkiDirectoryCertificateStore applicationCertificateStore = new PkiDirectoryCertificateStore("PKI/CA");
    final PkiDirectoryCertificateStore applicationIssuerCertificateStore =
        new PkiDirectoryCertificateStore("PKI/CA/issuers");

    // CertificateValidator defines the details about how to trust previously untrusted Applications
    final DefaultCertificateValidator certValidator =
        new DefaultCertificateValidator(applicationCertificateStore, applicationIssuerCertificateStore);
    // Set validator to accept CA certificates without CRLs
    certValidator.getIgnoredChecks().add(IgnoredChecks.IGNORE_CA_MISSING_CRL);
    client.setCertificateValidator(certValidator);

    // ValidationListener can be used to customize the validation, for example,
    // to prompt the user what to do
    validationListener = new MyCertificateValidationListener(client, disableHostnameVerification);
    certValidator.setValidationListener(validationListener);

    // The folder in which to save the Application's own certificate and private key
    File privatePath = new File(applicationCertificateStore.getBaseDir(), "private");

    // *** Application Description

    ApplicationDescription applicationDescription = initApplicationDescription(APP_NAME, ApplicationType.Client);

    // *** Application Identity

    final ApplicationIdentity identity =
        initApplicationIdentity(applicationDescription, applicationCertificateStore, privatePath);

    client.setApplicationIdentity(identity);


    // Define our user locale - the default is Locale.getDefault()
    client.setLocale(Locale.ENGLISH);

    // Define the call timeout in milliseconds. Default is null - to
    // use the value of UaClient.getEndpointConfiguration() which is
    // 120000 (2 min) by default
    client.setTimeout(30000);

    // StatusCheckTimeout is used to detect communication
    // problems and start automatic reconnection.
    // These are the default values:
    client.setStatusCheckTimeout(10000);
    // client.setAutoReconnect(true);

    // Listen to server status changes
    client.addServerStatusListener(serverStatusListener);

    // Define the security mode

    // securityMode is defined from the command line
    client.setSecurityMode(securityMode);

    // This is the way how you can define the SecurityMode
    // client.setSecurityMode(SecurityMode.BASIC256SHA256_SIGN_ENCRYPT);
    // client.setSecurityMode(SecurityMode.BASIC256SHA256_SIGN);
    // client.setSecurityMode(SecurityMode.NONE);


    initHttps(certValidator);

    // If the server supports user authentication, you can set the user
    // identity.
    if (userName == null) {
      // - Default is to use Anonymous authentication, like this:
      client.setUserIdentity(new UserIdentity());
    } else {
      // - Use username/password authentication (note requires security,
      // above):
      if (passWord == null) {
        print("Enter password for user " + userName + ":");
        passWord = readInput(false);
      }
      client.setUserIdentity(new UserIdentity(userName, passWord));
    }
    // - Read the user certificate and private key from files:
    // client.setUserIdentity(new UserIdentity(new java.net.URL(
    // "my_certificate.der"), new java.net.URL("my_protectedkey.pfx"),
    // "my_protectedkey_password"));

    // Session timeout 10 minutes; default is one hour
    // client.setSessionTimeout(600000);

    // Set endpoint configuration parameters
    client.getEndpointConfiguration().setMaxByteStringLength(Integer.MAX_VALUE);
    client.getEndpointConfiguration().setMaxArrayLength(Integer.MAX_VALUE);

    // TCP Buffer size parameters - these may help with high traffic
    // situations.
    // See http://fasterdata.es.net/host-tuning/background/ for some hints
    // how to use them
    // TcpConnection.setReceiveBufferSize(700000);
    // TcpConnection.setSendBufferSize(700000);


    if (reversePort > 0) {
      println("Waiting for a Reverse Connection at port: " + reversePort);
    } else {
      println("Connecting to " + serverAddress);
    }
  }

  /**
   * The main menu loop. When this exits, the program terminates.
   */
  protected void mainMenu() throws ServerListException, URISyntaxException {

    if (connectToDiscoveryServer) {
      if (!discover()) {
        return;
      }
    }

    // Try to connect to the server already at this point.
    connect();

    // Subscribe to items specified from command line
    subscribeToInitialItems();

    // You have one node selected all the time, and all operations
    // target that. We can initialize that to the standard ID of the
    // RootFolder (unless it was specified from command line).

    // Identifiers contains a list of all standard node IDs
    if (nodeId == null) {
      nodeId = Identifiers.RootFolder;
    }

    /******************************************************************************/
    /* Wait for user command to execute next action. */
    do {
      printMenu(nodeId);

      try {
        switch (readAction()) {
          case ACTION_RETURN:
            disconnect();
            return;
          case 0:
            if (discover()) {
              connect();
            }
            break;
          case 1:
            connect();
            break;
          case 2:
            disconnect();
            break;
          case 3:
            NodeId browseId = browse(nodeId, null);
            if (browseId != null) {
              nodeId = browseId;
            }
            break;
          case 4:
            read(nodeId);
            break;
          case 5:
            write(nodeId);
            break;
          case 6:
            registerNodes(nodeId);
            break;
          case 7:
            unregisterNodes();
            break;
          case 8:
            subscribe(nodeId);
            break;
          case 9:
            callMethod(nodeId);
            break;
          case 10:
            readHistory(nodeId);
            break;
          case 11:
            addNode(nodeId);
            break;
          default:
            continue;
        }
      } catch (Exception e) {
        printException(e);
      }

    } while (true);
    /******************************************************************************/
  }

  /**
   * Parse Command line arguments. See the Usage for the expected options.
   * 
   * @param args the arguments
   * @return true if the program may continue.
   */
  protected boolean parseCmdLineArgs(String[] args) throws IllegalArgumentException {
    int i = 0;
    boolean secModeSet = false;
    while ((args.length > i) && ((args[i].startsWith("-") || args[i].startsWith("/")))) {
      if (args[i].equals("-d")) {
        println("Connecting to a discovery server.");
        connectToDiscoveryServer = true;
      } else if (args[i].equals("-n")) {
        nodeId = NodeId.parseNodeId(args[++i]);
      } else if (args[i].equals("-s")) {
        String arg = args[++i];
        parseSecurityMode(arg);
        secModeSet = true;

      } else if (args[i].equals("-k")) {
        certKeySize = Integer.parseInt(args[++i]);
      } else if (args[i].equals("-m")) {
        initialMonitoredItems.add(args[++i]);
      } else if (args[i].equals("-u")) {
        userName = args[++i];
      } else if (args[i].equals("-p")) {
        passWord = args[++i];
      } else if (args[i].equals("-t")) {
        stackTraceOnException = true;
      } else if (args[i].equals("-r")) {
        reversePort = Integer.parseInt(args[++i]);
      } else if (args[i].equals("-dt")) {
        showReadValueDataType = true;
      } else if (args[i].equals("-w")) {
        // do nothing, was handled at startup
      } else if (args[i].equals("--disable-hostname-verification") || args[i].equals("-H")) {
        disableHostnameVerification = true;
      } else if (args[i].equals("-?")) {
        return false;
      } else {
        throw new IllegalArgumentException(args[i]);
      }
      i++;
    }
    if (i < args.length) {
      serverAddress = args[i++];
      while ((i < args.length) && !args[i].startsWith("#")) {
        cmdSequence.add(args[i++]);
      }
    }
    if (reversePort == 0 && serverAddress == null) {
      serverAddress = promptServerAddress();
      if (!secModeSet) {
        promptSecurityMode();
      }
    }
    return true;
  }

  /**
   * Convert the security mode command line argument to a Security Mode.
   * 
   * @return
   */
  protected boolean parseSecurityMode(String arg) {
    if (arg == null || arg.isEmpty()) {
      return false;
    }

    arg = arg.toLowerCase();
    char secModeStr = arg.charAt(0);
    int level = 0; // See the default segment below in the switch for the default option
    if (arg.length() > 1) {
      try {
        level = Integer.parseInt(arg.substring(1, 2));
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Parameter for Security Mode is invalid: " + arg);
      }
    }
    if (secModeStr == 'n') {
      securityMode = SecurityMode.NONE;
    } else if (secModeStr == 's') {
      switch (level) {
        case 1:
          securityMode = SecurityMode.BASIC128RSA15_SIGN;
          break;
        case 2:
          securityMode = SecurityMode.BASIC256_SIGN;
          break;
        default:
        case 3:
          securityMode = SecurityMode.BASIC256SHA256_SIGN;
          break;
        case 4:
          securityMode = SecurityMode.AES128_SIGN;
          break;
        case 5:
          securityMode = SecurityMode.AES256_SIGN;
          break;
      }
    } else if (secModeStr == 'e') {
      switch (level) {
        case 1:
          securityMode = SecurityMode.BASIC128RSA15_SIGN_ENCRYPT;
          break;
        case 2:
          securityMode = SecurityMode.BASIC256_SIGN_ENCRYPT;
          break;
        default:
        case 3:
          securityMode = SecurityMode.BASIC256SHA256_SIGN_ENCRYPT;
          break;
        case 4:
          securityMode = SecurityMode.AES128_SIGN_ENCRYPT;
          break;
        case 5:
          securityMode = SecurityMode.AES256_SIGN_ENCRYPT;
          break;
      }
    } else {
      throw new IllegalArgumentException(
          "Parameter for SecurityMode (-s) is invalid, expected 'n', 's' or 'e'; was '" + secModeStr + "'");
    }
    return true;
  }

  protected void printCancelled() {
    println("Cancelled.");
  }

  /**
   * Show the currently selected node.
   */
  protected void printCurrentNode(NodeId nodeId) {
    if (client.isConnected()) {
      // Find the node from the NodeCache
      try {
        UaNode node = client.getAddressSpace().getNode(nodeId);

        if (node == null) {
          return;
        }
        String currentNodeStr = getCurrentNodeAsString(node);
        if (currentNodeStr != null) {
          println(currentNodeStr);
          println("");
        }
      } catch (ServiceException e) {
        printException(e);
      } catch (AddressSpaceException e) {
        printException(e);
      }
    }
  }

  /**
   * Show the endpoints supported by the server.
   */
  protected void printEndpoints(EndpointDescription[] endpoints) {
    println("Endpoints supported by the server (by Discovery Service)");
    for (EndpointDescription e : endpoints) {
      println(String.format("%s [%s,%s]", e.getEndpointUrl(), e.getSecurityPolicyUri(), e.getSecurityMode()));
    }

  }

  /**
   * Show the main menu of the application.
   */
  protected void printMenu(NodeId nodeId) {
    println("");
    println("");
    println("");
    if (client.isConnected()) {
      println("*** Connected to: " + client.getAddress());
      println("");
      if (nodeId != null) {
        printCurrentNode(nodeId);
      }
    } else {
      println("*** NOT connected to: " + client.getAddress());
    }

    System.out.println("-------------------------------------------------------");
    println("- Enter x to close client");
    System.out.println("-------------------------------------------------------");
    System.out.println("- Enter 0 to start discovery                          -");
    System.out.println("- Enter 1 to connect to server                        -");
    System.out.println("- Enter 2 to disconnect from server                   -");
    System.out.println("- Enter 3 to browse the server address space          -");
    System.out.println("- Enter 4 to read values                              -");
    System.out.println("- Enter 5 to write values                             -");
    System.out.println("- Enter 6 to register nodes                           -");
    System.out.println("- Enter 7 to unregister nodes                         -");
    if (subscription == null) {
      System.out.println("- Enter 8 to create a subscription                    -");
    } else {
      System.out.println("- Enter 8 to add a new item to the subscription       -");
    }
    System.out.println("- Enter 9 to call a method                            -");
    System.out.println("- Enter 10 to read history                            -");
    System.out.println("- Enter 11 to add a node to the server                -");
    System.out.println("-------------------------------------------------------");
  }

  /**
   * Show the Method Output Arguments after a method call.
   */
  protected void printOutputArguments(UaMethod method, Variant[] outputs)
      throws ServiceException, AddressSpaceException, MethodArgumentException, StatusException {
    if ((outputs != null) && (outputs.length > 0)) {
      println("Output values:");
      Argument[] outputArguments = method.getOutputArguments();
      for (int i = 0; i < outputArguments.length; i++) {
        UaNode dataType = client.getAddressSpace().getType(outputArguments[i].getDataType());
        println(String.format("%s: %s {%s} = %s", outputArguments[i].getName(), dataType.getBrowseName(),
            outputArguments[i].getDescription().getText(), outputs[i].getValue()));
      }
    } else {
      println("OK (no output)");
    }
  }

  /**
   * Show the Security Modes supported by the server.
   */
  protected void printSecurityModes(List<SecurityMode> supportedSecurityModes) {
    println("SecurityModes supported by the server:");
    for (SecurityMode m : supportedSecurityModes) {
      println(m.toString());
    }

  }

  /**
   * Show the User Identity Tokens supported by the server.
   */
  protected void printUserIdentityTokens(UserTokenPolicy[] supportedUserIdentityTokens) {
    println("The server supports the following user tokens:");
    for (UserTokenPolicy p : supportedUserIdentityTokens) {
      println(p.getTokenType().toString());
    }

  }

  /**
   * Prompt the user for input.
   */
  protected String prompt(String promptString) {
    println(promptString);
    return readInput(false);
  }

  /**
   * Prompt the user for an aggregate function to use in {@link #readHistory(NodeId)}
   * 
   */
  protected NodeId promptAggregateFunction() {
    // Check, which Aggregate function to use
    final NodeId[] aggregateTypes = new NodeId[] {Identifiers.AggregateFunction_Average,
        Identifiers.AggregateFunction_Count, Identifiers.AggregateFunction_Delta, Identifiers.AggregateFunction_End,
        Identifiers.AggregateFunction_Maximum, Identifiers.AggregateFunction_MaximumActualTime,
        Identifiers.AggregateFunction_Minimum, Identifiers.AggregateFunction_MinimumActualTime,
        Identifiers.AggregateFunction_Range, Identifiers.AggregateFunction_Start,
        Identifiers.AggregateFunction_WorstQuality};

    int aggregate = -1;
    do {
      println("");
      println("Select aggregate function:");

      // Unless stated otherwise, all the Aggregates are calculated from the
      // last 30 minutes, using 5 seconds intervals and forward time flow.
      println(" 0 - Average");
      println(" 1 - Count");
      println(" 2 - Delta");
      println(" 3 - End");
      println(" 4 - Maximum");
      println(" 5 - MaximumActualTime");
      println(" 6 - Minimum");
      println(" 7 - MinimumActualTime");
      println(" 8 - Range");
      println(" 9 - Start");
      println("10 - WorstQuality");
      aggregate = readAction();
    } while (aggregate >= aggregateTypes.length);
    if (aggregate < 0) {
      return null;
    }
    return aggregateTypes[aggregate];
  }

  /**
   * Prompt the user to read an attributeId.
   */
  protected UnsignedInteger promptAttributeId() {

    println("Select the node attribute:");
    for (long i = Attributes.NodeId.getValue(); i <= Attributes.AccessLevelEx.getValue(); i++) {
      printf("%d - %s\n", i, AttributesUtil.toString(UnsignedInteger.valueOf(i)));
    }
    int action = readAction();
    if (action < 0) {
      return null;
    }
    UnsignedInteger attributeId = UnsignedInteger.valueOf(action);
    System.out.println("attribute: " + AttributesUtil.toString(attributeId));
    return attributeId;
  }

  /**
   * Prompt the values for each input argument from the console.
   *
   * @param method The method whose inputs are read
   * @return Variant array of the input values
   * @throws ServiceException if a service call fails
   * @throws AddressSpaceException if the input data types cannot be determined
   * @throws ServerConnectionException if we are not connected to the client
   * @throws MethodArgumentException if the input arguments are not validly defined for the node
   */
  protected Variant[] promptInputArguments(UaMethod method)
      throws ServiceException, ServerConnectionException, AddressSpaceException, MethodArgumentException {
    Argument[] inputArguments = method.getInputArguments();
    if ((inputArguments == null) || (inputArguments.length == 0)) {
      return new Variant[0];
    }
    Variant[] inputs = new Variant[inputArguments.length];
    println("Enter value for Inputs:");
    for (int i = 0; i < inputs.length; i++) {
      UaDataType dataType = (UaDataType) client.getAddressSpace().getType(inputArguments[i].getDataType());
      println(String.format("%s: %s {%s} = ", inputArguments[i].getName(), dataType.getDisplayName().getText(),
          inputArguments[i].getDescription().getText()));
      while (inputs[i] == null) {
        try {
          inputs[i] = client.getAddressSpace().getDataTypeConverter().parseVariant(readInput(false), dataType);
        } catch (NumberFormatException e) {
          printException(e);
        }
      }
    }
    return inputs;
  }

  /**
   * Show the available methods of an object and prompt the user to select one of them.
   * 
   * @param nodeId of the object to look for methods
   * @return the NodeId of the selected method
   */
  protected NodeId promptMethodId(NodeId nodeId)
      throws ServerConnectionException, ServiceException, StatusException, AddressSpaceException {
    // Ensure that we are in an object node
    NodeClass nodeClass = client.getAddressSpace().getNode(nodeId).getNodeClass();
    if (!nodeClass.equals(NodeClass.Object)) {
      println("Not in an object.");
      println("You must be in an object that has methods to be able to call methods.");
      if (nodeClass.equals(NodeClass.Method)) {
        println("Since you are currently at a method node, back up one step to the object.");
      }
      return null;
    }
    // A lightweight way to list the methods is to use browseMethods
    // List<ReferenceDescription> methodRefs =
    // client.getAddressSpace().browseMethods(nodeId);
    List<UaMethod> methods = client.getAddressSpace().getMethods(nodeId);
    if (methods.size() == 0) {
      println("No methods available.");
      return null;
    }
    println("Select the method to execute.");
    for (int i = 0; i < methods.size(); i++) {
      printf("%d - %s\n", i, methods.get(i).getDisplayName().getText());
    }
    int action;
    do {
      action = readAction();
    } while (action >= methods.size());
    if (action < 0) {
      return null;
    }
    NodeId methodId = methods.get(action).getNodeId();
    System.out.println("Method: " + methodId);
    return methodId;
  }

  /**
   * Prompt the user for the Security Mode.
   */
  protected void promptSecurityMode() {
    println("Select the security mode to use.");
    println("Sign and SignAndEncrypt require the server to trust the certificate of SampleConsoleClient.");
    println("(n=None,s=Sign,e=SignAndEncrypt)");

    // repeat until input is valid
    while (true) {
      try {
        if (parseSecurityMode(readInput(false))) {
          break;
        }
      } catch (IllegalArgumentException e) {
        printException(e);
      }
    }
  }

  /**
   * Prompt the user for the server connection address.
   */
  protected String promptServerAddress() throws IllegalArgumentException {
    while (true) {
      println("Enter the connection URL of the server to connect to\n(press enter to use the default address="
          + defaultServerAddress + "):");

      String address = readInput(false, defaultServerAddress);
      try {
        UaAddress.validate(address);
        return address;
      } catch (URISyntaxException e) {
        print(e.getMessage() + "\n\n");
      }
    }
  }

  /**
   * Read a node attribute value from the server.
   *
   */
  protected void read(NodeId nodeId) throws ServiceException, StatusException {
    println("read node " + nodeId);
    UnsignedInteger attributeId = promptAttributeId();
    DataValue value = client.readAttribute(nodeId, attributeId);
    println(dataValueToString(nodeId, attributeId, value));

  }

  protected LocalizedText readDisplayName(NodeId aggregateType) throws ServiceException, StatusException {
    return client.readAttribute(aggregateType, Attributes.DisplayName).getValue().asClass(LocalizedText.class,
        LocalizedText.EMPTY);
  }

  /**
   * readHistory sample.
   *
   */
  protected void readHistory(NodeId nodeId) throws ServiceException, StatusException, AddressSpaceException {
    UaNode node = client.getAddressSpace().getNode(nodeId);

    if (node instanceof UaVariable) {

      // Validate that history is readable for the node

      UaVariable variable = (UaVariable) node;
      if (!variable.getAccessLevel().contains(AccessLevelType.Options.HistoryRead)) {
        println("The variable does not have history");
        return;
      }
      if (!variable.getUserAccessLevel().contains(AccessLevelType.Options.HistoryRead)) {
        println("The variable has history, but it is not readable with this user account");
        return;
      }

      println("Reading history for the variable " + variable.getBrowseName());

      // Check which history read we want to make
      println("");
      println("Select history read method:");

      println(" 0 - HistoryReadRaw");
      println(" 1 - HistoryReadAtTimes");
      println(" 2 - HistoryReadProcessed...");

      int action = readAction();

      try {
        DateTime endTime = DateTime.currentTime();
        DateTime startTime = endTime.minus(30, ChronoUnit.MINUTES);
        DataValue[] values = null;

        println("");
        switch (action) {
          case 0:
            println("Reading raw data history between " + startTime + " and " + endTime);

            values = client.historyReadRaw(nodeId, startTime, endTime, UnsignedInteger.valueOf(1000), true, null,
                TimestampsToReturn.Source);
            break;
          case 1:
            println("at " + startTime + " and " + endTime);

            DateTime[] reqTimes = new DateTime[] {startTime, endTime};
            values = client.historyReadAtTimes(nodeId, reqTimes, null, TimestampsToReturn.Source);
            break;
          case 2:
            NodeId aggregateFunction = promptAggregateFunction();

            if (aggregateFunction != null) {
              Double processingInterval = 5 * 1000.0;
              println("Reading processed data history between " + startTime + " and " + endTime
                  + " using processingInterval=" + processingInterval.toString());
              AggregateConfiguration aggregateConfiguration =
                  new AggregateConfiguration(false, true, UnsignedByte.valueOf(100), UnsignedByte.valueOf(100), false);
              NumericRange indexRange = null;

              values = client.historyReadProcessed(nodeId, startTime, endTime, processingInterval, aggregateFunction,
                  aggregateConfiguration, indexRange, TimestampsToReturn.Source);
            } else {
              printCancelled();
            }
            break;
          default:
            printCancelled();
        }

        if (values != null) {
          println("Got " + values.length + " samples:");
          for (int i = 0; i < values.length; i++) {
            println("Value " + (i + 1) + " = " + values[i].getValue().getValue() + " | "
                + values[i].getSourceTimestamp() + " | " + values[i].getStatusCode());
          }
        }
      } catch (Exception e) {
        printException(e);
      }
    } else if (node instanceof UaObject) {

      // Validate that history is readable for the node

      UaObject object = (UaObject) node;
      if (object.getEventNotifier().contains(EventNotifierType.Options.HistoryRead)) {

        println("Reading event history for the object " + node);
        try {
          DateTime endTime = DateTime.currentTime();
          DateTime startTime = endTime.minus(1, ChronoUnit.HOURS);

          println("between " + startTime + " and " + endTime);

          // Use a similar filter that is used by MonitoredEventItems
          initEventFieldNames();
          EventFilter eventFilter = createEventFilter(eventFieldNames);
          HistoryEventFieldList[] events = client.historyReadEvents(nodeId, startTime, endTime,
              UnsignedInteger.valueOf(1000), eventFilter, TimestampsToReturn.Source);

          if (events != null) {
            println("Count = " + events.length);
            for (int i = 0; i < events.length; i++) {
              println("Event " + (i + 1) + " = "
                  + eventFieldsToString(eventFieldNames, Variant.asVariantArray(events[i].getEventFields())));
            }
          }
        } catch (Exception e) {
          printException(e);
        }
      } else {
        println("The object does not have history");
        return;
      }
    } else {
      println("History is only available for object and variable nodes. The current node is a " + node.getNodeClass()
          + ".");
    }
  }

  /**
   * Convert a ReferenceDescription to a string that can be displayed.
   */
  protected String referenceToString(ReferenceDescription r)
      throws ServerConnectionException, ServiceException, StatusException {
    if (r == null) {
      return "";
    }
    String referenceTypeStr = null;
    try {
      // Find the reference type from the NodeCache
      UaReferenceType referenceType = (UaReferenceType) client.getAddressSpace().getType(r.getReferenceTypeId());
      if ((referenceType != null) && (referenceType.getDisplayName() != null)) {
        if (r.getIsForward()) {
          referenceTypeStr = referenceType.getDisplayName().getText();
        } else {
          referenceTypeStr = referenceType.getInverseName().getText();
        }
      }
    } catch (AddressSpaceException e) {
      printException(e);
      print(r.toString());
      referenceTypeStr = r.getReferenceTypeId().getValue().toString();
    }
    String typeStr;
    switch (r.getNodeClass()) {
      case Object:
      case Variable:
        try {
          // Find the type from the NodeCache
          UaNode type = client.getAddressSpace().getNode(r.getTypeDefinition());
          if (type != null) {
            typeStr = type.getDisplayName().getText();
          } else {
            typeStr = r.getTypeDefinition().getValue().toString();
          }
        } catch (AddressSpaceException e) {
          printException(e);
          print("type not found: " + r.getTypeDefinition().toString());
          typeStr = r.getTypeDefinition().getValue().toString();
        }
        break;
      default:
        typeStr = nodeClassToStr(r.getNodeClass());
        break;
    }
    return String.format("%s%s (ReferenceType=%s, BrowseName=%s%s)", r.getDisplayName().getText(), ": " + typeStr,
        referenceTypeStr, r.getBrowseName(), r.getIsForward() ? "" : " [Inverse]");
  }

  /**
   * Call the register nodes service. This is seldom used in practice - mainly to notify the server
   * that certain nodes will be read often, so that it can start polling them in the background and
   * optimize for faster use.
   *
   */
  protected void registerNodes(NodeId nodeId) {
    try {
      NodeId[] registeredNodeId = client.getAddressSpace().registerNodes(nodeId);
      println("Registered NodeId " + nodeId + " -> registeredNodeId is " + registeredNodeId[0]);
    } catch (ServiceException e) {
      printException(e);
    }
  }

  /**
   * @param serverUri
   */
  protected void setServerAddress(String serverUri) {
    this.serverAddress = serverUri;
  }

  /**
   * Subscribe to a variable or object node for data changes or events, respectively.
   */
  protected void subscribe(NodeId nodeId) {
    if (nodeId == null) {
      println("*** Select a node to subscribe first ");
      println("");
      return;
    }
    println("*** Subscribing to node: " + nodeId);
    println("");
    try {
      // Create the subscription
      if (subscription == null) {
        subscription = createSubscription();
      }
      // Create the monitored item
      createMonitoredItem(subscription, nodeId);

    } catch (ServiceException e) {
      printException(e);
    } catch (StatusException e) {
      printException(e);
    }

    /*
     * Show the menu and wait for action.
     */
    try {
      subscriptionMenu();
    } catch (ServiceException e) {
      printException(e);
    } catch (StatusException e) {
      printException(e);
    }

  }

  /**
   * Subscribe to items specified from command line
   */
  protected void subscribeToInitialItems() {
    if (!initialMonitoredItems.isEmpty()) {
      // while (true) {
      try {
        if (subscription == null) {
          subscription = createSubscription();
        }
      } catch (ServiceException e) {
        printException(e);
        return;
      } catch (StatusException e) {
        printException(e);
        return;
      }
    }
    for (String s : initialMonitoredItems) {
      try {
        if (s.endsWith("/*")) {
          createMonitoredItemsForChildren(subscription, s);
        } else {
          createMonitoredItem(subscription, NodeId.parseNodeId(s));
        }
      } catch (IllegalArgumentException e1) {
        printException(e1);
      } catch (ServiceException e1) {
        printException(e1);
      } catch (StatusException e1) {
        printException(e1);
      }
    }
    try {
      if (subscription != null) {
        subscriptionMenu();
      }
    } catch (ServiceException e) {
      printException(e);
    } catch (StatusException e) {
      printException(e);
    }
    // }
  }

  /**
   * The Subscription Menu used when the user selects the subscription monitoring from the main
   * menu.
   * 
   */
  protected void subscriptionMenu() throws ServiceException, StatusException {
    // Make sure the subscription is re-enabled, in case it was
    // paused earlier (see below). By default, new subscriptions are
    // enabled
    subscription.setPublishingEnabled(true);

    do {
      println("-------------------------------------------------------");
      println("- Enter x to end (and remove) the subcription");
      println("- Enter p to pause the subscription (e.g. to add new items)");
      println("- Enter r to remove an item from the subscription");
      println("- Enter i to change the publishing interval of the subscription");
      println("-------------------------------------------------------");
      String input;
      input = readInput(true);
      if (input.equals("r")) {
        subscription.setPublishingEnabled(false);
        try {
          MonitoredItemBase removedItem = null;
          while (removedItem == null) {
            println("-------------------------------------------------------");
            println("Monitored Items:");
            for (MonitoredItemBase item : subscription.getItems()) {
              println(item.toString());
            }
            println("- Enter the ClientHandle of the item to remove it");
            println("- Enter x to cancel.");
            println("-------------------------------------------------------");
            String handleStr = readInput(true);
            if (handleStr.equals("x")) {
              break;
            }
            try {
              UnsignedInteger handle = UnsignedInteger.parseUnsignedInteger(handleStr);
              removedItem = subscription.removeItem(handle);
              printf(removedItem != null ? "Item %s removed\n" : "No such item: %s\n", handle);
            } catch (Exception e) {
              printException(e);
            }
          }
        } finally {
          subscription.setPublishingEnabled(true);

        }
      } else if (input.equals("p")) {
        subscription.setPublishingEnabled(false);
        break;
      } else if (input.equals("x")) {
        try {
          StatusCode sc = client.removeSubscription(subscription);
          if ((sc != null) && sc.isNotGood()) {
            throw new StatusException(sc);
          }
        } catch (Exception e) {
          /*
           * Nevermind. Well actually the CTT does check that these exceptions are displayed so..
           */
          print("Got exception while deleting Subscription: ");
          printException(e);
        }
        subscription = null;
        break;
      } else if (input.equals("i")) {
        boolean wasEnabled = subscription.isPublishingEnabled();
        println("Current publishing interval is " + subscription.getPublishingInterval() + " ms");
        print("New publishing interval: ");
        try {
          subscription.setPublishingEnabled(false);
          try {
            double interval = Double.parseDouble(readInput(true));
            subscription.setPublishingInterval(interval);
            println("Publishing interval changed to " + interval + " ms");
          } catch (NumberFormatException e) {
            printException(e);
          }
        } finally {
          subscription.setPublishingEnabled(wasEnabled);
        }
      }
    } while (true);
  }

  /**
   * Toggle the setting, whether we want to browse all references or only forward hierarchical ones.
   */
  protected NodeId toggleBrowseAll(NodeId nodeId, NodeId prevId) throws ServiceException, StatusException {
    if (NodeId.isNull(client.getAddressSpace().getReferenceTypeId())) {
      client.getAddressSpace().setReferenceTypeId(Identifiers.HierarchicalReferences);
      client.getAddressSpace().setBrowseDirection(BrowseDirection.Forward);
    } else {
      // request all types
      client.getAddressSpace().setReferenceTypeId(NodeId.NULL);
      client.getAddressSpace().setBrowseDirection(BrowseDirection.Both);
    }
    // if (ReferencesToReturn == null) {
    // ReferencesToReturn = Identifiers.HierarchicalReferences;
    // client.getAddressSpace().setBrowseDirection(
    // BrowseDirection.Forward);
    // } else {
    // ReferencesToReturn = null;
    // client.getAddressSpace().setBrowseDirection(
    // BrowseDirection.Both);
    // }
    return browse(nodeId, prevId);
  }

  /**
   * Use the translateBrowsePathToNodeId service in AddressSPace to locate a node relative to te
   * current node.
   * 
   * @param nodeId
   * @throws ServiceException
   */
  protected void translateBrowsePathToNodeId(NodeId nodeId) throws ServiceException {
    println("Which node do you wish to translate?");
    println("Use / to separate nodes in the browsePath, e.g. 'Types/ObjectTypes/BaseObjectType/3:YourType'");
    println(
        "where each element is a 'parseable' BrowseName, i.e. the namespaceIndex can be defined with a prefix, like '3:'");
    String browsePathString = readInput(false);

    List<RelativePathElement> browsePath = new ArrayList<RelativePathElement>();
    for (String s : browsePathString.split("/")) {
      final QualifiedName targetName = QualifiedName.parseQualifiedName(s);
      browsePath.add(new RelativePathElement(Identifiers.HierarchicalReferences, false, true, targetName));
    }
    // The result may always contain several targets (if there are nodes with
    // the same browseName), although normally only one is expected.
    BrowsePathTarget[] pathTargets;
    try {
      pathTargets =
          client.getAddressSpace().translateBrowsePathToNodeId(nodeId, browsePath.toArray(new RelativePathElement[0]));
      for (BrowsePathTarget pathTarget : pathTargets) {
        String targetStr = "Target: " + pathTarget.getTargetId();
        if (!pathTarget.getRemainingPathIndex().equals(UnsignedInteger.MAX_VALUE)) {
          targetStr = targetStr + " - RemainingPathIndex: " + pathTarget.getRemainingPathIndex();
        }
        println(targetStr);
      }
    } catch (StatusException e1) {
      printException(e1);
    }
  }

  /**
   * Unregisters all previously registered nodes.
   */
  protected void unregisterNodes() {
    try {
      NodeId[] nodes = client.getAddressSpace().unregisterAllNodes();
      println("Unregistered " + nodes.length + " node(s).");
    } catch (ServiceException e) {
      printException(e);
    }
  }

  /**
   * Write a value to an attribute of the selected node.
   */
  protected void write(NodeId nodeId) throws ServiceException, AddressSpaceException, StatusException {
    UnsignedInteger attributeId = promptAttributeId();

    if (attributeId != null) {
      UaNode node = client.getAddressSpace().getNode(nodeId);
      println("Writing to node " + nodeId + " - " + node.getDisplayName().getText());

      // Find the DataType if setting Value - for other properties you must
      // find the correct data type yourself
      UaDataType dataType = null;
      if (attributeId.equals(Attributes.Value) && (node instanceof UaVariable)) {
        UaVariable v = (UaVariable) node;
        dataType = v.getDataType();
        println("DataType: " + dataType.getDisplayName().getText());
      }

      print("Enter the value to write: ");
      String value = readInput(true);
      try {
        boolean status = client.writeAttribute(nodeId, attributeId, value, true);
        if (status) {
          println("OK");
        } else {
          println("OK (completes asynchronously)");
        }
      } catch (ServiceException e) {
        printException(e);
      } catch (StatusException e) {
        printException(e);
      } catch (DataTypeConversionException e) {
        printException(e);
      }
    } else {
      printCancelled();
    }

  }



}
