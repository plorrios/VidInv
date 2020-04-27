package com.pabloor.vidinv.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pabloor.vidinv.Objects.Game;
import com.pabloor.vidinv.Objects.User;
import com.pabloor.vidinv.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{
    private List<User> userList;
    private UserAdapter.IClickListener shortClk;

    public UserAdapter (List<User> users, UserAdapter.IClickListener onClk) {
        userList = users;
        shortClk = onClk;
    }

    public interface IClickListener {
        void onClickListener(int position);
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View firstView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, null);
        final UserAdapter.ViewHolder vh = new UserAdapter.ViewHolder(firstView);

        firstView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shortClk.onClickListener(vh.getAdapterPosition());
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        holder.userItem.setText(userList.get(position).getNickname());
        Picasso.get().load(userList.get(position).getImage()).into(holder.userImage);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView userItem;
        private ImageView userImage;

        public ViewHolder (View view) {
            super(view);
            userItem = (TextView) view.findViewById(R.id.UserSearchTitle);
            userImage = (ImageView) view.findViewById(R.id.UserSearchImage);
        }
    }

    public void addUser(User user){ userList.add(user); }

    public User GetUser(int position){
        return userList.get(position);
    }
}