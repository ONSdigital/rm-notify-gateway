package uk.gov.ons.ctp.response.notify.message.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.notify.message.ActionInstructionReceiver;
import uk.gov.ons.ctp.response.notify.service.NotifyService;

import javax.inject.Inject;

/**
 * The service that reads ActionInstructions from the inbound channel
 */
@Slf4j
@MessageEndpoint
public class ActionInstructionReceiverImpl implements ActionInstructionReceiver {

  private static final String PROCESS_INSTRUCTION = "ProcessingInstruction";

  @Inject
  private Tracer tracer;

  @Inject
  private NotifyService notifyService;

  /**
   * To process ActionInstructions from the input channel actionInstructionTransformed
   * @param instruction the ActionInstruction to be processed
   * @throws CTPException when notifyService processing fails
   */
  @ServiceActivator(inputChannel = "actionInstructionTransformedWithHeader")
  public final void processInstruction(final ActionInstruction instruction) throws CTPException {
    log.debug("entering processInstruction with instruction {}", instruction);
    Span span = tracer.createSpan(PROCESS_INSTRUCTION);
    notifyService.process(instruction);
    tracer.close(span);
  }
}
