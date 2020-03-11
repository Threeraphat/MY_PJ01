package com.example.my_pj01.Models;

public class PromotionModel{
    private int promid;
    private String promotion;

    public PromotionModel(){
    }

    public int getProdid() {
        return promid;
    }

    public void setProdid(int prodid) {
        this.promid = prodid;
    }

    public String getPromotion() {
        return promotion;
    }

    public void setPromotion(String promotion) {
        this.promotion = promotion;
    }
}