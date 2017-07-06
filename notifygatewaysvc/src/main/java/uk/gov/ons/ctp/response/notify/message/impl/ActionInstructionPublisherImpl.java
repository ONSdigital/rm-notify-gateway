package uk.gov.ons.ctp.response.notify.message.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.MessageEndpoint;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.notify.message.ActionInstructionPublisher;

/**
 * The publisher of ActionInstruction to queues
 */
@Slf4j
@MessageEndpoint
public class ActionInstructionPublisherImpl implements ActionInstructionPublisher {

  @Qualifier("actionInstructionRabbitTemplate")
  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Override
  public void send(ActionInstruction actionInstruction) {
    log.debug("send to queue actionInstruction {}", actionInstruction);
    rabbitTemplate.convertAndSend(actionInstruction);
  }
}
