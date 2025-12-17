package com.prosysopc.ua.samples.server;

import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.ValueRanks;
import com.prosysopc.ua.nodes.UaMethod;
import com.prosysopc.ua.nodes.UaNode;
import com.prosysopc.ua.server.*;
import com.prosysopc.ua.server.nodes.*;
import com.prosysopc.ua.stack.builtintypes.*;
import com.prosysopc.ua.stack.core.*;

import java.util.*;

/**
 * CNC Node Manager – alternative Version (für Version 3 Projekt)
 * - neue interne Variablen
 * - andere Struktur
 * - andere Simulation
 * - andere Methoden-Registrierung
 * - aber gleiche OPC UA Knoten wie in der PDF vorgeschrieben
 */
public class CncNodeManager extends NodeManagerUaNode {

    public static final String NS_URI = "http://example.com/CNC";

    private UaObjectNode rootMachine;

    // interne Variablennamen
    private UaVariableNode statusNode;
    private UaVariableNode spindleTargetNode, spindleActualNode;
    private UaVariableNode feedTargetNode, feedActualNode;
    private UaVariableNode toolLifeNode;
    private UaVariableNode coolantTempNode;
    private UaVariableNode posX, posY, posZ;
    private UaVariableNode sfTargetNode, sfActualNode;
    private UaVariableNode productionProgressNode;
    private UaVariableNode alarmNode;

    private UaVariableNode machineNameNode, serialNode, plantNode, lineNode;
    private UaVariableNode orderNode, articleNode, quantityNode;

    private UaVariableNode cuttingForceX, cuttingForceY, cuttingForceZ;
    private UaVariableNode coolantFlowTarget, coolantFlowActual;
    private UaVariableNode cycleTimeTarget, cycleTimeActual;
    private UaVariableNode machiningPhaseNode;

    private UaVariableNode goodPartsNode, badPartsNode, totalPartsNode;

    private final Random rng = new Random();

    public CncNodeManager(UaServer server, String namespaceUri) {
        super(server, namespaceUri);
    }

    @Override
    protected void init() throws StatusException {
        super.init();
        createRootMachineNode();
        createAllVariables();
        registerMethods();
    }

    // -------------------------------------------------------------------------
    // Root Node
    // -------------------------------------------------------------------------
    private void createRootMachineNode() throws StatusException {
        int ns = getNamespaceIndex();
        rootMachine = new UaObjectNode(
                this,
                new NodeId(ns, "CncMachine"),
                new QualifiedName(ns, "CncMachine"),
                LocalizedText.english("CNC Machining Center")
        );

        addNodeAndReference(
                getServer().getNodeManagerRoot().getObjectsFolder(),
                rootMachine,
                Identifiers.Organizes
        );
    }

    // -------------------------------------------------------------------------
    // Variable Builder
    // -------------------------------------------------------------------------
    private UaVariableNode createVar(String name, Object value) throws StatusException {
        int ns = getNamespaceIndex();

        PlainVariable<?> var = new PlainVariable<>(
                this,
                new NodeId(ns, name),
                new QualifiedName(ns, name),
                LocalizedText.english(name)
        );

        var.addReference(Identifiers.HasTypeDefinition, Identifiers.BaseDataVariableType, false);
        var.setDescription(LocalizedText.english("Variable " + name));
        var.setValue(new Variant(value));
        var.setDataTypeId(getDataTypeId(value));

        addNodeAndReference(rootMachine, var, Identifiers.HasComponent);
        return var;
    }

    private NodeId getDataTypeId(Object v) {
        if (v instanceof Integer) return Identifiers.Int32;
        if (v instanceof Double || v instanceof Float) return Identifiers.Double;
        if (v instanceof Boolean) return Identifiers.Boolean;
        return Identifiers.String;
    }

    // -------------------------------------------------------------------------
    // Variable Creation
    // -------------------------------------------------------------------------
    private void createAllVariables() throws StatusException {

        // Pflichtfelder aus der PDF
        machineNameNode = createVar("MachineName", "PrecisionCraft VMC-850 #3");
        serialNode      = createVar("MachineSerialNumber", "VMC850-2023-003");
        plantNode       = createVar("Plant", "Munich Precision Manufacturing");
        lineNode        = createVar("ProductionLine", "5-Axis Machining Cell C");

        orderNode       = createVar("ProductionOrder", "PO-2024-AERO-0876");
        articleNode     = createVar("Article", "ART-TB-7075-T6");
        quantityNode    = createVar("OrderQuantity", 120.0);

        statusNode      = createVar("MachineStatus", "Running");

        spindleTargetNode = createVar("TargetSpindleSpeed", 8500.0);
        spindleActualNode = createVar("ActualSpindleSpeed", 8487.0);

        feedTargetNode  = createVar("TargetFeedRate", 1200.0);
        feedActualNode  = createVar("ActualFeedRate", 1197.5);

        toolLifeNode    = createVar("ToolLifeRemaining", 73.2);
        coolantTempNode = createVar("CoolantTemperature", 22.5);

        posX            = createVar("X", 125.847);
        posY            = createVar("Y", 89.234);
        posZ            = createVar("Z", -45.678);

        sfTargetNode    = createVar("TargetSurfaceFinish", 0.8);
        sfActualNode    = createVar("ActualSurfaceFinish", 0.75);

        productionProgressNode = createVar("ProductionOrderProgress", 57.5);

        cuttingForceX   = createVar("CuttingForceX", 245.7);
        cuttingForceY   = createVar("CuttingForceY", 189.3);
        cuttingForceZ   = createVar("CuttingForceZ", 567.8);

        coolantFlowTarget = createVar("TargetCoolantFlow", 25.0);
        coolantFlowActual = createVar("ActualCoolantFlow", 24.8);

        cycleTimeTarget = createVar("TargetCycleTime", 75.0);
        cycleTimeActual = createVar("ActualCycleTime", 73.2);

        machiningPhaseNode = createVar("MachiningPhase", "Roughing");

        goodPartsNode   = createVar("GoodParts", 2847.0);
        badPartsNode    = createVar("BadParts", 23.0);
        totalPartsNode  = createVar("TotalParts", 2870.0);

        alarmNode       = createVar("AlarmMessage", "OK");
    }

