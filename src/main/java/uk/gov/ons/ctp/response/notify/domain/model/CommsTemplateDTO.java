package uk.gov.ons.ctp.response.notify.domain.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Builder
@Data
public class CommsTemplateDTO {
    private String id;
    private String label;
    private String type;
    private String uri;
    private Map<String, String> classifiers;
    private Map<String, String> params;
}
