package uk.gov.ons.ctp.response.notify.representation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Domain model object
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public abstract class NotifyRequestDTO {

    private String reference;

    private Map<String, String> personalisation;
}
