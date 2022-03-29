package com.cinamidea.natour.navigation.main.views;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.cinamidea.natour.MainActivity;
import com.cinamidea.natour.R;
import com.cinamidea.natour.admin.AdminActivity;
import com.cinamidea.natour.chat.HomeChatActivity;
import com.cinamidea.natour.home.HomeFragment;
import com.cinamidea.natour.map.MapActivity;
import com.cinamidea.natour.map.views.AllPathsFragment;
import com.cinamidea.natour.navigation.compilation.views.CompilationActivity;
import com.cinamidea.natour.navigation.main.contracts.HomeActivityContract;
import com.cinamidea.natour.navigation.main.models.HomeActivityModel;
import com.cinamidea.natour.navigation.main.presenters.HomeActivityPresenter;
import com.cinamidea.natour.navigation.search.views.GeoSearchActivity;
import com.cinamidea.natour.navigation.search.views.SearchActivity;
import com.cinamidea.natour.user.changepassword.ChangePasswordActivity;
import com.cinamidea.natour.utilities.UserType;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.logger.ChatLogLevel;
import io.getstream.chat.android.client.models.User;
import io.getstream.chat.android.livedata.ChatDomain;
import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;


@RequiresApi(api = Build.VERSION_CODES.N)
public class HomeActivity extends AppCompatActivity implements HomeActivityContract.View {

