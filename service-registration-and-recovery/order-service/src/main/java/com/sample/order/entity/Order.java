package com.sample.order.entity;

import java.math.BigDecimal;
import java.util.UUID;

//represent an order
public class Order {
    private String id;
    private Item item;
    private BigDecimal amount;

    public Order(Item item, BigDecimal amount) {
        this.id = "OD_" + UUID.randomUUID();
        this.item = item;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", item=" + item +
                ", amount=" + amount +
                '}';
    }
}
