package com.cinamidea.natour.user.signup.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.core.content.res.ResourcesCompat;

import com.cinamidea.natour.R;
import com.cinamidea.natour.user.CustomAuthFragment;
import com.cinamidea.natour.user.signup.contracts.SignUpContract;
import com.cinamidea.natour.user.signup.models.SignUpModel;
import com.cinamidea.natour.user.signup.presenters.SignUpPresenter;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class SignupFragment extends CustomAuthFragment implements SignUpContract.View {

    private Button signup_button;
    private EditText edit_user;
    private EditText edit_email;
    private EditText edit_password;
    private EditText edit_retypepassword;
    private String username;
    private ProgressBar progressbar;

    private SignUpContract.Presenter presenter = new SignUpPresenter(this, new SignUpModel());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViewComponents(view);
        setListeners();

    }

    @Override
    protected void setupViewComponents(View view) {

        signup_button = view.findViewById(R.id.fragmentSignup_signup);
        edit_user = view.findViewById(R.id.fragmentSignup_username);
        edit_email = view.findViewById(R.id.fragmentSignup_email);
        edit_password = view.findViewById(R.id.fragmentSignup_password);
        edit_retypepassword = view.findViewById(R.id.fragmentSignup_retypepassw);
        progressbar = view.findViewById(R.id.fragmentSignup_progressbar);

        setupAnimation();

    }

    private void setListeners() {

        signup_button.setOnClickListener(v -> {

            runAnimation(signup_button);
            String password = edit_password.getText().toString();
            String retype_password = edit_retypepassword.getText().toString();

            if (!password.equals(retype_password)) {

               displayError("Passwords don't match");
                return;

            }

            progressbar.setVisibility(View.VISIBLE);
            String username = edit_user.getText().toString();
            this.username = username;
            String email = edit_email.getText().toString();
            presenter.signUpButtonPressed(username,email,password);
        });

    }

    @Override
    public void signUpSuccess() {
        getActivity().runOnUiThread(() -> progressbar.setVisibility(View.GONE));
        Intent intent = new Intent(getActivity(), ConfirmSignupActivity.class);
        intent.putExtra("username", username);
        getActivity().startActivity(intent);
    }

    @Override
    public void displayError(String message) {
        getActivity().runOnUiThread(()-> {
            progressbar.setVisibility(View.GONE);
            MotionToast.Companion.createColorToast(getActivity(),"",
                    message,
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(getContext(),R.font.helvetica_regular));
        });
    }
}