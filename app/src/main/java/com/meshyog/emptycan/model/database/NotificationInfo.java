package com.meshyog.emptycan.model.database;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by varadhan on 29-11-2016.
 */
public class NotificationInfo implements Serializable , Comparable<NotificationInfo> {
    public String notificationTitle;
    public String notificationBody;
    public String notificationClickAction;
    public String notificationImage;
    public String notificationData;
    public String notificationSound;
    public String   notificationRcvdDate;
    public String notificationMsgId;

    public String getNotificationMsgId() {
        return notificationMsgId;
    }

    public void setNotificationMsgId(String notificationMsgId) {
        this.notificationMsgId = notificationMsgId;
    }

    public Date notificationSendDate;
    public int   notificationImgRsrc;
     public int   notificationType;

    public int getNotificationImgRsrc() {
        return notificationImgRsrc;
    }
    public int getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
    }
    public void setNotificationImgRsrc(int notificationImgRsrc) {
        this.notificationImgRsrc = notificationImgRsrc;
    }


    public String getNotificationRcvdDate() {
        return notificationRcvdDate;
    }

    public void setNotificationRcvdDate(String notificationRcvdDate) {
        this.notificationRcvdDate = notificationRcvdDate;
    }


    public boolean isRead;

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getNotificationBody() {
        return notificationBody;
    }

    public void setNotificationBody(String notificationBody) {
        this.notificationBody = notificationBody;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationSound() {
        return notificationSound;
    }

    public void setNotificationSound(String notificationSound) {
        this.notificationSound = notificationSound;
    }

    public String getNotificationClickAction() {
        return notificationClickAction;
    }

    public void setNotificationClickAction(String notificationClickAction) {
        this.notificationClickAction = notificationClickAction;
    }

    public String getNotificationData() {
        return notificationData;
    }

    public void setNotificationData(String notificationData) {
        this.notificationData = notificationData;
    }

    public String getNotificationImage() {
        return notificationImage;
    }

    public void setNotificationImage(String notificationImage) {
        this.notificationImage = notificationImage;
    }

    public Date getNotificationSendDate() {
        return notificationSendDate;
    }

    public void setNotificationSendDate(Date notificationSendDate) {
        this.notificationSendDate = notificationSendDate;
    }
    @Override
    public int compareTo(NotificationInfo o) {
        return getNotificationSendDate().compareTo(o.getNotificationSendDate());
    }
}
