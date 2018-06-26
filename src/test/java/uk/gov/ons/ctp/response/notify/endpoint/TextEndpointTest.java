package uk.gov.ons.ctp.response.notify.endpoint;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.ons.ctp.common.MvcHelper.postJson;
import static uk.gov.ons.ctp.common.error.RestExceptionHandler.PROVIDED_JSON_INCORRECT;
import static uk.gov.ons.ctp.common.utility.MockMvcControllerAdviceHelper.mockAdviceFor;

import java.util.UUID;
import ma.glasnost.orika.MapperFacade;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
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
import uk.gov.ons.ctp.response.notify.domain.Response;
import uk.gov.ons.ctp.response.notify.message.notify.NotifyRequest;
import uk.gov.ons.ctp.response.notify.service.ResilienceService;

public class TextEndpointTest {

  @InjectMocks private TextEndpoint textEndpoint;

  @Mock private ResilienceService resilienceService;

  @Spy private MapperFacade mapperFacade = new NotifySvcBeanMapper();

  private MockMvc mockMvc;

  private static final String SEND_TEXT_MSG = "sendSMS";
  private static final String TEMPLATE_ID = "f3778220-f877-4a3d-80ed-e8fa7d104563";

  private static final String INVALID_JSON = "{\"some\":\"text\"}";

  private static final String BAD_PHONE_NUMBER = "01234";
  private static final String VALID_PHONE_NUMBER = "01234567890";
  private static final String MESSAGE_REFERENCE = "the reference";
  private static final String JSON_SKELETON = "{\"phoneNumber\":\"%s\", \"reference\":\"%s\"}";
  private static final String VALID_JSON_BAD_PHONE_NUMBER =
      String.format(JSON_SKELETON, BAD_PHONE_NUMBER, MESSAGE_REFERENCE);
  private static final String VALID_JSON_VALID_PHONE_NUMBER =
      String.format(JSON_SKELETON, VALID_PHONE_NUMBER, MESSAGE_REFERENCE);

  private static final UUID MESSAGE_ID = UUID.fromString("de0da3c1-2cad-421a-bddd-054ef374c6ab");

  /**
   * Set up of tests
   *
   * @throws Exception exception thrown
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    this.mockMvc =
        MockMvcBuilders.standaloneSetup(textEndpoint)
            .setHandlerExceptionResolvers(mockAdviceFor(RestExceptionHandler.class))
            .setMessageConverters(new MappingJackson2HttpMessageConverter(new CustomObjectMapper()))
            .build();
  }

  /**
   * a test providing bad json
   *
   * @throws Exception if the postJson fails
   */
  @Test
  public void textInvalidJson() throws Exception {
    ResultActions actions =
        mockMvc.perform(postJson(String.format("/texts/%s", TEMPLATE_ID), INVALID_JSON));

    actions
        .andExpect(status().isBadRequest())
        .andExpect(handler().handlerType(TextEndpoint.class))
        .andExpect(handler().methodName(SEND_TEXT_MSG))
        .andExpect(jsonPath("$.error.code", is(CTPException.Fault.VALIDATION_FAILED.name())))
        .andExpect(jsonPath("$.error.message", is(PROVIDED_JSON_INCORRECT)))
        .andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  /**
   * a test providing correct json but invalid phone number
   *
   * @throws Exception if the postJson fails
   */
  @Test
  public void textInvalidPhoneNumber() throws Exception {
    ResultActions actions =
        mockMvc.perform(
            postJson(String.format("/texts/%s", TEMPLATE_ID), VALID_JSON_BAD_PHONE_NUMBER));

    actions
        .andExpect(status().isBadRequest())
        .andExpect(handler().handlerType(TextEndpoint.class))
        .andExpect(handler().methodName(SEND_TEXT_MSG))
        .andExpect(jsonPath("$.error.code", is(CTPException.Fault.VALIDATION_FAILED.name())))
        .andExpect(jsonPath("$.error.message", is(RestExceptionHandler.INVALID_JSON)))
        .andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  /**
   * a test providing correct json and valid phone number
   *
   * @throws Exception if the postJson fails
   */
  @Test
  public void textHappyPath() throws Exception {
    Response response =
        Response.builder()
            .id(MESSAGE_ID)
            .reference(MESSAGE_REFERENCE)
            .templateId(UUID.fromString(TEMPLATE_ID))
            .fromNumber(VALID_PHONE_NUMBER)
            .build();
    when(resilienceService.process(any(NotifyRequest.class))).thenReturn(response);

    ResultActions actions =
        mockMvc.perform(
            postJson(String.format("/texts/%s", TEMPLATE_ID), VALID_JSON_VALID_PHONE_NUMBER));

    actions
        .andExpect(status().is2xxSuccessful())
        .andExpect(handler().handlerType(TextEndpoint.class))
        .andExpect(handler().methodName(SEND_TEXT_MSG))
        .andExpect(jsonPath("$.*", Matchers.hasSize(4)))
        .andExpect(jsonPath("$.id", CoreMatchers.is(MESSAGE_ID.toString())))
        .andExpect(jsonPath("$.reference", CoreMatchers.is(MESSAGE_REFERENCE)))
        .andExpect(jsonPath("$.templateId", CoreMatchers.is(TEMPLATE_ID.toString())))
        .andExpect(jsonPath("$.fromNumber", CoreMatchers.is(VALID_PHONE_NUMBER)));
  }
}
