package org.natour;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import org.natour.daos.RouteDAO;
import org.natour.daos.factories.RouteDAOFactory;
import org.natour.database.MySqlDB;
import org.natour.entities.QueryFilters;
import org.natour.entities.Route;
import org.natour.exceptions.PersistenceException;
import org.natour.s3.NatourS3Bucket;
import org.natour.tokens.CognitoTokens;
import org.natour.tokens.GoogleAuth;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.List;


public class Function implements RequestHandler<Event, String> {

    //All these object will be reused as long as lambda keeps warm (1 database connection for each warm lambda function)
    RouteDAO route_dao = (new RouteDAOFactory(MySqlDB.getConnection())).getRouteDAO("mysql");
    CognitoTokens cognito_tokens = new CognitoTokens();
    Gson gson = new Gson();

    @Override
    public String handleRequest(Event event, Context context) {

        String authorization_header = event.getId_token();

        if (authorization_header.contains("Cognito"))
            cognito_tokens.verifyIdToken(authorization_header.substring(7));
        else if (authorization_header.contains("Google")) {
            try {
                if (!GoogleAuth.isIdTokenValid(authorization_header.substring(6)))
                    throw new RuntimeException("Invalid Session");
            } catch (GeneralSecurityException | IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        String action = event.getAction();

        switch (action) {

            case "GET": //

                try {
                    List<Route> routes = null;
                    QueryFilters query_filters = event.getQuery_filters();

                    if (!query_filters.getRoute_name().equals("") || query_filters.getRadius() != 0) //Get filtered routes
                        routes = route_dao.getFilteredRoutes(query_filters);
                    else if (!query_filters.getLevel().equals(""))//Get routes by difficulty level
                        routes = route_dao.getRoutesByLevel(query_filters.getLevel());
                    else if (query_filters.getEnd() == 0)//Get all routes
                        routes = route_dao.getAll();
                    else if (query_filters.getEnd() != 0) //Get only a subset of routes
                        routes = route_dao.getN(query_filters.getStart(), query_filters.getEnd());

                    return gson.toJson(routes);

                } catch (PersistenceException e) {
                    throw new RuntimeException(e.getMessage());
                }

            case "INSERT_PROFILE_IMAGE": //

                try {

                    NatourS3Bucket bucket = new NatourS3Bucket();

                    byte image_as_byte_array[] = Base64.getDecoder().decode(event.getProfile_image_base64());

                    bucket.putUserProfileImage(event.getQuery_filters().getUsername(), image_as_byte_array);

                    return "User profile image inserted successfully";

                } catch (PersistenceException e) {
                    throw new RuntimeException(e.getMessage());
                }

            case "GET_PROFILE_IMAGE": //

                try {

                    NatourS3Bucket bucket = new NatourS3Bucket();

                    byte image_as_byte_array[] = bucket.fetchUserProfileImage(event.getQuery_filters().getUsername());

                    return Base64.getEncoder().encodeToString(image_as_byte_array);

                } catch (PersistenceException e) {
                    throw new RuntimeException(e.getMessage());
                }

            case "GET_FILTERED_ROUTES": {

                try {

                    List<Route> routes = route_dao.getFilteredRoutes(event.getQuery_filters());

                    return gson.toJson(routes);

                } catch (PersistenceException e) {
                    throw new RuntimeException(e.getMessage());
                }

            }

            case "INSERT": //

                try {

                    Route route = event.getRoute();
                    route_dao.insert(route);

                    return "Route inserted successfully";

                } catch (PersistenceException e) {
                    throw new RuntimeException(e.getMessage());
                }

            case "INSERT_REPORT": //

                try {

                    route_dao.insertReport(event.getReport());

                    return "Report [" + event.getReport().getType() + "] inserted successfully";

                } catch (PersistenceException e) {
                    throw new RuntimeException(e.getMessage());
                }

            case "CREATE_ROUTES_COMPILATION":

                try {
                    route_dao.createRoutesCompilation(event.getRoutes_compilation());

                    return "Routes compilation created successfully";

                } catch (PersistenceException e) {
                    throw new RuntimeException(e.getMessage());
                }

            default:
                return "WRONG ACTION";

        }

    }
}


