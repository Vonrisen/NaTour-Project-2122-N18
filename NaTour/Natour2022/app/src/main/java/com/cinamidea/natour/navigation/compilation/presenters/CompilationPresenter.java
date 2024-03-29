package com.cinamidea.natour.navigation.compilation.presenters;

import com.cinamidea.natour.entities.RoutesCompilation;
import com.cinamidea.natour.navigation.compilation.contracts.CompilationContract;

import java.util.ArrayList;

public class CompilationPresenter implements CompilationContract.Presenter, CompilationContract.Model.OnFinishedListener {

    private final CompilationContract.View view;
    private final CompilationContract.Model model;

    public CompilationPresenter(CompilationContract.View view, CompilationContract.Model model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void getUserCompilationsButtonClicked(String username, String id_token) {
        model.getUserCompilations(username, id_token, this);
    }


    @Override
    public void onSuccess(ArrayList<RoutesCompilation> compilations) {
        view.loadCompilations(compilations);
    }

    @Override
    public void onError(String response) {
        view.displayError(response);
    }

    @Override
    public void onUserUnauthorized() {
        view.logOutUnauthorizedUser();
    }

}
