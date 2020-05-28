package uk.gov.ons.ctp.response.notify.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import uk.gov.ons.ctp.response.notify.lib.notify.NotificationDTO;
import uk.gov.ons.ctp.response.notify.lib.notify.NotifyRequest;
import uk.gov.ons.ctp.response.notify.lib.notify.NotifyRequestForEmailDTO;
import uk.gov.service.notify.Notification;

public class NotifyMapperTest {
 
  @Test
  public void dtoMapsToRequest() {

    Map<String, String> personalisation = new HashMap<>();
    personalisation.put("key", "value");

    NotifyRequestForEmailDTO emailDto = new NotifyRequestForEmailDTO();
    emailDto.setEmailAddress("test@test.com");
    emailDto.setReference("123");
    emailDto.setPersonalisation(personalisation);

    NotifyRequest req = NotifyRequestMapper.INSTANCE.mapToNotifyRequest(emailDto);

    assertEquals("test@test.com", req.getEmailAddress());
    assertEquals("123", req.getReference());
  }

  @Test
  public void requestMapsToDto() throws IOException, ParseException {

    Map<String, String> personalisation = new HashMap<>();
    personalisation.put("key", "value");

    Resource rsc = new ClassPathResource("notification-api.json");
    Reader reader = new InputStreamReader(rsc.getInputStream());
    String str = FileCopyUtils.copyToString(reader);
    Notification notifyRequest = new Notification(str);
    NotificationDTO dto = NotifyRequestMapper.INSTANCE.mapToNotificationDTO(notifyRequest);
    
    assertEquals(UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff"), dto.getTemplateId());
    assertEquals("test@test.com", dto.getEmailAddress());
    assertEquals("123", dto.getReference());
    assertEquals(new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2012").toString(),
            dto.getCreatedAt().toString());

    reader.close();
  }
}