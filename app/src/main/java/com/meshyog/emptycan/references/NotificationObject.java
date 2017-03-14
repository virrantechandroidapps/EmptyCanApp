package com.meshyog.emptycan.references;

/**
 * Created by Viswanathan on 26/10/16.
 */
public class NotificationObject {

    int type;
    int imageResource;
    String title;
    String subTitle;
    String message;


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
