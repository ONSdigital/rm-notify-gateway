package uk.gov.ons.ctp.response.notify.message.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.oxm.Marshaller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.action.message.feedback.Outcome;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequests;
import uk.gov.ons.ctp.response.notify.message.ActionFeedbackPublisher;
import uk.gov.ons.ctp.response.notify.message.ActionInstructionPublisher;
import uk.gov.ons.ctp.response.notify.message.ActionInstructionReceiver;
import uk.gov.ons.ctp.response.notify.service.NotifyService;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.regex.Pattern;

import static uk.gov.ons.ctp.response.notify.service.impl.NotifyServiceImpl.NOTIFY_SMS_NOT_SENT;

/**
 * The service that reads ActionInstructions from the inbound channel
 */
@Slf4j
@MessageEndpoint
public class ActionInstructionReceiverImpl implements ActionInstructionReceiver {

  private static final String ERROR_PROCESSING_ACTION_REQUEST =
          "An exception occurred while processing action request with action id";
  private static final String NOTIFY_GW = "NotifyGateway";
  private static final String PROCESS_INSTRUCTION = "ProcessingInstruction";
  private static final String TELEPHONE_REGEX = "[\\d]{7,11}";
  private static final String UNEXPECTED_ERROR_PROCESSING_ACTION_REQUEST =
          "An unexpected exception occurred while processing action request.";

  public static final int SITUATION_MAX_LENGTH = 100;

  @Inject
  private Tracer tracer;

  @Inject
  @Qualifier("actionInstructionMarshaller")
  Marshaller marshaller;

  @Inject
  private NotifyService notifyService;

  @Inject
  private ActionInstructionPublisher actionInstructionPublisher;

  @Inject
  private ActionFeedbackPublisher actionFeedbackPublisher;

  /**
   * To process ActionInstructions from the input channel actionInstructionTransformed
   * @param instruction the ActionInstruction to be processed
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  @ServiceActivator(inputChannel = "actionInstructionTransformed")
  public final void processInstruction(final ActionInstruction instruction) {
    try {
      process(instruction);
    } catch (Throwable t) {
      // In this case, we do not want to replay the ActionInstruction. If we did, duplicate sms may be sent.
      log.error("Unexpected exception: {}", t);
      throw new AmqpRejectAndDontRequeueException(UNEXPECTED_ERROR_PROCESSING_ACTION_REQUEST);
    }
  }

  /**
   * this is where the processing is really done
   * @param instruction to process
   */
  private void process(final ActionInstruction instruction) {
    log.debug("entering process with instruction {}", instruction);
    Span span = tracer.createSpan(PROCESS_INSTRUCTION);

    ActionRequests actionRequests = instruction.getActionRequests();
    if (actionRequests != null) {
      for (ActionRequest actionRequest : actionRequests.getActionRequests()) {
        ActionFeedback actionFeedback = null;
        boolean responseRequired = actionRequest.isResponseRequired();
        BigInteger actionId = actionRequest.getActionId();

        if (responseRequired) {
          actionFeedback = new ActionFeedback(actionId,
                  NOTIFY_GW.length() <= SITUATION_MAX_LENGTH ?
                          NOTIFY_GW : NOTIFY_GW.substring(0, SITUATION_MAX_LENGTH),
                  Outcome.REQUEST_ACCEPTED);
          actionFeedbackPublisher.sendFeedback(actionFeedback);
          actionFeedback = null;
        }

        if (validate(actionRequest)) {
          try {
            actionFeedback = notifyService.process(actionRequest);
          } catch (CTPException e) {
            String errorMsg = String.format("%s %d - %s", ERROR_PROCESSING_ACTION_REQUEST, actionId, e.getMessage());
            log.error(errorMsg);
            actionInstructionPublisher.send(buildActionInstruction(actionRequest));
          }
        } else {
          log.error("Data validation failed for actionRequest with action id {}", actionRequest.getActionId());
          actionFeedback = new ActionFeedback(actionId, NOTIFY_SMS_NOT_SENT.length() <= SITUATION_MAX_LENGTH ?
                  NOTIFY_SMS_NOT_SENT : NOTIFY_SMS_NOT_SENT.substring(0, SITUATION_MAX_LENGTH),
                  Outcome.REQUEST_DECLINED);
        }
        if (actionFeedback != null && responseRequired) {
          actionFeedbackPublisher.sendFeedback(actionFeedback);
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

  /**
   * This validates the phone number in the given ActionRequest
   * @param actionRequest the ActionRequest
   * @return true if the phone number is valid
   */
  private boolean validate(ActionRequest actionRequest) {
    boolean result = false;
    if (actionRequest != null && actionRequest.getContact() != null) {
      String phoneNumber = actionRequest.getContact().getPhoneNumber();
      Pattern pattern = Pattern.compile(TELEPHONE_REGEX);
      result = pattern.matcher(phoneNumber).matches();
    }
    return result;
  }
}
