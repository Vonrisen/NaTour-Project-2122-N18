package com.cinamidea.natour.user.resetpassword.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.cinamidea.natour.R;
import com.cinamidea.natour.user.resetpassword.ResetCRFragmentSwitcher;

public class ResetCRUserFragment extends Fragment {

    private ResetCRFragmentSwitcher resetCRFragmentSwitcher;
    private Button button;
    private TextView text_back;
    private EditText et_username;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_resetcr_user, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        resetCRFragmentSwitcher = (ResetCRFragmentSwitcher) getActivity();
        button = view.findViewById(R.id.fragmentCR1_continue);
        text_back = view.findViewById(R.id.fragmentCR1_back);
        et_username = view.findViewById(R.id.fragmentCR1_user);

        button.setOnClickListener(view1 -> {

            resetCRFragmentSwitcher.switchToResetCRPasswordFragment(et_username.getText().toString());

        });

        text_back.setOnClickListener(view12 -> getActivity().finish());

    }

}