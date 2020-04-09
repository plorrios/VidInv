package com.pabloor.vidinv.Adapters;

import android.content.Context;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.pabloor.vidinv.Objects.Game;
import com.pabloor.vidinv.Objects.GamesList;
import com.pabloor.vidinv.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GamesListAdapter extends RecyclerView.Adapter<GamesListAdapter.CustomViewHolder>{

    InterfaceClick interfaceClick;
    GamesList gamesList;
    ArrayList<Game> games;
    private Context mContext;


    public GamesListAdapter(Context context , GamesList gamesL, InterfaceClick ParameterInterfaceClick){
        gamesList = gamesL;
        games = new ArrayList<>();
        games.addAll(Arrays.asList(gamesL.GetGames()));
        interfaceClick = ParameterInterfaceClick;
        mContext = context;
    }

    public Game GetGame(int position){
        return games.get(position);
    }

    public void addGames(GamesList gamesl)
    {
        games.addAll(Arrays.asList(gamesl.GetGames()));
    }

    public Game GetGames(int position){
        return games.get(position);
    }


    @NonNull
    @Override
    public GamesListAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View FirstView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game,parent,false);
        final CustomViewHolder viewHolder = new CustomViewHolder(FirstView);
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
    public void onBindViewHolder(GamesListAdapter.CustomViewHolder holder, int position) {

        Picasso.get().load(games.get(position).getBackgroundImage()).resize(107,60).into(holder.image);
        holder.name.setText(games.get(position).getName());

        RequestOptions options = new RequestOptions();
        Glide
                .with(this.mContext)
                .load(games.get(position).getBackgroundImage())
                .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(10)))
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public interface InterfaceClick{
        void OnInterfaceClick(int position) throws UnsupportedEncodingException;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name;

        public CustomViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.GameSearchedImage);
            name = (TextView) itemView.findViewById(R.id.GameSearchedTitle);
        }
    }

}
