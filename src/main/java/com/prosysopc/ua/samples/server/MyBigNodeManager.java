/**
 * Prosys OPC UA Java SDK
 * Copyright (c) Prosys OPC Ltd.
 * <http://www.prosysopc.com>
 * All rights reserved.
 */
package com.prosysopc.ua.samples.server;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.prosysopc.ua.AccessLevels;
import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.ValueRanks;
import com.prosysopc.ua.nodes.UaNode;
import com.prosysopc.ua.nodes.UaReference;
import com.prosysopc.ua.nodes.UaReferenceType;
import com.prosysopc.ua.nodes.UaValueNode;
import com.prosysopc.ua.server.IoManager;
import com.prosysopc.ua.server.MonitoredDataItem;
import com.prosysopc.ua.server.MonitoredItem;
import com.prosysopc.ua.server.NodeManager;
import com.prosysopc.ua.server.ServiceContext;
import com.prosysopc.ua.server.Subscription;
import com.prosysopc.ua.server.UaServer;
import com.prosysopc.ua.stack.builtintypes.DataValue;
import com.prosysopc.ua.stack.builtintypes.DateTime;
import com.prosysopc.ua.stack.builtintypes.ExpandedNodeId;
import com.prosysopc.ua.stack.builtintypes.LocalizedText;
import com.prosysopc.ua.stack.builtintypes.NodeId;
import com.prosysopc.ua.stack.builtintypes.QualifiedName;
import com.prosysopc.ua.stack.builtintypes.StatusCode;
import com.prosysopc.ua.stack.builtintypes.UnsignedInteger;
import com.prosysopc.ua.stack.builtintypes.Variant;
import com.prosysopc.ua.stack.common.ServiceResultException;
import com.prosysopc.ua.stack.core.Attributes;
import com.prosysopc.ua.stack.core.EventNotifierType;
import com.prosysopc.ua.stack.core.Identifiers;
import com.prosysopc.ua.stack.core.NodeClass;
import com.prosysopc.ua.stack.core.StatusCodes;
import com.prosysopc.ua.stack.core.TimestampsToReturn;
import com.prosysopc.ua.stack.utils.NumericRange;

/**
 * A sample implementation of a NodeManager which does not use UaNode objects, but connects to an
 * underlying system for the data.
 */
public class MyBigNodeManager extends NodeManager {

  /**
   * Helper for holding data. This simulates cases where the real-world-data would have been stored
   * elsewhere e.g. in a database.
   */
  public class DataItem {
    private NodeId dataType = Identifiers.Double;
    private final String name;
    private StatusCode status = StatusCode.valueOf(StatusCodes.Bad_WaitingForInitialData);
    private DateTime timestamp;
    private double value;

    public DataItem(String name) {
      super();
      this.name = name;
    }

    public NodeId getDataType() {
      return dataType;
    }

    /**
     * Sets the data of this item to the given DataValue.
     */
    public void getDataValue(DataValue dataValue) {
      dataValue.setValue(new Variant(getValue()));
      dataValue.setStatusCode(getStatus());
      dataValue.setServerTimestamp(DateTime.currentTime());
      dataValue.setSourceTimestamp(timestamp);
    }

    public String getName() {
      return name;
    }

    public StatusCode getStatus() {
      return status;
    }

    /**
     * The timestamp when the value or status changed.
     */
    public DateTime getTimestamp() {
      return timestamp;
    }

    public double getValue() {
      return value;
    }

    public void setDataType(NodeId dataType) {
      this.dataType = dataType;
    }

    public void setValue(double value) {
      setValue(value, StatusCode.GOOD);
    }

    /**
     * Sets value and status of this item.
     */
    public void setValue(double value, StatusCode status) {
      if (status == null) {
        status = StatusCode.BAD;
      }
      if ((this.value != value) || !this.status.equals(status)) {
        this.value = value;
        this.status = status;
        this.timestamp = DateTime.currentTime();
      }
    }
  }

  /**
   * An IO Manager which provides the values for the attributes of the nodes.
   */
  public class MyBigIoManager extends IoManager {

