package com.pabloor.vidinv;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pabloor.vidinv.Adapters.GameListAdapter;
import com.pabloor.vidinv.Objects.Game;

import java.util.ArrayList;

public class GameListActivity extends AppCompatActivity {

    ArrayList<Game> mainList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_games);
        getSupportActionBar().hide();

        SharedPreferences preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        username = preferences.getString("Username", null);

        mainList = (ArrayList<Game>) getIntent().getSerializableExtra("NAME_LIST");

        RecyclerView gameList = findViewById(R.id.gameList);
        gameList.setLayoutManager( new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        gameList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        GameListAdapter gadapt = new GameListAdapter(mainList,  new GameListAdapter.IClickListener() {
            @Override
            public void onClickListener(int position) {
                showToast();
            }
        }, new GameListAdapter.ILongClickListener() {
            @Override
            public void onClickLongListener(int position) {
                showToast();
            }
        });
        gameList.setAdapter(gadapt);

        //String msg = mainList.isEmpty() ? "Empty list" : "Clicked on " + mainList.get(0).toString();
        //Comprobación: Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void deleteGame(int id) {
        db.collection("users/" + username + "/games").document(id + "")
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("error","Juego no eliminado");
                    }
                });
    }

    public void showToast() {
        Toast.makeText(this, "Juego eliminado", Toast.LENGTH_LONG).show();
    }
}
