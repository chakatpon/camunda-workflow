package com.x10.viriyah.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class objCustomerBroker {
    @Builder.Default
    private String classes = "ALL";
    @Builder.Default
    private String subClass = "ALL";
    @Builder.Default
    private String partner = "00000";

    @Builder.Default
    private String insured = "Y";
    @Builder.Default
    private String broker = "Y";
    @Builder.Default
    private String sendEmail = "Y";
    @Builder.Default
    private String sendSms = "Y";
    @Builder.Default
    private String sendOnline = "N";
    @Builder.Default
    private String mail = "Y";
    @Builder.Default
    private String returnLink = "N";
}
