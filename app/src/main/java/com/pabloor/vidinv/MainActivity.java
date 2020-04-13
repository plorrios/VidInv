package com.pabloor.vidinv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableLayout;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pabloor.vidinv.Objects.Game;

import java.util.ArrayList;
import java.util.List;

import adapters.ListOfListsAdapter;

public class MainActivity extends AppCompatActivity {
    //Repositorio: https://github.com/maiky86/KotlinRepos
    //Glide library para fragments varias funciones interesantes
    //ViewModelpara vistas con mejores ciclos de vida (Presenter con patron de diseño modelview presenter)
    //Estructura View - ViewModel - Repository (Database and Network)
    //Live Data para programacion mas orientada a eventos. MutableLiveData, cambia informacion. LiveData no cambia informacion
    //retrofit convierte APIs en interfaces android facil de usar para acceder a archivos JSON
    //databinding para indicar el texto desde el layout sin tener que buscar el text y setearle el texto

    //Lista de los nombres de las listas
    List<String> listOfLists;
    //Lista local de las listas completas de un determinado usuario
    List<List<Game>> userList;

    BottomAppBar bottom;
    FloatingActionButton fab;

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

        listOfLists = new ArrayList<String>();

        //Aquí debería ir el método de coger los nombres de las diferentes listas de un usuario desde la base de datos
        for (int i = 0; i < 15; i++) {
            listOfLists.add("Lista " + i);
        }

        RecyclerView gameListview = (RecyclerView) findViewById(R.id.listLists);
        gameListview.setLayoutManager( new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        gameListview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        ListOfListsAdapter adapter = new ListOfListsAdapter(this, R.layout.list_item, listOfLists);
        gameListview.setAdapter(adapter);
    }

    public void openGame(View view) {
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    public void openSettings(View view) {
        Intent intent = new Intent (MainActivity.this, Settings.class);
        startActivity(intent);
    }
}
