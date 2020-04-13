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

    public ListOfListsAdapter(Context cnt, int resource, List<String> list) {
        cont = cnt;
        res = resource;
        gameList = list;
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
}
