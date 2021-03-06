package uk.gov.ons.ctp.response.notify.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.ons.ctp.response.notify.service.NotifyService.FIRSTNAME_KEY;
import static uk.gov.ons.ctp.response.notify.service.NotifyService.LASTNAME_KEY;
import static uk.gov.ons.ctp.response.notify.service.NotifyService.NOTIFY_EMAIL_SENT;
import static uk.gov.ons.ctp.response.notify.service.NotifyService.REPORTING_UNIT_REF_KEY;
import static uk.gov.ons.ctp.response.notify.service.NotifyService.RESPONDENT_PERIOD_KEY;
import static uk.gov.ons.ctp.response.notify.service.NotifyService.RETURN_BY_DATE_KEY;
import static uk.gov.ons.ctp.response.notify.service.NotifyService.RU_NAME_KEY;
import static uk.gov.ons.ctp.response.notify.service.NotifyService.SURVEY_ID_KEY;
import static uk.gov.ons.ctp.response.notify.service.NotifyService.SURVEY_NAME_KEY;
import static uk.gov.ons.ctp.response.notify.service.NotifyService.TRADING_STYLE_KEY;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.ACTION_ID;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.EMAIL_ADDRESS;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.FORENAME;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.INVALID_EMAIL_ADDRESS;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.NOTIFICATION_ID;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.RESPONDENT_PERIOD;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.RETURN_BY_DATE;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.RU_NAME;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.SAMPLE_UNIT_REF;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.SURNAME;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.SURVEY_NAME;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.SURVEY_REF;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.TRADING_STYLE;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.buildCommsTemplateDTO;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.buildNotificationForEmail;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.buildNotificationForSMS;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.buildSendEmailResponse;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.buildSendSmsResponse;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.action.message.feedback.Outcome;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.notify.client.CommsTemplateClientException;
import uk.gov.ons.ctp.response.notify.client.LoggingNotificationClient;
import uk.gov.ons.ctp.response.notify.config.NotifyConfiguration;
import uk.gov.ons.ctp.response.notify.lib.common.CTPException;
import uk.gov.ons.ctp.response.notify.lib.notify.NotifyRequest;
import uk.gov.ons.ctp.response.notify.utility.ObjectBuilder;
import uk.gov.service.notify.NotificationClientException;

/** To unit test NotifyServiceImpl */
@RunWith(MockitoJUnitRunner.class)
public class NotifyServiceTest {

  @InjectMocks private NotifyService notifyService;

  @Mock private LoggingNotificationClient notificationClient;

  @Mock private CommsTemplateClient commsTemplateClient;

