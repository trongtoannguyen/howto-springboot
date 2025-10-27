package com.sample.common;

import java.math.BigDecimal;

public class PaymentRequest {
    private String orderId;
    private BigDecimal amount;
    private WalletInfo walletInfo;

    public PaymentRequest(String orderId, BigDecimal amount, WalletInfo walletInfo) {
        this.orderId = orderId;
        this.amount = amount;
        this.walletInfo = walletInfo;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public WalletInfo getWalletInfo() {
        return walletInfo;
    }

    public void setWalletInfo(WalletInfo walletInfo) {
        this.walletInfo = walletInfo;
    }

    public static class WalletInfo {
        private Long walletNumber;
        private Integer cvv;

        public WalletInfo(Long walletNumber, Integer cvv) {
            this.walletNumber = walletNumber;
            this.cvv = cvv;
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
    }
}
