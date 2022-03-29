package com.cinamidea.natour.user.resetpassword.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.cinamidea.natour.R;
import com.cinamidea.natour.user.resetpassword.ResetCRFragmentSwitcher;

public class ResetCRPasswordFragment extends Fragment {

    private ResetCRFragmentSwitcher resetCRFragmentSwitcher;
    private Button button;
    private EditText et_password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_resetcr_password, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        resetCRFragmentSwitcher = (ResetCRFragmentSwitcher) getActivity();
        button = view.findViewById(R.id.fragmentCR3_continue);
        et_password = view.findViewById(R.id.fragmentCR3_password);

        button.setOnClickListener(view1 -> {

            resetCRFragmentSwitcher.switchToResetCRCodeFragment(et_password.getText().toString());

        });

    }

}