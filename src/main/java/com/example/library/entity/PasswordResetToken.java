package com.example.library.entity;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
public class PasswordResetToken extends BaseEntity{

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name="user_id")
    private User user;

    private Date expiryDate;

    public PasswordResetToken() {}

    public PasswordResetToken(User user, String token){
        this.user = user;
        this.token = token;
        Date dt = new Date();
        this.expiryDate = new Date(dt.getTime() + (1000 * 60 * 60 * 24));
    }
}
