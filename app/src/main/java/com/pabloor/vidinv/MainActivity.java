package com.pabloor.vidinv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;

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


    ListView gameListview;
    List<Game> listOfLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listOfLists = new ArrayList<Game>();

        for (int i = 0; i < 50; i++) {
            listOfLists.add(new Game(i, "Prueba " + i, "Generic"));
        }

        gameListview = (ListView) findViewById(R.id.listLists);
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
