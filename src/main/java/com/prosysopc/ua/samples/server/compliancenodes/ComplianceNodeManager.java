/**
 * Prosys OPC UA Java SDK
 * Copyright (c) Prosys OPC Ltd.
 * <http://www.prosysopc.com>
 * All rights reserved.
 */
package com.prosysopc.ua.samples.server.compliancenodes;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

import com.prosysopc.ua.AccessLevels;
import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.ValueRanks;
import com.prosysopc.ua.nodes.UaDataType;
import com.prosysopc.ua.nodes.UaNode;
import com.prosysopc.ua.nodes.UaObject;
import com.prosysopc.ua.nodes.UaProperty;
import com.prosysopc.ua.nodes.UaVariable;
import com.prosysopc.ua.samples.server.ComplianceMethodListener;
import com.prosysopc.ua.server.MethodManagerUaNode;
import com.prosysopc.ua.server.NodeBuilderException;
import com.prosysopc.ua.server.NodeManagerUaNode;
import com.prosysopc.ua.server.UaInstantiationException;
import com.prosysopc.ua.server.UaServer;
import com.prosysopc.ua.server.instantiation.NodeBuilderConfiguration;
import com.prosysopc.ua.server.io.IoManagerListener;
import com.prosysopc.ua.server.nodes.PlainMethod;
import com.prosysopc.ua.server.nodes.UaObjectNode;
import com.prosysopc.ua.server.nodes.UaVariableNode;
import com.prosysopc.ua.stack.builtintypes.DataValue;
import com.prosysopc.ua.stack.builtintypes.DateTime;
import com.prosysopc.ua.stack.builtintypes.ExpandedNodeId;
import com.prosysopc.ua.stack.builtintypes.LocalizedText;
import com.prosysopc.ua.stack.builtintypes.NodeId;
import com.prosysopc.ua.stack.builtintypes.QualifiedName;
import com.prosysopc.ua.stack.builtintypes.StatusCode;
import com.prosysopc.ua.stack.builtintypes.UnsignedByte;
import com.prosysopc.ua.stack.builtintypes.UnsignedInteger;
import com.prosysopc.ua.stack.builtintypes.UnsignedLong;
import com.prosysopc.ua.stack.builtintypes.UnsignedShort;
import com.prosysopc.ua.stack.builtintypes.Variant;
import com.prosysopc.ua.stack.common.NamespaceTable;
import com.prosysopc.ua.stack.core.AccessLevelType;
import com.prosysopc.ua.stack.core.Argument;
import com.prosysopc.ua.stack.core.AttributeWriteMask;
import com.prosysopc.ua.stack.core.AxisInformation;
import com.prosysopc.ua.stack.core.AxisScaleEnumeration;
import com.prosysopc.ua.stack.core.EnumValueType;
import com.prosysopc.ua.stack.core.Identifiers;
import com.prosysopc.ua.stack.core.Range;
import com.prosysopc.ua.stack.core.StandardEngineeringUnits;
import com.prosysopc.ua.stack.utils.AttributesUtil;
import com.prosysopc.ua.stack.utils.MultiDimensionArrayUtils;
import com.prosysopc.ua.types.opcua.AnalogItemType;
import com.prosysopc.ua.types.opcua.CubeItemType;
import com.prosysopc.ua.types.opcua.DataItemType;
import com.prosysopc.ua.types.opcua.FolderType;
import com.prosysopc.ua.types.opcua.Ids;
import com.prosysopc.ua.types.opcua.ImageItemType;
import com.prosysopc.ua.types.opcua.MultiStateDiscreteType;
import com.prosysopc.ua.types.opcua.MultiStateValueDiscreteType;
import com.prosysopc.ua.types.opcua.NDimensionArrayItemType;
import com.prosysopc.ua.types.opcua.TwoStateDiscreteType;
import com.prosysopc.ua.types.opcua.XYArrayItemType;
import com.prosysopc.ua.types.opcua.YArrayItemType;
import com.prosysopc.ua.types.opcua.server.BaseDataVariableTypeNode;

public class ComplianceNodeManager extends NodeManagerUaNode {

  private static IoManagerListener listener = new ComplianceIoManagerListener();

  private FolderType accessLevelVariableFolder;
  private FolderType analogItemArrayFolder;
  private FolderType analogItemFolder;
  private FolderType dataItemFolder;
  private FolderType deepFolder;
  private FolderType multiStateFolder;
  private FolderType multiStateValueFolder;
  private UaObject objectsFolder;
  private FolderType staticArrayVariableFolder;
  private FolderType staticMultidimensionalArrayVariableFolder;
  private FolderType staticDataFolder;
  private FolderType staticVariableFolder;
  private FolderType otherValueRanksFolder;
  private FolderType staticValueRankAnyVariableFolder;
  private FolderType staticValueRankOneOrMoreDimensionsVariableFolder;
  private FolderType staticValueRankScalarOrOneDimensionVariableFolder;
  private FolderType static3DimensionsVariableFolder;
  private FolderType static4DimensionsVariableFolder;
  private FolderType static5DimensionsVariableFolder;
  private FolderType twoStateFolder;
  private FolderType methodFolder;
  private FolderType arrayItemFolder;

  public ComplianceNodeManager(UaServer server, String namespaceUri) {
    super(server, namespaceUri);

    try {
      initialize();
    } catch (Exception e) {
      throw new RuntimeException("Failed to initialize Compliance nodes", e);
    }
  }

