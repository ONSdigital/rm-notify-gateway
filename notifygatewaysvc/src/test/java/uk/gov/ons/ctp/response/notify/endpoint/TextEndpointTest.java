package uk.gov.ons.ctp.response.notify.endpoint;

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
import uk.gov.ons.ctp.response.notify.service.NotifyService;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.ons.ctp.common.error.RestExceptionHandler.PROVIDED_JSON_INCORRECT;
import static uk.gov.ons.ctp.common.MvcHelper.postJson;
import static uk.gov.ons.ctp.common.utility.MockMvcControllerAdviceHelper.mockAdviceFor;

public class TextEndpointTest {

    @InjectMocks
    private TextEndpoint textEndpoint;

    @Mock
    private NotifyService notifyService;

    @Spy
    private MapperFacade mapperFacade = new NotifySvcBeanMapper();

    private MockMvc mockMvc;

    private static final String SEND_TEXT_MSG = "sendTextMessage";
    private static final String TEMPLATE_ID = "f3778220-f877-4a3d-80ed-e8fa7d104563";

    private static final String INVALID_JSON = "{\"some\":\"text\"}";
    private static final String VALID_JSON_BAD_PHONE_NUMBER = "{\"phoneNumber\":\"01234\"}";
    private static final String VALID_JSON_VALID_PHONE_NUMBER = "{\"phoneNumber\":\"01234567890\"}";

    /**
     * Set up of tests
     * @throws Exception exception thrown
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(textEndpoint)
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
        ResultActions actions = mockMvc.perform(postJson(String.format("/texts/%s", TEMPLATE_ID), INVALID_JSON));

        actions.andExpect(status().isBadRequest());
        actions.andExpect(handler().handlerType(TextEndpoint.class));
        actions.andExpect(handler().methodName(SEND_TEXT_MSG));
        actions.andExpect(jsonPath("$.error.code", is(CTPException.Fault.VALIDATION_FAILED.name())));
        actions.andExpect(jsonPath("$.error.message", is(PROVIDED_JSON_INCORRECT)));
        actions.andExpect(jsonPath("$.error.timestamp", isA(String.class)));
    }

    /**
     * a test providing correct json but invalid phone number
     *
     * @throws Exception if the postJson fails
     */
    @Test
    public void textInvalidPhoneNumber() throws Exception {
        ResultActions actions = mockMvc.perform(postJson(String.format("/texts/%s", TEMPLATE_ID),
                VALID_JSON_BAD_PHONE_NUMBER));

        actions.andExpect(status().isBadRequest());
        actions.andExpect(handler().handlerType(TextEndpoint.class));
        actions.andExpect(handler().methodName(SEND_TEXT_MSG));
        actions.andExpect(jsonPath("$.error.code", is(CTPException.Fault.VALIDATION_FAILED.name())));
        actions.andExpect(jsonPath("$.error.message", is(RestExceptionHandler.INVALID_JSON)));
        actions.andExpect(jsonPath("$.error.timestamp", isA(String.class)));
    }

    /**
     * a test providing correct json and valid phone number
     *
     * @throws Exception if the postJson fails
     */
    @Test
    public void textHappyPath() throws Exception {
        ResultActions actions = mockMvc.perform(postJson(String.format("/texts/%s", TEMPLATE_ID),
                VALID_JSON_VALID_PHONE_NUMBER));

        actions.andExpect(status().is2xxSuccessful());
        actions.andExpect(handler().handlerType(TextEndpoint.class));
        actions.andExpect(handler().methodName(SEND_TEXT_MSG));
    }
}
