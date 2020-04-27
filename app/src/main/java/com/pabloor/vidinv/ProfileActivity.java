package com.pabloor.vidinv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    TextView nickname, completeNum, pendingNum, droppedNum, plannedNum;
    String email;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().hide();

        nickname = findViewById(R.id.usernameText);
        completeNum = findViewById(R.id.complete_num);
        pendingNum = findViewById(R.id.pending_num);
        droppedNum = findViewById(R.id.dropped_num);
        plannedNum = findViewById(R.id.pending_num);

        email = getIntent().getStringExtra("email");

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

                            completeNum.setText(document.getLong("completed").toString());
                            pendingNum.setText(document.getLong("pending").toString());
                            droppedNum.setText(document.getLong("dropped").toString());
                            plannedNum.setText(document.getLong("planned").toString());
                        }
                    }
                });
    }
}
