package com.example.library.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "books"})
@Entity
@Table(name="orders")
public class Order extends BaseEntity{

    @Transient
    private final Map<String, String> payMethodMapping = new LinkedHashMap<>(){{
        put("PBL", "Transfer Online");
        put("CARD_TOKEN", "Card Payment");
        put("INSTALLMENTS", "PayU|Installments");
    }};

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="user_id")
    private User user;

    @Column(name="price")
    private float price;

    @Column(name="order_status")
    private String status;

    @Column(name="order_date")//, columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column(name="payU_order_id")
    private String payUOrderId;

    @Column(name="pay_method")
    private String payMethod;

    @Embedded
    private Address address;

    @ManyToMany
    @JoinTable(
            name="orders_books",
            joinColumns = @JoinColumn(name="order_id"),
            inverseJoinColumns = @JoinColumn(name="book_id")
    )
    private List<Book> books;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="order_detail_id")
    private OrderDetails orderDetail;

}
