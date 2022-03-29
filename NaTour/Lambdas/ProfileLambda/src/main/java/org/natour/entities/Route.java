package org.natour.entities;

import java.util.ArrayList;
import java.util.List;

public class Route {

    private String name;

    private String description;

    private String creator_username;

    private String level;

    private int duration;

    private int report_count;

    private boolean disability_access;

    private List<LatLng> coordinates;

    private String tags;

    private String image_base64;

    private float length;

    private int likes;

    public Route(){

    }



    public Route(String name, String description, String creator_username, String level, int duration, int report_count, boolean disability_access, String tags, String image_base64, float length, int likes) {
        this.name = name;
        this.description = description;
        this.creator_username = creator_username;
        this.level = level;
        this.duration = duration;
        this.report_count = report_count;
        this.disability_access = disability_access;
        this.tags = tags;
        this.image_base64 = image_base64;
        this.length = length;
        this.coordinates = new ArrayList<>();
        this.likes = likes;
    }

    public Route(String name, String description, String creator_username, String level, int duration, int report_count, boolean disability_access, String tags, float length, int likes) {
        this.name = name;
        this.description = description;
        this.creator_username = creator_username;
        this.level = level;
        this.duration = duration;
        this.report_count = report_count;
        this.disability_access = disability_access;
        this.tags = tags;
        this.length = length;
        this.coordinates = new ArrayList<>();
        this.likes = likes;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreator_username() {
        return creator_username;
    }

    public void setCreator_username(String creator_username) {
        this.creator_username = creator_username;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getReport_count() {
        return report_count;
    }

    public void setReport_count(int report_count) {
        this.report_count = report_count;
    }

    public boolean isDisability_access() {
        return disability_access;
    }

    public void setDisability_access(boolean disability_access) {
        this.disability_access = disability_access;
    }

    public List<LatLng> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<LatLng> coordinates) {
        this.coordinates = coordinates;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getImage_base64() {
        return image_base64;
    }

    public void setImage_base64(String image_base64) {
        this.image_base64 = image_base64;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    @Override
    public String toString() {
        return "Route{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", creator_username='" + creator_username + '\'' +
                ", level='" + level + '\'' +
                ", duration=" + duration +
                ", report_count=" + report_count +
                ", disability_access=" + disability_access +
                ", coordinates=" + coordinates.toString() +
                ", tags='" + tags + '\'' +
                ", image_base64='" + image_base64 + '\'' +
                ", length=" + length +
                ", likes=" + likes +
                '}';
    }
}