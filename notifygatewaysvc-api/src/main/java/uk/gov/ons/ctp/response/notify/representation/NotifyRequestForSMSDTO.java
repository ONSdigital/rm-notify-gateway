package uk.gov.ons.ctp.response.notify.representation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Domain model object
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class NotifyRequestForSMSDTO extends NotifyRequestDTO {

    public static final String TELEPHONE_REGEX = "[\\d]{7,11}";

    @NotNull
    @Pattern(regexp=TELEPHONE_REGEX)
    private String phoneNumber;
}
