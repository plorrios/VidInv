package com.pabloor.vidinv.tasks;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Debug;
import android.util.Log;

import com.google.gson.Gson;
import com.pabloor.vidinv.GamePageActivity;
import com.pabloor.vidinv.GamesActivity;
import com.pabloor.vidinv.Objects.Game;
import com.pabloor.vidinv.Objects.GamesList;
import com.pabloor.vidinv.PetitionsManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class GetGameThread extends AsyncTask<Void, Void, Game> {

    WeakReference<GamePageActivity> PetitionsWeakReference;
    String busquedaLocal;

    public GetGameThread(GamePageActivity activity, String busqueda){
        busquedaLocal = busqueda;
        this.PetitionsWeakReference = new WeakReference<GamePageActivity>(activity);
    }

    @Override
    protected Game doInBackground(Void... voids) {
        //return getGame();
        return getGame(getGamesList(busquedaLocal).GetGames()[0].getId());
    }

    private GamesList getGamesList(String search){GamesList games = null;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("api.rawg.io");
        builder.appendPath("api");
        builder.appendEncodedPath("games?search=" + search);
        try{
            URL url = new URL(builder.build().toString());
            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            Log.d("RespondesCode",connection.getResponseMessage());
            //if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                Gson gsonfile =  new Gson();
                Log.d("",gsonfile.toString());
                games = gsonfile.fromJson(reader,GamesList.class);
                reader.close();
                Log.d("received","received");
            //}
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d("error","error1");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("error","error2");
        }
        Log.d("search",Integer.toString(games.GetCount()));
        //Log.d("Description",games.GetGames()[0].getDescription());
        return games;
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
        Log.d("game", game.getName());
        return game;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Game game) {
        PetitionsWeakReference.get().gameValues(game);
        //gamesActivityWeakReference.get().setGameList(gameList);
        super.onPostExecute(game);
    }
}
