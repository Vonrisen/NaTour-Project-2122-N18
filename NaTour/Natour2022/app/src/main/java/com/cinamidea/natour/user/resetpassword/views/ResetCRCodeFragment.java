package com.cinamidea.natour.user.resetpassword.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.chaos.view.PinView;
import com.cinamidea.natour.R;
import com.cinamidea.natour.user.resetpassword.ResetCRFragmentSwitcher;

public class ResetCRCodeFragment extends Fragment {

    private ResetCRFragmentSwitcher resetCRFragmentSwitcher;
    private Button button;
    private PinView pin_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_resetcr_code, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        resetCRFragmentSwitcher = (ResetCRFragmentSwitcher) getActivity();
        button = view.findViewById(R.id.fragmentCR2_continue);
        pin_view = view.findViewById(R.id.fragmentCR2_code);

        button.setOnClickListener(view1 -> {

            resetCRFragmentSwitcher.switchToSigninFragment(pin_view.getText().toString());

        });

    }

}