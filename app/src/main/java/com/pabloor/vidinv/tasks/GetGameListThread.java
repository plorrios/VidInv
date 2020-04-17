package com.pabloor.vidinv.tasks;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.pabloor.vidinv.GamePageActivity;
import com.pabloor.vidinv.Objects.Game;
import com.pabloor.vidinv.Objects.GamesList;
import com.pabloor.vidinv.Searchable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class GetGameListThread extends AsyncTask<Void, Void, GamesList> {

    WeakReference<Searchable> PetitionsWeakReference;
    String busquedaLocal;
    int pageLocal = 1;
    boolean end = false;

    public GetGameListThread(Searchable activity, String busqueda, int page){
        busquedaLocal = busqueda;
        this.PetitionsWeakReference = new WeakReference<Searchable>(activity);
        pageLocal = page;
    }

    // Para usar en otro metodo crear otra WeakReference para la nueva actividad y un nuevo constructor.

    @Override
    protected GamesList doInBackground(Void... voids) {
        //return getGame();
        return getGamesList(busquedaLocal);
    }

    private GamesList getGamesList(String search){
        GamesList games = null;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("api.rawg.io");
        builder.appendPath("api");
        builder.appendEncodedPath("games?page=" + pageLocal + "&page_size=40" + "&search=" + search);
        Log.d("search",builder.build().toString());
        try{
            URL url = new URL(builder.build().toString());
            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            //Log.d("RespondesCode",connection.getResponseMessage());
            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                Gson gsonfile =  new Gson();
                //Log.d("",gsonfile.toString());
                games = gsonfile.fromJson(reader,GamesList.class);
                reader.close();
                //Log.d("received","received");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d("error","error1");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("error","error2");
        }
        if(games!=null) {
            Log.d("search", Integer.toString(games.GetCount()));

        }else{
            end = true;
        }
        return games;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(GamesList games) {
        if (PetitionsWeakReference!=null && !end) {
            PetitionsWeakReference.get().AddGames(games);
        }else if (PetitionsWeakReference!=null && end){
            PetitionsWeakReference.get().LastGame();
        }
        //para añadir uno nuevo añadir un else if para la nueva reference comprobando que no sea null y hacer un get y ejecutar el metodo pasandole el juego
        //gamesActivityWeakReference.get().setGameList(gameList);
        super.onPostExecute(games);
    }
}
