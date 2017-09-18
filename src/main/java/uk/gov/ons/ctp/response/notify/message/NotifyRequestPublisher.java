package uk.gov.ons.ctp.response.notify.message;

import uk.gov.ons.ctp.response.notify.message.notify.NotifyRequest;

/**
 * The publisher of NotifyRequests to queue
 */
public interface NotifyRequestPublisher {

  /**
   * To publish a NotifyRequest to queue
   * @param notifyRequest to be published
   */
  void send(NotifyRequest notifyRequest);
}
