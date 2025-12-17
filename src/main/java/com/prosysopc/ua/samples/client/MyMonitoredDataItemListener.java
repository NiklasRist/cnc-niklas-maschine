/**
 * Prosys OPC UA Java SDK
 * Copyright (c) Prosys OPC Ltd.
 * <http://www.prosysopc.com>
 * All rights reserved.
 */
package com.prosysopc.ua.samples.client;

import com.prosysopc.ua.client.MonitoredDataItem;
import com.prosysopc.ua.client.MonitoredDataItemListener;
import com.prosysopc.ua.stack.builtintypes.DataValue;
import com.prosysopc.ua.stack.core.AggregateFilter;

/**
 * A sampler listener for monitored data changes.
 */
public class MyMonitoredDataItemListener implements MonitoredDataItemListener {
  private final SampleConsoleClient client;

  private String aggregateNote = null;

  public MyMonitoredDataItemListener(SampleConsoleClient client) {
    this.client = client;
  }

  @Override
  public void onDataChange(MonitoredDataItem sender, DataValue prevValue, DataValue value) {
    String aggregateNote = getAggregateTypeName(sender);

    SampleConsoleClient
        .println(client.dataValueToString(sender.getNodeId(), sender.getAttributeId(), value) + aggregateNote);
  }

  private String getAggregateTypeName(MonitoredDataItem item) {
    // Get the name of the aggregateType from the server,
    // if an AggregateFilter is used in the MonitoredItem
    if (aggregateNote == null) {
      aggregateNote = "";
      if (item.getFilter() instanceof AggregateFilter) {
        try {
          aggregateNote = " AggregateType="
              + client.readDisplayName(((AggregateFilter) item.getFilter()).getAggregateType()).getText();
        } catch (Exception e) {
        }
      }
    }
    return aggregateNote;
  }

}
