package com.pabloor.vidinv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pabloor.vidinv.Objects.Game;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    TextView nickname, completeNum, pendingNum, droppedNum, playingNum;
    String email;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().hide();

        nickname = findViewById(R.id.usernameText);
        completeNum = findViewById(R.id.complete_num);
        pendingNum = findViewById(R.id.pending_num);
        droppedNum = findViewById(R.id.dropped_num);
        playingNum = findViewById(R.id.playing_num);

        email = getIntent().getStringExtra("email");

        llenarCosas();
    }

    private void llenarCosas() {
        db.collection("users").document(email).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            nickname.setText(document.getString("nickname"));

                            if (document.getLong("completed") != null) {
                                completeNum.setText(document.getLong("completed").toString());
                            } else { completeNum.setText("0"); }
                            if (document.getLong("pending") != null) {
                                pendingNum.setText(document.getLong("pending").toString());
                            } else { pendingNum.setText("0"); }
                            if (document.getLong("dropped") != null) {
                                droppedNum.setText(document.getLong("dropped").toString());
                            } else { droppedNum.setText("0"); }
                            if (document.getLong("playing") != null) {
                                playingNum.setText(document.getLong("playing").toString());
                            } else { playingNum.setText("0"); }
                        }
                    }
                });
    }

    public void OpenList(android.view.View view){
        switch (view.getId())
        {
            case R.id.complete_Profile:
                getList("completed");
                break;
            case R.id.dropped_Profile:
                getList("dropped");
                break;
            case R.id.pending_Profile:
                getList("pending");
                break;
            case R.id.playing_Profile:
                getList("playing");
                break;
        }

    }

    public void getList(final String s){
        final ArrayList<Game> games = new ArrayList<Game>();
        db.collection("users").document(email).collection("games")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            Game current;
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    if (s.equals(documentSnapshot.get("list"))) {
                        current = new Game(Integer.parseInt(documentSnapshot.getId()), documentSnapshot.getString("name"), documentSnapshot.getString("image"));
                        if (!checkGameInList(games, current)) {games.add(current);}
                    }
                }
                ListOpen(games);
            }
        });
        Log.d("count",Integer.toString(games.size()));

    }

    void ListOpen(ArrayList<Game> games){
        if (!games.isEmpty()) {
            Intent intent = new Intent(ProfileActivity.this, GameListActivity.class);
            intent.putExtra("NAME_LIST", (ArrayList<Game>)games);
            startActivity(intent);
        }else {
            Toast.makeText(this, R.string.empty_list, Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkGameInList(List<Game> list, Game game) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == game.getId()) return true;
        }
        return false;
    }

}
