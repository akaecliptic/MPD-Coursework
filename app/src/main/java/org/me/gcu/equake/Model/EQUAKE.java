package org.me.gcu.equake.Model;

import java.io.Serializable;
import java.time.LocalDate;

import static org.me.gcu.equake.Utility.EQuakeUtility.capitaliseTitle;

/**
 * Developed by: Michael A. F.
 */
public class EQUAKE implements Serializable {
    private String location;
    private LocalDate occurred;
    private double latitude;
    private double longitude;
    private float magnitude;
    private float depth;
    private String link;

    public EQUAKE() {

    }

    public EQUAKE(String location, LocalDate occurred, double latitude, double longitude, float magnitude, float depth, String link) {
        this.location = location;
        this.occurred = occurred;
        this.latitude = latitude;
        this.longitude = longitude;
        this.magnitude = magnitude;
        this.depth = depth;
        this.link = link;
    }

    //For debugging
    public EQUAKE(boolean debug){
        if(debug){
            this.location = "Glasgow";
            this.occurred = LocalDate.now();
            this.latitude = 55.860916;
            this.longitude = -4.251433;
            this.magnitude = 7.1f;
            this.depth = 8f;
            this.link = "URL";
        }else{
            new EQUAKE();
        }
    }

    public String getLocation() {
        return location;
    }

    public String getDisplayLocation() {
        return capitaliseTitle(location);
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getOccurred() {
        return occurred;
    }

    public void setOccurred(LocalDate occurred) {
        this.occurred = occurred;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getMagnitude() {
        return magnitude;
    }

    public String getDisplayMagnitude() {
        String temp = Float.toString(magnitude);
        if(temp.endsWith(".0"))
            temp = temp.split("\\.")[0];
        return "M: " + temp;
    }

    public void setMagnitude(float magnitude) {
        this.magnitude = magnitude;
    }

    public float getDepth() {
        return depth;
    }

    public String getDisplayDepth() {
        String temp = Float.toString(depth);
        if(temp.endsWith(".0"))
            temp = temp.split("\\.")[0];
        return "D: " + temp;
    }

    public void setDepth(float depth) {
        this.depth = depth;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCoordinates() {
        return this.latitude + ", " + this.longitude;
    }

    public String getTime() {
        return this.occurred.toString();
    }
}
