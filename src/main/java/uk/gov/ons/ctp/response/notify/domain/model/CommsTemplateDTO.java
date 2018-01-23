package uk.gov.ons.ctp.response.notify.domain.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class CommsTemplateDTO {
    private String id;
    private String label;
    private String type;
    private String uri;
    private Map<String, String> classification;
    private List params;
}