  private void addDeepObject(UaNode parent, int depth, int maxDepth) {
    if (depth <= maxDepth) {
      final String name = String.format("DeepObject%02d", depth);
      UaObjectNode newObject = new UaObjectNode(this, new NodeId(getNamespaceIndex(), name), name, getDefaultLocale());
      try {
        addNodeAndReference(parent, newObject, Identifiers.Organizes);
      } catch (StatusException e) {
      }
      addDeepObject(newObject, depth + 1, maxDepth);
    }
  }

  private void createAccessLevelVariable(String name, AccessLevelType accessLevel, AccessLevelType userAccessLevel)
      throws StatusException {
    final NodeId nodeId = new NodeId(getNamespaceIndex(), name);

    // just some datatype
    UaDataType type = getServer().getNodeManagerRoot().getDataType(Identifiers.Int32);

    BaseDataVariableTypeNode node = createInstance(BaseDataVariableTypeNode.class, name, nodeId);
    node.setDataType(type);
    node.setValueRank(ValueRanks.Scalar);
    node.setValue(new DataValue(new Variant(0), StatusCode.GOOD, new DateTime(), new DateTime()));
    node.setAccessLevel(accessLevel);
    node.setUserAccessLevel(userAccessLevel);
    accessLevelVariableFolder.addReference(node, Identifiers.HasComponent, false);
  }

  private void createAnalogItem(AnalogData a) throws StatusException, UaInstantiationException, NodeBuilderException {
    NodeBuilderConfiguration conf = new NodeBuilderConfiguration();
    conf.addOptional(Ids.BaseAnalogType_EngineeringUnits);
    conf.addOptional(Ids.DataItemType_Definition);
    if (a.getInstrumentRange() != null) {
      conf.addOptional(Ids.BaseAnalogType_InstrumentRange);
    }

    // Add ValuePrecision for Double, Float and DateTime
    if (a.getDataTypeId().equals(Identifiers.Double) || a.getDataTypeId().equals(Identifiers.Float)
        || a.getDataTypeId().equals(Identifiers.DateTime)) {
      conf.addOptional(n2e(Identifiers.DataItemType_ValuePrecision));
    }

    AnalogItemType item =
        createNodeBuilder(AnalogItemType.class, conf).setName(a.getDataTypeName() + "AnalogItem").build();
    item.setDefinition("Test definition of type " + a.getDataTypeName());
    item.setValueRank(ValueRanks.Scalar);
    item.setEngineeringUnits(StandardEngineeringUnits.METRE);
    item.setEURange(a.getEURange());
    if (a.getInstrumentRange() != null) {
      item.setInstrumentRange(a.getInstrumentRange());
    }
    item.setDataTypeId(a.getDataTypeId());
    item.setValue(new DataValue(new Variant(a.getInitialValue()), StatusCode.GOOD, new DateTime(), new DateTime()));

    // Handle ValuePrecision for Double, Float and DateTime
    if (a.getDataTypeId().equals(Identifiers.Double)) {
      item.setValuePrecision(15.0);
    } else if (a.getDataTypeId().equals(Identifiers.Float)) {
      item.setValuePrecision(7.0);
    } else if (a.getDataTypeId().equals(Identifiers.DateTime)) {
      item.setValuePrecision(1000000.0);
    }

    initNode(item, a, analogItemFolder);
  }

  private void createAnalogItemArray(AnalogData a)
      throws StatusException, UaInstantiationException, NodeBuilderException {
    NodeBuilderConfiguration conf = new NodeBuilderConfiguration();
    conf.addOptional(Ids.BaseAnalogType_EngineeringUnits);
    conf.addOptional(Ids.DataItemType_Definition);
    if (a.getInstrumentRange() != null) {
      conf.addOptional(Ids.BaseAnalogType_InstrumentRange);
    }

    // Add ValuePrecision for Double, Float and DateTime
    if (a.getDataTypeId().equals(Identifiers.Double) || a.getDataTypeId().equals(Identifiers.Float)
        || a.getDataTypeId().equals(Identifiers.DateTime)) {
      conf.addOptional(n2e(Identifiers.DataItemType_ValuePrecision));
    }

    AnalogItemType item =
        createNodeBuilder(AnalogItemType.class, conf).setName(a.getDataTypeName() + "AnalogItemArray").build();
    item.setDefinition("Test definition of type " + a.getDataTypeName() + " as array");
    item.setValueRank(ValueRanks.OneDimension);
    item.setArrayDimensions(new UnsignedInteger[] {UnsignedInteger.ZERO});
    item.setEngineeringUnits(StandardEngineeringUnits.METRE);
    item.setEURange(a.getEURange());
    if (a.getInstrumentRange() != null) {
      item.setInstrumentRange(a.getInstrumentRange());
    }
    item.setDataTypeId(a.getDataTypeId());
    item.setValue(new DataValue(new Variant(a.getInitialValue()), StatusCode.GOOD, new DateTime(), new DateTime()));

    // Handle ValuePrecision for Double, Float and DateTime
    if (a.getDataTypeId().equals(Identifiers.Double)) {
      item.setValuePrecision(15.0);
    } else if (a.getDataTypeId().equals(Identifiers.Float)) {
      item.setValuePrecision(7.0);
    } else if (a.getDataTypeId().equals(Identifiers.DateTime)) {
      item.setValuePrecision(1000000.0);
    }

    initNode(item, a, analogItemArrayFolder);
  }

