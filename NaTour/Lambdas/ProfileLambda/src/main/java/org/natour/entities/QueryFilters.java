package org.natour.entities;

public class QueryFilters {

    private String route_name;
    private String creator_username;
    private String username;

    public QueryFilters(){

    }

    public QueryFilters(String route_name, String creator_username, String username) {
        this.route_name = route_name;
        this.creator_username = creator_username;
        this.username = username;
    }

    public String getRoute_name() {
        return route_name;
    }

    public void setRoute_name(String route_name) {
        this.route_name = route_name;
    }

    public String getCreator_username() {
        return creator_username;
    }

    public void setCreator_username(String creator_username) {
        this.creator_username = creator_username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

