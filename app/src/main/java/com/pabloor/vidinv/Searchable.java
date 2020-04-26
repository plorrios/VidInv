package com.pabloor.vidinv;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.pabloor.vidinv.Adapters.GamesListAdapter;
import com.pabloor.vidinv.Objects.Game;
import com.pabloor.vidinv.Objects.GamesList;
import com.pabloor.vidinv.tasks.GetGameListThread;
import com.pabloor.vidinv.tasks.GetGameThread;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class Searchable extends Fragment {

    Searchable s = this;
    GamesListAdapter adapter;
    GetGameListThread task;
    GamesList gamesList;
    Handler handler;
    String query;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    LinearLayoutManager linearLayoutManager;
    int page;
    boolean noMoreGames = false;
    View rootView;
    boolean isLoading = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_searchable);
        handler = new Handler();
        page = 1;

        Game[] games = new Game[0];
        gamesList = new GamesList(games);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.activity_searchable, container, false);


        adapter = new GamesListAdapter(getActivity(), gamesList, new GamesListAdapter.InterfaceClick() {
            @Override
            public void OnInterfaceClick(int position) {
                if (adapter.GetGame(position) == null && getActivity() instanceof  SearchActivity) {
                    Toast.makeText(getActivity(), "Game not found", Toast.LENGTH_SHORT).show();
                }
                        Intent intent = new Intent(getActivity(), GamePageActivity.class);
                        intent.putExtra("GAME_ID", adapter.GetGame(position).getId());
                        startActivity(intent);
            }
        });

        //RecyclerView recyclerView = findViewById(R.id.SearchableLayout);
        RecyclerView recyclerView = rootView.findViewById(R.id.SearchableLayout);
        linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount && !noMoreGames &&  !isLoading) {
                        adapter.addFakeTop();
                        page = page + 1;
                        startTask(s);
                        isLoading = true;
                    }
                }
            }
        });

        return rootView;
    }

    public void fillWithList(ArrayList<Game> games){

        Game[] gamesArray = new Game[games.size()];
        games.toArray(gamesArray);
        gamesList = new GamesList(gamesArray);
        AddGames(gamesList);
        if (games.size()<40 * page){
            noMoreGames=true;
        }

    }

    public void startSearch(String s){

        query = s;
        startTask(this);

    }

    private void startTask(Searchable v) {

        task = new GetGameListThread(this, query, page);

        if (hasConnectivity()) {
            task.execute();
        }
    }

    public boolean hasConnectivity(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return ((networkInfo != null) && (networkInfo.isConnected()));
    }

    public void LastGame(){
        noMoreGames=true;
        adapter.removeTopGame();
        adapter.notifyItemRemoved(adapter.getItemCount()-1);
        Toast.makeText(getActivity(), "Reached end of searh", Toast.LENGTH_SHORT).show();
    }


    public void AddGames(final GamesList gamesL)
    {
        if (gamesL.GetGames().length==0)
        {
            if (getActivity() instanceof SearchActivity)
            {
                ((SearchActivity)getActivity()).finishedTask();
                Toast.makeText(getActivity(), "Game not found", Toast.LENGTH_SHORT).show();
            }

            return;
        }

        if (!noMoreGames) {
            if (page >= 2) {
                adapter.removeTopGame();
                page = page + 1;
                adapter.addGames(gamesL);
                adapter.notifyItemRangeInserted(1 + (40 * page), 40 * page);
                isLoading = false;
            } else {
                page = 1;
                gamesList.changeGames(gamesL.GetGames());
                adapter.ChangeGames(gamesL);
                adapter.notifyDataSetChanged();
                Log.d("error",adapter.GetGames()[0].getName());
                if (getActivity() instanceof SearchActivity)
                { ((SearchActivity)getActivity()).finishedTask(); }
                isLoading = false;
            }
        }
    }

}
