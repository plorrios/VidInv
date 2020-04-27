package com.pabloor.vidinv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import io.grpc.Context;

public class TestActivity extends AppCompatActivity {

    public static final int PICK_IMAGE = 1;
    Uri ImageUri;
    ImageView imagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        imagen = findViewById(R.id.testImage);

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
    }

    public void SelectImage(android.view.View view){

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

}
