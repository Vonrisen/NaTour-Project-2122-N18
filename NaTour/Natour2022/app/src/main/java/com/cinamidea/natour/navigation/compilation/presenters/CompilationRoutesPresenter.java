package com.cinamidea.natour.navigation.compilation.presenters;

import com.cinamidea.natour.entities.Route;
import com.cinamidea.natour.navigation.compilation.contracts.CompilationRoutesContract;

import java.util.ArrayList;

public class CompilationRoutesPresenter implements CompilationRoutesContract.Presenter {

    private final CompilationRoutesContract.View view;
    private final CompilationRoutesContract.Model model;
    volatile ArrayList<Route> user_routes;


    public CompilationRoutesPresenter(CompilationRoutesContract.View view, CompilationRoutesContract.Model model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void getRoutesButtonClicked(String username, String compilation_id, String id_token) {

        model.getRoutes(username, compilation_id, id_token, new CompilationRoutesContract.Model.OnFinishedListener() {
            @Override
            public void onSuccess(ArrayList<Route> routes) {

                user_routes = routes;

                model.getFavourites(username, id_token, favourite_routes -> {
                    view.loadRoutes(routes, favourite_routes);
                });

             }

            @Override
            public void onError(String response) {
                view.displayError(response);
            }

            @Override
            public void onUserUnauthorized() {
                view.logOutUnauthorizedUser();
            }

        });

    }

}
