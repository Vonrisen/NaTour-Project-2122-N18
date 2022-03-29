package org.natour;

import org.natour.entities.QueryFilters;
import org.natour.entities.Route;
import org.natour.entities.RoutesCompilation;

public class Event {

    private String action;
    private String id_token;
    private Route route;
    private QueryFilters query_filters;
    private String profile_image_base64;
    private RoutesCompilation routes_compilation;

    public Event(){

    }

    public Event(String action, String id_token, Route route, QueryFilters query_filters, String profile_image_base64, RoutesCompilation routes_compilation) {
        this.action = action;
        this.id_token = id_token;
        this.route = route;
        this.query_filters = query_filters;
        this.profile_image_base64 = profile_image_base64;
        this.routes_compilation = routes_compilation;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getId_token() {
        return id_token;
    }

    public void setId_token(String id_token) {
        this.id_token = id_token;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public QueryFilters getQuery_filters() {
        return query_filters;
    }

    public void setQuery_filters(QueryFilters query_filters) {
        this.query_filters = query_filters;
    }

    public String getProfile_image_base64() {
        return profile_image_base64;
    }

    public void setProfile_image_base64(String profile_image_base64) {
        this.profile_image_base64 = profile_image_base64;
    }

    public RoutesCompilation getRoutes_compilation() {
        return routes_compilation;
    }

    public void setRoutes_compilation(RoutesCompilation routes_compilation) {
        this.routes_compilation = routes_compilation;
    }
}
