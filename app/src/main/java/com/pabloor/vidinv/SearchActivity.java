package com.pabloor.vidinv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;

public class SearchActivity extends AppCompatActivity {

    Searchable fragment;
    ProgressBar progressBar = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().hide();

        /*FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragment = new Searchable();
        fragmentTransaction.add(R.id.my_fragment, fragment);
        fragmentTransaction.commit();*/

        fragment = (Searchable) getSupportFragmentManager().findFragmentById(R.id.my_fragment);

        progressBar = findViewById(R.id.progressBar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


            @Override
            public boolean onQueryTextSubmit(String s) {
                //Searchable searchable = (Searchable) getSupportFragmentManager().findFragmentByTag("com.pabloor.vidinv.Searchable");
                //searchable.startSearch(s);
                fragment.startSearch(s);
                progressBar.setVisibility(View.VISIBLE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // do your search on change or save the last string in search
                return false;
            }
        });

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        return true;
    }

    public void finishedTask()
    {
        progressBar.setVisibility(View.INVISIBLE);
    }

}
