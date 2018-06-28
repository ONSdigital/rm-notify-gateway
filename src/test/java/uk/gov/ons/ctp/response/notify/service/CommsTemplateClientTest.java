package uk.gov.ons.ctp.response.notify.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.rest.RestUtility;
import uk.gov.ons.ctp.common.rest.RestUtilityConfig;
import uk.gov.ons.ctp.response.notify.client.CommsTemplateClientException;
import uk.gov.ons.ctp.response.notify.config.AppConfig;
import uk.gov.ons.ctp.response.notify.config.CommsTemplateService;
import uk.gov.ons.ctp.response.notify.domain.model.CommsTemplateDTO;
import uk.gov.ons.ctp.response.notify.service.impl.CommsTemplateClientImpl;

@RunWith(MockitoJUnitRunner.class)
public class CommsTemplateClientTest {

  @InjectMocks private CommsTemplateClientImpl commsTemplateClient;

  @Mock private CommsTemplateService commsTemplateService;

  @Mock private AppConfig appConfig;

  @Mock private ObjectMapper objectMapper;

  @Mock private RestTemplate restTemplate;

  @Spy private RestUtility restUtility = new RestUtility(new RestUtilityConfig());

  @Before
  public void setUp() {
    Mockito.when(appConfig.getCommsTemplateService()).thenReturn(commsTemplateService);
    Mockito.when(commsTemplateService.getTemplateByClassifiersPath()).thenReturn("/templates");
  }

  @Test
  public void testMakesCorrectGetRequest() throws CommsTemplateClientException, IOException {
    // Given
    Mockito.when(
            restTemplate.exchange(
                any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
        .thenReturn(new ResponseEntity(HttpStatus.OK));

    // passed in like this as can't mix matchers and concrete values, if not declared as string then
    // mockito complains it is an ambiguous method call (could be one of 2 methods)
    String nullString = null;
    Mockito.when(objectMapper.readValue(nullString, CommsTemplateDTO.class))
        .thenReturn(CommsTemplateDTO.builder().build());

    ArgumentCaptor<URI> uriArgumentCaptor = ArgumentCaptor.forClass(URI.class);

    // When
    commsTemplateClient.getCommsTemplateByClassifiers(generateClassifiers());

    // Then
    Mockito.verify(restTemplate)
        .exchange(uriArgumentCaptor.capture(), eq(HttpMethod.GET), anyObject(), eq(String.class));
    String expectedURL = "http://localhost:8080/templates?LEGALBASIS=YY&REGION=NI";
    assertEquals(expectedURL, uriArgumentCaptor.getValue().toString());
  }

  @Test
  public void testThrowsExceptionWhenUnableToFindTemplate() {
    // Given
    Mockito.when(
            restTemplate.exchange(
                any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
        .thenReturn(new ResponseEntity(HttpStatus.BAD_REQUEST));

    try {
      // When
      commsTemplateClient.getCommsTemplateByClassifiers(generateClassifiers());
      fail();
    } catch (CommsTemplateClientException exception) {
      // Then
      assertEquals(CommsTemplateClientException.class, exception.getClass());
      assertEquals(CTPException.Fault.SYSTEM_ERROR, exception.getFault());
    }
  }

  private MultiValueMap<String, String> generateClassifiers() {
    MultiValueMap<String, String> classifiers = new LinkedMultiValueMap<>();
    classifiers.add("LEGALBASIS", "YY");
    classifiers.add("REGION", "NI");
    return classifiers;
  }
}
