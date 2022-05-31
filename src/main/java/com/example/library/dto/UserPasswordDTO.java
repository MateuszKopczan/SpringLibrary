package com.example.library.dto;

import com.example.library.validation.ValidPassword;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPasswordDTO {

    private String currentPassword;

    @ValidPassword
    private String newPassword;

    @ValidPassword
    private String matchingPassword;
}
