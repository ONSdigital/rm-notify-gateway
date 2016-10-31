package uk.gov.ons.ctp.response.notify.message.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.notify.message.InstructionReceiver;
import uk.gov.ons.ctp.response.notify.service.NotifyService;

import javax.inject.Inject;

/**
 * The service that reads ActionInstructions from the inbound channel
 */
@Slf4j
@MessageEndpoint
public class InstructionReceiverImpl implements InstructionReceiver {

  @Inject
  private NotifyService notifyService;

  /**
   * To process ActionInstructions from the input channel actionInstructionTransformed
   * @param instruction the ActionInstruction to be processed
   */
  @ServiceActivator(inputChannel = "actionInstructionTransformed")
  public final void processInstruction(final ActionInstruction instruction) throws CTPException {
    log.debug("entering processInstruction with instruction {}", instruction);
    notifyService.process(instruction);
  }
}
