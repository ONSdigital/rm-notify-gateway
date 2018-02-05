package uk.gov.ons.ctp.response.notify.service;

import org.springframework.util.MultiValueMap;
import uk.gov.ons.ctp.response.notify.client.CommsTemplateClientException;
import uk.gov.ons.ctp.response.notify.domain.model.CommsTemplateDTO;

public interface CommsTemplateClient {

    CommsTemplateDTO getCommsTemplateByClassifiers(MultiValueMap<String, String> classifiers) throws CommsTemplateClientException;
}
