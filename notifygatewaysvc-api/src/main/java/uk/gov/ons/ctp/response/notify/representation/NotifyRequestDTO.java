package uk.gov.ons.ctp.response.notify.representation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

/**
 * Domain model object
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class NotifyRequestDTO {

    public static final String TELEPHONE_REGEX = "[\\d]{7,11}";

    @Pattern(regexp=TELEPHONE_REGEX)
    private String phoneNumber;

    private String reference;

    // TODO personalisation
}
