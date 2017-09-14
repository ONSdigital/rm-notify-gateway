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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertNull;
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

  private static final String IAC_KEY = "iac";
  private static final String IAC_VALUE = "ABCD-EFGH-IJKL-MNOP";
  private static final String OTHER_FIELD_KEY = "otherfield";
  private static final String OTHER_FIELD_VALUE = "other,value";
  private static final String PERSONALISATION_FORMAT_FROM_ORIKA = "{%s=%s, %s=%s}";
  private static final String PRIVATE_METHOD_NAME = "buildMapFromString";

  private static final String NOTIFY_REQUEST_ID = "f3778220-f877-4a3d-80ed-e8fa7d104564";
  private static final String TEMPLATE_ID = "f3778220-f877-4a3d-80ed-e8fa7d104563";
  private static final String VALID_EMAIL_ADDRESS = "tester@ons.gov.uk";
  private static final String VALID_PHONE_NUMBER = "01234567890";
  private static final String MESSAGE_REFERENCE = "the reference";

//  /**
//   * To test the happy path when processing an ActionRequest for Census.
//   *
//   * @throws CTPException when notifyService.process does
//   * @throws NotificationClientException when censusNotificationClient does
//   */
//  @Test
//  public void testProcessActionRequestHappyPath() throws CTPException, NotificationClientException {
//    Mockito.when(censusNotificationClient.sendSms(any(String.class), any(String.class),
//            any(HashMap.class),any(String.class))).thenReturn(buildSendSmsResponse());
//    Mockito.when(censusNotificationClient.getNotificationById(any(String.class))).thenReturn(buildNotificationForSMS());
//
//    ActionFeedback result = notifyService.process(ObjectBuilder.buildActionRequest(ACTION_ID, FORENAME, SURNAME,
//            PHONENUMBER, true));
//    assertEquals(ACTION_ID, result.getActionId());
//    assertEquals(NOTIFY_SMS_SENT, result.getSituation());
//    assertEquals(Outcome.REQUEST_COMPLETED, result.getOutcome());
//
//    HashMap<String, String> personalisation = new HashMap<>();
//    personalisation.put(IAC_KEY, IAC_AS_DISPLAYED_IN_SMS);
//    verify(censusNotificationClient, times(1)).sendSms(any(String.class), eq(PHONENUMBER),
//            eq(personalisation), any(String.class));
//  }

  /**
   * To test the happy path when processing a NotifyRequest (SMS scenario)
   *
   * @throws NotificationClientException when censusNotificationClient does
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
            .withPersonalisation(String.format(PERSONALISATION_FORMAT_FROM_ORIKA, IAC_KEY, IAC_VALUE, OTHER_FIELD_KEY,
                    OTHER_FIELD_VALUE))
            .build());

    HashMap<String, String> personalisation = new HashMap<>();
    personalisation.put(IAC_KEY, IAC_VALUE);
    personalisation.put(OTHER_FIELD_KEY, OTHER_FIELD_VALUE);
    verify(notificationClient, times(1)).sendSms(any(String.class), eq(VALID_PHONE_NUMBER),
            eq(personalisation), eq(MESSAGE_REFERENCE));
  }

  /**
   * To test the happy path when processing a NotifyRequest (Email scenario)
   *
   * @throws NotificationClientException when censusNotificationClient does
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
            .withPersonalisation(String.format(PERSONALISATION_FORMAT_FROM_ORIKA, IAC_KEY, IAC_VALUE, OTHER_FIELD_KEY,
                    OTHER_FIELD_VALUE))
            .build());

    HashMap<String, String> personalisation = new HashMap<>();
    personalisation.put(IAC_KEY, IAC_VALUE);
    personalisation.put(OTHER_FIELD_KEY, OTHER_FIELD_VALUE);
    verify(notificationClient, times(1)).sendEmail(any(String.class), eq(VALID_EMAIL_ADDRESS),
            eq(personalisation), eq(MESSAGE_REFERENCE));
  }

  /**
   * Happy path with string provided by orika as expected
   * {iac=ABCD-EFGH-IJKL-MNOP, otherfield=other,value}
   *
   * @throws Exception thrown if Java Reflexion issues
   */
  @Test
  public void testBuildMapFromStringHappyPath() throws Exception {
    Method methodUndertest = NotifyServiceImpl.class.getDeclaredMethod(PRIVATE_METHOD_NAME, String.class);
    methodUndertest.setAccessible(true);
    Map<String, String> result = (Map<String, String>) methodUndertest.invoke(notifyService,
            String.format(PERSONALISATION_FORMAT_FROM_ORIKA, IAC_KEY, IAC_VALUE, OTHER_FIELD_KEY, OTHER_FIELD_VALUE));
    assertEquals(2, result.size());
    assertEquals(IAC_VALUE, result.get(IAC_KEY));
    assertEquals(OTHER_FIELD_VALUE, result.get(OTHER_FIELD_KEY));
  }

  /**
   * string provided is null
   *
   * @throws Exception thrown if Java Reflexion issues
   */
  @Test
  public void testBuildMapFromStringNullString() throws Exception {
    Method methodUndertest = NotifyServiceImpl.class.getDeclaredMethod(PRIVATE_METHOD_NAME, String.class);
    methodUndertest.setAccessible(true);
    String parameter = null;
    Map<String, String> result = (Map<String, String>) methodUndertest.invoke(notifyService, parameter);
    assertNull(result);
  }

  /**
   * string provided is empty
   *
   * @throws Exception thrown if Java Reflexion issues
   */
  @Test
  public void testBuildMapFromStringEmptyString() throws Exception {
    Method methodUndertest = NotifyServiceImpl.class.getDeclaredMethod(PRIVATE_METHOD_NAME, String.class);
    methodUndertest.setAccessible(true);
    String parameter = "";
    Map<String, String> result = (Map<String, String>) methodUndertest.invoke(notifyService, parameter);
    assertNull(result);
  }

  /**
   * string provided is in an unexpected format
   *
   * @throws Exception thrown if Java Reflexion issues
   */
  @Test
  public void testBuildMapFromStringUnexpectedFormat() throws Exception {
    Method methodUndertest = NotifyServiceImpl.class.getDeclaredMethod(PRIVATE_METHOD_NAME, String.class);
    methodUndertest.setAccessible(true);
    String parameter = "something";
    Map<String, String> result = (Map<String, String>) methodUndertest.invoke(notifyService, parameter);
    assertNull(result);
  }
}
