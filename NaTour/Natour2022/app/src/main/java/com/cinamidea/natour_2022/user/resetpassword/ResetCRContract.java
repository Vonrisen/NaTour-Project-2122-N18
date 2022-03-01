
package com.cinamidea.natour_2022.user.resetpassword;

public interface ResetCRContract {

    interface View {

        void changeFragmentToResetCRPassword();
        void resetDone(String message);
        void displayUsernameNotFoundError(String message);
        void displayResetError(String message);
    }

    interface Presenter {

        void requestCodeButtonClicked(String username);
        void resetPasswordButtonClicked(String username, String password, String code);

    }

    interface Model {

        interface OnFinishListener {
            void onSuccess(String message);
            void onError(String message);
        }

        void requestCode(String username, Model.OnFinishListener onFinishListener);
        void resetPassword(String username, String password, String code, Model.OnFinishListener onFinishListener);

    }

}
