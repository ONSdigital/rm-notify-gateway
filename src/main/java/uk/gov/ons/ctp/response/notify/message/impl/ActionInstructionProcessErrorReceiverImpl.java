package uk.gov.ons.ctp.response.notify.message.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.notify.message.ActionInstructionPublisher;

import javax.inject.Inject;

/**
 * The reader of messages put on channel actionInstructionProcessErrorFailedMsgOnly
 */
@Slf4j
@MessageEndpoint
public class ActionInstructionProcessErrorReceiverImpl {

  @Inject
  private ActionInstructionPublisher actionInstructionPublisher;

// TODO Reinstate the poller inside the @ServiceActivator: currently gives an exception at runtime
//  ,
//  poller = @Poller(
//          fixedDelay = "${notify-error-poller.fixedDelay}",
//          maxMessagesPerPoll = "${notify-error-poller.msgPerPoll}")
  /**
   * To process messages put on channel actionInstructionProcessErrorFailedMsgOnly
   * @param message the message to process
   */
  @ServiceActivator(inputChannel = "actionInstructionProcessErrorFailedMsgOnly")
  public void process(Message<?> message) {
    log.debug("entering process with message {}", message);
    ActionInstruction actionInstructionToReprocess = (ActionInstruction) message.getPayload();
    log.debug("actionInstructionToReprocess = {}", actionInstructionToReprocess);

    actionInstructionPublisher.send(actionInstructionToReprocess);
  }

}
