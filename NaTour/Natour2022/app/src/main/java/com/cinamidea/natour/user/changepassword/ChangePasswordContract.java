package com.cinamidea.natour.user.changepassword;

import com.cinamidea.natour.utilities.UserType;


public interface ChangePasswordContract {

    interface View{
        void onPasswordChanged();
        void displayError(String message);
        void logOutUnauthorizedUser();
    }

    interface Model {
        interface OnFinishedListener{

            void onSuccess();
            void onError(String response);
            void onUserUnauthorized(String response);

        }
        void changePassword(UserType user_type, String old_password, String new_password, Model.OnFinishedListener listener);

    }

    interface Presenter {
        void changePasswordButtonPressed(UserType user_type, String old_password, String new_password);
    }

}
