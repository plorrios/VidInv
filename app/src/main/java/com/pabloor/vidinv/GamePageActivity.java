package com.pabloor.vidinv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pabloor.vidinv.Objects.Game;
import com.pabloor.vidinv.tasks.GetGameThread;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class GamePageActivity extends AppCompatActivity {
    GetGameThread task;
    String title, username, targetList;
    TextView description, gameStudio, gameRelease;
    ImageView gameBanner;
    Game currentGame;
    CollapsingToolbarLayout collapsingToolbarLayout;
    int gameId, userGameScore;
    boolean canAdd = false;

    //database instance
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_page);
        SharedPreferences preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);

        username = preferences.getString("Username", null);

        gameId = getIntent().getIntExtra("GAME_ID",-1);
        if (gameId == -1){
            //devolver error de juego no encontrado
        }

        collapsingToolbarLayout = findViewById(R.id.collapsToolbar);
        description = findViewById(R.id.gameDescription);
        gameStudio = findViewById(R.id.studioName);
        gameRelease = findViewById(R.id.gameRelease);
        gameBanner = findViewById(R.id.appbarImage);

        if (username != null) {
            canAdd = true;
            //no user, disable add button
        }

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
        selectListAlert();

        //Toast.makeText(this, "Game test added", Toast.LENGTH_LONG).show();
    }

    private void selectListAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_list_title);
        builder.setMessage(R.string.dialog_list_summary);

        final View customLayout = getLayoutInflater().inflate(R.layout.list_alert_dialogue, null);
        builder.setView(customLayout);

        final LinearLayout scorePart = customLayout.findViewById(R.id.scorePart);
        scorePart.setVisibility(View.GONE);

        final TextView scoreInfo = customLayout.findViewById(R.id.scoreInfo);
        scoreInfo.setText("Score (1/10)");

        SeekBar scoreBar = customLayout.findViewById(R.id.scoreBar);
        scoreBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String score = "Score (" + progress + "/10)";
                scoreInfo.setText(score);
                userGameScore = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });


        RadioGroup radioGroup = customLayout.findViewById(R.id.listGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.completeBtn:
                        targetList = "completed";
                        scorePart.setVisibility(View.VISIBLE);
                        break;
                    case R.id.playingBtn:
                        targetList = "playing";
                        scorePart.setVisibility(View.GONE);
                        break;
                    case R.id.plannedBtn:
                        targetList = "planned";
                        scorePart.setVisibility(View.GONE);
                        break;
                    case R.id.droppedBtn:
                        targetList = "dropped";
                        scorePart.setVisibility(View.GONE);
                        break;
                }
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pushGameToDb(targetList, userGameScore);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void pushGameToDb(String tl, int p) {
        System.out.println("Lista:" + tl);
        System.out.println("Puntuacion:" + p);
    }
}
