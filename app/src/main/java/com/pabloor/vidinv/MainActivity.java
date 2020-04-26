package com.pabloor.vidinv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.TransformedResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pabloor.vidinv.Objects.Game;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pabloor.vidinv.Adapters.ListOfListsAdapter;

import io.opencensus.resource.Resource;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    //Repositorio: https://github.com/maiky86/KotlinRepos
    //Glide library para fragments varias funciones interesantes
    //ViewModelpara vistas con mejores ciclos de vida (Presenter con patron de diseño modelview presenter)
    //Estructura View - ViewModel - Repository (Database and Network)
    //Live Data para programacion mas orientada a eventos. MutableLiveData, cambia informacion. LiveData no cambia informacion
    //retrofit convierte APIs en interfaces android facil de usar para acceder a archivos JSON
    //databinding para indicar el texto desde el layout sin tener que buscar el text y setearle el texto

    GoogleSignInClient mGoogleSignInClient;

    //Lista de los nombres de las listas
    List<String> listOfLists;
    //Lista local de las listas de un determinado usuario
    List<Game> pending;
    List<Game> completed;
    List<Game> dropped;
    List<Game> playing;

    BottomAppBar bottom;
    FloatingActionButton fab;

    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String user;

    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        bottom = findViewById(R.id.bottomAppBar);
        fab = findViewById(R.id.newListButton);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Username");
        builder.setMessage("Introduce the username you want to use. This can be changed at any time.");
        // Set up the input
        final EditText input = new EditText(this);
        int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
        int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
        int top = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        int bottomnumber = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        input.setPadding(left, top, right, bottomnumber);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Username");
        builder.setView(input);
        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                preferences.edit().putString("Username",input.getText().toString()).apply();
            }
        });
        builder.show();

        //setSupportActionBar(bottom);
        mAuth = FirebaseAuth.getInstance();
        SignInGoogle();

        check4User();

        bottom.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettings(v);
            }
        });

        instantiateList();

        dummyBD();
        linearLayoutManager = new LinearLayoutManager(this);    //linearLayoutManager.getOrientation()
        RecyclerView gameListview = (RecyclerView) findViewById(R.id.listLists);
        //gameListview.setLayoutManager( new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        gameListview.setLayoutManager(new GridLayoutManager(this, 2));
        gameListview.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL));

        ListOfListsAdapter adapter = new ListOfListsAdapter(this, R.layout.list_item, listOfLists,  new ListOfListsAdapter.IClickListener() {
            @Override
            public void onClickListener(int position) {
                click(position);
            }
        }, new ListOfListsAdapter.ILongClickListener() {
            @Override
            public void onClickLongListener(int position) {
                click(position);
            }
        });
        gameListview.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    public void check4User() {
        SharedPreferences preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        user = preferences.getString("Username", null);
        if (user != null) Log.d(TAG, "User is:" + user.toString());
    }

    private void SignInGoogle()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null){openSingInScreen();}
        else{
            Log.d("Account",account.getDisplayName());
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            preferences.edit().putString("Email",account.getEmail()).apply();
            //preferences.edit().putString("Username",account.getDisplayName()).apply();
            //Log.d("Email",preferences.getString("Email","nul"));
        }
        //updateUI(account);
    }

    private void openSingInScreen()
    {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            preferences.edit().putString("Email",user.getEmail()).apply();


                            preferences.edit().putString("Username",user.getDisplayName()).apply();



                            //Log.d("Email",preferences.getString("Email","nul"));
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication Failed.", Toast.LENGTH_SHORT).show();
                            //Snackbar.make(findViewById(R.id.activity_main), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d("account",account.getDisplayName());
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            preferences.edit().putString("Username",account.getEmail()).apply();
            // Signed in successfully, show authenticated UI.
            //updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }

    public void instantiateList() {
        listOfLists = new ArrayList<String>();
        listOfLists.add("Playing");
        listOfLists.add("Pending");
        listOfLists.add("Dropped");
        listOfLists.add("Completed");
    }

    public void dummyBD() {
        Log.d(TAG, "Retrieving user data...");
        playing = new ArrayList<Game>();
        dropped = new ArrayList<Game>();
        completed = new ArrayList<Game>();
        pending = new ArrayList<Game>();

        check4User();

        if (user == null) {
            //Toast.makeText(this, R.string.not_found_user, Toast.LENGTH_SHORT).show();
        } else {
            db.collection("users").document(user).collection("games")
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    Game current;
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Log.d(TAG, documentSnapshot.getString("name") + ":" + documentSnapshot.getString("list"));
                        switch (documentSnapshot.getString("list")) {
                            case "playing":
                                current = new Game(Integer.parseInt(documentSnapshot.getId()), documentSnapshot.getString("name"), documentSnapshot.getString("image"));
                                if (!checkGameInList(playing, current)) playing.add(current);
                                break;
                            case "dropped":
                                current = new Game(Integer.parseInt(documentSnapshot.getId()), documentSnapshot.getString("name"), documentSnapshot.getString("image"));
                                if (!checkGameInList(dropped, current)) dropped.add(current);
                                break;
                            case "completed":
                                current = new Game(Integer.parseInt(documentSnapshot.getId()), documentSnapshot.getString("name"), documentSnapshot.getString("image"));
                                if (!checkGameInList(completed, current)) completed.add(current);
                                break;
                            case "pending":
                                current = new Game(Integer.parseInt(documentSnapshot.getId()), documentSnapshot.getString("name"), documentSnapshot.getString("image"));
                                if (!checkGameInList(pending, current)) pending.add(current);
                                break;
                        }
                    }
                    Log.d(TAG, "Playing:" + playing.toString());
                    Log.d(TAG, "Dropped:" + dropped.toString());
                    Log.d(TAG, "Completed:" + completed.toString());
                    Log.d(TAG, "Pending:" + pending.toString());
                }
            });
        }
    }

    public boolean checkGameInList(List<Game> list, Game game) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == game.getId()) return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        dummyBD();
        Log.d(TAG, "MAINACTIVITY RETURNED");
    }

    public void openGame(View view) {
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    public void openSettings(View view) {
        Intent intent = new Intent (MainActivity.this, Settings.class);
        startActivity(intent);
    }

    public void click(int pos) {
        List<Game> dummy = new ArrayList<Game>();
        switch (pos) {
            case 0:
                dummy = playing;
                break;
            case 1:
                dummy = pending;
                break;
            case 2:
                dummy = dropped;
                break;
            case 3:
                dummy = completed;
                break;
        }
        if (!dummy.isEmpty()) {
            Log.d(TAG + " - click()", dummy.toString());
            Intent intent = new Intent(getBaseContext(), GameListActivity.class);
            intent.putExtra("NAME_LIST", (ArrayList<Game>) dummy);
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.empty_list, Toast.LENGTH_SHORT).show();
        }
    }
}
