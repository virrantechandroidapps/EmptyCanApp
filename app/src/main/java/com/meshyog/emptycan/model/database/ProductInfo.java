package com.meshyog.emptycan.model.database;

import java.io.Serializable;

/**
 * Created by varadhan on 10-12-2016.
 */
public class ProductInfo implements Serializable {
    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public String getOfferEndDate() {
        return offerEndDate;
    }

    public void setOfferEndDate(String offerEndDate) {
        this.offerEndDate = offerEndDate;
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public int getProductDiscountPercentage() {
        return productDiscountPercentage;
    }

    public void setProductDiscountPercentage(int productDiscountPercentage) {
        this.productDiscountPercentage = productDiscountPercentage;
    }

    public String getProductExpiryDate() {
        return productExpiryDate;
    }

    public void setProductExpiryDate(String productExpiryDate) {
        this.productExpiryDate = productExpiryDate;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getProductImgUrl() {
        return productImgUrl;
    }

    public void setProductImgUrl(String productImgUrl) {
        this.productImgUrl = productImgUrl;
    }

    public String getProductManufacturingDate() {
        return productManufacturingDate;
    }

    public void setProductManufacturingDate(String productManufacturingDate) {
        this.productManufacturingDate = productManufacturingDate;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public float getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(float productPrice) {
        this.productPrice = productPrice;
    }

    public int getProductQty() {
        return productQty;
    }

    public void setProductQty(int productQty) {
        this.productQty = productQty;
    }

    public String getProductQtyType() {
        return productQtyType;
    }

    public void setProductQtyType(String productQtyType) {
        this.productQtyType = productQtyType;
    }

    private long productId;
    private String productName;
    private String productQtyType;//KG,LITER
    private int productQty;
    private float productPrice;
    private String productManufacturingDate;
    private String productExpiryDate;
    private String discountType; //OFFER ,DISCOUNT,NONE
    private String productImgUrl;//NONE
    private int productDiscountPercentage;
    private String offerName;
    private String offerEndDate;

    public int getProductUnits() {
        return productUnits;
    }

    public void setProductUnits(int productUnits) {
        this.productUnits = productUnits;
    }

    private int productUnits;






}
