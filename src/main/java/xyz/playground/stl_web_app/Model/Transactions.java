package xyz.playground.stl_web_app.Model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String reference;

    @Column
    private String transactionType;

    @Column
    private Long createdBy;

    @Column
    private double amount;

    @Column
    private LocalDateTime createdDate;

    @Column
    private LocalDateTime updateDate;
}
