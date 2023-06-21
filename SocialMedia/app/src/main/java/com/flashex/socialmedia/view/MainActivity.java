package com.flashex.socialmedia.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.flashex.socialmedia.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //initialize
        auth = FirebaseAuth.getInstance();

        //check to see if the user is currently signed in.
        FirebaseUser currentUser=auth.getCurrentUser();

        if (currentUser!=null){
            Intent intent=new Intent(MainActivity.this, FeedActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void SignInClick(View view) {
        String email = binding.emailTextEdit.getText().toString();
        String password = binding.passwordEditText.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Emil and Password Please", Toast.LENGTH_SHORT).show();
        } else {
            auth.signInWithEmailAndPassword(email,password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Intent intent = new Intent(MainActivity.this, FeedActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void SignUpClick(View view) {
        String email = binding.emailTextEdit.getText().toString();
        String password = binding.passwordEditText.getText().toString();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Emil and Password Please", Toast.LENGTH_SHORT).show();
        } else {
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Intent intent = new Intent(MainActivity.this, FeedActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}