package uk.gov.ons.ctp.response.notify.util;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.Test;

import uk.gov.ons.ctp.response.notify.domain.Response;
import uk.gov.ons.ctp.response.notify.lib.notify.ResponseDTO;

public class ResponseMapperTest {
  
  @Test
  public void willMapResponseToResponseDTO() {
    
    UUID expectedUUID = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
    Response resp = new Response();
    resp.setFromEmail("test@test.com");
    resp.setFromNumber("01010101010");
    resp.setId(expectedUUID);
    resp.setReference("123");
    resp.setTemplateId(expectedUUID);

    ResponseDTO dto = ResponseMapper.INSTANCE.mapToResponseDto(resp);

    assertEquals("test@test.com", dto.getFromEmail());
    assertEquals("01010101010", dto.getFromNumber());
    assertEquals(expectedUUID, dto.getId());
    assertEquals(expectedUUID, dto.getTemplateId());
    assertEquals("123", dto.getReference());
  }
}