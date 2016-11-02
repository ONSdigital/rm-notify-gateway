package uk.gov.ons.ctp.response.notify.message.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequests;
import uk.gov.ons.ctp.response.notify.message.ActionInstructionPublisher;
import uk.gov.ons.ctp.response.notify.message.ActionInstructionReceiver;
import uk.gov.ons.ctp.response.notify.service.NotifyService;

import javax.inject.Inject;
import java.util.List;

/**
 * The service that reads ActionInstructions from the inbound channel
 */
@Slf4j
@MessageEndpoint
public class ActionInstructionReceiverImpl implements ActionInstructionReceiver {

  private static final String PROCESS_INSTRUCTION = "ProcessingInstruction";
  private static final String ERROR_PROCESSING_ACTION_REQUEST =
          "An exception occurred while processing action request with action id";

  @Inject
  private Tracer tracer;

  @Inject
  private NotifyService notifyService;

  @Inject
  private ActionInstructionPublisher actionInstructionPublisher;

  /**
   * To process ActionInstructions from the input channel actionInstructionTransformed
   * @param instruction the ActionInstruction to be processed
   */
  @ServiceActivator(inputChannel = "actionInstructionTransformed")
  public final void processInstruction(final ActionInstruction instruction) {
    log.debug("entering processInstruction with instruction {}", instruction);
    Span span = tracer.createSpan(PROCESS_INSTRUCTION);
    ActionRequests actionRequests = instruction.getActionRequests();
    if (actionRequests != null) {
      for (ActionRequest actionRequest : actionRequests.getActionRequests()) {
        try {
          notifyService.process(actionRequest);
        } catch (CTPException e) {
          String errorMsg = String.format("%s %d - %s", ERROR_PROCESSING_ACTION_REQUEST, actionRequest.getActionId(),
                  e.getMessage());
          log.error(errorMsg);
          actionInstructionPublisher.send(buildActionInstruction(actionRequest));
        }
      }
    }

    tracer.close(span);
  }

  /**
   * To build an ActionInstruction containing one ActionRequest
   * @param actionRequest the ActionRequest
   * @return an ActionInstruction
   */
  private ActionInstruction buildActionInstruction(ActionRequest actionRequest) {
    ActionRequests actionRequests = new ActionRequests();
    List<ActionRequest> actionRequestList = actionRequests.getActionRequests();
    actionRequestList.add(actionRequest);
    ActionInstruction actionInstruction = new ActionInstruction();
    actionInstruction.setActionRequests(actionRequests);
    return actionInstruction;
  }
}
