package com.pabloor.vidinv.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.pabloor.vidinv.R;

import java.util.List;

public class ListOfListsAdapter extends RecyclerView.Adapter<ListOfListsAdapter.ViewHolder> {
    Context cont;
    int res;
    List<String> gameList;
    private IClickListener clickList;
    private ILongClickListener longClickList;


    public ListOfListsAdapter(Context cnt, int resource, List<String> list, IClickListener onClk, ILongClickListener onLongClk) {
        cont = cnt;
        res = resource;
        gameList = list;
        clickList = onClk;
        longClickList = onLongClk;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameList;

        public ViewHolder (View view) {
            super(view);
            nameList = (TextView) view.findViewById(R.id.nameList);
        }
    }

    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inf = LayoutInflater.from(cont);
        View view = inf.inflate(res, null);

        return view;
    }

    @NonNull
    @Override
    public ListOfListsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View firstView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, null);
        final ViewHolder vh = new ViewHolder(firstView);

        firstView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickList.onClickListener(vh.getAdapterPosition());
            }
        });

        firstView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longClickList.onClickLongListener(vh.getAdapterPosition());
                return true;
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ListOfListsAdapter.ViewHolder holder, int position) {
        holder.nameList.setText(gameList.get(position));
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    public boolean deleteAll() {
        try {
            gameList.removeAll(gameList);
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
        notifyDataSetChanged();
        return true;
    }

    public interface IClickListener {
        void onClickListener(int position);
    }

    public interface ILongClickListener {
        void onClickLongListener(int position);
    }
}

