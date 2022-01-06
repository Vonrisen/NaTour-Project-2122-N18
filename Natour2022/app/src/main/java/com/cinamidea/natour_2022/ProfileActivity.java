package com.cinamidea.natour_2022;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cinamidea.natour_2022.chat.HomeChatActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class ProfileActivity extends AppCompatActivity {

    private ImageButton button_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        button_menu = findViewById(R.id.activityProfile_menuButton);

        button_menu.setOnClickListener(view -> {


            startActivity(new Intent(ProfileActivity.this, HomeChatActivity.class));
            /*
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ProfileActivity.this, R.style.BottomSheetDialogTheme);
            View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(
                    R.layout.menu_bottom_layout,
                    findViewById(R.id.menuLayout_container)
            );

            bottomSheetView.findViewById(R.id.menuLayout_settings).setOnClickListener(view1 -> {
                Toast.makeText( ProfileActivity.this, "Settings...", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            });
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();

             */

        });



    }
}