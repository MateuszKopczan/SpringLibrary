package com.example.library.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="category")
public class Category extends BaseEntity{


    @Column(name="name")
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