    /**
     * Constructor for the IoManager.
     *
     * @param nodeManager the node manager that uses this IO Manager.
     */
    public MyBigIoManager(NodeManager nodeManager) {
      super(nodeManager);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.prosysopc.ua.server.IoManager#readNonValue(com.prosysopc.ua. server .ServiceContext,
     * org.opcfoundation.ua.builtintypes.NodeId, com.prosysopc.ua.nodes.UaNode,
     * org.opcfoundation.ua.builtintypes.UnsignedInteger,
     * org.opcfoundation.ua.builtintypes.DataValue)
     */
    @Override
    protected void readNonValue(ServiceContext serviceContext, Object operationContext, NodeId nodeId, UaNode node,
        UnsignedInteger attributeId, DataValue dataValue) throws StatusException {
      Object value = null;
      UnsignedInteger status = StatusCodes.Bad_AttributeIdInvalid;

      DataItem dataItem = getDataItem(nodeId);
      final ExpandedNodeId expandedNodeId = getNamespaceTable().toExpandedNodeId(nodeId);
      if (attributeId.equals(Attributes.NodeId)) {
        value = nodeId;
      } else if (attributeId.equals(Attributes.BrowseName)) {
        value = getBrowseName(expandedNodeId, node);
      } else if (attributeId.equals(Attributes.DisplayName)) {
        value = getDisplayName(expandedNodeId, node, null);
      } else if (attributeId.equals(Attributes.Description)) {
        status = StatusCodes.Bad_AttributeIdInvalid;
      } else if (attributeId.equals(Attributes.NodeClass)) {
        value = getNodeClass(expandedNodeId, node);
      } else if (attributeId.equals(Attributes.WriteMask)) {
        value = UnsignedInteger.ZERO;
      } else if (dataItem != null) {
        if (attributeId.equals(Attributes.DataType)) {
          value = Identifiers.Double;
        } else if (attributeId.equals(Attributes.ValueRank)) {
          value = ValueRanks.Scalar;
        } else if (attributeId.equals(Attributes.ArrayDimensions)) {
          status = StatusCodes.Bad_AttributeIdInvalid;
        } else if (attributeId.equals(Attributes.AccessLevel)) {
          value = AccessLevels.READ_ONLY.asBuiltInType();
        } else if (attributeId.equals(Attributes.UserAccessLevel)) {
          value = AccessLevels.READ_ONLY.asBuiltInType();
        } else if (attributeId.equals(Attributes.Historizing)) {
          value = false;
        }
      } else if (attributeId.equals(Attributes.EventNotifier)) {
        // this is only requested for the folder
        value = EventNotifierType.of();
      }

      if (value == null) {
        dataValue.setStatusCode(status);
      } else {
        dataValue.setValue(new Variant(value));
      }
      dataValue.setServerTimestamp(DateTime.currentTime());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.prosysopc.ua.server.IoManager#readValue(com.prosysopc.ua.server .ServiceContext,
     * org.opcfoundation.ua.builtintypes.NodeId, com.prosysopc.ua.nodes.UaVariable,
     * org.opcfoundation.ua.utils.NumericRange, org.opcfoundation.ua.core.TimestampsToReturn,
     * org.opcfoundation.ua.builtintypes.DateTime, org.opcfoundation.ua.builtintypes.DataValue)
     */
    @Override
    protected void readValue(ServiceContext serviceContext, Object operationContext, NodeId nodeId, UaValueNode node,
        NumericRange indexRange, TimestampsToReturn timestampsToReturn, DateTime minTimestamp, DataValue dataValue)
        throws StatusException {
      DataItem dataItem = getDataItem(nodeId);
      if (dataItem == null) {
        throw new StatusException(StatusCodes.Bad_NodeIdInvalid);
      }
      dataItem.getDataValue(dataValue);

    }

