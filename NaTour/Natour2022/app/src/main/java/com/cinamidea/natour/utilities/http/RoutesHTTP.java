package com.cinamidea.natour.utilities.http;

import com.cinamidea.natour.entities.Route;
import com.cinamidea.natour.entities.RouteFilters;
import com.cinamidea.natour.entities.RoutesCompilation;
import com.google.gson.Gson;

import okhttp3.Headers;
import okhttp3.Request;

public class RoutesHTTP extends OkHTTPRequest {


    public static Request insertRoute(Route route, String id_token) {

        String url = "https://t290f5jgg8.execute-api.eu-central-1.amazonaws.com/api/routes";
        String tags = (route.getTags().length() > 0) ? route.getTags() : null;

        Gson gson = new Gson();

        String json_coords = gson.toJson(route.getCoordinates());

        String request_body = "{\"name\":" + route.getName() + ",\"action\":" + "INSERT"
                + ",\"description\":" + route.getDescription() + ",\"level\":" + route.getLevel() +
                ",\"duration\":" + route.getDuration() + ",\"report_count\":" + route.getReport_count() + ",\"disability_access\":"
                + route.isDisability_access() + ",\"creator_username\":" + route.getCreator_username() +
                ",\"coordinates\":" + json_coords + ",\"tags\":" + tags + ",\"image_base64\":" + route.getImage_base64() + ",\"length\":" + route.getLength() + "}";

        Headers header = new Headers.Builder().add("Authorization", "\"" + id_token + "\"").build();

        return getPostRequest(url, request_body, header);

    }

    public static Request getAllRoutes(String id_token){

        String url = "https://t290f5jgg8.execute-api.eu-central-1.amazonaws.com/api/routes";

        Headers header = new Headers.Builder().add("Authorization", "\"" + id_token + "\"").build();

        return getGetRequest(url, header);

    }

    public static Request getNRoutes(String id_token, int start, int end) {

        String url = "https://t290f5jgg8.execute-api.eu-central-1.amazonaws.com/api/routes?start=" + start + "&end=" + end;

        Headers header = new Headers.Builder().add("Authorization", "\"" + id_token + "\"").build();

        return getGetRequest(url, header);

    }



    public static Request insertFavouriteRoute(String route_name, String username, String id_token) {

        String url = "https://t290f5jgg8.execute-api.eu-central-1.amazonaws.com/api/users/" + username + "/routes/favourites";

        String request_body = "{\"route_name\":" + route_name + "}";

        Headers header = new Headers.Builder().add("Authorization", "\"" + id_token + "\"").build();

        return getPostRequest(url, request_body, header);

    }

    public static Request insertToVisitRoute(String route_name, String username, String id_token) {

        String url = "https://t290f5jgg8.execute-api.eu-central-1.amazonaws.com/api/users/" + username + "/routes/to-visit";

        String request_body = "{\"route_name\":" + route_name + "}";

        Headers header = new Headers.Builder().add("Authorization", "\"" + id_token + "\"").build();

        return getPostRequest(url, request_body, header);

    }


    public static Request createRoutesCompilation(RoutesCompilation routes_compilation, String id_token){

        String url = "https://t290f5jgg8.execute-api.eu-central-1.amazonaws.com/api/routes-compilations";

        String request_body = "{\"creator_username\":" + routes_compilation.getCreator_username() + ",\"title\":" + routes_compilation.getTitle() + ",\"description\":"
                + routes_compilation.getDescription() +"}";

        Headers header = new Headers.Builder().add("Authorization", "\"" + id_token + "\"").build();

        return getPostRequest(url, request_body, header);

    }

    public static Request insertRouteIntoCompilation(String username, String route_name, String compilation_id, String id_token){

        String url = "https://t290f5jgg8.execute-api.eu-central-1.amazonaws.com/api/users/"+username+"/routes/compilations/"+compilation_id;

        String request_body = "{\"route_name\":" + route_name+"}";

        Headers header = new Headers.Builder().add("Authorization", "\"" + id_token + "\"").build();

        return getPostRequest(url, request_body, header);

    }

    public static Request getFavouriteRoutes(String username, String id_token) {

        String url = "https://t290f5jgg8.execute-api.eu-central-1.amazonaws.com/api/users/" + username + "/routes/favourites";

        Headers header = new Headers.Builder().add("Authorization", "\"" + id_token + "\"").build();

        return getGetRequest(url, header);

    }

    public static Request deleteFavouriteRoute(String username, String id_token, String route_name) {

        String url = "https://t290f5jgg8.execute-api.eu-central-1.amazonaws.com/api/users/" + username + "/routes/favourites?route-name=" + route_name;

        Headers header = new Headers.Builder().add("Authorization", "\"" + id_token + "\"").build();

        return getDeleteRequest(url, header);

    }

    public static Request deleteToVisitRoute(String username, String id_token, String route_name) {

        String url = "https://t290f5jgg8.execute-api.eu-central-1.amazonaws.com/api/users/" + username + "/routes/to-visit?route-name=" + route_name;

        Headers header = new Headers.Builder().add("Authorization", "\"" + id_token + "\"").build();

        return getDeleteRequest(url, header);


    }

    public static Request getToVisitRoutes(String username, String id_token) {

        String url = "https://t290f5jgg8.execute-api.eu-central-1.amazonaws.com/api/users/" + username + "/routes/to-visit";

        Headers header = new Headers.Builder().add("Authorization", "\"" + id_token + "\"").build();

        return getGetRequest(url, header);

    }

    public static Request getUserRoutes(String creator_username, String id_token) {

        String url = "https://t290f5jgg8.execute-api.eu-central-1.amazonaws.com/api/routes?creator-username=" + creator_username;

        Headers header = new Headers.Builder().add("Authorization", "\"" + id_token + "\"").build();

        return getGetRequest(url, header);

    }

    public static Request getRoutesByLevel(String id_token, String level) {

        String url = "https://t290f5jgg8.execute-api.eu-central-1.amazonaws.com/api/routes?level=" + level;

        Headers header = new Headers.Builder().add("Authorization", "\"" + id_token + "\"").build();

        return getGetRequest(url, header);

    }

    public static Request getFilteredRoutes(RouteFilters route_filters, String id_token)
    {

        String url = "https://t290f5jgg8.execute-api.eu-central-1.amazonaws.com/api/routes?"+"route-name="+route_filters.getRoute_name()+"&level="+route_filters.getLevel()+"&duration="+route_filters.getDuration()+
                "&disability-access"+route_filters.isIs_disability_access()+"&centre-latitude="+route_filters.getCentre().latitude+"&centre-longitude="+route_filters.getCentre().longitude+"&radius="+route_filters.getRadius()+"&tags="+route_filters.getTags();

        Headers header = new Headers.Builder().add("Authorization", "\"" + id_token + "\"").build();

        return getGetRequest(url, header);

    }

    public static Request getUserRoutesCompilation(String username, String compilation_id, String id_token)
    {

        String url = "https://t290f5jgg8.execute-api.eu-central-1.amazonaws.com/api/users/"+username+"/routes/compilations/"+compilation_id;

        Headers header = new Headers.Builder().add("Authorization", "\"" + id_token + "\"").build();

        return getGetRequest(url, header);

    }

    public static Request getUserRoutesCompilations(String username, String id_token)
    {

        String url = "https://t290f5jgg8.execute-api.eu-central-1.amazonaws.com/api/users/"+username+"/routes/compilations";

        Headers header = new Headers.Builder().add("Authorization", "\"" + id_token + "\"").build();

        return getGetRequest(url, header);

    }







}