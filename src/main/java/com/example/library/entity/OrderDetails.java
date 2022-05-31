package com.example.library.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user", "books"})
@Builder
@Entity
@Table(name="orders_details")
public class OrderDetails extends BaseEntity {

    @Column(name="email")
    private String email;

    @Column(name="phone_number")
    private String phone;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    @Embedded
    private Address address;

}
