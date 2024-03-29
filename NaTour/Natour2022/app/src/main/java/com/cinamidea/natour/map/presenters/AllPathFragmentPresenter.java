package com.cinamidea.natour.map.presenters;

import com.cinamidea.natour.entities.Route;
import com.cinamidea.natour.map.contracts.AllPathFragmentContract;

public class AllPathFragmentPresenter implements AllPathFragmentContract.Presenter{
    private AllPathFragmentContract.View view;
    private AllPathFragmentContract.Model model;

    public AllPathFragmentPresenter(AllPathFragmentContract.View allPathFragments, AllPathFragmentContract.Model model) {
        this.view = allPathFragments;
        this.model = model;
    }

    @Override
    public void getAllRoutesOnCreate(String id_token) {
        view.showLoadingDialog();
        model.getAllRoutes(new AllPathFragmentContract.Model.OnFinishedListener() {
            @Override
            public void onSuccess(Route[] routes) {
                view.dismissLoadingDialog();
                view.updateLocationUI();
                view.drawRoutes(routes);
            }

            @Override
            public void onError(String response_body) {
                view.displayError(response_body);
            }

            @Override
            public void onUserUnauthorized(String response_body) {
                view.logOutUnauthorizedUser();
            }

        }, id_token);

    }



}
