package com.pabloor.vidinv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.TransformedResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pabloor.vidinv.Objects.Game;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pabloor.vidinv.Adapters.ListOfListsAdapter;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    //Repositorio: https://github.com/maiky86/KotlinRepos
    //Glide library para fragments varias funciones interesantes
    //ViewModelpara vistas con mejores ciclos de vida (Presenter con patron de diseño modelview presenter)
    //Estructura View - ViewModel - Repository (Database and Network)
    //Live Data para programacion mas orientada a eventos. MutableLiveData, cambia informacion. LiveData no cambia informacion
    //retrofit convierte APIs en interfaces android facil de usar para acceder a archivos JSON
    //databinding para indicar el texto desde el layout sin tener que buscar el text y setearle el texto

    //Lista de los nombres de las listas
    List<String> listOfLists;
    //Lista local de las listas de un determinado usuario
    List<String> pending;
    List<String> completed;
    List<String> dropped;
    List<String> playing;

    BottomAppBar bottom;
    FloatingActionButton fab;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottom = findViewById(R.id.bottomAppBar);
        fab = findViewById(R.id.newListButton);

        //setSupportActionBar(bottom);

        bottom.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettings(v);
            }
        });

        instantiateList();

        //Aquí debería ir el método de coger los nombres de las diferentes listas de un usuario desde la base de datos
        dummyBD();

        RecyclerView gameListview = (RecyclerView) findViewById(R.id.listLists);
        gameListview.setLayoutManager( new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        gameListview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        ListOfListsAdapter adapter = new ListOfListsAdapter(this, R.layout.list_item, listOfLists,  new ListOfListsAdapter.IClickListener() {
            @Override
            public void onClickListener(int position) {
                click(position);
            }
        }, new ListOfListsAdapter.ILongClickListener() {
            @Override
            public void onClickLongListener(int position) {
                click(position);
            }
        });
        gameListview.setAdapter(adapter);
    }

    public void instantiateList() {
        listOfLists = new ArrayList<String>();
        listOfLists.add("Completed");
        listOfLists.add("Dropped");
        listOfLists.add("Pending");
        listOfLists.add("Playing");
    }

    public void dummyBD() {
        playing = new ArrayList<String>();
        dropped = new ArrayList<String>();
        completed = new ArrayList<String>();
        pending = new ArrayList<String>();

        db.collection("users").document("anleus").collection("games")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Log.d(TAG, documentSnapshot.getString("name") + ":" + documentSnapshot.getString("list"));
                    switch (documentSnapshot.getString("list")) {
                        case "playing":
                            playing.add(documentSnapshot.getId());
                            break;
                        case "dropped":
                            dropped.add(documentSnapshot.getId());
                            break;
                        case "completed":
                            completed.add(documentSnapshot.getId());
                            break;
                        case "pending":
                            pending.add(documentSnapshot.getId());
                            break;
                    }
                }
                Log.d(TAG, "Playing:" + playing.toString());
                Log.d(TAG, "Dropped:" + dropped.toString());
                Log.d(TAG, "Completed:" + completed.toString());
                Log.d(TAG, "Pending:" + pending.toString());
            }
        });

    }

    public void openGame(View view) {
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    public void openSettings(View view) {
        Intent intent = new Intent (MainActivity.this, Settings.class);
        startActivity(intent);
    }

    public void click(int pos) {
        List<String> dummy = new ArrayList<String>();
        switch (pos) {
            case 0:
                dummy = completed;
                break;
            case 1:
                dummy = dropped;
                break;
            case 2:
                dummy = pending;
                break;
            case 3:
                dummy = playing;
                break;
        }
        if (!dummy.isEmpty()) {
            Intent intent = new Intent(getBaseContext(), GameListActivity.class);
            intent.putStringArrayListExtra("NAME_LIST", (ArrayList<String>) dummy);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Esta lista está vacía", Toast.LENGTH_SHORT).show();
        }
    }
}