  private void createArrayItems() throws NodeBuilderException, StatusException {

    NodeBuilderConfiguration conf = new NodeBuilderConfiguration();
    conf.addOptional(Ids.DataItemType_Definition);
    conf.addOptional(Ids.ArrayItemType_InstrumentRange);

    // ArrayItems have to be handled one by one

    // CubeItem
    CubeItemType cube = createNodeBuilder(CubeItemType.class, conf).setName("CubeItem").build();
    cube.setValueRank(3);
    cube.setArrayDimensions(new UnsignedInteger[] {UnsignedInteger.ZERO, UnsignedInteger.ZERO, UnsignedInteger.ZERO});
    cube.setValue(ArrayItem.CUBE.getInitialValue());
    cube.setEngineeringUnits(StandardEngineeringUnits.METRE);
    cube.setEURange(new Range(-100.0, 100.0));
    cube.setInstrumentRange(new Range(-100.0, 100.0));
    cube.setAxisScaleType(AxisScaleEnumeration.Linear);
    cube.setTitle(new LocalizedText("Cube", Locale.ENGLISH));
    cube.setDefinition("Test Definition of CubeItem");
    cube.setXAxisDefinition(new AxisInformation(StandardEngineeringUnits.METRE, new Range(-100.0, 100.0),
        new LocalizedText("X-Axis", Locale.ENGLISH), AxisScaleEnumeration.Linear, null));
    cube.setYAxisDefinition(new AxisInformation(StandardEngineeringUnits.METRE, new Range(-100.0, 100.0),
        new LocalizedText("Y-Axis", Locale.ENGLISH), AxisScaleEnumeration.Linear, null));
    cube.setZAxisDefinition(new AxisInformation(StandardEngineeringUnits.METRE, new Range(-100.0, 100.0),
        new LocalizedText("Z-Axis", Locale.ENGLISH), AxisScaleEnumeration.Linear, null));

    initNode(cube, ArrayItem.CUBE, arrayItemFolder);

    // ImageItem
    ImageItemType image = createNodeBuilder(ImageItemType.class, conf).setName("ImageItem").build();
    image.setValueRank(2);
    image.setArrayDimensions(new UnsignedInteger[] {UnsignedInteger.ZERO, UnsignedInteger.ZERO});
    image.setValue(ArrayItem.IMAGE.getInitialValue());
    image.setEngineeringUnits(StandardEngineeringUnits.METRE);
    image.setEURange(new Range(-100.0, 100.0));
    image.setInstrumentRange(new Range(-100.0, 100.0));
    image.setAxisScaleType(AxisScaleEnumeration.Linear);
    image.setTitle(new LocalizedText("Image", Locale.ENGLISH));
    image.setDefinition("Test Definition of ImageItem");
    image.setXAxisDefinition(new AxisInformation(StandardEngineeringUnits.METRE, new Range(-100.0, 100.0),
        new LocalizedText("X-Axis", Locale.ENGLISH), AxisScaleEnumeration.Linear, null));
    image.setYAxisDefinition(new AxisInformation(StandardEngineeringUnits.METRE, new Range(-100.0, 100.0),
        new LocalizedText("Y-Axis", Locale.ENGLISH), AxisScaleEnumeration.Linear, null));

    initNode(image, ArrayItem.IMAGE, arrayItemFolder);

    // NDimensionItem
    NDimensionArrayItemType nDim =
        createNodeBuilder(NDimensionArrayItemType.class, conf).setName("NDimensionItem").build();
    nDim.setValueRank(4);
    nDim.setArrayDimensions(
        new UnsignedInteger[] {UnsignedInteger.ZERO, UnsignedInteger.ZERO, UnsignedInteger.ZERO, UnsignedInteger.ZERO});
    nDim.setValue(ArrayItem.NDIMENSION.getInitialValue());
    nDim.setEngineeringUnits(StandardEngineeringUnits.METRE);
    nDim.setEURange(new Range(-100.0, 100.0));
    nDim.setInstrumentRange(new Range(-100.0, 100.0));
    nDim.setAxisScaleType(AxisScaleEnumeration.Linear);
    nDim.setTitle(new LocalizedText("NDimension", Locale.ENGLISH));
    nDim.setDefinition("Test Definition of NDimensionItem");
    nDim.setAxisDefinition(new AxisInformation[] {
        new AxisInformation(StandardEngineeringUnits.METRE, new Range(-100.0, 100.0),
            new LocalizedText("Axis 1", Locale.ENGLISH), AxisScaleEnumeration.Linear, null),
        new AxisInformation(StandardEngineeringUnits.METRE, new Range(-100.0, 100.0),
            new LocalizedText("Axis 2", Locale.ENGLISH), AxisScaleEnumeration.Linear, null),
        new AxisInformation(StandardEngineeringUnits.METRE, new Range(-100.0, 100.0),
            new LocalizedText("Axis 3", Locale.ENGLISH), AxisScaleEnumeration.Linear, null),
        new AxisInformation(StandardEngineeringUnits.METRE, new Range(-100.0, 100.0),
            new LocalizedText("Axis 4", Locale.ENGLISH), AxisScaleEnumeration.Linear, null)});

    initNode(nDim, ArrayItem.NDIMENSION, arrayItemFolder);

    // XYItem

    XYArrayItemType xy = createNodeBuilder(XYArrayItemType.class, conf).setName("XYItem").build();
    xy.setValueRank(2);
    xy.setArrayDimensions(new UnsignedInteger[] {UnsignedInteger.ZERO, UnsignedInteger.ZERO});
    xy.setValue(ArrayItem.XY.getInitialValue());
    xy.setEngineeringUnits(StandardEngineeringUnits.METRE);
    xy.setEURange(new Range(-100.0, 100.0));
    xy.setInstrumentRange(new Range(-100.0, 100.0));
    xy.setAxisScaleType(AxisScaleEnumeration.Linear);
    xy.setTitle(new LocalizedText("XY", Locale.ENGLISH));
    xy.setDefinition("Test Definition of XYArrayItem");
    xy.setXAxisDefinition(new AxisInformation(StandardEngineeringUnits.METRE, new Range(-100.0, 100.0),
        new LocalizedText("X-Axis", Locale.ENGLISH), AxisScaleEnumeration.Linear, null));

    initNode(xy, ArrayItem.XY, arrayItemFolder);

    // YItem
    YArrayItemType y = createNodeBuilder(YArrayItemType.class, conf).setName("YItem").build();
    y.setValueRank(1);
    y.setArrayDimensions(new UnsignedInteger[] {UnsignedInteger.ZERO});
    y.setValue(ArrayItem.Y.getInitialValue());
    y.setEngineeringUnits(StandardEngineeringUnits.METRE);
    y.setEURange(new Range(-100.0, 100.0));
    y.setInstrumentRange(new Range(-100.0, 100.0));
    y.setAxisScaleType(AxisScaleEnumeration.Linear);
    y.setTitle(new LocalizedText("Y", Locale.ENGLISH));
    y.setDefinition("Test Definition of YArrayItem");
    y.setXAxisDefinition(new AxisInformation(StandardEngineeringUnits.METRE, new Range(-100.0, 100.0),
        new LocalizedText("X-Axis", Locale.ENGLISH), AxisScaleEnumeration.Linear, null));

    initNode(y, ArrayItem.Y, arrayItemFolder);
  }

