package uk.gov.ons.ctp.response.notify.message.impl;

import static uk.gov.ons.ctp.response.notify.representation.NotifyRequestForEmailDTO.EMAIL_ADDRESS_REGEX;
import static uk.gov.ons.ctp.response.notify.representation.NotifyRequestForSMSDTO.TELEPHONE_REGEX;
import static uk.gov.ons.ctp.response.notify.service.impl.NotifyServiceImpl.NOTIFY_SMS_NOT_SENT;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.action.message.feedback.Outcome;
import uk.gov.ons.ctp.response.action.message.instruction.ActionContact;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.notify.client.CommsTemplateClientException;
import uk.gov.ons.ctp.response.notify.message.ActionFeedbackPublisher;
import uk.gov.ons.ctp.response.notify.message.ActionInstructionReceiver;
import uk.gov.ons.ctp.response.notify.service.NotifyService;
import uk.gov.service.notify.NotificationClientException;

/**
 * The service that reads ActionInstructions from the inbound channel
 */
@Slf4j
@MessageEndpoint
public class ActionInstructionReceiverImpl implements ActionInstructionReceiver {

  private static final String NOTIFY_GW = "NotifyGateway";

  public static final int SITUATION_MAX_LENGTH = 100;

  @Autowired
  private NotifyService notifyService;

  @Autowired
  private ActionFeedbackPublisher actionFeedbackPublisher;

  /**
   * To process ActionInstructions from the input channel actionInstructionTransformed
   *
   * @param instruction the ActionInstruction to be processed
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  @ServiceActivator(inputChannel = "actionInstructionTransformed", adviceChain = "actionInstructionRetryAdvice")
  public void processInstruction(final ActionInstruction instruction) throws NotificationClientException,
          CommsTemplateClientException {
    log.debug("entering process with instruction {}", instruction);

    ActionRequest actionRequest = instruction.getActionRequest();
    if (actionRequest != null) {
      ActionFeedback actionFeedback = null;
      String actionId = actionRequest.getActionId();

      boolean responseRequired = actionRequest.isResponseRequired();
      if (responseRequired) {
        actionFeedback = new ActionFeedback(actionId,
                NOTIFY_GW.length() <= SITUATION_MAX_LENGTH ? NOTIFY_GW : NOTIFY_GW.substring(0, SITUATION_MAX_LENGTH),
                Outcome.REQUEST_ACCEPTED);
        actionFeedbackPublisher.sendFeedback(actionFeedback);
      }

      actionRequest = tidyUp(actionRequest);

      if (validate(actionRequest)) {
        actionFeedback = notifyService.process(actionRequest);
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

  /**
   * To tidy up phone number & email address within an ActionRequest as we have got no control on the upstream
   *
   * @param actionRequest the ActionRequest to tidy up
   * @return the tidied ActionRequest
   */
  private ActionRequest tidyUp(ActionRequest actionRequest) {
    ActionContact actionContact = actionRequest.getContact();
    if (actionContact != null) {
      String phoneNumber = actionContact.getPhoneNumber();
      if (phoneNumber != null) {
        // TODO For BRES, currently removing the number to stop any SMS being sent by error. This null phone number is
        // TODO also used as the switch in NotifyServiceImpl to determine whether to processSms or processEmail.
        // TODO Plan for when we produce a solution serving both BRES & Census.
//        phoneNumber = phoneNumber.replaceAll("\\s+","");  // removes all whitespaces and non-visible characters (e.g., tab, \n).
//        phoneNumber = phoneNumber.replaceAll("\\(", "");
//        phoneNumber = phoneNumber.replaceAll("\\)", "");
//        actionContact.setPhoneNumber(phoneNumber);
        actionContact.setPhoneNumber(null);
      }

      String emailAddress = actionContact.getEmailAddress();
      if (emailAddress != null) {
        actionContact.setEmailAddress(emailAddress.trim());
      }

      actionRequest.setContact(actionContact);
    }

    return actionRequest;
  }

  /**
   * This validates the phone number (Census) or the email address (BRES) in the given ActionRequest
   * @param actionRequest the ActionRequest
   * @return true if the phone number or the email address is valid
   */
  private boolean validate(ActionRequest actionRequest) {
    boolean result = false;

    if (actionRequest != null) {
      ActionContact actionContact = actionRequest.getContact();
      if (actionContact != null) {
        String phoneNumber = actionContact.getPhoneNumber();
        log.debug("phoneNumber is {}", phoneNumber);
        if (phoneNumber == null) {
          String emailAddress = actionContact.getEmailAddress();
          log.debug("emailAddress is {}", emailAddress);
          if (emailAddress != null) {
            Pattern pattern = Pattern.compile(EMAIL_ADDRESS_REGEX);
            result = pattern.matcher(emailAddress).matches();
          }
        } else {
          Pattern pattern = Pattern.compile(TELEPHONE_REGEX);
          result = pattern.matcher(phoneNumber).matches();
        }
      }
    }

    return result;
  }
}
