package com.pabloor.vidinv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class Settings extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().hide();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                Log.d("key",key);
                if (key.equals("MainVisualization")) {
                    Toast.makeText(getApplicationContext(),
                            "Restart the application to see the changes ",
                            Toast.LENGTH_SHORT).show();
                }
                else if(key.equals("Username")) {
                    Toast.makeText(getApplicationContext(), "Username successfully changed", Toast.LENGTH_LONG).show();
                    db.collection("users")
                            .document(prefs.getString("Email", null))
                            .update("nickname", prefs.getString("Username", null));
                }
            }
        };

        preferences.registerOnSharedPreferenceChangeListener(listener);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_layout, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences_setttings, rootKey);
        }
    }
}
