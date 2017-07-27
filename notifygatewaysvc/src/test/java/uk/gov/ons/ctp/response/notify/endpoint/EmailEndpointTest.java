package uk.gov.ons.ctp.response.notify.endpoint;

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

import java.util.UUID;

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

public class EmailEndpointTest {

    @InjectMocks
    private EmailEndpoint emailEndpoint;

    @Mock
    private ResilienceService resilienceService;

    @Spy
    private MapperFacade mapperFacade = new NotifySvcBeanMapper();

    private MockMvc mockMvc;

    private static final String SEND_EMAIL_MSG = "sendEmail";
    private static final String TEMPLATE_ID = "f3778220-f877-4a3d-80ed-e8fa7d104563";

    private static final String INVALID_JSON = "{\"some\":\"text\"}";

    private static final String BAD_EMAIL_ADDRESS = "something";
    private static final String VALID_EMAIL_ADDRESS = "tester@ons.gov.uk";
    private static final String MESSAGE_REFERENCE = "the reference";
    private static final String JSON_SKELETON = "{\"emailAddress\":\"%s\", \"reference\":\"%s\"}";
    private static final String VALID_JSON_BAD_EMAIL_ADDRESS = String.format(JSON_SKELETON, BAD_EMAIL_ADDRESS,
            MESSAGE_REFERENCE);
    private static final String VALID_JSON_VALID_EMAIL_ADDRESS = String.format(JSON_SKELETON, VALID_EMAIL_ADDRESS,
            MESSAGE_REFERENCE);

    private static final UUID MESSAGE_ID = UUID.fromString("de0da3c1-2cad-421a-bddd-054ef374c6ab");

    /**
     * Set up of tests
     * @throws Exception exception thrown
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(emailEndpoint)
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
        ResultActions actions = mockMvc.perform(postJson(String.format("/emails/%s", TEMPLATE_ID), INVALID_JSON));

        actions.andExpect(status().isBadRequest())
                .andExpect(handler().handlerType(EmailEndpoint.class))
                .andExpect(handler().methodName(SEND_EMAIL_MSG))
                .andExpect(jsonPath("$.error.code", is(CTPException.Fault.VALIDATION_FAILED.name())))
                .andExpect(jsonPath("$.error.message", is(PROVIDED_JSON_INCORRECT)))
                .andExpect(jsonPath("$.error.timestamp", isA(String.class)));
    }

    /**
     * a test providing correct json but invalid email address
     *
     * @throws Exception if the postJson fails
     */
    @Test
    public void textInvalidEmailAddress() throws Exception {
        ResultActions actions = mockMvc.perform(postJson(String.format("/emails/%s", TEMPLATE_ID),
                VALID_JSON_BAD_EMAIL_ADDRESS));

        actions.andExpect(status().isBadRequest())
                .andExpect(handler().handlerType(EmailEndpoint.class))
                .andExpect(handler().methodName(SEND_EMAIL_MSG))
                .andExpect(jsonPath("$.error.code", is(CTPException.Fault.VALIDATION_FAILED.name())))
                .andExpect(jsonPath("$.error.message", is(RestExceptionHandler.INVALID_JSON)))
                .andExpect(jsonPath("$.error.timestamp", isA(String.class)));
    }

    /**
     * a test providing correct json and valid email address
     *
     * @throws Exception if the postJson fails
     */
    @Test
    public void textHappyPath() throws Exception {
        Response response = Response.builder()
                .id(MESSAGE_ID)
                .reference(MESSAGE_REFERENCE)
                .templateId(UUID.fromString(TEMPLATE_ID))
                .fromEmail(VALID_EMAIL_ADDRESS)
                .build();
        when(resilienceService.process(any(NotifyRequest.class))).thenReturn(response);

        ResultActions actions = mockMvc.perform(postJson(String.format("/emails/%s", TEMPLATE_ID),
                VALID_JSON_VALID_EMAIL_ADDRESS));

        actions.andExpect(status().is2xxSuccessful())
                .andExpect(handler().handlerType(EmailEndpoint.class))
                .andExpect(handler().methodName(SEND_EMAIL_MSG))
                .andExpect(jsonPath("$.*", Matchers.hasSize(5)))
                .andExpect(jsonPath("$.id", CoreMatchers.is(MESSAGE_ID.toString())))
                .andExpect(jsonPath("$.reference", CoreMatchers.is(MESSAGE_REFERENCE)))
                .andExpect(jsonPath("$.templateId", CoreMatchers.is(TEMPLATE_ID.toString())))
                .andExpect(jsonPath("$.fromEmail", CoreMatchers.is(VALID_EMAIL_ADDRESS)));
    }
}
