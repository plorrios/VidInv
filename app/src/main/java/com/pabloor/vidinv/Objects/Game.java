package com.pabloor.vidinv.Objects;

import androidx.annotation.NonNull;

public class Game {

    //añadir @SerializeName cuando nombre de variable sea distinto al nombre en el json

    private int id;
    @NonNull
    private String slug;
    @NonNull
    private String name;
    @NonNull
    private String nameOriginal;
    @NonNull
    private String description;

    private int metacritic;
    private String released;
    private boolean tba;


    private String updated;
    private String backgroundImage;
    private String backgroundImageAdditional;
    private String website;

    @NonNull
    private Double rating;
    private int rating_top;
    private Ratings[] ratings;
    private Reactions reactions;
    private int added;
    private AddedByStatus addedByStatus;
    private int playtime;
    private int screenshotsCount;
    private int moviesCount;
    private int creatorsCount;
    private int achievementsCount;
    private String parentAchievementsCount;
    private String redditUrl;
    private String redditName;
    private String redditDescription;
    private String redditLogo;
    private int redditCount;
    private String twitchCount;
    private String youtubeCount;
    private String reviewsTextCount;
    private int ratingsCount;
    private int suggestionsCount;
    private String[] alternativeNames;
    private String metacriticURL;
    private int parentsCount;
    private int additionsCount;
    private int gameSeriesCount;
    private String developer;

    public Game(int id, @NonNull String name, @NonNull String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getDescription() { return description; }

    public String getReleaseDate() { return released; }

    public String getBackgroundImage() { return backgroundImage; }
}