    private static final int PERMISSION_REQUEST_CODE = 1;
    public static CircleImageView imgbutton_avatar;
    public static boolean is_updated = false;
    public static boolean[] counter_updated = {true, true, true};
    ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                        Boolean fineLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_FINE_LOCATION, false);
                        Boolean coarseLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_COARSE_LOCATION, false);
                        AllPathsFragment.locationPermissionGranted = fineLocationGranted && coarseLocationGranted;
                    }
            );
    private TextView textview_username;
    private ImageButton button_home, button_profile;
    private Fragment fragment_home, fragment_profile;
    private ImageButton button_openmap, button_search, button_chat_or_menu;
    private FragmentManager fragmentManager;
    private byte check_chat_or_menu = 0;
    private ChatClient client;

    private HomeActivityContract.Presenter presenter;
    private  UserType user_type;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        user_type = new UserType(this);
        username = user_type.getUsername();

        setupViewComponents();
        setupChatUser();
        setListeners();

        presenter = new HomeActivityPresenter(this, new HomeActivityModel());
        fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.activityHome_fragmentcontainer, fragment_home);
        fragmentTransaction.add(R.id.activityHome_fragmentcontainer, fragment_profile);
        fragmentTransaction.hide(fragment_profile);
        fragmentTransaction.commit();

        button_home.performClick();
        getPermissions();

    }

    private void setupViewComponents() {

        fragment_profile = new ProfileFragment();
        fragment_home = new HomeFragment();

        imgbutton_avatar = findViewById(R.id.activityHome_avatar);
        textview_username = findViewById(R.id.activityHome_usernameText);
        button_home = findViewById(R.id.activityHome_home);
        button_profile = findViewById(R.id.activityHome_profile);
        button_chat_or_menu = findViewById(R.id.activityHome_chat_or_menu);
        button_search = findViewById(R.id.activityHome_search);
        button_openmap = findViewById(R.id.activityHome_openMap);

        textview_username.setText(username);

        Glide.with(this).load("https://streamimages1.s3.eu-central-1.amazonaws.com/Users/ProfilePics/"+user_type.getUsername()).placeholder(R.drawable.natour_avatar).into(imgbutton_avatar);


    }

    private void setupChatUser() {
        client = new ChatClient.Builder(getString(R.string.chat_api_key), getApplicationContext())
                .logLevel(ChatLogLevel.ALL) // Set to NOTHING in prod
                .build();
        new ChatDomain.Builder(client, getApplicationContext()).build();

        User user = new User();

        user.setId(username);
        user.setName(username);
        user.setImage("https://streamimages1.s3.eu-central-1.amazonaws.com/Users/ProfilePics/" + username);
        String token = client.devToken(user.getId());
        client.connectUser(
                user,
                token
        ).enqueue();
    }

    private void setListeners() {

        button_home.setOnClickListener(view -> {

            button_home.setClickable(false);
            button_home.setImageResource(R.drawable.ic_home_active);
            button_profile.setImageResource(R.drawable.ic_profile_inactive);
            button_chat_or_menu.setImageResource(R.drawable.ic_chat);
            check_chat_or_menu = 0;

            changeFragment(fragment_home);

            button_profile.setClickable(true);
            imgbutton_avatar.setClickable(true);

        });

        button_search.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {

                Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.select_search);
                dialog.getWindow().setBackgroundDrawable(this.getDrawable(R.drawable.background_alert_dialog));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();

                dialog.findViewById(R.id.selectSearch_normal).setOnClickListener(v -> {
                    dialog.hide();
                    startActivity(new Intent(this, SearchActivity.class));
                });

                dialog.findViewById(R.id.selectSearch_Geo).setOnClickListener(v -> {
                    dialog.hide();
                    startActivity(new Intent(this, GeoSearchActivity.class));
                });

            }else{
                getPermissions();
            }

        });

        button_profile.setOnClickListener(view -> {

            button_profile.setClickable(false);
            imgbutton_avatar.setClickable(false);
            button_profile.setImageResource(R.drawable.ic_profile_active);
            button_home.setImageResource(R.drawable.ic_home_inactive);
            button_chat_or_menu.setImageResource(R.drawable.ic_menu);
            check_chat_or_menu = 1;

            changeFragment(fragment_profile);


            button_home.setClickable(true);

        });

        imgbutton_avatar.setOnClickListener(view -> {

            button_profile.performClick();

        });

        button_chat_or_menu.setOnClickListener(view -> {

            if (check_chat_or_menu == 0)
                startActivity(new Intent(this, HomeChatActivity.class));
            else
                openMenu();

        });

        button_openmap.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(this, MapActivity.class));
            } else {
                showPermissionDialog();
            }
        });
    }

    private void changeFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (fragment instanceof HomeFragment) {

            if (is_updated) {
                fragmentTransaction.remove(fragment_home);
                fragment_home = new HomeFragment();
                fragmentTransaction.add(R.id.activityHome_fragmentcontainer, fragment_home);
                is_updated = false;
            }
            fragmentTransaction.show(fragment_home);
            fragmentTransaction.hide(fragment_profile);

        } else if (fragment instanceof ProfileFragment) {

            if (is_updated) {
                fragmentTransaction.remove(fragment_profile);
                fragment_profile = new ProfileFragment();
                fragmentTransaction.add(R.id.activityHome_fragmentcontainer, fragment_profile);
                is_updated = false;
            }
            fragmentTransaction.show(fragment_profile);
            fragmentTransaction.hide(fragment_home);

        }
        fragmentTransaction.commit();

    }

    private void openMenu() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.menu_bottom_layout,
                findViewById(R.id.menuLayout_container)
        );

        if(user_type.getUsername().equals("admin0"))
            bottomSheetView.findViewById(R.id.menuLayout_admin).setVisibility(View.VISIBLE);

        if (!new UserType(getApplicationContext()).getUserType().equals("Cognito"))
            bottomSheetView.findViewById(R.id.menuLayout_changePassword).setVisibility(View.GONE);

        bottomSheetView.findViewById(R.id.menuLayout_logout).setOnClickListener(view1 -> {
            bottomSheetDialog.dismiss();
            logout();
        });
        bottomSheetView.findViewById(R.id.menuLayout_savedPaths).setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            startActivity(new Intent(this, CompilationActivity.class));

        });
        bottomSheetView.findViewById(R.id.menuLayout_changePassword).setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            startActivity(new Intent(this, ChangePasswordActivity.class));
        });

        bottomSheetView.findViewById((R.id.menuLayout_editPhoto)).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            openImage();
        });

        bottomSheetView.findViewById((R.id.menuLayout_admin)).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            startActivity(new Intent(this, AdminActivity.class));
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

    }

    private void openImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 4);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 4 && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            Uri uri = data.getData();
            try {
                InputStream iStream = getContentResolver().openInputStream(uri);
                UserType user_type = new UserType(this);
                presenter.putImage(user_type.getUsername(),user_type.getUserType()+user_type.getIdToken(),iStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == PERMISSION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data == null)
                return;

            AllPathsFragment.locationPermissionGranted = true;
        }


    }


    public void showPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enable Permissions");
        builder.setMessage("You have to enable permissions in order to use the map");
        Intent permissions_intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        builder.setPositiveButton("Enable permissions", (dialog, which) ->
                startActivityForResult(permissions_intent, PERMISSION_REQUEST_CODE)).setNegativeButton("No, Just Exit", (dialog, which) -> {
        });
        AlertDialog mGPSDialog = builder.create();
        mGPSDialog.show();
    }

    private boolean getPermissions() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            AllPathsFragment.locationPermissionGranted = true;
        } else {
            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }

        return AllPathsFragment.locationPermissionGranted;
    }

    private void logout() {

        Log.d("LOGOUT", "Logging user out..");
        UserType user_type = new UserType(this);

        if(user_type.getUserType().equals("Google")) {
            GoogleSignInClient googlesignin_client = GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestIdToken(("556927589955-pahgt8na4l8de0985mvlc9gugfltbkef.apps.googleusercontent.com"))
                    .build());
            googlesignin_client.signOut();
        }
        user_type.clear();
        Log.d("LOGOUT", "User logged out successfully");
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);


    }

    @Override
    public void loadProfileImage(byte[] image_as_byte_array) {
        runOnUiThread(() -> Glide.with(HomeActivity.this).load(image_as_byte_array).circleCrop().into(imgbutton_avatar));
    }

    @Override
    public void setChatUserImage() {
        client.getCurrentUser().setImage("https://streamimages1.s3.eu-central-1.amazonaws.com/Users/ProfilePics/"+ user_type.getUsername());
    }

    @Override
    public void displayError(String message) {
        runOnUiThread(()-> {
            MotionToast.Companion.createColorToast(HomeActivity.this, "",
                    message,
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(getApplicationContext(), R.font.helvetica_regular));
        });

    }

    @Override
    public void logOutUnauthorizedUser() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}