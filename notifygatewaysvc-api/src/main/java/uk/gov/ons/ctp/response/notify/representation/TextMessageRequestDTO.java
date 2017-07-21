package uk.gov.ons.ctp.response.notify.representation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model object
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class TextMessageRequestDTO {
    // TODO ADd regex validation
    private String phoneNumber;

    // TODO personalisation
}
