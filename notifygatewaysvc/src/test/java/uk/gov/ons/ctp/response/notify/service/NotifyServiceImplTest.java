package uk.gov.ons.ctp.response.notify.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.action.message.feedback.Outcome;
import uk.gov.ons.ctp.response.notify.config.NotifyConfiguration;
import uk.gov.ons.ctp.response.notify.message.notify.NotifyRequest;
import uk.gov.ons.ctp.response.notify.service.impl.NotifyServiceImpl;
import uk.gov.ons.ctp.response.notify.utility.ObjectBuilder;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.ons.ctp.response.notify.service.impl.NotifyServiceImpl.*;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.*;

/**
 * To unit test NotifyServiceImpl
 */
@RunWith(MockitoJUnitRunner.class)
public class NotifyServiceImplTest {

  @InjectMocks
  private NotifyServiceImpl notifyService;

  @Mock
  private NotificationClient notificationClient;

  @Mock
  private NotifyConfiguration notifyConfiguration;

  private static final String NOTIFY_REQUEST_ID = "f3778220-f877-4a3d-80ed-e8fa7d104564";
  private static final String TEMPLATE_ID = "f3778220-f877-4a3d-80ed-e8fa7d104563";
  private static final String VALID_EMAIL_ADDRESS = "tester@ons.gov.uk";
  private static final String VALID_PHONE_NUMBER = "01234567890";
  private static final String MESSAGE_REFERENCE = "the reference";

  /**
   * To test the happy path when processing an ActionRequest
   *
   * @throws CTPException when notifyService.process does
   * @throws NotificationClientException when notificationClient does
   */
  @Test
  public void testProcessActionRequestHappyPath() throws CTPException, NotificationClientException {
    Mockito.when(notificationClient.sendSms(any(String.class), any(String.class), any(HashMap.class),any(String.class))).thenReturn(buildSendSmsResponse());
    Mockito.when(notificationClient.getNotificationById(any(String.class))).thenReturn(buildNotificationForSMS());

    ActionFeedback result = notifyService.process(ObjectBuilder.buildActionRequest(ACTION_ID, FORENAME, SURNAME, PHONENUMBER, true));
    assertEquals(ACTION_ID, result.getActionId());
    assertEquals(NOTIFY_SMS_SENT, result.getSituation());
    assertEquals(Outcome.REQUEST_COMPLETED, result.getOutcome());

    HashMap<String, String> personalisation = new HashMap<>();
    personalisation.put(IAC_KEY, IAC_AS_DISPLAYED_IN_SMS);
    verify(notificationClient, times(1)).sendSms(any(String.class), eq(PHONENUMBER), eq(personalisation),any(String.class));
  }

  /**
   * To test the processing of an ActionRequest when an exception is received from GOV.UK Notify
   *
   * @throws NotificationClientException when notificationClient does
   */
  @Test
  public void testProcessSendSmsException() throws NotificationClientException {
    NotificationClientException exception = new NotificationClientException(new Exception());
    Mockito.when(notificationClient.sendSms(any(String.class), any(String.class), any(HashMap.class), any(String.class))).thenThrow(exception);

    boolean exceptionThrown = false;
    try {
      notifyService.process(ObjectBuilder.buildActionRequest(ACTION_ID, FORENAME, SURNAME, PHONENUMBER, true));
    } catch(CTPException e) {
      exceptionThrown = true;
      assertEquals(CTPException.Fault.SYSTEM_ERROR, e.getFault());
      assertTrue(e.getMessage().startsWith(EXCEPTION_NOTIFY_SERVICE));
    }

    assertTrue(exceptionThrown);
    verify(notificationClient, times(1)).sendSms(any(String.class), any(String.class), any(HashMap.class), any(String.class));
  }

  /**
   * To test the happy path when processing a NotifyRequest (SMS scenario)
   *
   * @throws NotificationClientException when notificationClient does
   */
  @Test
  public void testProcessNotifyRequestHappyPathSMS() throws NotificationClientException {
    Mockito.when(notificationClient.sendSms(
            any(String.class), any(String.class), any(HashMap.class),any(String.class)))
            .thenReturn(buildSendSmsResponse());
    Mockito.when(notificationClient.getNotificationById(any(String.class))).thenReturn(buildNotificationForSMS());

    notifyService.process(NotifyRequest.builder()
            .withId(NOTIFY_REQUEST_ID)
            .withTemplateId(TEMPLATE_ID)
            .withReference(MESSAGE_REFERENCE)
            .withPhoneNumber(VALID_PHONE_NUMBER)
            .build());

    HashMap<String, String> personalisation = new HashMap<>();
    personalisation.put(IAC_KEY, IAC_AS_DISPLAYED_IN_SMS);
    // TODO Be specific on personalisation once implemented verify(notificationClient, times(1)).sendSms(
    // TODO any(String.class), eq(PHONENUMBER), eq(personalisation), any(String.class));
    verify(notificationClient, times(1)).sendSms(any(String.class), eq(VALID_PHONE_NUMBER),
            any(Map.class),eq(MESSAGE_REFERENCE));
  }

  /**
   * To test the happy path when processing a NotifyRequest (Email scenario)
   *
   * @throws NotificationClientException when notificationClient does
   */
  @Test
  public void testProcessNotifyRequestHappyPathEmail() throws NotificationClientException {
    Mockito.when(notificationClient.sendEmail(
            any(String.class), any(String.class), any(HashMap.class),any(String.class)))
            .thenReturn(buildSendEmailResponse());
    Mockito.when(notificationClient.getNotificationById(any(String.class))).thenReturn(buildNotificationForSMS());

    notifyService.process(NotifyRequest.builder()
            .withId(NOTIFY_REQUEST_ID)
            .withTemplateId(TEMPLATE_ID)
            .withReference(MESSAGE_REFERENCE)
            .withEmailAddress(VALID_EMAIL_ADDRESS)
            .build());

    HashMap<String, String> personalisation = new HashMap<>();
    personalisation.put(IAC_KEY, IAC_AS_DISPLAYED_IN_SMS);
    // TODO Be specific on personalisation once implemented verify(notificationClient, times(1)).sendSms(
    // TODO any(String.class), eq(PHONENUMBER), eq(personalisation), any(String.class));
    verify(notificationClient, times(1)).sendEmail(any(String.class), eq(VALID_EMAIL_ADDRESS),
            any(Map.class),eq(MESSAGE_REFERENCE));
  }
}
