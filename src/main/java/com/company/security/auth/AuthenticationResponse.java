package com.company.security.auth;

import lombok.*;
/**
 * Author : Khonimov Ulugbek
 * Date : 1/19/2023
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AuthenticationResponse {
    private String token;
}
