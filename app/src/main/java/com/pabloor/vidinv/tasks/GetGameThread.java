package com.pabloor.vidinv.tasks;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Debug;
import android.util.Log;

import com.google.gson.Gson;
import com.pabloor.vidinv.GamesActivity;
import com.pabloor.vidinv.Objects.Game;
import com.pabloor.vidinv.Objects.GamesList;

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

    WeakReference<GamesActivity> gamesActivityWeakReference;

    public GetGameThread(GamesActivity activity){
        this.gamesActivityWeakReference = new WeakReference<GamesActivity>(activity);
    }

    @Override
    protected Game doInBackground(Void... voids) {
        //return getGame();
        return getGamesList().GetGames()[0];
    }

    private GamesList getGamesList(){GamesList games = null;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("api.rawg.io");
        builder.appendPath("api");
        builder.appendEncodedPath("games?search=Cyberpunk"); //preguntar porque
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
        return games;
    }

    private Game getGame(){
        Game game = null;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("api.rawg.io");
        builder.appendPath("api");
        builder.appendPath("games");
        builder.appendPath("123");
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
        Log.d("game",game.getName());
        return game;}

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Game game) {
        gamesActivityWeakReference.get().getGame(game);
        super.onPostExecute(game);
    }
}
