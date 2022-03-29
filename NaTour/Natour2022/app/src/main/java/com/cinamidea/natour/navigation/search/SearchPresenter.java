package com.cinamidea.natour.navigation.search;

import com.cinamidea.natour.entities.Route;
import com.cinamidea.natour.entities.RouteFilters;

import java.util.ArrayList;

public class SearchPresenter implements SearchContract.Presenter{

    private final SearchContract.View view;
    private final SearchContract.Model model;

    public SearchPresenter(SearchContract.View view, SearchContract.Model model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void searchButtonClicked(String username, RouteFilters route_filters, String id_token) {

        model.getFilteredRoutes(username, route_filters, id_token, new SearchContract.Model.OnFinishedListener() {
            @Override
            public void onSuccess(ArrayList<Route> filtered_routes) {
                model.getFavourites(username, id_token, favourite_routes -> {
                    view.loadResults(filtered_routes, favourite_routes);
                });
            }

            @Override
            public void onError(String message) {
                view.displayError(message);
            }

            @Override
            public void onUserUnauthorized() {
view.logOutUnauthorizedUser();
            }
        });

    }

}
