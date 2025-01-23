package com.anup.bgu.mail.dto;

import java.io.Serializable;
import java.util.Map;

public record MailData(
        String to,
        String subject,
        String templateName,
        Map<String, Object> variables
) implements Serializable {
}