    // If you wish to enable writing, also disable simulation in
    // MyBigNodeManager.simulate() and check the value of WriteMask returned
    // (above).
    // /*
    // * (non-Javadoc)
    // *
    // * @see
    // *
    // com.prosysopc.ua.server.IoManager#writeValue(com.prosysopc.ua.server
    // * .ServiceContext, org.opcfoundation.ua.builtintypes.NodeId,
    // * com.prosysopc.ua.nodes.UaVariable,
    // * org.opcfoundation.ua.utils.NumericRange,
    // * org.opcfoundation.ua.builtintypes.DataValue)
    // */
    // @Override
    // protected boolean writeValue(ServiceContext serviceContext,
    // NodeId nodeId, UaVariable node, NumericRange indexRange,
    // DataValue dataValue) throws StatusException {
    // DataItem dataItem = getDataItem(nodeId);
    // if (dataItem == null)
    // throw new StatusException(StatusCodes.Bad_NodeIdInvalid);
    // dataItem.setValue(dataValue.getValue().doubleValue(),
    // dataValue.getStatusCode());
    // return true;
    // }
  }

  /**
   * Helper for holding data. This simulates cases where the real-world-data would have been stored
   * elsewhere e.g. in a database.
   */
  public class MyReference extends UaReference {

    private final NodeId referenceTypeId;
    private final ExpandedNodeId sourceId;
    private final ExpandedNodeId targetId;

    /**
     * Creates a new simulated reference data.
     */
    public MyReference(ExpandedNodeId sourceId, ExpandedNodeId targetId, NodeId referenceType) {
      super();
      this.sourceId = sourceId;
      this.targetId = targetId;
      this.referenceTypeId = referenceType;
    }

    public MyReference(NodeId sourceId, NodeId targetId, NodeId referenceType) {
      this(getNamespaceTable().toExpandedNodeId(sourceId), getNamespaceTable().toExpandedNodeId(targetId),
          referenceType);
    }

    @Override
    public void delete() {
      throw new RuntimeException("StatusCodes.Bad_NotImplemented");
    }

    @Override
    public boolean getIsInverse(NodeId nodeId) {
      try {
        if (nodeId.equals(getNamespaceTable().toNodeId(sourceId))) {
          return false;
        }
        if (nodeId.equals(getNamespaceTable().toNodeId(targetId))) {
          return true;
        }
      } catch (ServiceResultException e) {
        throw new RuntimeException(e);
      }
      throw new RuntimeException("not a source nor target");
    }

    @Override
    public boolean getIsInverse(UaNode node) {
      return getIsInverse(node.getNodeId());
    }

