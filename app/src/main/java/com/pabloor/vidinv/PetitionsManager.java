package com.pabloor.vidinv;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pabloor.vidinv.Objects.Game;
import com.pabloor.vidinv.tasks.GetGameThread;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

public class PetitionsManager extends AppCompatActivity {

    GetGameThread task;
    TextView textView;
    Game game;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        task = new GetGameThread(this);

        if(hasConectivity()) {
            task.execute();
        }
    }

    public void setGame(Game gameFromPetition){
        game = gameFromPetition;
    }
    public void setGameList(Game gameFromPetition){
        game = gameFromPetition;
    }

    public Game getGame(){
        return game;
    }


    public boolean hasConectivity(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return ((networkInfo != null) && (networkInfo.isConnected()));
    }

}