  private void createDataItem(StaticData s) throws StatusException, UaInstantiationException, NodeBuilderException {
    NodeBuilderConfiguration conf = new NodeBuilderConfiguration();
    conf.addOptional(n2e(Identifiers.DataItemType_Definition));

    // Add ValuePrecision for Double, Float and DateTime
    if (s.getDataTypeId().equals(Identifiers.Double) || s.getDataTypeId().equals(Identifiers.Float)
        || s.getDataTypeId().equals(Identifiers.DateTime)) {
      conf.addOptional(n2e(Identifiers.DataItemType_ValuePrecision));
    }

    DataItemType item = createNodeBuilder(DataItemType.class, conf).setName(s.getDataTypeName() + "DataItem").build();
    item.setDefinition("Test definition of type " + s.getDataTypeName());
    item.setValueRank(ValueRanks.Scalar);
    item.setValue(createValue(s.getInitialValue()));

    // Handle ValuePrecision for Double, Float and DateTime
    if (s.getDataTypeId().equals(Identifiers.Double)) {
      item.setValuePrecision(15.0);
    } else if (s.getDataTypeId().equals(Identifiers.Float)) {
      item.setValuePrecision(7.0);
    } else if (s.getDataTypeId().equals(Identifiers.DateTime)) {
      item.setValuePrecision(1000000.0);
    }

    initNode(item, s, dataItemFolder);
  }

  private FolderType createFolder(String name, UaObject parent) throws UaInstantiationException, NodeBuilderException {
    FolderType node = createNodeBuilder(FolderType.class).setName(name).build();
    parent.addReference(node, Identifiers.Organizes, false);
    return node;
  }

  private void createMethod(ComplianceMethod m) throws StatusException {
    // Based on createMethodNode of MyNodeManager
    int ns = this.getNamespaceIndex();
    final NodeId complianceMethodId = new NodeId(ns, m.getName());
    PlainMethod complianceMethod = new PlainMethod(this, complianceMethodId, m.getName(), Locale.ENGLISH);
    DateTime now = DateTime.currentTime();

    if (m.hasInput()) {
      Argument[] inputs = new Argument[1];
      inputs[0] = new Argument();
      inputs[0].setName("Operation");
      inputs[0].setDataType(Identifiers.Double);
      inputs[0].setValueRank(ValueRanks.Scalar);
      inputs[0].setArrayDimensions(null);
      inputs[0].setDescription(new LocalizedText("X", Locale.ENGLISH));
      complianceMethod.setInputArguments(inputs);

      // Set TimeStamps, as required by the CTT
      // Copied from MyObjectsNodeManager of Simulation Server
      final QualifiedName iarg = new QualifiedName("InputArguments");
      final UaProperty iprop = complianceMethod.getProperty(iarg);
      final DataValue dv1 = iprop.getValue().clone();
      dv1.setSourceTimestamp(now);
      dv1.setServerTimestamp(now);
      iprop.setValue(dv1);
    }

    if (m.hasOutput()) {
      Argument[] outputs = new Argument[1];
      outputs[0] = new Argument();
      outputs[0].setName("Result");
      outputs[0].setDataType(Identifiers.Double);
      outputs[0].setValueRank(ValueRanks.Scalar);
      outputs[0].setArrayDimensions(null);
      if (m.hasInput()) {
        outputs[0].setDescription(new LocalizedText("-2X - 1", Locale.ENGLISH));
      } else {
        outputs[0].setDescription(new LocalizedText("Always returns 1.2345", Locale.ENGLISH));
      }
      complianceMethod.setOutputArguments(outputs);

      // Set TimeStamps, as required by the CTT
      // Copied from MyObjectsNodeManager of Simulation Server
      final QualifiedName oarg = new QualifiedName("OutputArguments");
      final UaProperty oprop = complianceMethod.getProperty(oarg);
      final DataValue dv2 = oprop.getValue().clone();
      dv2.setSourceTimestamp(now);
      dv2.setServerTimestamp(now);
      oprop.setValue(dv2);
    }
    this.addNodeAndReference(methodFolder, complianceMethod, Identifiers.HasComponent);
    ComplianceMethodListener complianceMethodListener =
        new ComplianceMethodListener(complianceMethod, m.hasInput(), m.hasOutput());
    MethodManagerUaNode methodManager = (MethodManagerUaNode) this.getMethodManager();
    methodManager.addCallListener(complianceMethodListener);
  }

