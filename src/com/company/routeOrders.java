package com.company;

public class routeOrders {
    int orderID;
    int huisNr;
    String naam;
    String plaats;
    String straat;
    String lon;
    String lat;
    routeOrders neighrest;

    public routeOrders(int orderID, String naam, String plaats, String straat, int huisNr) {
        this.orderID = orderID;
        this.huisNr = huisNr;
        this.naam = naam;
        this.plaats = plaats;
        this.straat = straat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public void setNeighrest(routeOrders n) {
        this.neighrest = n;
    }

    public routeOrders getNeighrest() {
        return this.neighrest;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    @Override
    public String toString() {
        return "Order: " + this.orderID + " \t naar: " + this.plaats;
    }
}


