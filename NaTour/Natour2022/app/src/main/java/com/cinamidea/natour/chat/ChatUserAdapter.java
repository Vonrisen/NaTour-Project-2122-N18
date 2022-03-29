package com.cinamidea.natour.chat;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.cinamidea.natour.R;

import java.util.List;

import io.getstream.chat.android.client.models.User;

public class ChatUserAdapter extends ArrayAdapter<User> {
    public ChatUserAdapter(@NonNull Activity context, List<User> userList) {
        super(context, 0, userList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listView = convertView;
        if (listView == null) {
            listView = LayoutInflater.from(getContext()).inflate(R.layout.chat_list_users, parent, false);
        }

        User current_user = getItem(position);

        TextView nome = listView.findViewById(R.id.username_list_users);
        nome.setText(current_user.getId());

        ImageView immagine_profilo = listView.findViewById(R.id.img_list_users);
        Glide.with(getContext()).load(current_user.getImage()).placeholder(R.drawable.natour_avatar).into(immagine_profilo);

        return listView;
    }

}
