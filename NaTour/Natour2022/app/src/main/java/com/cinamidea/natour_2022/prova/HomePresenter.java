package com.cinamidea.natour_2022.prova;

import com.cinamidea.natour_2022.entities.Route;

import java.util.ArrayList;

public class HomePresenter implements HomeContract.Presenter {

    private final HomeContract.View view;
    private final HomeContract.Model model;


    public HomePresenter(HomeContract.View view) {
        this.view = view;
        this.model = new HomeModel();
    }

    @Override
    public void getAllRoutesButtonClicked(String id_token) {

        model.getAllRoutes(id_token, new HomeContract.Model.OnFinishedListener() {
            @Override
            public void onSuccess(ArrayList<Route> routes, ArrayList<Route> fav_routes) {

                view.loadRoutes(routes, fav_routes);

            }

            @Override
            public void onError(String response) {

            }

            @Override
            public void onUserUnauthorized(String response) {

            }

            @Override
            public void onNetworkError(String response) {

            }

        });

    }

    @Override
    public void getRoutesByDifficultyButtonClicked(String id_token, String difficulty) {

        model.getRoutesByDifficulty(id_token, difficulty, new HomeContract.Model.OnFinishedListener() {
            @Override
            public void onSuccess(ArrayList<Route> routes, ArrayList<Route> fav_routes) {
                view.loadRoutes(routes, fav_routes);
            }

            @Override
            public void onError(String response) {

            }

            @Override
            public void onUserUnauthorized(String response) {

            }

            @Override
            public void onNetworkError(String response) {

            }
        });

    }

}