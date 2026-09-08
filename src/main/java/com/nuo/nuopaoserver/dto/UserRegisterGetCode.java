package com.nuo.nuopaoserver.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterGetCode {
    @Email(message = "Invalid email format")
    private String email;
}
