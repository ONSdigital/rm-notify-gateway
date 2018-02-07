package uk.gov.ons.ctp.response.notify.config;

import lombok.Builder;
import lombok.Data;
import uk.gov.ons.ctp.common.rest.RestUtilityConfig;

@Data
@Builder
public class CommsTemplateService {
    private RestUtilityConfig connectionConfig;
    private String templateByClassifiersPath;
}