  @Mock private NotifyConfiguration notifyConfiguration;

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
   * To test the happy path when processing an ActionRequest for Email. Note phonenumber is at null.
   *
   * @throws CTPException when notifyService.process does
   * @throws NotificationClientException when censusNotificationClient does
   */
  @Test
  public void testProcessActionRequestHappyPathEmail()
      throws NotificationClientException, CommsTemplateClientException {
    Mockito.when(
            notificationClient.sendEmail(
                any(String.class), any(String.class), any(HashMap.class), any(String.class)))
        .thenReturn(buildSendEmailResponse());
    Mockito.when(notificationClient.getNotificationById(any(String.class)))
        .thenReturn(buildNotificationForEmail());
    Mockito.when(commsTemplateClient.getCommsTemplateByClassifiers(anyObject()))
        .thenReturn(buildCommsTemplateDTO());

    ActionFeedback result =
        notifyService.process(
            ObjectBuilder.buildActionRequest(
                ACTION_ID, FORENAME, SURNAME, null, EMAIL_ADDRESS, true));
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
    personalisation.put(RESPONDENT_PERIOD_KEY, RESPONDENT_PERIOD);
    verify(notificationClient, times(1))
        .sendEmail(any(String.class), eq(EMAIL_ADDRESS), eq(personalisation), any(String.class));
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
  public void testProcessActionRequestErrorPathEmail()
      throws NotificationClientException, CommsTemplateClientException {
    Mockito.when(
            notificationClient.sendEmail(
                any(String.class), any(String.class), any(HashMap.class), any(String.class)))
        .thenThrow(new NotificationClientException(new Exception()));
    Mockito.when(commsTemplateClient.getCommsTemplateByClassifiers(any()))
        .thenReturn(buildCommsTemplateDTO());

    try {
      notifyService.process(
          ObjectBuilder.buildActionRequest(
              ACTION_ID, FORENAME, SURNAME, null, INVALID_EMAIL_ADDRESS, true));
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
    personalisation.put(RESPONDENT_PERIOD_KEY, RESPONDENT_PERIOD);
    verify(notificationClient, times(1))
        .sendEmail(
            any(String.class), eq(INVALID_EMAIL_ADDRESS), eq(personalisation), any(String.class));
    verify(notificationClient, never()).getNotificationById(any(String.class));
  }

  /**
   * To test the happy path when processing a NotifyRequest (Email scenario)
   *
   * @throws NotificationClientException when censusNotificationClient does
   */
  @Test
  public void testProcessNotifyRequestHappyPathEmail() throws NotificationClientException {
    Mockito.when(
            notificationClient.sendEmail(
                any(String.class), any(String.class), any(HashMap.class), any(String.class)))
        .thenReturn(buildSendEmailResponse());
    Mockito.when(notificationClient.getNotificationById(any(String.class)))
        .thenReturn(buildNotificationForSMS());

    notifyService.process(
        NotifyRequest.builder()
            .withId(NOTIFY_REQUEST_ID)
            .withTemplateId(TEMPLATE_ID)
            .withReference(MESSAGE_REFERENCE)
            .withEmailAddress(VALID_EMAIL_ADDRESS)
            .withPersonalisation(
                String.format(
                    PERSONALISATION_FORMAT_FROM_ORIKA,
                    IAC_KEY,
                    IAC_VALUE,
                    OTHER_FIELD_KEY,
                    OTHER_FIELD_VALUE))
            .build());

    HashMap<String, String> personalisation = new HashMap<>();
    personalisation.put(IAC_KEY, IAC_VALUE);
    personalisation.put(OTHER_FIELD_KEY, OTHER_FIELD_VALUE);
    verify(notificationClient, times(1))
        .sendEmail(
            any(String.class), eq(VALID_EMAIL_ADDRESS), eq(personalisation), eq(MESSAGE_REFERENCE));
  }

  /**
   * Happy path with string provided by orika as expected {iac=ABCD-EFGH-IJKL-MNOP,
   * otherfield=other,value}
   *
   * @throws Exception thrown if Java Reflexion issues
   */
  @Test
  public void testBuildMapFromStringHappyPath() throws Exception {
    Method methodUndertest =
        NotifyService.class.getDeclaredMethod(PRIVATE_METHOD_NAME, String.class);
    methodUndertest.setAccessible(true);
    Map<String, String> result =
        (Map<String, String>)
            methodUndertest.invoke(
                notifyService,
                String.format(
                    PERSONALISATION_FORMAT_FROM_ORIKA,
                    IAC_KEY,
                    IAC_VALUE,
                    OTHER_FIELD_KEY,
                    OTHER_FIELD_VALUE));
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
    Method methodUndertest =
        NotifyService.class.getDeclaredMethod(PRIVATE_METHOD_NAME, String.class);
    methodUndertest.setAccessible(true);
    String parameter = null;
    Map<String, String> result =
        (Map<String, String>) methodUndertest.invoke(notifyService, parameter);
    assertNull(result);
  }

  /**
   * string provided is empty
   *
   * @throws Exception thrown if Java Reflexion issues
   */
  @Test
  public void testBuildMapFromStringEmptyString() throws Exception {
    Method methodUndertest =
        NotifyService.class.getDeclaredMethod(PRIVATE_METHOD_NAME, String.class);
    methodUndertest.setAccessible(true);
    String parameter = "";
    Map<String, String> result =
        (Map<String, String>) methodUndertest.invoke(notifyService, parameter);
    assertNull(result);
  }

  /**
   * string provided is in an unexpected format
   *
   * @throws Exception thrown if Java Reflexion issues
   */
  @Test
  public void testBuildMapFromStringUnexpectedFormat() throws Exception {
    Method methodUndertest =
        NotifyService.class.getDeclaredMethod(PRIVATE_METHOD_NAME, String.class);
    methodUndertest.setAccessible(true);
    String parameter = "something";
    Map<String, String> result =
        (Map<String, String>) methodUndertest.invoke(notifyService, parameter);
    assertNull(result);
  }

  @Test
  public void testGetClassifiersMapNotification() {
    ActionRequest actionRequest = createActionRequest("BSNE", "YY", "Statistics of Trade Act 1947", "");
    MultiValueMap<String, String> expectedClassifierMap =
        getExpectedClassifiersMap("NOTIFICATION", "YY", "Statistics of Trade Act 1947", "");
    MultiValueMap<String, String> actualClassifiersMap =
        notifyService.getClassifiers(actionRequest);
    assertTrue(EqualsBuilder.reflectionEquals(expectedClassifierMap, actualClassifiersMap));
  }

  @Test
  public void testGetClassifiersMapReminder() {
    ActionRequest actionRequest = createActionRequest("BSRE", "WW", "Statistics of Trade Act 1947", "");
    MultiValueMap<String, String> expectedClassifierMap =
        getExpectedClassifiersMap("REMINDER", "WW", "Statistics of Trade Act 1947", "");
    MultiValueMap<String, String> actualClassifiersMap =
        notifyService.getClassifiers(actionRequest);
    assertTrue(EqualsBuilder.reflectionEquals(expectedClassifierMap, actualClassifiersMap));
  }

  @Test
  public void testGetClassifiersMapNotificationWithCovidSurveyRef() {
    ActionRequest actionRequest = createActionRequest("BSNE", "", "Voluntary Not Stated", "283");
    MultiValueMap<String, String> expectedClassifierMap =
            getExpectedClassifiersMap("NOTIFICATION", "", "Voluntary Not Stated", "283");
    MultiValueMap<String, String> actualClassifiersMap =
            notifyService.getClassifiers(actionRequest);
    assertTrue(EqualsBuilder.reflectionEquals(expectedClassifierMap, actualClassifiersMap));
  }

  @Test
  public void testGetClassifiersMapReminderWithCovidSurveyRef() {
    ActionRequest actionRequest = createActionRequest("BSRE", "", "Voluntary Not Stated", "283");
    MultiValueMap<String, String> expectedClassifierMap =
            getExpectedClassifiersMap("REMINDER", "", "Voluntary Not Stated", "283");
    MultiValueMap<String, String> actualClassifiersMap =
            notifyService.getClassifiers(actionRequest);
    assertTrue(EqualsBuilder.reflectionEquals(expectedClassifierMap, actualClassifiersMap));
  }

  @Test
  public void testGetClassifiersMapForNugeEmail() {
    ActionRequest actionRequest = createActionRequest("BSNUE", "", "Voluntary Not Stated", "283");
    MultiValueMap<String, String> expectedClassifierMap =
            getExpectedClassifiersMap("NUDGE", "", "", "");
    MultiValueMap<String, String> actualClassifiersMap =
            notifyService.getClassifiers(actionRequest);
    assertTrue(EqualsBuilder.reflectionEquals(expectedClassifierMap, actualClassifiersMap));
  }

  private ActionRequest createActionRequest(String actionType, String region, String legalBasis, String surveyRef) {
    ActionRequest actionRequest = new ActionRequest();
    actionRequest.setActionType(actionType);
    actionRequest.setLegalBasis(legalBasis);

    if (!region.isEmpty()) {
      actionRequest.setRegion(region);
    }

    if (!surveyRef.isEmpty()) {
      actionRequest.setSurveyRef(surveyRef);
    }
    return actionRequest;
  }

  private MultiValueMap<String, String> getExpectedClassifiersMap(
      final String notificationType, final String region, final String legalBasis, final String surveyRef) {
    MultiValueMap<String, String> classifierMap = new LinkedMultiValueMap<>();

    if (!notificationType.isEmpty()) {
      List<String> communicationType = new ArrayList<>();
      communicationType.add(notificationType);
      classifierMap.put("COMMUNICATION_TYPE", communicationType);
    }

    if (!region.isEmpty()) {
      List<String> regionClassifier = new ArrayList<>();
      regionClassifier.add(region);
      classifierMap.put("REGION", regionClassifier);
    }

    if (!legalBasis.isEmpty()) {
      List<String> legalBasisClassifier = new ArrayList<>();
      legalBasisClassifier.add(legalBasis);
      classifierMap.put("LEGAL_BASIS", legalBasisClassifier);
    }

    if (!surveyRef.isEmpty()){
      List<String> surveyClassifier = new ArrayList<>();
      surveyClassifier.add(surveyRef);
      classifierMap.put("SURVEY", surveyClassifier);
    }

    return classifierMap;
  }
}
