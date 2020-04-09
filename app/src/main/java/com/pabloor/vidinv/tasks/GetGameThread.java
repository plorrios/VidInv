package com.pabloor.vidinv.tasks;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.pabloor.vidinv.GamePageActivity;
import com.pabloor.vidinv.Objects.Game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class GetGameThread extends AsyncTask<Void, Void, Game> {

    WeakReference<GamePageActivity> PetitionsWeakReference;
    int id;

    // Para usar en otro metodo crear otra WeakReference para la nueva actividad y un nuevo constructor.

    public GetGameThread(GamePageActivity activity, int idBusqueda){
        id = idBusqueda;
        this.PetitionsWeakReference = new WeakReference<GamePageActivity>(activity);
    }

    @Override
    protected Game doInBackground(Void... voids) {
        return getGame(id);
    }

    private Game getGame(int id){
        Game game = null;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("api.rawg.io");
        builder.appendPath("api");
        builder.appendPath("games");
        builder.appendPath(Integer.toString(id));
        try{
            URL url = new URL(builder.build().toString());
            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                Gson gsonfile =  new Gson();
                Log.d("",gsonfile.toString());
                game = gsonfile.fromJson(reader,Game.class);
                reader.close();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("petition",builder.build().toString());
        Log.d("game", game.getName());
        Log.d("game", Integer.toString(game.getId()));
        return game;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Game game) {
        if (PetitionsWeakReference!=null) {
            PetitionsWeakReference.get().gameValues(game);
        }

        //para añadir uno nuevo añadir un else if para la nueva reference comprobando que no sea null y hacer un get y ejecutar el metodo pasandole el juego

        //gamesActivityWeakReference.get().setGameList(gameList);
        super.onPostExecute(game);
    }
}
