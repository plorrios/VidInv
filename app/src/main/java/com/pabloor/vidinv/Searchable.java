package com.pabloor.vidinv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.pabloor.vidinv.Adapters.GamesListAdapter;
import com.pabloor.vidinv.Objects.Game;
import com.pabloor.vidinv.Objects.GamesList;
import com.pabloor.vidinv.tasks.GetGameListThread;
import com.pabloor.vidinv.tasks.GetGameThread;

import java.util.Arrays;
import java.util.List;

public class Searchable extends AppCompatActivity {

    GamesListAdapter adapter;
    GetGameListThread task;
    GamesList gamesList;
    Handler handler;
    String query;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        handler = new Handler();

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        query = intent.getStringExtra(SearchManager.QUERY);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            startTask(this);
        }
    }

    private void startTask(Searchable v) {

        task = new GetGameListThread(this, query);

        if (hasConnectivity()) {
            task.execute();
        }
    }

    public boolean hasConnectivity(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return ((networkInfo != null) && (networkInfo.isConnected()));
    }


    public void AddGames(final GamesList gamesL)
    {
        gamesList = gamesL;
        adapter = new GamesListAdapter(gamesList, new GamesListAdapter.InterfaceClick() {
            @Override
            public void OnInterfaceClick(int position) {
                if (adapter.GetGame(position) == null) {
                    Toast.makeText(Searchable.this, "Game not found", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(Searchable.this, GamePageActivity.class);
                intent.putExtra("GAME_ID", gamesL.GetGames()[position].getId());
                startActivity(intent);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.SearchableLayout);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }
}
