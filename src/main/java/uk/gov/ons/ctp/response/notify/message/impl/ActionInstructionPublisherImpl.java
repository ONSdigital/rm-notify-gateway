package uk.gov.ons.ctp.response.notify.message.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.Publisher;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.notify.message.ActionInstructionPublisher;

import javax.inject.Named;

/**
 * The publisher to queues
 */
@Slf4j
@Named
public class ActionInstructionPublisherImpl implements ActionInstructionPublisher {
  @Publisher(channel = "actionInstructionOutbound")
  @Override
  public ActionInstruction send(ActionInstruction actionInstruction) {
    log.debug("send to queue actionInstruction {}", actionInstruction);
    return actionInstruction;
  }
}
