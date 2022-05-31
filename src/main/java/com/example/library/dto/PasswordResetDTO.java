package com.example.library.dto;

import com.example.library.validation.FieldMatch;
import com.example.library.validation.ValidPassword;
import lombok.*;

@FieldMatch.List(
        @FieldMatch(first = "password", second="matchingPassword", message = "The password fields must match")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetDTO {

    @ValidPassword
    public String password;

    @ValidPassword
    public String matchingPassword;

    public String token;
}
