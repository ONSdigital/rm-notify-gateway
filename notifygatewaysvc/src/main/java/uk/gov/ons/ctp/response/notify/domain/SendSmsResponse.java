package uk.gov.ons.ctp.response.notify.domain;


import lombok.Builder;

import java.util.UUID;

@Builder
public class SendSmsResponse {
    private UUID id;
    private UUID templateId;

    private String reference;
    private String fromNumber;
}
