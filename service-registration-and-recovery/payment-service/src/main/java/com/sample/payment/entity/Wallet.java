package com.sample.payment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

// Represent your e-wallet, you can implement persistence layer on this class
@Entity
@Table
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private Long walletNumber;
    @Column
    private Integer cvv;
    @Column
    private BigDecimal balance;

    public Wallet() {
        this.walletNumber = Math.round(Math.random() * 1000000000);
        this.cvv = 100 + (int) (Math.random() * 900);
        this.balance = BigDecimal.ZERO;
    }

    public Wallet(Long walletNumber, Integer cvv, BigDecimal balance) {
        this.walletNumber = walletNumber;
        this.cvv = cvv;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWalletNumber() {
        return walletNumber;
    }

    public void setWalletNumber(Long walletNumber) {
        this.walletNumber = walletNumber;
    }

    public Integer getCvv() {
        return cvv;
    }

    public void setCvv(Integer cvv) {
        this.cvv = cvv;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void deductBalance(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }

    public void addBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "walletNumber=" + walletNumber +
                ", cvv=" + cvv +
                ", balance=" + balance +
                '}';
    }
}
