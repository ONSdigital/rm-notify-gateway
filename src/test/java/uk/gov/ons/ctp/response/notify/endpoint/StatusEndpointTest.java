package uk.gov.ons.ctp.response.notify.endpoint;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.ons.ctp.common.MvcHelper.getJson;
import static uk.gov.ons.ctp.common.utility.MockMvcControllerAdviceHelper.mockAdviceFor;
import static uk.gov.ons.ctp.response.notify.endpoint.StatusEndpoint.ERRORMSG_MESSAGE_NOTFOUND;
import static uk.gov.ons.ctp.response.notify.endpoint.StatusEndpoint.ERRORMSG_NOTIFICATION_ISSUE;
import static uk.gov.ons.ctp.response.notify.endpoint.StatusEndpoint.ERRORMSG_NOTIFICATION_NOTDEFINED;
import static uk.gov.ons.ctp.response.notify.endpoint.StatusEndpoint.ERRORMSG_NOTIFICATION_NOTFOUND;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.NOTIFICATION_ID;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.PHONENUMBER;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.REFERENCE;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.SENT;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.SMS;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.TEMPLATE_ID;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.buildNotificationForSMS;

import java.util.UUID;
import ma.glasnost.orika.MapperFacade;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.RestExceptionHandler;
import uk.gov.ons.ctp.common.jackson.CustomObjectMapper;
import uk.gov.ons.ctp.response.notify.NotifySvcBeanMapper;
import uk.gov.ons.ctp.response.notify.domain.model.Message;
import uk.gov.ons.ctp.response.notify.service.NotifyService;
import uk.gov.ons.ctp.response.notify.service.ResilienceService;
import uk.gov.service.notify.NotificationClientException;

public class StatusEndpointTest {

  @InjectMocks private StatusEndpoint statusEndpoint;

  @Mock private ResilienceService resilienceService;

  @Mock private NotifyService notifyService;

  @Spy private MapperFacade mapperFacade = new NotifySvcBeanMapper();

  private MockMvc mockMvc;

  private static final String CREATED_AT = "2004-12-14T05:39:45.618+0000";
  private static final String GET_STATUS = "getStatus";
  private static final String GENERAL_EXCEPTION = "java.lang.Exception";
  private static final String EXISTING_MSG_ID = "9bc9d99b-9999-99b9-ba99-99f9d9cf1111";
  private static final String NON_EXISTING_MSG_ID = "9bc9d99b-9999-99b9-ba99-99f9d9cf9999";

