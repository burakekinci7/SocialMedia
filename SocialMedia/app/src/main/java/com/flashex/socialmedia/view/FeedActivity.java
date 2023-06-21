package com.flashex.socialmedia.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.flashex.socialmedia.R;
import com.flashex.socialmedia.adapter.PostAdapter;
import com.flashex.socialmedia.databinding.ActivityFeedBinding;

import com.flashex.socialmedia.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    Toolbar toolbar;
    private FirebaseFirestore firestore;
    ArrayList<Post> postArrayList;
    private ActivityFeedBinding binding;
    PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //initialize
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        postArrayList = new ArrayList<>();

        //toolbar
        setSupportActionBar(binding.toolbar);

        //call func
        getData();

        //set adapter
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter=new PostAdapter(postArrayList);
        binding.recyclerView.setAdapter(postAdapter);
    }

    private void getData() {
        firestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(getApplicationContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                if (value != null) {
                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        Map<String, Object> data = snapshot.getData();

                        String email = (String) data.get("useremail");
                        String comment = (String) data.get("comment");
                        String downloadUri = (String) data.get("downloaduri");
                        System.out.println(email);

                        Post post = new Post(email, comment, downloadUri);
                        postArrayList.add(post);
                    }
                    postAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.addPost) {
            //Upload Activity
            Intent intent = new Intent(FeedActivity.this, UploadActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.signOut) {
            //sign Out
            auth.signOut();
            Intent intentToMain = new Intent(FeedActivity.this, MainActivity.class);
            startActivity(intentToMain);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}