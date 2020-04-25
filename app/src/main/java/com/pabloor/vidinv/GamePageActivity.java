package com.pabloor.vidinv;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pabloor.vidinv.Objects.Game;
import com.pabloor.vidinv.tasks.GetGameThread;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GamePageActivity extends AppCompatActivity {
    GetGameThread task;
    String title, username, targetList;
    TextView description, gameStudio, gameRelease, redditURL, metacriticURL;
    FloatingActionButton addButton, editButton;
    ImageView gameBanner;
    Game currentGame;
    CollapsingToolbarLayout collapsingToolbarLayout;
    int gameId, userGameScore;

    //database instance
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_page);

        getSupportActionBar().hide();

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
        redditURL = findViewById(R.id.reddit_link);
        metacriticURL = findViewById(R.id.metacritic_link);
        addButton = findViewById(R.id.add_btn);
        editButton = findViewById(R.id.edit_btn);

        if (username == null) {
            addButton.setVisibility(View.GONE);
            editButton.setVisibility(View.GONE);
        }
        
        startTask(this);

        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
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

    private void gameInDatabase(final Game game) {
        Query existInDb = db.collection("users/" + username + "/games")
                .whereEqualTo("name", game.getName());

        db.collection("users/" + username + "/games")
                .whereEqualTo("name", game.getName())
                .limit(1).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                activateAddBtn();
                            } else {
                                activateEditBtn();
                            }
                        }
                    }
                });

    }

    public void gameValues(Game game) throws ParseException {
        gameInDatabase(game);

        currentGame = game;
        title = game.getName();
        collapsingToolbarLayout.setTitle(title.subSequence(0, title.length()));
        Picasso.get().load(game.getBackgroundImage()).into(gameBanner);
        description.setText(htmlToText(game.getDescription()));
        gameRelease.setText(dataFormat(game.getReleaseDate()));
        redditURL.setText(game.getRedditURL());
        metacriticURL.setText(game.getMetacriticURL());
    }

    private String htmlToText(String html) {
        String text = html.replaceAll("\\<.*?\\>", "");
        return text;
    }

    private String dataFormat(String oldDate) throws ParseException {
        String oldFormat = "yyyy-MM-dd";
        String newFormat = "dd/MM/yyyy";

        String newDate;
        SimpleDateFormat sdf = new SimpleDateFormat(oldFormat);
        Date d = sdf.parse(oldDate);
        sdf.applyPattern(newFormat);
        newDate = sdf.format(d);

        return newDate;
    }

    public void addGame(View view) {
        selectListAlert(false);
    }

    public void editGame(View view) {
        selectListAlert(true);
    }

    private void selectListAlert(final boolean edit) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_list_title);
        builder.setMessage(R.string.dialog_list_summary);

        final View customLayout = getLayoutInflater().inflate(R.layout.list_alert_dialogue, null);
        builder.setView(customLayout);

        final LinearLayout scorePart = customLayout.findViewById(R.id.scorePart);
        scorePart.setVisibility(View.GONE);

        final TextView scoreInfo = customLayout.findViewById(R.id.scoreInfo);
        scoreInfo.setText("Score (5/10)");

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
                        targetList = "pending";
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
                if (!edit) {
                    pushGameToDb(targetList, userGameScore);
                    activateEditBtn();
                } else {
                    editGameInDB(targetList, userGameScore);
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void editGameInDB(String selectList, int userScore) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("list", selectList);
        if (selectList.equals("completed")) {
            updates.put("score", userScore);
        } else {
            updates.put("score", "-");
        }

        db.collection("users/" + username + "/games")
                .document(currentGame.getId() + "")
                    .update(updates)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("si", "DocumentSnapshot successfully updated!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("si", "Error updating document", e);
                        }
                    });
    }

    private void pushGameToDb(String selectList, int userScore) {
        Map<String, Object> savedGame = new HashMap<>();
        savedGame.put("name", currentGame.getName());
        savedGame.put("image", currentGame.getBackgroundImage());
        savedGame.put("list", selectList);

        if(selectList.equals("completed")) {
            savedGame.put("score", userScore);
        } else {
            savedGame.put("score", "-");
        }

        db.collection("users/" + username + "/games")
                .document(currentGame.getId() + "")
                .set(savedGame);
    }

    public void activateAddBtn() {
        addButton.setVisibility(View.VISIBLE);
        editButton.setVisibility(View.INVISIBLE);
    }

    public void activateEditBtn() {
        addButton.setVisibility(View.INVISIBLE);
        editButton.setVisibility(View.VISIBLE);
    }
}
