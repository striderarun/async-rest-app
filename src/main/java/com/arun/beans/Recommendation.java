package com.arun.beans;

public class Recommendation {

    private String destination;
    private String forecast;
    private int price;

    public Recommendation() {
    }

    public Recommendation(final String destination, final String forecast, final int price) {
        this.destination = destination;
        this.forecast = forecast;
        this.price = price;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(final String destination) {
        this.destination = destination;
    }

    public String getForecast() {
        return forecast;
    }

    public void setForecast(final String forecast) {
        this.forecast = forecast;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(final int price) {
        this.price = price;
    }
}
