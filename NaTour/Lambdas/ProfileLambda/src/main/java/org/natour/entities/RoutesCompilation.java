package org.natour.entities;

import java.util.ArrayList;
import java.util.List;

public class RoutesCompilation {

    private String id;
    private String creator_username;
    private String title;
    private String description;
    private List<Route> routes = new ArrayList<>();

    public RoutesCompilation(){

    }

    public RoutesCompilation(String id, String creator_username, String title, String description, List<Route> routes) {
        this.id = id;
        this.creator_username = creator_username;
        this.title = title;
        this.description = description;
        this.routes = routes;
    }

    public RoutesCompilation(String id, String creator_username, String title, String description) {
        this.creator_username = creator_username;
        this.title = title;
        this.description = description;
        this.id = id;
    }

    public RoutesCompilation(String creator_username, String title, String description, List<Route> routes) {
        this.creator_username = creator_username;
        this.title = title;
        this.description = description;
        this.routes = routes;
    }


    public String getCreator_username() {
        return creator_username;
    }

    public void setCreator_username(String creator_username) {
        this.creator_username = creator_username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
