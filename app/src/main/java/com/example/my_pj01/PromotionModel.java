package com.example.my_pj01;

public class PromotionModel{
    private int id;
    private String promotion;

    public PromotionModel(int id, String promotion){
        this.id = id;
        this.promotion = promotion;
    }

    public PromotionModel(){
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPromotion() {
        return promotion;
    }

    public void setPromotion(String promotion) {
        this.promotion = promotion;
    }
}