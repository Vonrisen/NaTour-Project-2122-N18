package com.cinamidea.natour.navigation.search;

import com.cinamidea.natour.entities.Route;
import com.cinamidea.natour.entities.RouteFilters;

import java.util.ArrayList;

public interface SearchContract {

    interface View {

        void loadResults(ArrayList<Route> filtered_routes, ArrayList<Route> favourite_routes);
        void displayError(String message);
        void logOutUnauthorizedUser();
    }

    interface Presenter {

        void searchButtonClicked(String username, RouteFilters route_filters, String id_token);

    }

    interface Model {

        interface OnFinishedListener {

            void onSuccess(ArrayList<Route>filtered_routes);
            void onError(String message);
            void onUserUnauthorized();

        }

        interface OnFavouriteRoutesFetchedListener {
            void onSuccess(ArrayList<Route> favourite_routes);
        }

        void getFilteredRoutes(String username, RouteFilters route_filters, String id_token, OnFinishedListener listener);

        void getFavourites(String username, String id_token, OnFavouriteRoutesFetchedListener listener);


    }

}