    @Override
    public UaReferenceType getReferenceType() {
      try {
        return (UaReferenceType) getNodeManagerTable().getNode(getReferenceTypeId());
      } catch (StatusException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public NodeId getReferenceTypeId() {
      return referenceTypeId;
    }

    @Override
    public ExpandedNodeId getSourceId() {
      return sourceId;
    }

    @Override
    public UaNode getSourceNode() {
      return null;
    }

    @Override
    public ExpandedNodeId getTargetId() {
      return targetId;
    }

    @Override
    public UaNode getTargetNode() {
      return null;
    }

  }

  private static ExpandedNodeId dataItemType;

  private static final Logger logger = LoggerFactory.getLogger(MyBigNodeManager.class);

  private final ExpandedNodeId dataItemFolder;

  private final Map<String, DataItem> dataItems;

  private final Map<String, Collection<MonitoredDataItem>> monitoredItems =
      new ConcurrentHashMap<String, Collection<MonitoredDataItem>>();

  @SuppressWarnings("unused")
  private final MyBigIoManager myBigIoManager;

  private double simulationtime = 0;

  /**
   * Default constructor.
   *
   * @param server the UaServer, which owns the NodeManager
   * @param namespaceUri the namespace which this node manager handles
   * @param nofItems number of data items to create for the manager
   */
  public MyBigNodeManager(UaServer server, String namespaceUri, int nofItems) {
    super(server, namespaceUri);
    dataItemType = new ExpandedNodeId(null, getNamespaceIndex(), "DataItemType");
    dataItemFolder = new ExpandedNodeId(null, getNamespaceIndex(), "MyBigNodeManager");
    try {
      getNodeManagerTable().getNodeManagerRoot().getObjectsFolder()
          .addReference(getNamespaceTable().toNodeId(dataItemFolder), Identifiers.Organizes, false);
    } catch (ServiceResultException e) {
      throw new RuntimeException(e);
    }
    dataItems = new TreeMap<String, DataItem>();
    for (int i = 0; i < nofItems; i++) {
      addDataItem(String.format("DataItem_%04d", i));
    }

    myBigIoManager = new MyBigIoManager(this);
  }

  /**
   * Adds a new node - unless it already exists in the node manager.
   *
   * @param node the node to add. The NodeId of the node must have the same namepsaceIndex as the
   *        node manager.
   * @return newNode, if it was added or the node with the same NodeId if such was already in the
   *         address space.
   * @throws com.prosysopc.ua.StatusException if the NodeId is invalid (i.e. null)
   */
  @Override
  public UaNode addNode(UaNode node) throws StatusException {
    return null;
  }

  @Override
  public NodeId getVariableDataType(NodeId nodeId, UaValueNode variable) throws StatusException {
    DataItem item = getDataItem(nodeId);
    return item.getDataType();
  }

  @Override
  public boolean hasNode(NodeId nodeId) {
    return nodeId.getValue().equals("MyBigNodeManager") || getNamespaceTable().nodeIdEquals(nodeId, dataItemType)
        || (getDataItem(nodeId) != null);
  }

  private void addDataItem(String name) {
    dataItems.put(name, new DataItem(name));
  }

  /**
   * Finds the DataItem corresponding to the NodeId.
   *
   * @param nodeId ID of the node - the Value part corresponds to the name of the item
   * @return the DataItem object
   */
  private DataItem getDataItem(ExpandedNodeId nodeId) {
    String name = (String) nodeId.getValue();
    return dataItems.get(name);
  }

  /**
   * Finds the DataItem corresponding to the NodeId.
   *
   * @param nodeId ID of the node - the Value part corresponds to the name of the item
   * @return the DataItem object
   */
  private DataItem getDataItem(NodeId nodeId) {
    String name = (String) nodeId.getValue();
    return dataItems.get(name);
  }

  private String getNodeName(ExpandedNodeId nodeId) {
    String name = nodeId.getValue().toString();
    if (getNamespaceTable().nodeIdEquals(nodeId, dataItemType)) {
      name = "DataItemType";
    }
    if (getNamespaceTable().nodeIdEquals(nodeId, dataItemFolder)) {
      name = "MyBigNodeManager";
    } else {
      DataItem dataItem = getDataItem(nodeId);
      // Use the namespaceIndex of the NodeManager name space also for the
      // browse names
      if (dataItem != null) {
        name = dataItem.getName();
      }
    }
    return name;
  }

  /**
   * Send a data change notification for all monitored data items that are monitoring the dataItem.
   */
  private void notifyMonitoredDataItems(DataItem dataItem) {
    // Get the list of items watching dataItem
    Collection<MonitoredDataItem> c = monitoredItems.get(dataItem.getName());
    if (c != null) {
      for (MonitoredDataItem item : c) {
        DataValue dataValue = new DataValue();
        dataItem.getDataValue(dataValue);
        item.notifyDataChange(dataValue);
      }
    }
  }

  @Override
  protected void afterCreateMonitoredDataItem(ServiceContext serviceContext, Subscription subscription,
      MonitoredDataItem item) {
    // Add all items that monitor the same node to the same collection
    final Object dataItemName = item.getNodeId().getValue();
    Collection<MonitoredDataItem> c = monitoredItems.get(dataItemName);
    if (c == null) {
      c = new CopyOnWriteArrayList<MonitoredDataItem>();
      monitoredItems.put((String) dataItemName, c);
    }
    c.add(item);
    logger.debug("afterCreateMonitoredDataItem: nodeId={} c.size()={}", item.getNodeId(), c.size());
  }

  @Override
  protected void deleteMonitoredItem(ServiceContext serviceContext, Subscription subscription, MonitoredItem item)
      throws StatusException {
    // Find the collection in which the monitoredItem is
    // and remove the item from the collection
    Object dataItemName = item.getNodeId().getValue();
    Collection<MonitoredDataItem> c = monitoredItems.get(dataItemName);
    if (c != null) {
      logger.debug("deleteMonitoredItem: collection size={}", c.size());
      c.remove(item);
      if (c.isEmpty()) {
        monitoredItems.remove(dataItemName);
        logger.debug("deleteMonitoredItem: monitoredItems size={}", monitoredItems.size());
      }
    }
  }

  @Override
  protected QualifiedName getBrowseName(ExpandedNodeId nodeId, UaNode node) {
    final String name = getNodeName(nodeId);
    return new QualifiedName(getNamespaceIndex(), name);
  }

  @Override
  protected LocalizedText getDisplayName(ExpandedNodeId nodeId, UaNode targetNode, Locale locale) {
    final String name = getNodeName(nodeId);
    return new LocalizedText(name, LocalizedText.NO_LOCALE);
  }

  @Override
  protected NodeClass getNodeClass(NodeId nodeId, UaNode node) {
    if (getNamespaceTable().nodeIdEquals(nodeId, dataItemType)) {
      return NodeClass.VariableType;
    }
    if (getNamespaceTable().nodeIdEquals(nodeId, dataItemFolder)) {
      return NodeClass.Object;
    }
    // All data items are variables
    return NodeClass.Variable;
  }

  @Override
  protected UaReference[] getReferences(NodeId nodeId, UaNode node) {
    try {
      // Define reference to our type
      if (nodeId.equals(getNamespaceTable().toNodeId(dataItemType))) {
        return new UaReference[] {new MyReference(new ExpandedNodeId(Identifiers.BaseDataVariableType), dataItemType,
            Identifiers.HasSubtype)};
      }
      // Define reference from and to our Folder for the DataItems
      if (nodeId.equals(getNamespaceTable().toNodeId(dataItemFolder))) {
        UaReference[] folderItems = new UaReference[dataItems.size() + 2];
        // Inverse reference to the ObjectsFolder
        folderItems[0] =
            new MyReference(new ExpandedNodeId(Identifiers.ObjectsFolder), dataItemFolder, Identifiers.Organizes);
        // Type definition reference
        folderItems[1] = new MyReference(dataItemFolder,
            getTypeDefinition(getNamespaceTable().toExpandedNodeId(nodeId), node), Identifiers.HasTypeDefinition);
        int i = 2;
        // Reference to all items in the folder
        for (DataItem d : dataItems.values()) {
          folderItems[i] = new MyReference(dataItemFolder, new ExpandedNodeId(null, getNamespaceIndex(), d.getName()),
              Identifiers.HasComponent);
          i++;
        }
        return folderItems;
      }
    } catch (ServiceResultException e) {
      throw new RuntimeException(e);
    }

    // Define references from our DataItems
    DataItem dataItem = getDataItem(nodeId);
    if (dataItem == null) {
      return null;
    }
    final ExpandedNodeId dataItemId = new ExpandedNodeId(null, getNamespaceIndex(), dataItem.getName());
    return new UaReference[] {
        // Inverse reference to the folder
        new MyReference(dataItemFolder, dataItemId, Identifiers.HasComponent),
        // Type definition
        new MyReference(dataItemId, dataItemType, Identifiers.HasTypeDefinition)};
  }

  @Override
  protected ExpandedNodeId getTypeDefinition(ExpandedNodeId nodeId, UaNode node) {
    // ExpandedNodeId.equals cannot be trusted, since some IDs are defined
    // with NamespaceIndex while others use NamespaceUri
    if (getNamespaceTable().nodeIdEquals(nodeId, dataItemType)) {
      return null;
    }
    if (getNamespaceTable().nodeIdEquals(nodeId, dataItemFolder)) {
      return getNamespaceTable().toExpandedNodeId(Identifiers.FolderType);
    }
    return dataItemType;
  }

  void simulate() {
    simulationtime = simulationtime + (Math.PI / 180);
    double value = 100 * Math.sin(simulationtime);
    for (DataItem d : dataItems.values()) {
      d.setValue(value);
      notifyMonitoredDataItems(d);
    }
  }

}
