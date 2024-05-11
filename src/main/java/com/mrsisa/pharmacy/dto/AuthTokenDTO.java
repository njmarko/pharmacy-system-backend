package com.mrsisa.pharmacy.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
public class AuthTokenDTO {
    private String jwt;
    private String username;
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean loggedIn;
    private List<String> authorities;
}
