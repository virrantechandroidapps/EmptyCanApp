package com.meshyog.emptycan.model;

/**
 * Created by varadhan on 03-03-2017.
 */
public interface IGPSActivity {
    public void locationChanged(double longitude, double latitude);
    public void displayGPSSettingsDialog();
}
