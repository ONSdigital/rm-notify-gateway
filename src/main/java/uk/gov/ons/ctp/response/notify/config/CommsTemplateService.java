package uk.gov.ons.ctp.response.notify.config;

import lombok.Data;
import uk.gov.ons.ctp.common.rest.RestUtilityConfig;

@Data
public class CommsTemplateService {
    private RestUtilityConfig connectionConfig;
    private String templateByClassifiersPath;
}
