package com.pabloor.vidinv.Objects;

public class GamesList {


    private int count;
    private String next;
    private String previous;
    private Game[] results;

    public Game[] GetGames()
    {return results;}
    public int GetCount()
    {return count;}

    public GamesList(Game[] games){
        count = games.length;
        results = games;
    }

    public void changeGames(Game[] games){results = games;}

}
