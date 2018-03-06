package uk.gov.ons.ctp.response.notify.service;

import org.springframework.util.MultiValueMap;
import uk.gov.ons.ctp.response.notify.client.CommsTemplateClientException;
import uk.gov.ons.ctp.response.notify.domain.model.CommsTemplateDTO;

public interface CommsTemplateClient {

    /**
     * Retrieve a comms template from the comms templat service
     * @param classifiers, map of classifiers which specify the template in the comms template service
     * @return Comms Template
     * @throws CommsTemplateClientException
     */
    CommsTemplateDTO getCommsTemplateByClassifiers(MultiValueMap<String, String> classifiers)
            throws CommsTemplateClientException;
}