    // -------------------------------------------------------------------------
    // Helper zum Lesen von Doubles aus UaVariableNode
    // -------------------------------------------------------------------------
    private double readDouble(UaVariableNode node) throws StatusException {
        // DataValue -> Variant -> eigentlicher Wert
        Object raw = node.getValue().getValue().getValue();
        if (raw instanceof Number) {
            return ((Number) raw).doubleValue();
        }
        throw new IllegalStateException("Value of " + node.getBrowseName().getName() + " is not numeric: " + raw);
    }

    // -------------------------------------------------------------------------
    // simulateCycle – korrigiert
    // -------------------------------------------------------------------------
    public void simulateCycle() {

        try {
            // Spindel: leichte Variation um den Zielwert
            double targetRpm = readDouble(spindleTargetNode);
            double currentRpm = targetRpm + (rng.nextDouble() - 0.5) * 80.0;
            spindleActualNode.setValue(new Variant(currentRpm));

            // Vorschub: leichte Variation
            double targetFeed = readDouble(feedTargetNode);
            double currentFeed = targetFeed + (rng.nextDouble() - 0.5) * 20.0;
            feedActualNode.setValue(new Variant(currentFeed));

            // Werkzeugverschleiß: langsam abnehmend
            double toolLife = readDouble(toolLifeNode);
            toolLife = Math.max(0.0, toolLife - (0.1 + rng.nextDouble() * 0.3));
            toolLifeNode.setValue(new Variant(toolLife));

            // Oberflächenqualität: leichte Schwankung um Ziel
            double sfTarget = readDouble(sfTargetNode);
            double sfActual = sfTarget + (rng.nextDouble() - 0.5) * 0.01;
            sfActualNode.setValue(new Variant(sfActual));

            // Kühlmitteltemperatur: leichte Schwankungen
            double coolant = readDouble(coolantTempNode);
            coolant += (rng.nextDouble() - 0.5) * 0.2;
            coolantTempNode.setValue(new Variant(coolant));

            // einfache Alarm-Logik, anders als in deiner ersten Version
            if (Math.abs(currentRpm - targetRpm) > targetRpm * 0.12) {
                alarmNode.setValue(new Variant("Spindle deviation detected"));
                statusNode.setValue(new Variant("Error"));
            } else if (toolLife < 5) {
                alarmNode.setValue(new Variant("Tool end-of-life"));
                statusNode.setValue(new Variant("Warning"));
            } else {
                alarmNode.setValue(new Variant("OK"));
                statusNode.setValue(new Variant("Running"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------
    private void registerMethods() throws StatusException {
        int ns = getNamespaceIndex();
        MethodManagerUaNode manager = new MethodManagerUaNode(this);

        Map<String, Runnable> simpleMethods = new LinkedHashMap<>();
        simpleMethods.put("StartMachine", this::startMachine);
        simpleMethods.put("StopMachine", this::stopMachine);
        simpleMethods.put("EnterMaintenanceMode", this::enterMaintenance);
        simpleMethods.put("ResetCounters", this::resetCounters);
        simpleMethods.put("HomeAxes", this::homeAxes);

        for (String methodName : simpleMethods.keySet()) {

            UaMethodNode m = new UaMethodNode(
                    this,
                    new NodeId(ns, methodName),
                    new QualifiedName(ns, methodName),
                    LocalizedText.english(methodName)
            );
            m.setExecutable(true);
            m.setUserExecutable(true);
            addNodeAndReference(rootMachine, m, Identifiers.HasComponent);

            manager.addCallListener((ctx, objId, obj, mId, method, in, inRes, diag, out) -> {
                if (mId.equals(m.getNodeId())) {
                    simpleMethods.get(methodName).run();
                    return true;
                }
                return false;
            });
        }
    }

    private void startMachine() {
        safeSet(statusNode, "Running");
    }

    private void stopMachine() {
        safeSet(statusNode, "Stopped");
    }

    private void enterMaintenance() {
        safeSet(statusNode, "Maintenance");
    }

    private void resetCounters() {
        safeSet(goodPartsNode, 0.0);
        safeSet(badPartsNode, 0.0);
        safeSet(totalPartsNode, 0.0);
        safeSet(productionProgressNode, 0.0);
    }

    private void homeAxes() {
        safeSet(posX, 0.0);
        safeSet(posY, 0.0);
        safeSet(posZ, 0.0);
    }

    // helper
    private void safeSet(UaVariableNode node, Object value) {
        try {
            node.setValue(new Variant(value));
        } catch (StatusException e) {
            e.printStackTrace();
        }
    }
}
