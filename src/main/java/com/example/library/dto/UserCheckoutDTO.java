package com.example.library.dto;

import com.example.library.entity.Address;
import com.example.library.validation.ValidEmail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCheckoutDTO {

    @NotNull(message = "First name is required")
    @Size(min=3)
    private String firstName;

    @NotNull(message = "Last name is required")
    @Size(min=3)
    private String lastName;

    @NotNull(message = "Phone number is required")
    @Size(min=9)
    private String phoneNumber;

    @ValidEmail
    private String email;

    private Address address;
}
