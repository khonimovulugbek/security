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
public class RegisterRequest {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
}