  private void createMultiStateDiscreteItem(MultiState m)
      throws StatusException, UaInstantiationException, NodeBuilderException {

    NodeBuilderConfiguration conf = new NodeBuilderConfiguration();
    conf.addOptional(n2e(Identifiers.DataItemType_Definition));
    MultiStateDiscreteType item = createNodeBuilder(MultiStateDiscreteType.class, conf).setName(m.getName()).build();
    String[] statesString = m.getStates();
    LocalizedText[] states = new LocalizedText[statesString.length];
    for (int i = 0; i < states.length; i++) {
      states[i] = new LocalizedText(statesString[i], getDefaultLocale());
    }
    item.setValue(createValue(m.getInitialValue()));
    item.setEnumStrings(states);
    item.setDefinition("Test definition of " + m.getBaseName());
    item.setDataTypeId(m.getDataTypeId());
    item.setValueRank(ValueRanks.Scalar);

    initNode(item, m, multiStateFolder);
  }

  private void createMultiStateValueDiscreteItem(MultiStateValue m) throws NodeBuilderException, StatusException {

    NodeBuilderConfiguration conf = new NodeBuilderConfiguration();
    conf.addOptional(n2e(Identifiers.DataItemType_Definition));
    MultiStateValueDiscreteType item =
        createNodeBuilder(MultiStateValueDiscreteType.class, conf).setName(m.getName()).build();
    item.setEnumValues(m.getStates());
    item.setDataTypeId(m.getDataTypeId());
    item.setDefinition("Test definition of " + m.getBaseName());
    item.setValueRank(ValueRanks.Scalar);


    // Handle setting initial value with correct DataType
    // TODO try DataTypeConverter
    if (m.getDataTypeId().equals(Identifiers.Int64)) {
      item.setValue(createValue(m.getInitialValue()));
    } else if (m.getDataTypeId().equals(Identifiers.Int32)) {
      item.setValue(createValue(m.getInitialValue().intValue()));
    } else if (m.getDataTypeId().equals(Identifiers.Int16)) {
      item.setValue(createValue(m.getInitialValue().shortValue()));
    } else if (m.getDataTypeId().equals(Identifiers.SByte)) {
      item.setValue(createValue(m.getInitialValue().byteValue()));
    } else if (m.getDataTypeId().equals(Identifiers.UInt64)) {
      item.setValue(createValue(UnsignedLong.valueOf(m.getInitialValue())));
    } else if (m.getDataTypeId().equals(Identifiers.UInt32)) {
      item.setValue(createValue(UnsignedInteger.valueOf(m.getInitialValue())));
    } else if (m.getDataTypeId().equals(Identifiers.UInt16)) {
      item.setValue(createValue(UnsignedShort.valueOf(m.getInitialValue())));
    } else if (m.getDataTypeId().equals(Identifiers.Byte)) {
      item.setValue(createValue(UnsignedByte.valueOf(m.getInitialValue())));
    } else {
      item.setValue(createValue(m.getInitialValue()));
    }

    // Set initial value of ValueAsText
    // TODO decide format for ValueAsText
    for (EnumValueType e : m.getStates()) {
      if (e.getValue() == m.getInitialValue()) {
        item.setValueAsText(e.getDisplayName());
      }
    }

    initNode(item, m, multiStateValueFolder);
  }

  private NodeId createNodeId(String name) {
    return new NodeId(getNamespaceIndex(), name);

  }

  private String createNodeName(CommonComplianceInfo i, String postfix) {
    return i.getBaseName() + postfix;
  }

  private void createStaticArrayVariable(CommonComplianceInfo s) throws StatusException {
    String nodeName = createNodeName(s, "Array");
    BaseDataVariableTypeNode node = createInstance(BaseDataVariableTypeNode.class, nodeName, createNodeId(nodeName));
    node.setValueRank(ValueRanks.OneDimension);
    node.setArrayDimensions(new UnsignedInteger[] {UnsignedInteger.ZERO});
    node.setValue(new DataValue(new Variant(s.getInitialValue()), StatusCode.GOOD, new DateTime(), new DateTime()));

    initNode(node, s, staticArrayVariableFolder);
  }

