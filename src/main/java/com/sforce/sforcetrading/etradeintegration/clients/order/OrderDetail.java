package com.sforce.sforcetrading.etradeintegration.clients.order;

public class OrderDetail {
    private String date;
    private String orderId;
    private String type;
    private String action;
    private String qty;
    private String symbol;
    private String priceType;
    private String term;
    private String price;
    private String executed;
    private String status;

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOrderId() {
        return this.orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getQty() {
        return this.qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getPriceType() {
        return this.priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public String getTerm() {
        return this.term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getPrice() {
        return this.price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getExecuted() {
        return this.executed;
    }

    public void setExecuted(String executed) {
        this.executed = executed;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Getters and setters
    // Constructor, toString, equals, and hashCode methods
}
