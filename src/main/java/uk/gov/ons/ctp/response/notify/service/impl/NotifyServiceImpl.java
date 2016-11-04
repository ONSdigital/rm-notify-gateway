package uk.gov.ons.ctp.response.notify.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.action.message.feedback.Outcome;
import uk.gov.ons.ctp.response.action.message.instruction.ActionContact;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.notify.service.NotifyService;

import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.Notification;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.NotificationResponse;

import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigInteger;
import java.util.HashMap;

/**
 * The service implementation for NotifyService
 */
@Slf4j
@Named
public class NotifyServiceImpl implements NotifyService {

  @Value("${TEMPLATE_ID}")
  private String templateId;

  @Inject
  private NotificationClient notificationClient;

  public static final String FORENAME_KEY = "forename";
  public static final String SURNAME_KEY = "surname";
  public static final String IAC_KEY = "iac";

  public static final String EXCEPTION_NOTIFY_SERVICE = "An error occurred contacting GOV.UK Notify: ";
  public static final String NOTIFY_SMS_SENT = "Notify Sms Sent";

  @Override
  public ActionFeedback process(ActionRequest actionRequest) throws CTPException {
    log.debug("Entering process with actionRequest {}", actionRequest);
    try {
      BigInteger actionId = actionRequest.getActionId();
      ActionContact actionContact = actionRequest.getContact();
      HashMap<String, String> personalisation = new HashMap<>();
      personalisation.put(FORENAME_KEY, actionContact.getForename());
      personalisation.put(SURNAME_KEY, actionContact.getSurname());
      personalisation.put(IAC_KEY, actionRequest.getIac());
      String phoneNumber = actionContact.getPhoneNumber();
      log.debug("About to invoke sendSms with templateId {} - phone number {} - personalisation {} for actionId = {}",
              templateId, phoneNumber, personalisation, actionId);
      NotificationResponse response = notificationClient.sendSms(templateId, phoneNumber, personalisation);
      String notificationId = response.getNotificationId();
      Notification notification = notificationClient.getNotificationById(notificationId);
      String status = notification.getStatus();
      log.debug("status = {} for actionId = {}", status, actionId);
      ActionFeedback result = new ActionFeedback(actionId, NOTIFY_SMS_SENT, Outcome.REQUEST_COMPLETED, status);
      return result;
    } catch (NotificationClientException e) {
      String errorMsg = String.format("%s%s", EXCEPTION_NOTIFY_SERVICE, e.getMessage());
      log.error(errorMsg);
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, errorMsg);
    }
  }
}