  /**
   * Set up of tests
   *
   * @throws Exception exception thrown
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    this.mockMvc =
        MockMvcBuilders.standaloneSetup(statusEndpoint)
            .setHandlerExceptionResolvers(mockAdviceFor(RestExceptionHandler.class))
            .setMessageConverters(new MappingJackson2HttpMessageConverter(new CustomObjectMapper()))
            .build();
  }

  /**
   * Scenario where no message is found in the database
   *
   * @throws Exception when getJson does
   */
  @Test
  public void getStatusMessageNotFound() throws Exception {
    ResultActions actions =
        mockMvc.perform(getJson(String.format("/messages/%s", NON_EXISTING_MSG_ID)));

    actions
        .andExpect(status().isNotFound())
        .andExpect(handler().handlerType(StatusEndpoint.class))
        .andExpect(handler().methodName(GET_STATUS))
        .andExpect(jsonPath("$.error.code", is(CTPException.Fault.RESOURCE_NOT_FOUND.name())))
        .andExpect(
            jsonPath(
                "$.error.message",
                is(String.format(ERRORMSG_MESSAGE_NOTFOUND, NON_EXISTING_MSG_ID))))
        .andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  /**
   * Scenario where message is found in the database but notificationId has not yet been populated
   *
   * @throws Exception when getJson does
   */
  @Test
  public void getStatusMessageFoundWithoutNotificationId() throws Exception {
    Message message = Message.builder().build();
    when(resilienceService.findMessageById(UUID.fromString(EXISTING_MSG_ID))).thenReturn(message);

    ResultActions actions =
        mockMvc.perform(getJson(String.format("/messages/%s", EXISTING_MSG_ID)));

    actions
        .andExpect(status().isNotFound())
        .andExpect(handler().handlerType(StatusEndpoint.class))
        .andExpect(handler().methodName(GET_STATUS))
        .andExpect(jsonPath("$.error.code", is(CTPException.Fault.RESOURCE_NOT_FOUND.name())))
        .andExpect(
            jsonPath(
                "$.error.message",
                is(String.format(ERRORMSG_NOTIFICATION_NOTDEFINED, EXISTING_MSG_ID))))
        .andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  /**
   * Scenario where message is found in the database but notification retrieved from GOV.UK Notify
   * is null
   *
   * @throws Exception when getJson does
   */
  @Test
  public void getStatusMessageFoundNotificationNull() throws Exception {
    UUID notificationId = UUID.fromString(NOTIFICATION_ID);
    Message message = Message.builder().notificationId(notificationId).build();
    when(resilienceService.findMessageById(UUID.fromString(EXISTING_MSG_ID))).thenReturn(message);
    when(notifyService.findNotificationById(eq(notificationId))).thenReturn(null);

    ResultActions actions =
        mockMvc.perform(getJson(String.format("/messages/%s", EXISTING_MSG_ID)));

    actions
        .andExpect(status().isNotFound())
        .andExpect(handler().handlerType(StatusEndpoint.class))
        .andExpect(handler().methodName(GET_STATUS))
        .andExpect(jsonPath("$.error.code", is(CTPException.Fault.RESOURCE_NOT_FOUND.name())))
        .andExpect(
            jsonPath(
                "$.error.message",
                is(String.format(ERRORMSG_NOTIFICATION_NOTFOUND, EXISTING_MSG_ID))))
        .andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  /**
   * Scenario where message is found in the database but notification retrieval from GOV.UK Notify
   * throws Exception
   *
   * @throws Exception when getJson does
   */
  @Test
  public void getStatusMessageFoundNotificationException() throws Exception {
    UUID notificationId = UUID.fromString(NOTIFICATION_ID);
    Message message = Message.builder().notificationId(notificationId).build();
    when(resilienceService.findMessageById(UUID.fromString(EXISTING_MSG_ID))).thenReturn(message);
    when(notifyService.findNotificationById(eq(notificationId)))
        .thenThrow(new NotificationClientException(new Exception()));

    ResultActions actions =
        mockMvc.perform(getJson(String.format("/messages/%s", EXISTING_MSG_ID)));

    actions
        .andExpect(status().is5xxServerError())
        .andExpect(handler().handlerType(StatusEndpoint.class))
        .andExpect(handler().methodName(GET_STATUS))
        .andExpect(jsonPath("$.error.code", is(CTPException.Fault.SYSTEM_ERROR.name())))
        .andExpect(
            jsonPath(
                "$.error.message",
                is(
                    String.format(
                        ERRORMSG_NOTIFICATION_ISSUE, GENERAL_EXCEPTION, GENERAL_EXCEPTION))))
        .andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  /**
   * Scenario where a message is found in the database and a notification is retrieved from GOV.UK
   * Notify
   *
   * @throws Exception when getJson does
   */
  @Test
  public void getStatusMessageFoundAndNotificationRetrieved() throws Exception {
    UUID notificationId = UUID.fromString(NOTIFICATION_ID);
    Message message = Message.builder().notificationId(notificationId).build();
    when(resilienceService.findMessageById(UUID.fromString(EXISTING_MSG_ID))).thenReturn(message);
    when(notifyService.findNotificationById(eq(notificationId)))
        .thenReturn(buildNotificationForSMS());

    ResultActions actions =
        mockMvc.perform(getJson(String.format("/messages/%s", EXISTING_MSG_ID)));

    actions
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(StatusEndpoint.class))
        .andExpect(handler().methodName(GET_STATUS))
        .andExpect(jsonPath("$.*", hasSize(11)))
        .andExpect(jsonPath("$.id", is(NOTIFICATION_ID)))
        .andExpect(jsonPath("$.reference", is(REFERENCE)))
        .andExpect(jsonPath("$.emailAddress", is(nullValue())))
        .andExpect(jsonPath("$.phoneNumber", is(PHONENUMBER)))
        .andExpect(jsonPath("$.notificationType", is(SMS)))
        .andExpect(jsonPath("$.status", is(SENT)))
        .andExpect(jsonPath("$.templateId", is(TEMPLATE_ID)))
        .andExpect(jsonPath("$.templateVersion", is(1)))
        .andExpect(jsonPath("$.createdAt", is(CREATED_AT)))
        .andExpect(jsonPath("$.sentAt", is(nullValue())))
        .andExpect(jsonPath("$.completedAt", is(nullValue())));
  }
}
