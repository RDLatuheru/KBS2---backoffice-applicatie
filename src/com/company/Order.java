package com.company;

public class Order {
    int orderID;
    int customerID;
    String orderDate;
    String expected;

    public Order(int orderID, int customerID, String orderDate, String expected) {
        this.orderID = orderID;
        this.customerID = customerID;
        this.orderDate = orderDate;
        this.expected = expected;
    }

    @Override
    public String toString() {
        return "Order: "+orderID+" \t Verwacht: "+expected;
    }
}
