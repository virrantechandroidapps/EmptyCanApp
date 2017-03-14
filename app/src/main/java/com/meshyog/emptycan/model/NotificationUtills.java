package com.meshyog.emptycan.model;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.support.v4.app.NotificationCompat;

import com.meshyog.emptycan.R;
import com.meshyog.emptycan.model.database.NotificationInfo;

import java.util.List;
import java.util.Random;

/**
 * Created by varadhan on 29-11-2016.
 */
public class NotificationUtills  {
    private String TAG;
    private Bitmap avatarBitmap;
    private Context mContext;
    private PendingIntent resultPendingIntent;
    public NotificationUtills(Context mContext) {
        this.TAG = NotificationUtills.class.getSimpleName();
        this.avatarBitmap = null;
        this.resultPendingIntent = null;
        this.mContext = mContext;
    }
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (VERSION.SDK_INT > 20) {
            for (ActivityManager.RunningAppProcessInfo processInfo : am.getRunningAppProcesses()) {
                if (processInfo.importance == 100) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
            return isInBackground;
        } else if (((ActivityManager.RunningTaskInfo) am.getRunningTasks(1).get(0)).topActivity.getPackageName().equals(context.getPackageName())) {
            return false;
        } else {
            return true;
        }
    }

    public static int createRandomInteger(int aStart, int aEnd) {
        Random aRandom = new Random();
        if (aStart <= aEnd) {
            return ((int) (((double) ((aEnd - aStart) + 1)) * aRandom.nextDouble())) + aStart;
        }
        throw new IllegalArgumentException("Start cannot exceed End.");
    }
    public static void cancelNotifications(Context context, List<Integer> notificationIdList) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        for (Integer intValue : notificationIdList) {
            notificationManager.cancel(intValue.intValue());
        }
    }
    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }
}
