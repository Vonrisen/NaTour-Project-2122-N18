package com.cinamidea.natour.user.resetpassword.views;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cinamidea.natour.R;
import com.cinamidea.natour.user.resetpassword.ResetCRContract;
import com.cinamidea.natour.user.resetpassword.ResetCRFragmentSwitcher;
import com.cinamidea.natour.user.resetpassword.ResetCRModel;
import com.cinamidea.natour.user.resetpassword.ResetCRPresenter;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class ResetCRActivity extends AppCompatActivity implements ResetCRFragmentSwitcher, ResetCRContract.View {

    private String username;
    private String password;
    private ResetCRPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetcr);

        presenter = new ResetCRPresenter(this, new ResetCRModel());

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activityResetCR_framelayout, new ResetCRUserFragment());
        fragmentTransaction.commit();

    }

    @Override
    public void switchToResetCRPasswordFragment(String username) {

        this.username = username;
        presenter.requestCodeButtonClicked(username);

    }

    @Override
    public void switchToSigninFragment(String confirmation_code) {

        presenter.resetPasswordButtonClicked(username, password, confirmation_code);

    }

    @Override
    public void switchToResetCRCodeFragment(String password) {

        this.password = password;
        changeFragmentToResetCRCode();

    }

    private void changeFragmentToResetCRCode() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activityResetCR_framelayout, new ResetCRCodeFragment());
        fragmentTransaction.commit();

    }

    @Override
    public void changeFragmentToResetCRPassword() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activityResetCR_framelayout, new ResetCRPasswordFragment());
        fragmentTransaction.commit();

    }

    @Override
    public void resetDone(String message) {
        runOnUiThread(()-> {
            MotionToast.Companion.createColorToast(ResetCRActivity.this,"",
                    message,
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(getApplicationContext(),R.font.helvetica_regular));
        });
        finish();
    }

    @Override
    public void displayUsernameNotFoundError(String message) {
        runOnUiThread(()-> {
            MotionToast.Companion.createColorToast(ResetCRActivity.this,"",
                    message,
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(getApplicationContext(),R.font.helvetica_regular));
        });
    }

    @Override
    public void displayResetError(String message) {
        runOnUiThread(()-> {
            MotionToast.Companion.createColorToast(ResetCRActivity.this,"",
                    message,
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(getApplicationContext(),R.font.helvetica_regular));
        });
    }
}