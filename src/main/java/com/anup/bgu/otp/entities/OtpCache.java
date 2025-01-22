package com.anup.bgu.otp.entities;

import lombok.*;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OtpCache implements Serializable {
    String registrationId;
    String otp;
    String email;
}
