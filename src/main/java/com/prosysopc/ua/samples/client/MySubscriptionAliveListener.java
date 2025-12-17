/**
 * Prosys OPC UA Java SDK
 * Copyright (c) Prosys OPC Ltd.
 * <http://www.prosysopc.com>
 * All rights reserved.
 */
package com.prosysopc.ua.samples.client;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.prosysopc.ua.client.Subscription;
import com.prosysopc.ua.client.SubscriptionAliveListener;

/**
 * A sample listener for subscription alive events.
 */
public class MySubscriptionAliveListener implements SubscriptionAliveListener {

  private static final ZoneId TIME_ZONE = ZoneId.systemDefault();
  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(TIME_ZONE);

  private static void printInfo(String event, Subscription subscription) {
    String lastAlive = subscription.getLastAlive() == null ? "null" : FORMATTER.format(subscription.getLastAlive());
    SampleConsoleClient.println(String.format("%s Subscription %s: ID=%d lastAlive=%s", FORMATTER.format(Instant.now()),
        event, subscription.getSubscriptionId().getValue(), lastAlive));
  }

  @Override
  public void onAfterCreate(Subscription subscription) {
    // the subscription was (re)created to the server
    // this happens if the subscription was timed out during
    // a communication break and had to be recreated after reconnection
    printInfo("created", subscription);
  }

  @Override
  public void onAlive(Subscription subscription) {
    // the server acknowledged that the connection is alive,
    // although there were no changes to send
    printInfo("alive", subscription);
  }

  @Override
  public void onLifetimeTimeout(Subscription subscription) {
    printInfo("lifetime ended", subscription);
  }

  @Override
  public void onTimeout(Subscription subscription) {
    // the server did not acknowledge that the connection is alive, and the
    // maxKeepAliveCount has been exceeded
    printInfo("timeout", subscription);
  }

}
