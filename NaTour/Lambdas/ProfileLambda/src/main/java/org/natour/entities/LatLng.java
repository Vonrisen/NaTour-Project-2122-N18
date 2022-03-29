package org.natour.entities;

public class LatLng {

    private float latitude;
    private float longitude;

    public LatLng(){

    }

    public LatLng(float latitude, float longitude) {
        if(isLatLngValid(latitude,longitude)) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "LatLng{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    private boolean isLatLngValid(float latitude, float longitude){
        if(Math.abs(latitude)>90)
            throw new IllegalArgumentException("Wrong latitude");
        if(Math.abs(longitude)>180)
            throw new IllegalArgumentException("Wrong longitude");
        return true;
    }

}