  private void createStaticCustomArrayVariable(CommonComplianceInfo s, FolderType folder, int valueRank,
      int dimensionLength) throws StatusException {
    // Supports only ValueRanks greater than or equal to 1
    if (valueRank < 1 || dimensionLength < 1) {
      return;
    }
    String nodeName = createNodeName(s, "_" + ValueRanks.toString(valueRank).replace(" ", ""));
    BaseDataVariableTypeNode node = createInstance(BaseDataVariableTypeNode.class, nodeName, createNodeId(nodeName));
    node.setValueRank(valueRank);
    Object[] array;
    if (s.getInitialValue() != null) {
      // Create ArrayDimensions for the Attribute and dimension lengths for the array
      List<UnsignedInteger> arrayDimensions = new ArrayList<UnsignedInteger>();
      int[] dimensionLengths = new int[valueRank];
      int totalLength = 1;
      for (int i = 0; i < valueRank; i++) {
        arrayDimensions.add(UnsignedInteger.ZERO);
        dimensionLengths[i] = dimensionLength;
        totalLength = totalLength * dimensionLength;
      }
      node.setArrayDimensions(arrayDimensions.toArray(new UnsignedInteger[] {}));

      // Create a multidimensional array
      Object[] multiDimArray =
          MultiDimensionArrayUtils.createMultiDimArray(s.getInitialValue().getClass(), dimensionLengths);

      // Mux the multidimensional array to set values
      Object[] muxArray =
          (Object[]) MultiDimensionArrayUtils.muxArray(multiDimArray, dimensionLengths, s.getInitialValue().getClass());
      for (int i = 0; i < muxArray.length; i++) {
        // Use the same value for all array entries
        muxArray[i] = s.getInitialValue();
      }
      // Demux the muxed array
      array = (Object[]) MultiDimensionArrayUtils.demuxArray(muxArray, dimensionLengths);
    } else {
      // No initial value, use null as Value of Variable
      array = null;
    }
    node.setValue(new DataValue(new Variant(array), StatusCode.GOOD, new DateTime(), new DateTime()));
    initNode(node, s, folder);
  }

  private void createStaticMultidimensionalArrayVariable(CommonComplianceInfo s) throws StatusException {
    String nodeName = createNodeName(s, "MultidimensionalArray");
    BaseDataVariableTypeNode node = createInstance(BaseDataVariableTypeNode.class, nodeName, createNodeId(nodeName));
    node.setValueRank(2);
    node.setArrayDimensions(new UnsignedInteger[] {UnsignedInteger.ZERO, UnsignedInteger.ZERO});
    node.setValue(new DataValue(new Variant(s.getInitialValue()), StatusCode.GOOD, new DateTime(), new DateTime()));
    initNode(node, s, staticMultidimensionalArrayVariableFolder);
  }

  private UaVariableNode createStaticOneOrMoreDimensionsVariable(CommonComplianceInfo s) throws StatusException {
    String nodeName = createNodeName(s, "_OneOrMoreDimensions");
    BaseDataVariableTypeNode node = createInstance(BaseDataVariableTypeNode.class, nodeName, createNodeId(nodeName));
    node.setValueRank(ValueRanks.OneOrMoreDimensions);
    node.setArrayDimensions(new UnsignedInteger[] {UnsignedInteger.ZERO});
    node.setValue(new DataValue(new Variant(s.getInitialValue()), StatusCode.GOOD, new DateTime(), new DateTime()));
    initNode(node, s, staticValueRankOneOrMoreDimensionsVariableFolder);
    return node;
  }

  private UaVariableNode createStaticValueRankAnyVariable(CommonComplianceInfo s) throws StatusException {
    String nodeName = createNodeName(s, "_Any");
    BaseDataVariableTypeNode node = createInstance(BaseDataVariableTypeNode.class, nodeName, createNodeId(nodeName));
    node.setValueRank(ValueRanks.Any);
    node.setValue(new DataValue(new Variant(s.getInitialValue()), StatusCode.GOOD, new DateTime(), new DateTime()));
    initNode(node, s, staticValueRankAnyVariableFolder);
    return node;
  }

  private UaVariableNode createStaticValueRankScalarOrOneDimensionVariable(CommonComplianceInfo s)
      throws StatusException {
    String nodeName = createNodeName(s, "_ScalarOrOneDimension");
    BaseDataVariableTypeNode node = createInstance(BaseDataVariableTypeNode.class, nodeName, createNodeId(nodeName));
    node.setValueRank(ValueRanks.ScalarOrOneDimension);
    node.setValue(new DataValue(new Variant(s.getInitialValue()), StatusCode.GOOD, new DateTime(), new DateTime()));
    initNode(node, s, staticValueRankScalarOrOneDimensionVariableFolder);
    return node;
  }

  private UaVariableNode createStaticVariable(CommonComplianceInfo s) throws StatusException {
    String nodeName = createNodeName(s, "");
    BaseDataVariableTypeNode node = createInstance(BaseDataVariableTypeNode.class, nodeName, createNodeId(nodeName));
    node.setValueRank(ValueRanks.Scalar);
    node.setValue(new DataValue(new Variant(s.getInitialValue()), StatusCode.GOOD, new DateTime(), new DateTime()));
    initNode(node, s, staticVariableFolder);
    return node;
  }

  private void createTwoStateDiscreteItem(TwoState t)
      throws StatusException, UaInstantiationException, NodeBuilderException {
    NodeBuilderConfiguration conf = new NodeBuilderConfiguration();
    conf.addOptional(n2e(Identifiers.DataItemType_Definition));
    TwoStateDiscreteType item = createNodeBuilder(TwoStateDiscreteType.class, conf).setName(t.getName()).build();
    item.setTrueState(new LocalizedText(t.getTrueState(), getDefaultLocale()));
    item.setFalseState(new LocalizedText(t.getFalseState(), getDefaultLocale()));
    item.setValue(createValue(t.getInitialValue()));
    item.setDefinition("Test definition of " + t.getBaseName());
    item.setValueRank(ValueRanks.Scalar);

    initNode(item, t, twoStateFolder);
  }

