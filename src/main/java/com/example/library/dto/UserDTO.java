package com.example.library.dto;

import com.example.library.validation.FieldMatch;
import com.example.library.validation.ValidEmail;
import com.example.library.validation.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"password", "matchingPassword", "verifyCode"})
@FieldMatch.List({
        @FieldMatch(first = "password", second = "matchingPassword", message = "The password fields must match")
})
public class UserDTO {

    @NotNull(message = "is required")
    @Size(min = 3, message = "is required")
    private String userName;

    @ValidPassword
    private String password;

    @NotNull(message = "is required")
    @Size(min = 8, message = "is required")
    private String matchingPassword;

    @ValidEmail
    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    private String email;

    private String verifyCode;
}
