package uk.gov.ons.ctp.response.notify.message;

import org.springframework.messaging.Message;

/**
 * The reader of messages put on channel actionInstructionProcessErrorFailedMsgOnly
 */
public interface ActionInstructionProcessErrorReceiver {
  /**
   * To process messages put on channel actionInstructionProcessErrorFailedMsgOnly
   * @param message the message to process
   */
  void process(Message<?> message);
}
