package uk.gov.ons.ctp.response.notify.service;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.notify.config.NotifyConfiguration;
import uk.gov.ons.ctp.response.notify.domain.Response;
import uk.gov.ons.ctp.response.notify.domain.model.Message;
import uk.gov.ons.ctp.response.notify.domain.repository.MessageRepository;
import uk.gov.ons.ctp.response.notify.message.NotifyRequestPublisher;
import uk.gov.ons.ctp.response.notify.message.notify.NotifyRequest;
import uk.gov.ons.ctp.response.notify.service.impl.ResilienceServiceImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * To unit test ResilienceServiceImpl
 */
@RunWith(MockitoJUnitRunner.class)
public class ResilienceServiceImplTest {

    @InjectMocks
    private ResilienceServiceImpl resilienceService;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private NotifyRequestPublisher notifyRequestPublisher;

    @Mock
    private NotifyConfiguration notifyConfiguration;

    private static final String MESSAGE_REFERENCE = "the reference";
    private static final String PHONE_NUMBER = "01234567890";
    private static final String TEMPLATE_ID = "f3778220-f877-4a3d-80ed-e8fa7d104563";

    @Test
    public void testMessageNotSentIfNotifyDisabled() {
        //Given Notify is Disabled
        when(notifyConfiguration.getEnabled()).thenReturn(false);

        //When the Resilience service is called
        NotifyRequest notifyRequest = NotifyRequest.builder()
                .withTemplateId(TEMPLATE_ID)
                .withPhoneNumber(PHONE_NUMBER)
                .withReference(MESSAGE_REFERENCE)
                .build();
        Response response = resilienceService.process(notifyRequest);

        //Then it should receive a valid response but not send the message
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(PHONE_NUMBER, response.getFromNumber());
        assertEquals(MESSAGE_REFERENCE, response.getReference());
        assertEquals(TEMPLATE_ID, response.getTemplateId().toString());

        verify(notifyRequestPublisher, times(0)).send(any(NotifyRequest.class));
    }

    @Test
    public void testMessageSavedIfNotifyDisabled() {
        //Given Notify is Disabled
        when(notifyConfiguration.getEnabled()).thenReturn(false);

        //When the Resilience service is called
        NotifyRequest notifyRequest = NotifyRequest.builder()
                .withTemplateId(TEMPLATE_ID)
                .withPhoneNumber(PHONE_NUMBER)
                .withReference(MESSAGE_REFERENCE)
                .build();
        Response response = resilienceService.process(notifyRequest);

        //Then it should receive a valid response but not send the message
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(PHONE_NUMBER, response.getFromNumber());
        assertEquals(MESSAGE_REFERENCE, response.getReference());
        assertEquals(TEMPLATE_ID, response.getTemplateId().toString());

        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    public void testMessageSentIfNotifyEnabled() {
        //Given Notify is Enabled
        when(notifyConfiguration.getEnabled()).thenReturn(true);

        //When the Resilience service is called
        NotifyRequest notifyRequest = NotifyRequest.builder()
                .withTemplateId(TEMPLATE_ID)
                .withPhoneNumber(PHONE_NUMBER)
                .withReference(MESSAGE_REFERENCE)
                .build();
        Response response = resilienceService.process(notifyRequest);

        //Then it should receive a valid response and send the message
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(PHONE_NUMBER, response.getFromNumber());
        assertEquals(MESSAGE_REFERENCE, response.getReference());
        assertEquals(TEMPLATE_ID, response.getTemplateId().toString());

        verify(messageRepository, times(1)).save(any(Message.class));
        verify(notifyRequestPublisher, times(1)).send(any(NotifyRequest.class));
    }
}
