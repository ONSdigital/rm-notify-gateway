package uk.gov.ons.ctp.response.notify.representation;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Domain model object
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class NotifyRequestForEmailDTO extends NotifyRequestDTO {

    public static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";;

    @NotNull
    @Pattern(regexp=EMAIL_REGEX)
    private String emailAddress;
}
