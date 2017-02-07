package uk.gov.ons.ctp.response.notify.message.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.notify.message.ActionInstructionPublisher;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * The publisher to queues
 */
@Slf4j
@Named
public class ActionInstructionPublisherImpl implements ActionInstructionPublisher {

  @Qualifier("actionInstructionRabbitTemplate")
  @Inject
  private RabbitTemplate rabbitTemplate;

  @Override
  public void send(ActionInstruction actionInstruction) {
    log.debug("send to queue actionInstruction {}", actionInstruction);
    // TODO Leave this binding hardcoded? The same value is used in the xml flow.
    rabbitTemplate.convertAndSend("Action.Notify.binding", actionInstruction);
  }
}
