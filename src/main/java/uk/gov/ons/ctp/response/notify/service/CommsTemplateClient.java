package uk.gov.ons.ctp.response.notify.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.rest.RestUtility;
import uk.gov.ons.ctp.response.notify.client.CommsTemplateClientException;
import uk.gov.ons.ctp.response.notify.config.AppConfig;
import uk.gov.ons.ctp.response.notify.domain.model.CommsTemplateDTO;

@Slf4j
@Service
public class CommsTemplateClient {

  private AppConfig appConfig;

  private RestTemplate restTemplate;

  private RestUtility restUtility;

  private ObjectMapper objectMapper;

  @Autowired
  public CommsTemplateClient(
      AppConfig appConfig,
      RestTemplate restTemplate,
      RestUtility restUtility,
      ObjectMapper objectMapper) {
    this.appConfig = appConfig;
    this.restTemplate = restTemplate;
    this.restUtility = restUtility;
    this.objectMapper = objectMapper;
  }

  @Cacheable("commsTemplate")
  public CommsTemplateDTO getCommsTemplateByClassifiers(MultiValueMap<String, String> classifiers)
      throws CommsTemplateClientException {
    UriComponents uriComponents =
        restUtility.createUriComponents(
            appConfig.getCommsTemplateService().getTemplateByClassifiersPath(), classifiers);

    log.debug(
        "Attempting to get Comms Template for classifiers: {}, with uri {}",
        classifiers.toString(),
        uriComponents.toString());

    ResponseEntity<String> responseEntity =
        restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, null, String.class);

    CommsTemplateDTO result = null;
    if (responseEntity.getStatusCode().is2xxSuccessful()) {
      String responseBody = responseEntity.getBody();
      try {
        result = objectMapper.readValue(responseBody, CommsTemplateDTO.class);
        log.info("Got template from Comms Template Service: {}", result.toString());
      } catch (IOException e) {
        log.error("Couldn't unmarshal response from comms template service: {}", e.getMessage());
      }
    } else {
      log.error(
          "Unable to retrieve Comms Template, received {} response.",
          responseEntity.getStatusCode().toString());
      throw new CommsTemplateClientException(
          CTPException.Fault.SYSTEM_ERROR,
          String.format(
              "Unable to retrieve comms template, received a {} response",
              responseEntity.getStatusCode().toString()));
    }
    return result;
  }
}
