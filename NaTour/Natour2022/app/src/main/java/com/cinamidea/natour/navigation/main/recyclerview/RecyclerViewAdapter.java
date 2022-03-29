package com.cinamidea.natour.navigation.main.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cinamidea.natour.R;
import com.cinamidea.natour.chat.HomeChatActivity;
import com.cinamidea.natour.entities.Route;
import com.cinamidea.natour.map.DetailedMap;
import com.cinamidea.natour.navigation.compilation.views.CompilationActivity;
import com.cinamidea.natour.navigation.main.views.HomeActivity;
import com.cinamidea.natour.report.ReportActivity;
import com.cinamidea.natour.utilities.UserType;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    Context context;
    ArrayList<Route> routes;
    ArrayList<Route> favourite_routes;
    final DecimalFormat df = new DecimalFormat("0.00");

    private boolean is_favourite_fragment = false;
    private boolean is_tovisit_fragment = false;
    private UserType user_type;

    public RecyclerViewAdapter(Context context, ArrayList<Route> routes, boolean is_favourite_fragment) {
        this.context = context;
        this.routes = routes;
        this.is_favourite_fragment = is_favourite_fragment;
    }


    public RecyclerViewAdapter(Context context, ArrayList<Route> routes, ArrayList<Route> favourite_routes, boolean is_tovisit_fragment) {
        this.context = context;
        this.routes = routes;
        this.favourite_routes = favourite_routes;
        this.is_tovisit_fragment = is_tovisit_fragment;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.post, parent, false);
        user_type = new UserType(context);
        return new MyViewHolder(view);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupHolder(MyViewHolder holder, Route route) {

        SpannableStringBuilder sb = new SpannableStringBuilder(route.getCreator_username() + " " + route.getDescription());
        sb.setSpan(new StyleSpan(Typeface.BOLD), 0, route.getCreator_username().length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        holder.image.layout(0,0,0,0);

        holder.username.setText(route.getCreator_username());
        holder.title.setText(route.getName());
        holder.description.setText(sb);
        holder.duration.setText(getFormattedTime(route.getDuration()));
        holder.length.setText(getKm(route.getLength()) + "km");
        holder.difficulty.setText(route.getLevel());
        holder.favourites_number.setText(String.valueOf(route.getLikes()));
        String route_name = route.getName().replace(" ","+");
        Glide.with(context).load("https://streamimages1.s3.eu-central-1.amazonaws.com/Routes/Images/"+route_name).into(holder.image);
        Glide.with(context).load("https://streamimages1.s3.eu-central-1.amazonaws.com/Users/ProfilePics/"+holder.username.getText().toString()).circleCrop().placeholder(R.drawable.natour_avatar).into(holder.avatar);

        if (holder.username.getText().toString().equals(user_type.getUsername()))
            holder.chat.setVisibility(View.GONE);

        if (route.isDisability_access())
            holder.handicap.setVisibility(View.VISIBLE);

        if (route.getReport_count() >= 3)
            holder.isreported.setVisibility(View.VISIBLE);

        if (is_favourite_fragment == true) {

            holder.favourite.setImageResource(R.drawable.ic_liked);
            holder.favourite.setTag(1);

        }

        if (favourite_routes != null) {

            for (Route r : favourite_routes) {

                if (route.getName().equals(r.getName())) {
                    holder.favourite.setImageResource(R.drawable.ic_liked);
                    holder.favourite.setTag(1);
                }

            }

        }

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.presenter = new HomeRecyclerPresenter(holder, position, new HomeRecyclerModel());

        Route route = routes.get(position);
        setupHolder(holder, route);

        holder.chat.setOnClickListener(view -> {

            ArrayList<String> members = new ArrayList<>();
            members.add(user_type.getUsername());
            members.add(holder.username.getText().toString());
            Intent chat_intent = new Intent(context, HomeChatActivity.class);
            chat_intent.putStringArrayListExtra("members", members);
            context.startActivity(chat_intent);

        });

        holder.open_map.setOnClickListener(view -> {

            Intent map_detail = new Intent(context, DetailedMap.class);
            map_detail.putParcelableArrayListExtra("route", (ArrayList<? extends Parcelable>) route.getCoordinates());
            context.startActivity(map_detail);

        });

        holder.options.setOnClickListener(view -> {

            UserType user_type = new UserType(context);
            String id_token = user_type.getUserType() + user_type.getIdToken();
            openMenu(holder.options_container, holder, route, id_token);

        });

        holder.favourite.setOnClickListener(view -> {

            holder.favourite.setClickable(false);

            UserType user_type = new UserType(context);
            String id_token = user_type.getUserType() + user_type.getIdToken();

            if (holder.favourite.getTag().equals(1)) {
                holder.favourite.setImageResource(R.drawable.ic_like);
                holder.presenter.deleteFavouriteButtonClicked(user_type.getUsername(),route.getName(), id_token);
            } else {
                holder.favourite.setImageResource(R.drawable.ic_liked);
                holder.presenter.insertFavouriteButtonClicked(user_type.getUsername(),route.getName(), id_token);
            }


        });

        holder.add_compilation.setOnClickListener(view -> {

            Intent intent = new Intent(context, CompilationActivity.class);
            intent.putExtra("route_name", route.getName());
            context.startActivity(intent);

        });


    }

    @Override
    public int getItemCount() {
        return routes.size();
    }

    private String getKm(float meters) {

        meters = meters / 1000;

        return df.format(meters);

    }

    private String getFormattedTime(int time) {

        int hours = time / 60;
        int minutes = time % 60;

        String formattedTime = hours + "h" + minutes + "m";

        return formattedTime;

    }

    private void openMenu(ViewGroup options_container, MyViewHolder holder, Route route, String id_token) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(context).inflate(
                R.layout.post_options, options_container);
        if (is_tovisit_fragment)
            ((Button) bottomSheetView.findViewById(R.id.post_addtovisit)).setText("Remove From ToVisit");

        bottomSheetView.findViewById(R.id.post_addtovisit).setOnClickListener(view1 -> {
            bottomSheetDialog.dismiss();
            if (is_tovisit_fragment)
                holder.presenter.deleteToVisitButtonClicked(user_type.getUsername(),route.getName(),id_token);
            else
                holder.presenter.insertToVisitButtonClicked(user_type.getUsername(), route.getName(),id_token);

        });
        bottomSheetView.findViewById(R.id.post_report).setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            Intent intent = new Intent(context, ReportActivity.class);
            intent.putExtra("route_name",route.getName());
            context.startActivity(intent);
        });
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    public /*static */ class MyViewHolder extends RecyclerView.ViewHolder implements HomeRecyclerContract.View {

        TextView username;
        TextView title, description;
        TextView difficulty;
        ImageView image;
        TextView duration, length;
        ImageView handicap;
        ImageButton favourite;
        ImageButton add_compilation;
        ViewGroup options_container;
        ImageButton options;
        ImageButton chat;
        TextView favourites_number;
        ImageView isreported;
        ImageButton open_map;
        HomeRecyclerContract.Presenter presenter;
        ImageView avatar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.post_username);
            title = itemView.findViewById(R.id.post_title);
            description = itemView.findViewById(R.id.post_description);
            difficulty = itemView.findViewById(R.id.post_difficulty);
            image = itemView.findViewById(R.id.post_image);
            duration = itemView.findViewById(R.id.post_duration);
            length = itemView.findViewById(R.id.post_length);
            add_compilation = itemView.findViewById(R.id.post_collection);
            handicap = itemView.findViewById(R.id.post_handicap);
            favourite = itemView.findViewById(R.id.post_favourite);
            options = itemView.findViewById(R.id.post_options);
            isreported = itemView.findViewById(R.id.post_isreported);
            favourites_number = itemView.findViewById(R.id.post_numberoffavourites);
            options_container = itemView.findViewById(R.id.post_options_container);
            chat = itemView.findViewById(R.id.post_chat);
            open_map = itemView.findViewById(R.id.post_map);
            avatar = itemView.findViewById(R.id.post_avatar);




        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void addedToFavourites(int position) {

            Route route = routes.get(position);

            ((Activity) context).runOnUiThread(() -> {
                favourite.setTag(1);
                route.setLikes(route.getLikes() + 1);
                favourites_number.setText(String.valueOf(route.getLikes()));
                favourite.setClickable(true);
            });

            HomeActivity.is_updated = true;
            for (int i = 0; i < 3; i++)
                HomeActivity.counter_updated[i] = false;

            Log.d("HOME", "Favourite added.");

        }

        @Override
        public void addFavouriteError() {
            ((Activity) context).runOnUiThread(() -> {
                favourite.setImageResource(R.drawable.ic_like);
                favourite.setClickable(true);
            });
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void deletedFromFavourites(int position) {

            Route route = routes.get(position);

            ((Activity) context).runOnUiThread(() -> {
                favourite.setTag(0);
                route.setLikes(route.getLikes() - 1);
                favourites_number.setText(String.valueOf(route.getLikes()));
                favourite.setClickable(true);
                if (is_favourite_fragment) {
                    routes.remove(route);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, routes.size());
                    itemView.setVisibility(View.GONE);

                }
            });
            HomeActivity.is_updated = true;
            for (int i = 0; i < 3; i++)
                HomeActivity.counter_updated[i] = false;

            Log.d("HOME", "Favourite removed.");
        }

        @Override
        public void deleteFavouriteError(String message) {

        }


        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void addedToVisit() {
            HomeActivity.counter_updated[2] = false;
            Log.d("HOME", "To visit added.");
        }

        @Override
        public void addToVisitError(String message) {

        }

        @Override
        public void deletedFromToVisit(int position) {
            ((Activity) context).runOnUiThread(() -> {

                routes.remove(routes.get(position));
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, routes.size());
                itemView.setVisibility(View.GONE);

            });

            Log.d("HOME", "To visit removed.");

        }

        @Override
        public void deleteToVisitError(String message) {

        }


    }



}
