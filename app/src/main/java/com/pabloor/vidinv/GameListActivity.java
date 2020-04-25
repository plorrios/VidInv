package com.pabloor.vidinv;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pabloor.vidinv.Objects.Game;

import java.util.ArrayList;

public class GameListActivity extends AppCompatActivity {

    ArrayList<Game> mainList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_games);

        mainList = (ArrayList<Game>) getIntent().getSerializableExtra("NAME_LIST");
        String msg = mainList.isEmpty() ? "Empty list" : "Clicked on " + mainList.get(0).toString();
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
