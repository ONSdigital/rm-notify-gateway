package uk.gov.ons.ctp.response.notify.message.impl;

import static uk.gov.ons.ctp.response.notify.representation.NotifyRequestForEmailDTO.EMAIL_ADDRESS_REGEX;
import static uk.gov.ons.ctp.response.notify.representation.NotifyRequestForSMSDTO.TELEPHONE_REGEX;
import static uk.gov.ons.ctp.response.notify.service.impl.NotifyServiceImpl.NOTIFY_SMS_NOT_SENT;

import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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

/** The service that reads ActionInstructions from the inbound channel */
@Slf4j
@MessageEndpoint
public class ActionInstructionReceiverImpl implements ActionInstructionReceiver {

  private static final String NOTIFY_GW = "NotifyGateway";

  public static final int SITUATION_MAX_LENGTH = 100;

  @Autowired private NotifyService notifyService;

  @Autowired private ActionFeedbackPublisher actionFeedbackPublisher;

  /**
   * To process ActionInstructions from the input channel actionInstructionTransformed
   *
   * @param instruction the ActionInstruction to be processed
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  @ServiceActivator(
      inputChannel = "actionInstructionTransformed",
      adviceChain = "actionInstructionRetryAdvice")
  public void processInstruction(final ActionInstruction instruction)
      throws NotificationClientException, CommsTemplateClientException {

    log.debug("entering process with instruction {}", instruction);

    ActionRequest actionRequest = instruction.getActionRequest();

    if (actionRequest == null) {
      return;
    }

    final ResponsePublisher publisher = new ResponsePublisher(actionRequest.isResponseRequired());

    publisher.sendFeedback(notifyGatewayRequestAccepted(actionRequest));

    actionRequest = tidyUp(actionRequest);

    if (!validate(actionRequest)) {
      log.error(
          "Data validation failed for actionRequest with action id {}",
          actionRequest.getActionId());

      publisher.sendFeedback(smsNotSent(actionRequest));

      return;
    }

    ActionFeedback actionFeedback = notifyService.process(actionRequest);
    if (actionFeedback != null) {
      publisher.sendFeedback(actionFeedback);
    }
  }

  private ActionFeedback notifyGatewayRequestAccepted(final ActionRequest actionRequest) {
    return new ActionFeedback(
        actionRequest.getActionId(), getNotifyGateway(), Outcome.REQUEST_ACCEPTED);
  }

  private ActionFeedback smsNotSent(final ActionRequest actionRequest) {
    return new ActionFeedback(
        actionRequest.getActionId(), getNotifySMSNotSent(), Outcome.REQUEST_DECLINED);
  }

  private String getNotifySMSNotSent() {
    return truncateToSituationMaxLength(NOTIFY_SMS_NOT_SENT);
  }

  private String getNotifyGateway() {
    return truncateToSituationMaxLength(NOTIFY_GW);
  }

  private String truncateToSituationMaxLength(final String string) {
    return string.length() <= SITUATION_MAX_LENGTH
        ? string
        : string.substring(0, SITUATION_MAX_LENGTH);
  }

  private ActionRequest tidyUp(final ActionRequest actionRequest) {
    ActionContact actionContact = actionRequest.getContact();

    if (actionContact == null) {
      return actionRequest;
    }

    String phoneNumber = actionContact.getPhoneNumber();
    if (phoneNumber != null) {
      // TODO For BRES, currently removing the number to stop any SMS being sent by error. This
      // null phone number is
      // TODO also used as the switch in NotifyServiceImpl to determine whether to processSms or
      // processEmail.
      // TODO Plan for when we produce a solution serving both BRES & Census.
      //        phoneNumber = phoneNumber.replaceAll("\\s+","");  // removes all whitespaces and
      // non-visible characters (e.g., tab, \n).
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

    return actionRequest;
  }

  private boolean validate(final ActionRequest actionRequest) {
    if (actionRequest == null) {
      return false;
    }

    ActionContact actionContact = actionRequest.getContact();

    if (actionContact == null) {
      return false;
    }

    String phoneNumber = actionContact.getPhoneNumber();
    log.debug("phoneNumber is {}", phoneNumber);

    if (phoneNumber != null) {
      return isPhoneNumberValid(phoneNumber);
    }

    String emailAddress = actionContact.getEmailAddress();
    log.debug("emailAddress is {}", emailAddress);

    if (emailAddress != null) {
      return isEmailValid(emailAddress);
    }

    return false;
  }

  private boolean isEmailValid(final String emailAddress) {
    Pattern pattern = Pattern.compile(EMAIL_ADDRESS_REGEX);
    return pattern.matcher(emailAddress).matches();
  }

  private boolean isPhoneNumberValid(final String phoneNumber) {
    Pattern pattern = Pattern.compile(TELEPHONE_REGEX);
    return pattern.matcher(phoneNumber).matches();
  }

  /**
   * A wrapper for ActionFeedbackPublish which only sends an ActionFeedback instance if a response
   * is required.
   */
  private class ResponsePublisher {
    private final boolean responseRequired;

    ResponsePublisher(final boolean responseRequired) {
      this.responseRequired = responseRequired;
    }

    private void sendFeedback(final ActionFeedback actionFeedback) {
      if (responseRequired) {
        actionFeedbackPublisher.sendFeedback(actionFeedback);
      }
    }
  }
}