  private DataValue createValue(Object value) {
    return new DataValue(new Variant(value), StatusCode.GOOD, DateTime.currentTime(), UnsignedShort.ZERO,
        DateTime.currentTime(), UnsignedShort.ZERO);
  }

  private void initFolders() throws StatusException, UaInstantiationException, NodeBuilderException {
    objectsFolder = getServer().getNodeManagerRoot().getObjectsFolder();

    staticDataFolder = createFolder("ComplianceNodes", objectsFolder);
    staticDataFolder.setDescription(new LocalizedText("A Folder that contains Nodes for compliance testing."));
    staticVariableFolder = createFolder("StaticVariables", staticDataFolder);
    staticVariableFolder.setDescription(
        new LocalizedText("A Folder that contains Variable Nodes with ValueRank Scalar and different DataTypes."));
    staticArrayVariableFolder = createFolder("StaticArrayVariables", staticDataFolder);
    staticArrayVariableFolder.setDescription(
        new LocalizedText("A Folder that contains Variable Nodes with ValueRank 1 and different DataTypes."));
    staticMultidimensionalArrayVariableFolder = createFolder("StaticMultidimensionalArrayVariables", staticDataFolder);
    staticMultidimensionalArrayVariableFolder.setDescription(
        new LocalizedText("A Folder that contains Variable Nodes with ValueRank 2 and different DataTypes."));
    otherValueRanksFolder = createFolder("OtherValueRanks", staticDataFolder);
    otherValueRanksFolder.setDescription(
        new LocalizedText("A Folder that contains Variable Nodes with different ValueRanks and DataTypes."));
    staticValueRankAnyVariableFolder = createFolder("Any", otherValueRanksFolder);
    staticValueRankAnyVariableFolder.setDescription(
        new LocalizedText("A Folder that contains Variable Nodes with ValueRank Any and different DataTypes."));
    staticValueRankOneOrMoreDimensionsVariableFolder = createFolder("OneOrMoreDimensions", otherValueRanksFolder);
    staticValueRankOneOrMoreDimensionsVariableFolder.setDescription(new LocalizedText(
        "A Folder that contains Variable Nodes with ValueRank OneOrMoreDimensions and different DataTypes."));
    staticValueRankScalarOrOneDimensionVariableFolder = createFolder("ScalarOrOneDimension", otherValueRanksFolder);
    staticValueRankScalarOrOneDimensionVariableFolder.setDescription(new LocalizedText(
        "A Folder that contains Variable Nodes with ValueRank ScalarOrOneDimension and different DataTypes."));
    static3DimensionsVariableFolder = createFolder("3Dimensions", otherValueRanksFolder);
    static3DimensionsVariableFolder.setDescription(
        new LocalizedText("A Folder that contains Variable Nodes with ValueRank 3 and different DataTypes."));
    static4DimensionsVariableFolder = createFolder("4Dimensions", otherValueRanksFolder);
    static4DimensionsVariableFolder.setDescription(
        new LocalizedText("A Folder that contains Variable Nodes with ValueRank 4 and different DataTypes."));
    static5DimensionsVariableFolder = createFolder("5Dimensions", otherValueRanksFolder);
    static5DimensionsVariableFolder.setDescription(
        new LocalizedText("A Folder that contains Variable Nodes with ValueRank 5 and different DataTypes."));
    dataItemFolder = createFolder("DataItems", staticDataFolder);
    dataItemFolder.setDescription(new LocalizedText(
        "A Folder that contains Variable Nodes of DataItemType with ValueRank Scalar and different DataTypes."));
    analogItemFolder = createFolder("AnalogItems", staticDataFolder);
    analogItemFolder.setDescription(new LocalizedText(
        "A Folder that contains Variable Nodes of AnalogItemType with ValueRank Scalar and different DataTypes."));
    analogItemArrayFolder = createFolder("AnalogItemArrays", staticDataFolder);
    analogItemArrayFolder.setDescription(new LocalizedText(
        "A Folder that contains Variable Nodes of AnalogItemType with ValueRank 1 and different DataTypes."));
    accessLevelVariableFolder = createFolder("AccessLevels", staticDataFolder);
    accessLevelVariableFolder
        .setDescription(new LocalizedText("A Folder that contains Variable Nodes with different AccessLevels."));
    twoStateFolder = createFolder("TwoStateItems", staticDataFolder);
    twoStateFolder.setDescription(new LocalizedText("A Folder that contains Variable Nodes of TwoStateDiscreteType."));
    multiStateFolder = createFolder("MultiStateItems", staticDataFolder);
    multiStateFolder
        .setDescription(new LocalizedText("A Folder that contains Variable Nodes of MultiStateDiscreteType."));
    multiStateValueFolder = createFolder("MultiStateValueItems", staticDataFolder);
    multiStateValueFolder.setDescription(new LocalizedText(
        "A Folder that contains Variable Nodes of MultiStateValueDiscreteType with different DataTypes."));
    methodFolder = createFolder("Methods", staticDataFolder);
    methodFolder.setDescription(new LocalizedText(
        "A Folder that contains Method Nodes with different combinations of InputArguments and OutputArguments."));
    arrayItemFolder = createFolder("ArrayItems", staticDataFolder);
    arrayItemFolder
        .setDescription(new LocalizedText("A Folder of Variable Nodes of different SubTypes of ArrayItemType."));
  }

