package com.pabloor.vidinv;

import androidx.appcompat.app.AppCompatActivity;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pabloor.vidinv.Objects.Game;
import com.pabloor.vidinv.tasks.GetGameThread;
import com.squareup.picasso.Picasso;

public class GamePageActivity extends AppCompatActivity {

    private GetGameThread task;

    private String title;
    private TextView description;
    private TextView gameStudio;
    private TextView gameRelease;
    private ImageView gameBanner;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    int gameId;

    //root reference to database
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference gamesRef = rootRef.child("games-test");

    private Game currentGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_page);

        gameId = getIntent().getIntExtra("GAME_ID",-1);
        if (gameId == -1){
            //devolver error de juego no encontrado
        }

        collapsingToolbarLayout = findViewById(R.id.collapsToolbar);
        description = findViewById(R.id.gameDescription);
        gameStudio = findViewById(R.id.studioName);
        gameRelease = findViewById(R.id.gameRelease);
        gameBanner = findViewById(R.id.appbarImage);

        startTask(this);
    }

    private void startTask(GamePageActivity v) {
        task = new GetGameThread(this, gameId);

        if (hasConnectivity()) {
            task.execute();
        }
    }

    public boolean hasConnectivity() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return ((networkInfo != null) && (networkInfo.isConnected()));
    }

    public void gameValues(Game game) {
        currentGame = game;
        title = game.getName();
        //llamar a setTitle de CollapsingToolbarLayout para poner el titulo que queramos, creo
        collapsingToolbarLayout.setTitle(title.subSequence(0, title.length()));
        Picasso.get().load(game.getBackgroundImage()).into(gameBanner);
        description.setText(game.getDescription());
        gameRelease.setText(game.getReleaseDate());
    }

    public void addGame(View view) {
        gamesRef.child(currentGame.getId() + "").setValue(currentGame);

        Toast.makeText(this, "Game test added", Toast.LENGTH_LONG).show();
    }

}
