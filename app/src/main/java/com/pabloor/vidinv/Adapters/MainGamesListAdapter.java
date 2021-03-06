package com.pabloor.vidinv.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

public class MainGamesListAdapter extends RecyclerView.Adapter<MainGamesListAdapter.CustomViewHolder>{

    InterfaceClick interfaceClick;
    GamesList gamesList;
    ArrayList<Game> games;
    private Context mContext;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;


    public MainGamesListAdapter(Context context , GamesList gamesL, InterfaceClick ParameterInterfaceClick){
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

    public Game[] GetGames(){
        return gamesList.GetGames();
    }

    public void ChangeGames(GamesList gamesl){
        gamesList.changeGames(gamesl.GetGames());
        games.clear();
        games.addAll(Arrays.asList(gamesl.GetGames()));
        notifyDataSetChanged();
    }

    public void removeTopGame(){
        if (games.size()!=0)
        games.remove(games.size()-1);
    }

    public void addFakeTop(){
        games.add(null);
        notifyItemRangeInserted(games.size(),1);
    }

    public Game getTopGame(){
        return games.get(games.size()-1);
    }


    @NonNull
    @Override
    public MainGamesListAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("",Integer.toString(viewType));
        if (viewType == VIEW_TYPE_ITEM) {
            View FirstView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game2,parent,false);
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
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;

        ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.GameSearchedImage2);
            textView = view.findViewById(R.id.GameSearchedTitle);
        }
    }

    @Override
    public void onBindViewHolder(MainGamesListAdapter.CustomViewHolder holder, int position) {

        if (games.get(position)==null){}

        if (holder instanceof CustomViewHolder && games.get(position)!=null) {
            Picasso.get().load(games.get(position).getBackgroundImage()).resize(200,112).into(holder.image);
            //holder.name.setText(games.get(position).getName());

            RequestOptions options = new RequestOptions();
            Glide
                    .with(this.mContext)
                    .load(games.get(position).getBackgroundImage())
                    .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(10)))
                    .into(holder.image);
        } else if (holder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) holder, position);
        }

    }

    @Override
    public int getItemViewType(int position) {
        return games.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
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
            image = (ImageView) itemView.findViewById(R.id.GameSearchedImage2);
            //name = (TextView) itemView.findViewById(R.id.GameSearchedTitle);
        }
    }


    private class LoadingViewHolder extends CustomViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed

    }

}
