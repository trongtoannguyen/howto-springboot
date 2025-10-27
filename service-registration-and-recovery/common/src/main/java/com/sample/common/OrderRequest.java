package com.sample.common;

public class OrderRequest {
    private String productId;
    private int quantity;
    private String walletNumber;
    private Integer cvv;

    public OrderRequest(String productId, int quantity, String walletNumber, Integer cvv) {
        this.productId = productId;
        this.quantity = quantity;
        this.walletNumber = walletNumber;
        this.cvv = cvv;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getWalletNumber() {
        return walletNumber;
    }

    public void setWalletNumber(String walletNumber) {
        this.walletNumber = walletNumber;
    }

    public Integer getCvv() {
        return cvv;
    }

    public void setCvv(Integer cvv) {
        this.cvv = cvv;
    }
}
