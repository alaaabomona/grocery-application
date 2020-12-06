package com.example.groceryapp.models;

public class ModelProduct {

    private String productId, productCategory, productIcon, productName;

    public ModelProduct() {
    }

    public ModelProduct(String productId, String productCategory, String productIcon, String productName) {
        this.productId = productId;
        this.productCategory = productCategory;
        this.productIcon = productIcon;
        this.productName = productName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductIcon() {
        return productIcon;
    }

    public void setProductIcon(String productIcon) {
        this.productIcon = productIcon;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
