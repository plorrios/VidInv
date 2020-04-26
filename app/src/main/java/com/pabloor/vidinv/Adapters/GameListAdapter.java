package com.pabloor.vidinv.Adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pabloor.vidinv.Objects.Game;
import com.pabloor.vidinv.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.ViewHolder> {
    private List<Game> gameList;
    private IClickListener shortClk;
    private ILongClickListener longClk;

    public GameListAdapter (List<Game> games, IClickListener onClk, ILongClickListener onLongClk) {
        gameList = games;
        shortClk = onClk;
        longClk = onLongClk;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View firstView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game, null);
        final GameListAdapter.ViewHolder vh = new GameListAdapter.ViewHolder(firstView);

        firstView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shortClk.onClickListener(vh.getAdapterPosition());
            }
        });

        firstView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longClk.onClickLongListener(vh.getAdapterPosition());
                return true;
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.gameItem.setText(gameList.get(position).getName());
        Picasso.get().load(gameList.get(position).getDescription()).into(holder.gameImage);
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView gameItem;
        private ImageView gameImage;

        public ViewHolder (View view) {
            super(view);
            gameItem = (TextView) view.findViewById(R.id.GameSearchedTitle);
            gameImage = (ImageView) view.findViewById(R.id.GameSearchedImage);
        }
    }

    public interface IClickListener {
        void onClickListener(int position);
    }

    public interface ILongClickListener {
        void onClickLongListener(int position);
    }
}
