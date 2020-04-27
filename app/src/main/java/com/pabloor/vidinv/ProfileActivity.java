package com.pabloor.vidinv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pabloor.vidinv.Objects.Game;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    public static final int PICK_IMAGE = 1;
    TextView nickname, completeNum, pendingNum, droppedNum, playingNum;
    Uri ImageUri;
    String email;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ImageView imagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().hide();

        nickname = findViewById(R.id.usernameText);
        completeNum = findViewById(R.id.complete_num);
        pendingNum = findViewById(R.id.pending_num);
        droppedNum = findViewById(R.id.dropped_num);
        playingNum = findViewById(R.id.playing_num);

        email = getIntent().getStringExtra("email");


        imagen = findViewById(R.id.profile_image);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference gsReference = storage.getReferenceFromUrl("gs://vidinv-8c068.appspot.com");
        StorageReference referenceimage = gsReference.child("plorrios@gmail.com");
        //StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://vidinv-8c068.appspot.com/Captura.PNG");
        referenceimage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Picasso.get().load(uri.toString()).into(imagen);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });


        llenarCosas();
    }

    private void llenarCosas() {
        db.collection("users").document(email).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            nickname.setText(document.getString("nickname"));

                            if (document.getLong("completed") != null) {
                                completeNum.setText(document.getLong("completed").toString());
                            } else { completeNum.setText("0"); }
                            if (document.getLong("pending") != null) {
                                pendingNum.setText(document.getLong("pending").toString());
                            } else { pendingNum.setText("0"); }
                            if (document.getLong("dropped") != null) {
                                droppedNum.setText(document.getLong("dropped").toString());
                            } else { droppedNum.setText("0"); }
                            if (document.getLong("playing") != null) {
                                playingNum.setText(document.getLong("playing").toString());
                            } else { playingNum.setText("0"); }
                        }
                    }
                });
    }

    public void changeProfilePic(android.view.View view){
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(pickIntent, PICK_IMAGE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            ImageUri = data.getData();
            upload();
        }
    }

    public void upload() {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String name = preferences.getString("Email", "basura");
        StorageReference imageRef = storage.getReference(name);

        try {
            imagen.setImageURI(ImageUri);
            //InputStream stream = new FileInputStream(new File(ImageUri.getPath()));

            InputStream stream = getContentResolver().openInputStream(ImageUri);

            UploadTask uploadTask = imageRef.putFile(ImageUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getApplicationContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/vidinv-8c068.appspot.com/o/Captura.PNG?alt=media&token=127ce272-43fb-411f-b614-ee609a7ec96c").into(imagen);
                }
            });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    public void OpenList(android.view.View view){
        switch (view.getId())
        {
            case R.id.complete_Profile:
                getList("completed");
                break;
            case R.id.dropped_Profile:
                getList("dropped");
                break;
            case R.id.pending_Profile:
                getList("pending");
                break;
            case R.id.playing_Profile:
                getList("playing");
                break;
        }

    }

    public void getList(final String s){
        final ArrayList<Game> games = new ArrayList<Game>();
        db.collection("users").document(email).collection("games")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            Game current;
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    if (s.equals(documentSnapshot.get("list"))) {
                        current = new Game(Integer.parseInt(documentSnapshot.getId()), documentSnapshot.getString("name"), documentSnapshot.getString("image"));
                        if (!checkGameInList(games, current)) {games.add(current);}
                    }
                }
                ListOpen(games);
            }
        });
        Log.d("count",Integer.toString(games.size()));

    }

    void ListOpen(ArrayList<Game> games){
        if (!games.isEmpty()) {
            Intent intent = new Intent(ProfileActivity.this, GameListActivity.class);
            intent.putExtra("NAME_LIST", (ArrayList<Game>)games);
            startActivity(intent);
        }else {
            Toast.makeText(this, R.string.empty_list, Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkGameInList(List<Game> list, Game game) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == game.getId()) return true;
        }
        return false;
    }

}
