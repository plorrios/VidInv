package com.pabloor.vidinv;

import androidx.appcompat.app.AppCompatActivity;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pabloor.vidinv.Objects.Game;
import com.pabloor.vidinv.tasks.GetGameThread;

public class GamesActivity extends AppCompatActivity {

    GetGameThread task;
    TextView textView;
    PetitionsManager petitions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);

        textView = findViewById(R.id.textViewprueba);
        final Button button = findViewById(R.id.buttonprueba);
        /*button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                //startTask(v);
                getGame(petitions.getGame());
            }
        });*/
    }

    /*private void startTask(View v){
        task = new GetGameThread(petitions);

        if(hasConectivity()) {
            task.execute();
        }
    }*/
    public void getGame(Game game){
        textView.setText(game.getName());
    }


    public boolean hasConectivity(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return ((networkInfo != null) && (networkInfo.isConnected()));
    }



}