  private void initialize() throws StatusException, UaInstantiationException, NodeBuilderException {
    // make folders
    initFolders();

    // add IoManagerListener
    this.getIoManager().addListeners(listener);

    // Static test variables
    for (StaticData s : StaticData.STATIC_DATAS) {
      createStaticVariable(s);
      createStaticValueRankAnyVariable(s);
      createStaticValueRankScalarOrOneDimensionVariable(s);
      createStaticCustomArrayVariable(s, static3DimensionsVariableFolder, 3, 3);
      createStaticCustomArrayVariable(s, static4DimensionsVariableFolder, 4, 2);
      createStaticCustomArrayVariable(s, static5DimensionsVariableFolder, 5, 2);
    }

    // Static test array variables
    for (StaticData s : StaticData.STATIC_DATA_ARRAYS) {
      createStaticArrayVariable(s);
      createStaticOneOrMoreDimensionsVariable(s);
    }

    // Static test multidimensional array variables
    for (StaticData d : StaticData.STATIC_DATA_MULTIDIMENSIONAL_ARRAYS) {
      createStaticMultidimensionalArrayVariable(d);
    }

    // DataItem test variables
    for (StaticData d : StaticData.DATA_ITEMS) {
      createDataItem(d);
    }

    // AnalogItem test variables
    for (AnalogData a : AnalogData.ANALOG_ITEMS) {
      createAnalogItem(a);
    }

    // AnalogItemArray test variables
    for (AnalogData a : AnalogData.ANALOG_ITEM_ARRAYS) {
      createAnalogItemArray(a);
    }

    // Methods
    for (ComplianceMethod m : ComplianceMethod.COMPLIANCE_METHODS) {
      createMethod(m);
    }

    // Folder for deep object chain
    deepFolder = createNodeBuilder(FolderType.class).setName("DeepFolder").build();
    deepFolder.setDescription(
        new LocalizedText("A Folder that contains a Node for testing long chains of forward and inverse References."));
    staticDataFolder.addReference(deepFolder, Identifiers.Organizes, false);
    addDeepObject(deepFolder, 1, 20);

    AccessLevelType none = AccessLevelType.of();
    AccessLevelType readOnly = AccessLevels.READ_ONLY;
    AccessLevelType writeOnly = AccessLevels.WRITE_ONLY;
    AccessLevelType readWrite = AccessLevels.READ_WRITE;

    // AccessLevel nodes
    createAccessLevelVariable("AccessLevelCurrentReadWrite", readWrite, readWrite);
    createAccessLevelVariable("AccessLevelCurrentRead", readOnly, readOnly);
    createAccessLevelVariable("AccessLevelCurrentWrite", writeOnly, writeOnly);
    createAccessLevelVariable("AccessLevelCurrentReadNotUser", readOnly, none);
    createAccessLevelVariable("AccessLevelCurrentWriteNotUser", writeOnly, none);

    // TwoState nodes
    for (TwoState t : TwoState.TWOSTATE_ITEMS) {
      createTwoStateDiscreteItem(t);
    }

    // MultiState nodes
    for (MultiState m : MultiState.MULTISTATE_ITEMS) {
      createMultiStateDiscreteItem(m);
    }

    // MultiStateValue nodes
    for (MultiStateValue m : MultiStateValue.MULTISTATEVALUE_ITEMS) {
      createMultiStateValueDiscreteItem(m);
    }

    // ArrayItem nodes
    createArrayItems();

  }

  // set all common settings for the node
  private void initNode(UaVariable node, CommonComplianceInfo info, FolderType parent) throws StatusException {
    EnumSet<AttributeWriteMask.Options> set =
        AttributesUtil.getSupportedWriteAccess(AttributeUtil.getSupportedAttributes(node.getNodeClass()));
    // Compiance test tool requires writing attributes

    // removing these as the may break something with ctt
    set.remove(AttributeWriteMask.Options.NodeId);
    set.remove(AttributeWriteMask.Options.NodeClass);
    set.remove(AttributeWriteMask.Options.ArrayDimensions);
    set.remove(AttributeWriteMask.Options.AccessLevel);
    set.remove(AttributeWriteMask.Options.UserAccessLevel);
    set.remove(AttributeWriteMask.Options.BrowseName);
    set.remove(AttributeWriteMask.Options.DataType);
    set.remove(AttributeWriteMask.Options.WriteMask);
    set.remove(AttributeWriteMask.Options.UserWriteMask);

    AttributeWriteMask writeMask = AttributeWriteMask.of(set);
    node.setWriteMask(writeMask);
    node.setUserWriteMask(writeMask);

    if (!((info instanceof TwoState) || (info instanceof MultiStateValue) || (info instanceof MultiState))) {
      node.setDataTypeId(info.getDataTypeId());
    }

    // add node to address space
    if (info.getBaseName().equals("TwoStateDiscreteItem5")) {
      // This is needed for CTT.
      parent.addReference(node, Identifiers.HasOrderedComponent, false);
    } else {
      parent.addReference(node, Identifiers.HasComponent, false);
    }
  }

  private ExpandedNodeId n2e(NodeId id) {
    return new ExpandedNodeId(NamespaceTable.OPCUA_NAMESPACE, id.getValue());
  }

}
