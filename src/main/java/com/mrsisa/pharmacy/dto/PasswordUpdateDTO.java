package com.mrsisa.pharmacy.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PasswordUpdateDTO {
    @NotBlank
    private String oldPassword;
    @NotBlank
    private String newPassword;
}
