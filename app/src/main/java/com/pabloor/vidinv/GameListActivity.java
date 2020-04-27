package com.pabloor.vidinv;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pabloor.vidinv.Adapters.GameListAdapter;
import com.pabloor.vidinv.Objects.Game;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class GameListActivity extends AppCompatActivity {

    ArrayList<Game> mainList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String username;
    GameListAdapter gadapt;
    RecyclerView gameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_games);
        getSupportActionBar().hide();

        SharedPreferences preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        username = preferences.getString("Email", null);

        mainList = (ArrayList<Game>) getIntent().getSerializableExtra("NAME_LIST");
        Log.d("GameListActivity", mainList.toString());

        gameList = findViewById(R.id.gameList);
        gameList.setLayoutManager( new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        gameList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        gadapt = new GameListAdapter(mainList,  new GameListAdapter.IClickListener() {
            @Override
            public void onClickListener(int position) {
                //showToast(position);
                Intent intent = new Intent(GameListActivity.this, GamePageActivity.class);
                intent.putExtra("GAME_ID", mainList.get(position).getId());
                startActivity(intent);
            }
        }, new GameListAdapter.ILongClickListener() {
            @Override
            public void onClickLongListener(final int position) {
                new MaterialAlertDialogBuilder(GameListActivity.this)
                        .setTitle(R.string.deleteTitle)
                        .setMessage(R.string.deleteSup)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteGame(mainList.get(position).getId(), position);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Nothing. Hi.
                            }
                        })
                        .show();
            }
        });
        gameList.setAdapter(gadapt);
    }

    private void deleteGame(int id, final int pos) {
        db.collection("users/" + username + "/games").document(String.valueOf(id))
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //gadapt.notifyDataSetChanged();
                        mainList.remove(pos);
                        gameList.removeViewAt(pos);
                        gadapt.notifyItemRemoved(pos);
                        gadapt.notifyItemRangeChanged(pos, mainList.size());
                        showToastString(R.string.deleteConf);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToastString(R.string.deleteFailure);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        gadapt.notifyDataSetChanged();
    }

    public void openGame(View view) {
        Intent intent = new Intent(GameListActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    public void showToastString(int s) {
        Toast.makeText(this, getString(s), Toast.LENGTH_LONG).show();
    }

    public void showToast(int i) {
        Toast.makeText(this, "Juego: " + mainList.get(i).getId(), Toast.LENGTH_LONG).show();
    }
}
