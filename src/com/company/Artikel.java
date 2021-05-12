package com.company;

public class Artikel {
    int stockitemID;
    String stockItemName = "";
    int voorraad;
    int target;
    int reorderlevel;

    public Artikel(int stockitemID, int voorraad, int target, int reorderlevel) {
        this.stockitemID = stockitemID;
        this.voorraad = voorraad;
        this.target = target;
        this.reorderlevel = reorderlevel;
    }

    public Artikel(int stockitemID, String stockItemName, int voorraad) {
        this.stockitemID = stockitemID;
        this.stockItemName = stockItemName+" ";
        this.voorraad = voorraad;
    }

    @Override
    public String toString() {
        return "Artikel: "+stockitemID+" "+stockItemName+"- Voorraad: "+voorraad;
    }
}
