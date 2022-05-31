package com.example.library.entity;

import lombok.Data;
import org.springframework.lang.Nullable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

@Data
@Embeddable
public class Address {

    @NotNull(message = "City is required")
    @Size(min=3, message = "Invalid city")
    @Column(name="city")
    private String city;

    @NotNull(message = "Street is required")
    @Size(min=3, message = "Invalid street")
    @Column(name="street")
    private String street;

    @NotNull(message = "House number is required")
    @Size(min=1, message = "Invalid house number")
    @Column(name="house_number")
    private String houseNumber;

    @Column(name="flat_number")
    private String flatNumber;

    @NotNull(message = "Postal code is required")
    @Size(min=6, message = "Invalid postal code")
    @Column(name="postal_code")
    private String postalCode;

}
