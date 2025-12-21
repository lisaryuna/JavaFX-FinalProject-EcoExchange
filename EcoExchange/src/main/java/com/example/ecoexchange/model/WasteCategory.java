package com.example.ecoexchange.model;

public class WasteCategory {
    private int id;
    private String name;
    private double basePrice;

    public WasteCategory(int id, String name, double basePrice) {
        this.id = id;
        this.name = name;
        this.basePrice = basePrice;
    }

    public WasteCategory(String name, double basePrice) {
        this.name = name;
        this.basePrice = basePrice;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public double getBasePrice() {
        return basePrice;
    }

    @Override
    public String toString() {
        return name + ("Rp " + basePrice + "/kg");
    }
}
