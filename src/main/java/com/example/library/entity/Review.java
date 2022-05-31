package com.example.library.entity;

import com.fasterxml.jackson.databind.ser.Serializers;

import javax.persistence.*;

@Entity
@Table(name="review")
public class Review extends BaseEntity {

    @Column(name="rating")
    private int rating;

    @Column(name="comment")
    private String comment;

    @Column(name="username")
    private String username;

    public Review(){}

    public Review(int rating, String comment, String username) {
        this.rating = rating;
        this.comment = comment;
        this.username = username;
    }

//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + super.getId() +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
