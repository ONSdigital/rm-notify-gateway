package uk.gov.ons.ctp.response.notify.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import uk.gov.ons.ctp.common.rest.RestUtility;
import uk.gov.ons.ctp.response.notify.config.AppConfig;
import uk.gov.ons.ctp.response.notify.service.CommsTemplateClient;
import uk.gov.ons.ctp.response.notify.domain.model.CommsTemplateDTO;

import java.io.IOException;


@Slf4j
@Service
public class CommsTemplateClientImpl implements CommsTemplateClient {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    @Qualifier("commsTemplateSvcClient")
    private RestUtility restUtility;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public CommsTemplateDTO getCommsTemplateByClassifiers(MultiValueMap<String, String> classifiers) {
        UriComponents uriComponents = restUtility.createUriComponents(
                appConfig.getCommsTemplateService().getTemplateByClassifiersPath(),classifiers);

        HttpEntity<?> httpEntity = restUtility.createHttpEntity(null);

        ResponseEntity<String> responseEntity = restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET,
                httpEntity, String.class);

        CommsTemplateDTO result = null;
        if (responseEntity != null && responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = responseEntity.getBody();
            try {
                result = objectMapper.readValue(responseBody, CommsTemplateDTO.class);
            } catch (IOException e) {
                log.error(String.format("Couldn't unmarshal response from comms template service: {}", e.getMessage()));
            }

            log.info("Got template from Comms Template Service: {}", result);
        }
        return result;
    }
}
