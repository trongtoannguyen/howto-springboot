package com.sample.order.entity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

//represent an order
public class Order {
    private String id;
    private List<Item> items;
    private BigDecimal amount;

    public Order(List<Item> items, BigDecimal amount) {
        this.id = "OD_" + UUID.randomUUID();
        this.items = items;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
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
                ", items=" + items +
                ", amount=" + amount +
                '}';
    }
}
