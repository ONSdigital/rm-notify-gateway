package uk.gov.ons.ctp.response.notify.service.impl;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.action.message.feedback.Outcome;
import uk.gov.ons.ctp.response.action.message.instruction.ActionContact;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.notify.config.NotifyConfiguration;
import uk.gov.ons.ctp.response.notify.service.NotifyService;

import uk.gov.ons.ctp.response.notify.util.InternetAccessCodeFormatter;
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

  @Inject
  private NotifyConfiguration notifyConfiguration;

  @Inject
  private NotificationClient notificationClient;

  private static final String BAD_REQUEST = "Status code: 400";

  public static final String FORENAME_KEY = "forename";
  public static final String SURNAME_KEY = "surname";
  public static final String IAC_KEY = "iac";

  public static final String EXCEPTION_NOTIFY_SERVICE = "An error occurred contacting GOV.UK Notify: ";
  public static final String NOTIFY_SMS_NOT_SENT = "Notify Sms Not Sent";
  public static final String NOTIFY_SMS_SENT = "Notify Sms Sent";

  @Override
  public ActionFeedback process(ActionRequest actionRequest) throws CTPException {
    BigInteger actionId = actionRequest.getActionId();
    log.debug("Entering process with actionId {}", actionId);

    try {
      String templateId = notifyConfiguration.getTemplateId();

      ActionContact actionContact = actionRequest.getContact();
      String phoneNumber = actionContact.getPhoneNumber();
      HashMap<String, String> personalisation = new HashMap<>();
      personalisation.put(FORENAME_KEY, actionContact.getForename());
      personalisation.put(SURNAME_KEY, actionContact.getSurname());
      personalisation.put(IAC_KEY, InternetAccessCodeFormatter.externalize(actionRequest.getIac()));

      log.debug("About to invoke sendSms with templateId {} - phone number {} - personalisation {} for actionId = {}",
              templateId, phoneNumber, personalisation, actionId);
      NotificationResponse response = notificationClient.sendSms(templateId, phoneNumber, personalisation);

      Notification notification = notificationClient.getNotificationById(response.getNotificationId());
      log.debug("status = {} for actionId = {}", notification.getStatus(), actionId);
      return new ActionFeedback(actionId, NOTIFY_SMS_SENT, Outcome.REQUEST_COMPLETED);
    } catch (NotificationClientException e) {
      String errorMsg = String.format("%s%s", EXCEPTION_NOTIFY_SERVICE, e.getMessage());
      log.error(errorMsg);
      if (errorMsg.contains(BAD_REQUEST)) {
        return new ActionFeedback(actionId, NOTIFY_SMS_NOT_SENT, Outcome.REQUEST_COMPLETED);
      }
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, errorMsg);
    }
  }
}
