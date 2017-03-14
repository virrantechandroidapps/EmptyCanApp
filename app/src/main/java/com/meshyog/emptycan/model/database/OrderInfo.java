package com.meshyog.emptycan.model.database;

import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by varadhan on 10-12-2016.
 */
public class OrderInfo implements Serializable {


    private List<CartInfo> cartInfoList;
    private String orderDoneDateTime;
    private long consumerKey;
    private long orderAddressId;
    private Float totalPayment;
    private String paymentMode;
    private String userFcmId;
    private String orderStatus;
    private String latitude;
    private String longitude;
    private String altitude;
    private String accuracy;
    private String bearing;
    private String gpsProvider;
    private String speed;
    private String time;
    private String describeContents;
    private String refStatus;
    private String isFirstTimeBooking;

    public List<CartInfo> getCartInfoList() {
        return cartInfoList;
    }

    public void setCartInfoList(List<CartInfo> cartInfoList) {
        this.cartInfoList = cartInfoList;
    }

    public String getOrderDoneDateTime() {
        return orderDoneDateTime;
    }

    public void setOrderDoneDateTime(String orderDoneDateTime) {
        this.orderDoneDateTime = orderDoneDateTime;
    }

    public long getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(long consumerKey) {
        this.consumerKey = consumerKey;
    }

    public long getOrderAddressId() {
        return orderAddressId;
    }

    public void setOrderAddressId(long orderAddressId) {
        this.orderAddressId = orderAddressId;
    }

    public Float getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(Float totalPayment) {
        this.totalPayment = totalPayment;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getUserFcmId() {
        return userFcmId;
    }

    public void setUserFcmId(String userFcmId) {
        this.userFcmId = userFcmId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getBearing() {
        return bearing;
    }

    public void setBearing(String bearing) {
        this.bearing = bearing;
    }



    public String getSpeed() {
        return speed;
    }

    public String getGpsProvider() {
        return gpsProvider;
    }

    public void setGpsProvider(String gpsProvider) {
        this.gpsProvider = gpsProvider;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getDescribeContents() {
        return describeContents;
    }

    public void setDescribeContents(String describeContents) {
        this.describeContents = describeContents;
    }

    public String getRefStatus() {
        return refStatus;
    }

    public void setRefStatus(String refStatus) {
        this.refStatus = refStatus;
    }

    public String getIsFirstTimeBooking() {
        return isFirstTimeBooking;
    }

    public void setIsFirstTimeBooking(String isFirstTimeBooking) {
        this.isFirstTimeBooking = isFirstTimeBooking;
    }
}
