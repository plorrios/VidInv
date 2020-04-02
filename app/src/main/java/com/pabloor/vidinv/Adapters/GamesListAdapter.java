package com.pabloor.vidinv.Adapters;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pabloor.vidinv.Objects.Game;
import com.pabloor.vidinv.Objects.GamesList;
import com.pabloor.vidinv.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class GamesListAdapter extends RecyclerView.Adapter<GamesListAdapter.ViewHolder>{

    InterfaceClick interfaceClick;
    List<Game> games;


    public GamesListAdapter(GamesList gamesL, InterfaceClick ParameterInterfaceClick){
        games = Arrays.asList(gamesL.GetGames());
        interfaceClick = ParameterInterfaceClick;
    }

    public Game GetGame(int position){
        return games.get(position);
    }


    @NonNull
    @Override
    public GamesListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View FirstView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game,parent,false);
        final ViewHolder viewHolder = new ViewHolder(FirstView);
        FirstView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    interfaceClick.OnInterfaceClick(viewHolder.getAdapterPosition());

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        return viewHolder;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;

        ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.GameSearchedImage);
            textView = view.findViewById(R.id.GameSearchedTitle);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull GamesListAdapter.ViewHolder holder, int position) {
        Picasso.get().load(games.get(position).getBackgroundImage()).resize(107,60).into(holder.imageView);
        holder.textView.setText(games.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public interface InterfaceClick{
        void OnInterfaceClick(int position) throws UnsupportedEncodingException;
    }

}
