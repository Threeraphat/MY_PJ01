package com.example.my_pj01;

public class ProductModel extends PromotionModel{
    private int id;
    private String name, price, type, description, picture, productX, productY, row, column, shelf, weight;

    public ProductModel(int proid, String proname, String proprice, String protype, String prodes, String propicture, String productX, String productY, String row, String column, String shelf, String proweight) {
        this.id = proid;
        this.name = proname;
        this.price = proprice;
        this.type = protype;
        this.description = prodes;
        this.picture = propicture;
        this.productX = productX;
        this.productY = productY;
        this.row = row;
        this.column = column;
        this.shelf = shelf;
        this.weight = proweight;
    }

    public void getAllProduct(int id){
    }

    public ProductModel(){
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getProductX() {
        return productX;
    }

    public void setProductX(String productX) {
        this.productX = productX;
    }

    public String getProductY() {
        return productY;
    }

    public void setProductY(String productY) {
        this.productY = productY;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getShelf() {
        return shelf;
    }

    public void setShelf(String shelf) {
        this.shelf = shelf;
    }
}