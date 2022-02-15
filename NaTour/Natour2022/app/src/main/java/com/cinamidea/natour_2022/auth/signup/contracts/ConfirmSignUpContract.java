package com.cinamidea.natour_2022.auth.signup.contracts;

public interface ConfirmSignUpContract {

    interface View{
        void confirmSignUpSuccess();
        void displayError(String message);
    }

    interface Presenter{
        void confirmSignUpButtonPressed(String username, String confirmation_code);
    }

    interface Model{

        interface OnFinishListener {
            void onSuccess(String message);
            void onFailure(String message);
        }
        void confirmSignUp(String username, String confirmation_code, OnFinishListener listener);
    }

}
