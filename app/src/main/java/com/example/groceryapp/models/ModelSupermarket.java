package com.example.groceryapp.models;

public class ModelSupermarket {

    private String supermarketId, address, supermarketImage, supermarketName;

    public ModelSupermarket() {
    }

    public ModelSupermarket(String supermarketId, String address, String supermarketImage, String supermarketName) {
        this.supermarketId = supermarketId;
        this.address = address;
        this.supermarketImage = supermarketImage;
        this.supermarketName = supermarketName;
    }

    public String getSupermarketId() {
        return supermarketId;
    }

    public void setSupermarketId(String supermarketId) {
        this.supermarketId = supermarketId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSupermarketImage() {
        return supermarketImage;
    }

    public void setSupermarketImage(String supermarketImage) {
        this.supermarketImage = supermarketImage;
    }

    public String getSupermarketName() {
        return supermarketName;
    }

    public void setSupermarketName(String supermarketName) {
        this.supermarketName = supermarketName;
    }
}
