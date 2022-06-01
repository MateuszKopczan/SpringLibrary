package com.example.library.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"orders", "cart"})
@Entity
@Table(name="app_user")
public class User extends BaseEntity{

    @Column(name="username")
    @NotEmpty(message = "Username cannot be empty")
    @Size(min = 3, max = 250)
    private String userName;

    @Column(name="password")
    private String password;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    @Column(name="email")
    private String email;

    @Column(name="phone_number")
    private String phoneNumber;

    @Column(name="verify", columnDefinition = "boolean default false")
    private boolean verify;

    @Column(name="verification_code")
    private String verificationCode;

    @Embedded
    private Address address;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade={CascadeType.PERSIST, CascadeType.MERGE,
                                                                    CascadeType.DETACH, CascadeType.REFRESH})
    private List<Order> orders;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Collection<Role> roles;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Cart cart;

}
