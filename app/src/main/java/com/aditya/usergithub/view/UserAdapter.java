package com.aditya.usergithub.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aditya.usergithub.R;
import com.aditya.usergithub.model.User;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private ArrayList<User> listUsers = new ArrayList<>();
    private OnItemClickCallBack onItemClickCallBack;

    public void setData(ArrayList<User> items) {
        listUsers.clear();
        listUsers.addAll(items);
        notifyDataSetChanged();
    }

    public void setOnItemClickCallBack(OnItemClickCallBack onItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack;
    }


    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_cardview, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserAdapter.UserViewHolder holder, int position) {
        User users = listUsers.get(position);
        Glide.with(holder.itemView.getContext())
                .load(users.getAvatarUrl())
                .into(holder.imgAvatar);
        holder.tvUserName.setText(users.getUserName());
        holder.tvUrl.setText(users.getHtmlUrl());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickCallBack.onItemClicked(listUsers.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return listUsers.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgAvatar;
        private TextView tvUserName;
        private TextView tvUrl;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAvatar = itemView.findViewById(R.id.user_image);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvUrl = itemView.findViewById(R.id.tv_user_url);
        }
    }

    public interface OnItemClickCallBack {
        void onItemClicked(User data);
    }
}
