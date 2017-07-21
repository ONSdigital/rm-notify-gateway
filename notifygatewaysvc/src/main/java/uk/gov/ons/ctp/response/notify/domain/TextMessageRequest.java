package uk.gov.ons.ctp.response.notify.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Domain model object.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TextMessageRequest {
    // TODO ADd regex validation?
    private String phoneNumber;
    private String templateId;

    // TODO personalisation
}
