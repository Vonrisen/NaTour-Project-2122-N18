package org.natour;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import org.natour.daos.RouteDAO;
import org.natour.daos.factories.RouteDAOFactory;
import org.natour.database.MySqlDB;
import org.natour.entities.QueryFilters;
import org.natour.entities.Route;
import org.natour.entities.RoutesCompilation;
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
                    QueryFilters query_filters = event.getQuery_filters();
                    List<Route>  routes = route_dao.getUserRoutes(query_filters.getCreator_username());
                    return gson.toJson(routes);
                } catch (PersistenceException e) {
                    throw new RuntimeException(e.getMessage());
                }

            case "GET_USER_FAVOURITES": //

                try {

                    List<Route> routes = route_dao.getUserFavourites(event.getQuery_filters().getUsername());

                    return gson.toJson(routes);

                } catch (PersistenceException e) {
                    throw new RuntimeException(e.getMessage());
                }

            case "GET_USER_TOVISIT": //

                try {

                    List<Route> routes = route_dao.getUserToVisit(event.getQuery_filters().getUsername());

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

            case "GET_USER_COMPILATION":

                try {

                    List<Route> routes = route_dao.getUserRoutesCompilation(event.getRoutes_compilation().getId());

                    return gson.toJson(routes);

                } catch (PersistenceException e) {
                    throw new RuntimeException(e.getMessage());
                }


            case "GET_USER_COMPILATIONS":

                try {

                    List<RoutesCompilation> routes_compilations = route_dao.getUserRoutesCompilations(event.getRoutes_compilation().getCreator_username());

                    return gson.toJson(routes_compilations);

                } catch (PersistenceException e) {
                    throw new RuntimeException(e.getMessage());
                }


            case "INSERT_USER_FAVOURITE":

                try {

                    route_dao.insertFavourite(event.getQuery_filters().getUsername(), event.getQuery_filters().getRoute_name());

                    return "Route inserted successfully";

                } catch (PersistenceException e) {
                    throw new RuntimeException(e.getMessage());
                }


            case "INSERT_USER_TOVISIT": //

                try {

                    route_dao.insertToVisit(event.getQuery_filters().getUsername(), event.getQuery_filters().getRoute_name());

                    return "Route inserted successfully";

                } catch (PersistenceException e) {
                    throw new RuntimeException(e.getMessage());
                }

            case "INSERT_ROUTE_IN_COMPILATION":

                try {
                    route_dao.insertRouteIntoCompilation(event.getRoutes_compilation().getId(), event.getQuery_filters().getRoute_name());

                    return "Route inserted in compilation successfully";

                } catch (PersistenceException e) {
                    throw new RuntimeException(e.getMessage());
                }

            case "DELETE_USER_FAVOURITE": //

                try {

                    route_dao.deleteFavourite(event.getQuery_filters().getUsername(), event.getQuery_filters().getRoute_name());

                    return "Favourite route deleted successfully";

                } catch (PersistenceException e) {
                    throw new RuntimeException(e.getMessage());
                }

            case "DELETE_USER_TOVISIT": //

                try {

                    route_dao.deleteToVisit(event.getQuery_filters().getUsername(), event.getQuery_filters().getRoute_name());

                    return "Favourite route deleted successfully";

                } catch (PersistenceException e) {
                    throw new RuntimeException(e.getMessage());
                }


            default:
                return "WRONG ACTION";

        }

    }
}


