package com.arun.beans;



public class Calculation {

    private String destination;
    private int price;

    public Calculation() {
    }

    public Calculation(final String to, final int price) {
        this.destination = to;
        this.price = price;
    }

    public String getTo() {
        return destination;
    }

    public void setTo(final String to) {
        this.destination = to;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(final int price) {
        this.price = price;
    }
}
