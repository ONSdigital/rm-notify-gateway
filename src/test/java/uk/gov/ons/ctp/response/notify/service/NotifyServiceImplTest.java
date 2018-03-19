package uk.gov.ons.ctp.response.notify.service;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.action.message.feedback.Outcome;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.notify.client.CommsTemplateClientException;
import uk.gov.ons.ctp.response.notify.config.NotifyConfiguration;
import uk.gov.ons.ctp.response.notify.message.notify.NotifyRequest;
import uk.gov.ons.ctp.response.notify.service.impl.NotifyServiceImpl;
import uk.gov.ons.ctp.response.notify.utility.ObjectBuilder;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import javax.validation.constraints.AssertTrue;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
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
  private CommsTemplateClient commsTemplateClient;

  @Mock
  private NotifyConfiguration notifyConfiguration;

  private static final String EXCEPTION_MSG = "java.lang.Exception";
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

  /**
   * To test the happy path when processing an ActionRequest for SMS. Note emailAddress is at null.
   *
   * @throws CTPException when notifyService.process does
   * @throws NotificationClientException when censusNotificationClient does
   */
  @Test
  public void testProcessActionRequestHappyPathSMS() throws NotificationClientException, CommsTemplateClientException {
    Mockito.when(notificationClient.sendSms(any(String.class), any(String.class),
            any(HashMap.class),any(String.class))).thenReturn(buildSendSmsResponse());
    Mockito.when(notificationClient.getNotificationById(any(String.class))).thenReturn(buildNotificationForSMS());
    Mockito.when(commsTemplateClient.getCommsTemplateByClassifiers(anyObject())).thenReturn(buildCommsTemplateDTO());

    ActionFeedback result = notifyService.process(ObjectBuilder.buildActionRequest(ACTION_ID, FORENAME, SURNAME,
            PHONENUMBER, null, true));
    assertEquals(ACTION_ID, result.getActionId());
    assertEquals(NOTIFY_SMS_SENT, result.getSituation());
    assertEquals(Outcome.REQUEST_COMPLETED, result.getOutcome());

    HashMap<String, String> personalisation = new HashMap<>();
    personalisation.put(IAC_KEY, IAC_AS_DISPLAYED_IN_SMS);
    verify(notificationClient, times(1)).sendSms(any(String.class), eq(PHONENUMBER),
            eq(personalisation), any(String.class));
    verify(notificationClient, times(1)).getNotificationById(eq(NOTIFICATION_ID));
    verify(commsTemplateClient, times(1)).getCommsTemplateByClassifiers(anyObject());
  }

  /**
   * To test the error path when processing an ActionRequest for SMS. Note emailAddress is at null.
   *
   * @throws CTPException when notifyService.process does
   * @throws NotificationClientException when censusNotificationClient does
   */
  @Test
  public void testProcessActionRequestErrorPathSMS() throws NotificationClientException, CommsTemplateClientException {
    Mockito.when(notificationClient.sendSms(any(String.class), any(String.class),
        any(HashMap.class),any(String.class))).thenThrow(new NotificationClientException(new Exception()));
    Mockito.when(commsTemplateClient.getCommsTemplateByClassifiers(anyObject())).thenReturn(buildCommsTemplateDTO());

    try {
      notifyService.process(ObjectBuilder.buildActionRequest(ACTION_ID, FORENAME, SURNAME,
          INVALID_PHONENUMBER, null, true));
      fail();
    } catch (NotificationClientException e) {
      assertEquals(EXCEPTION_MSG, e.getMessage());

    }

    HashMap<String, String> personalisation = new HashMap<>();
    personalisation.put(IAC_KEY, IAC_AS_DISPLAYED_IN_SMS);
    verify(notificationClient, times(1)).sendSms(any(String.class), eq(INVALID_PHONENUMBER),
        eq(personalisation), any(String.class));
    verify(notificationClient, never()).getNotificationById(any(String.class));
    verify(commsTemplateClient, times(1)).getCommsTemplateByClassifiers(anyObject());
  }

  /**
   * To test the happy path when processing an ActionRequest for Email. Note phonenumber is at null.
   *
   * @throws CTPException when notifyService.process does
   * @throws NotificationClientException when censusNotificationClient does
   */
  @Test
  public void testProcessActionRequestHappyPathEmail() throws NotificationClientException,
          CommsTemplateClientException {
    Mockito.when(notificationClient.sendEmail(any(String.class), any(String.class),
        any(HashMap.class),any(String.class))).thenReturn(buildSendEmailResponse());
    Mockito.when(notificationClient.getNotificationById(any(String.class))).thenReturn(buildNotificationForEmail());
    Mockito.when(commsTemplateClient.getCommsTemplateByClassifiers(anyObject())).thenReturn(buildCommsTemplateDTO());

    ActionFeedback result = notifyService.process(ObjectBuilder.buildActionRequest(ACTION_ID, FORENAME, SURNAME,
        null, EMAIL_ADDRESS, true));
    assertEquals(ACTION_ID, result.getActionId());
    assertEquals(NOTIFY_EMAIL_SENT, result.getSituation());
    assertEquals(Outcome.REQUEST_COMPLETED, result.getOutcome());

    HashMap<String, String> personalisation = new HashMap<>();
    personalisation.put(REPORTING_UNIT_REF_KEY, SAMPLE_UNIT_REF);
    personalisation.put(SURVEY_NAME_KEY, SURVEY_NAME);
    personalisation.put(SURVEY_ID_KEY, SURVEY_REF);
    personalisation.put(FIRSTNAME_KEY, FORENAME);
    personalisation.put(LASTNAME_KEY, SURNAME);
    personalisation.put(RU_NAME_KEY, RU_NAME);
    personalisation.put(TRADING_STYLE_KEY, TRADING_STYLE);
    personalisation.put(RETURN_BY_DATE_KEY, RETURN_BY_DATE);
    verify(notificationClient, times(1)).sendEmail(any(String.class), eq(EMAIL_ADDRESS),
        eq(personalisation), any(String.class));
    verify(notificationClient, times(1)).getNotificationById(eq(NOTIFICATION_ID));
    verify(commsTemplateClient, times(1)).getCommsTemplateByClassifiers(anyObject());
  }

  /**
   * To test the error path when processing an ActionRequest for Email. Note phonenumber is at null.
   *
   * @throws CTPException when notifyService.process does
   * @throws NotificationClientException when censusNotificationClient does
   */
  @Test
  public void testProcessActionRequestErrorPathEmail() throws NotificationClientException,
          CommsTemplateClientException {
    Mockito.when(notificationClient.sendEmail(any(String.class), any(String.class),
        any(HashMap.class),any(String.class))).thenThrow(new NotificationClientException(new Exception()));
    Mockito.when(commsTemplateClient.getCommsTemplateByClassifiers(any())).thenReturn(buildCommsTemplateDTO());

    try {
      notifyService.process(ObjectBuilder.buildActionRequest(ACTION_ID, FORENAME, SURNAME,
          null, INVALID_EMAIL_ADDRESS, true));
      fail();
    } catch (NotificationClientException e) {
      assertEquals(EXCEPTION_MSG, e.getMessage());

    }

    HashMap<String, String> personalisation = new HashMap<>();
    personalisation.put(REPORTING_UNIT_REF_KEY, SAMPLE_UNIT_REF);
    personalisation.put(SURVEY_NAME_KEY, SURVEY_NAME);
    personalisation.put(SURVEY_ID_KEY, SURVEY_REF);
    personalisation.put(FIRSTNAME_KEY, FORENAME);
    personalisation.put(LASTNAME_KEY, SURNAME);
    personalisation.put(RU_NAME_KEY, RU_NAME);
    personalisation.put(TRADING_STYLE_KEY, TRADING_STYLE);
    personalisation.put(RETURN_BY_DATE_KEY, RETURN_BY_DATE);
    verify(notificationClient, times(1)).sendEmail(any(String.class), eq(INVALID_EMAIL_ADDRESS),
        eq(personalisation), any(String.class));
    verify(notificationClient, never()).getNotificationById(any(String.class));
  }

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

  @Test
  public void testGetClassifiersMapNotification() {
    ActionRequest actionRequest = createActionRequest("BSNE", "YY", "Statistics of Trade Act 1947");
    MultiValueMap<String, String> expectedClassifierMap = getExpectedClassifiersMap("NOTIFICATION", "YY", "Statistics of Trade Act 1947");
    MultiValueMap<String, String> actualClassifiersMap = notifyService.getClassifiers(actionRequest);
    assertTrue(EqualsBuilder.reflectionEquals(expectedClassifierMap, actualClassifiersMap));
  }

  @Test
  public void testGetClassifiersMapReminder() {
    ActionRequest actionRequest = createActionRequest("BSRE", "WW", "Statistics of Trade Act 1947");
    MultiValueMap<String, String> expectedClassifierMap = getExpectedClassifiersMap("REMINDER", "WW", "Statistics of Trade Act 1947");
    MultiValueMap<String, String> actualClassifiersMap = notifyService.getClassifiers(actionRequest);
    assertTrue(EqualsBuilder.reflectionEquals(expectedClassifierMap, actualClassifiersMap));
  }

  private ActionRequest createActionRequest(String actionType, String region, String legalBasis) {
    ActionRequest actionRequest = new ActionRequest();
    actionRequest.setActionType(actionType);
    actionRequest.setRegion(region);
    actionRequest.setLegalBasis(legalBasis);
    return actionRequest;
  }

  private MultiValueMap<String, String> getExpectedClassifiersMap(final String notificationType, final String region, final String legalBasis) {
    MultiValueMap<String, String> classifierMap = new LinkedMultiValueMap<>();

    List<String> communicationType = new ArrayList<>();
    communicationType.add(notificationType);
    classifierMap.put("COMMUNICATION_TYPE", communicationType);

    List<String> regionClassifier = new ArrayList<>();
    regionClassifier.add(region);
    classifierMap.put("REGION", regionClassifier);

    List<String> legalBasisClassifier = new ArrayList<>();
    legalBasisClassifier.add(legalBasis);
    classifierMap.put("LEGAL_BASIS", legalBasisClassifier);

    return classifierMap;

  }
}
